/*
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2006-2011 Serotonin Software Technologies Inc.
    @author Matthew Lohbihler
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.rt.maint.work;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import com.serotonin.mango.util.LoggingUtils;
import com.serotonin.mango.vo.event.EventHandlerVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.serotonin.io.StreamUtils;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.event.type.SystemEventType;
import com.serotonin.mango.rt.maint.BackgroundProcessing;
import com.serotonin.util.StringUtils;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * @author Matthew Lohbihler
 */
public class ProcessWorkItem extends AbstractBeforeAfterWorkItem {
    static final Log LOG = LogFactory.getLog(ProcessWorkItem.class);
    private static final int TIMEOUT = 15000; // 15 seconds

    @Deprecated
    public static void queueProcess(String command) {
        ProcessWorkItem item = new ProcessWorkItem(command);
        Common.ctx.getBackgroundProcessing().addWorkItem(item);
    }

    public static void queueProcess(String command, EventHandlerVO handler) {
        ProcessWorkItem item = new ProcessWorkItem(command, LoggingUtils.eventHandlerInfo(handler));
        Common.ctx.getBackgroundProcessing().addWorkItem(item);
    }

    final String command;
    final String details;

    @Deprecated
    public ProcessWorkItem(String command) {
        this.command = command;
        this.details = null;
    }

    public ProcessWorkItem(String command, String details) {
        this.command = command;
        this.details = details;
    }

    @Override
    public void work() {
        try {
            executeProcessCommand(command, details);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void workFail(Throwable e) {
        Throwable throwable = e.getCause() != null ? e.getCause() : e;
        LOG.error(this + " - " + throwable.getMessage(), throwable);
        SystemEventType.raiseEvent(new SystemEventType(SystemEventType.TYPE_PROCESS_FAILURE),
                System.currentTimeMillis(), false,
                new LocalizableMessage("event.process.failure", command, throwable.getMessage()));
    }

    public static void executeProcessCommand(String command, String details) throws IOException {
        BackgroundProcessing bp = Common.ctx.getBackgroundProcessing();

        Process process = Runtime.getRuntime().exec(command);

        InputReader out = new InputReader(process.getInputStream(), details);
        InputReader err = new InputReader(process.getErrorStream(), details);

        bp.addWorkItem(out);
        bp.addWorkItem(err);

        try {
            ProcessTimeout timeout = new ProcessTimeout(process, command, details);
            bp.addWorkItem(timeout);

            process.waitFor();
            out.join();
            err.join();
            process.destroy();

            // If we've made it this far, the process exited properly, so kill the timeout thread if it exists.
            timeout.interrupt();

            String input = out.getInput();
            if (!StringUtils.isEmpty(input))
                LOG.info("Process output: '" + input + "'");

            input = err.getInput();
            if (!StringUtils.isEmpty(input))
                LOG.warn("Process error: '" + input + "'");
        }
        catch (InterruptedException e) {
            throw new IOException("Timeout while running command: '" + command + "'");
        }
    }

    @Override
    public int getPriority() {
        return WorkItem.PRIORITY_HIGH;
    }

    static class ProcessTimeout extends AbstractBeforeAfterWorkItem {
        private final Process process;
        private final String command;
        private volatile boolean interrupted;
        private final String details;

        @Deprecated
        ProcessTimeout(Process process, String command) {
            this.process = process;
            this.command = command;
            this.details = null;
        }

        ProcessTimeout(Process process, String command, String details) {
            this.process = process;
            this.command = command;
            this.details = details;
        }

        @Override
        public int getPriority() {
            return WorkItem.PRIORITY_HIGH;
        }

        public void interrupt() {
            synchronized (this) {
                interrupted = true;
                notifyAll();
            }
        }

        @Override
        public void work() {
            try {
                synchronized (this) {
                    wait(TIMEOUT);
                }

                if (!interrupted) {
                    // If the sleep time has expired, destroy the process.
                    LOG.warn("Timeout waiting for process to end. command=" + command);
                    process.destroy();
                }
            }
            catch (InterruptedException e) { /* no op */
            }
        }

        @Override
        public void workFail(Throwable e) {
            LOG.error("Error in process timeout: " + this, e);
        }

        @Override
        public String toString() {
            return "ProcessTimeout{" +
                    "details='" + details + '\'' +
                    ", command='" + command + '\'' +
                    ", interrupted=" + interrupted +
                    "} " + super.toString();
        }

        @Override
        public String getDetails() {
            return this.toString();
        }
    }

    static class InputReader extends AbstractBeforeAfterWorkItem {
        private final InputStreamReader reader;
        private final StringWriter writer = new StringWriter();
        private boolean done;
        private final String details;

        @Deprecated
        InputReader(InputStream is) {
            this.reader = new InputStreamReader(is);
            this.details = null;
        }

        InputReader(InputStream is, String details) {
            this.reader = new InputStreamReader(is);
            this.details = details;
        }

        public String getInput() {
            return writer.toString();
        }

        public void join() {
            synchronized (this) {
                if (!done) {
                    try {
                        wait();
                    }
                    catch (InterruptedException e) {
                        // no op
                    }
                }
            }
        }

        @Override
        public int getPriority() {
            return WorkItem.PRIORITY_HIGH;
        }

        @Override
        public void work() {
            try {
                StreamUtils.transfer(reader, writer);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            finally {
                synchronized (this) {
                    done = true;
                    notifyAll();
                }
            }
        }

        @Override
        public void workFail(Throwable e) {
            LOG.error("Error in process input reader: " + this, e);
        }

        @Override
        public String toString() {
            return "InputReader{" +
                    "details='" + details + '\'' +
                    ", done=" + done +
                    "} " + super.toString();
        }

        @Override
        public String getDetails() {
            return this.toString();
        }
    }

    @Override
    public String toString() {
        return "ProcessWorkItem{" +
                "command='" + command + '\'' +
                ", details='" + details + '\'' +
                "}";
    }

    @Override
    public String getDetails() {
        return this.toString();
    }

    //
    // public static void main(String[] args) throws Exception {
    // // ServletContext ctx = new DummyServletContext();
    // BackgroundProcessing bp = new BackgroundProcessing();
    // bp.initialize();
    // // ctx.setAttribute(Common.ContextKeys.BACKGROUND_PROCESSING, bp);
    // // Common.ctx = new ContextWrapper(ctx);
    // // ProcessWorkItem.queueProcess("");
    // // bp.terminate();
    //        
    // // //ProcessBuilder pb = new ProcessBuilder("cmd /c dir");
    // // ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "dir");
    // // pb.redirectErrorStream(true);
    // // Process process = pb.start();
    // Process process = Runtime.getRuntime().exec("cmd /c java -version");
    //        
    // InputReader out = new InputReader(process.getInputStream());
    // InputReader err = new InputReader(process.getErrorStream());
    //        
    // bp.addWorkItem(out);
    // bp.addWorkItem(err);
    //        
    // process.waitFor();
    // out.join();
    // err.join();
    // process.destroy();
    // bp.terminate();
    //        
    // System.out.println("out: "+ out.getInput());
    // System.out.println("err: "+ err.getInput());
    // }
}
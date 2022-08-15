package org.scada_lts.web.mvc.api.json;


import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ThreadInfo {

    private long id;
    private boolean interrupted;
    private boolean alive;
    private boolean daemon;
    private int priority;
    private String name;
    private List<StackInfo> stackTrace;
    private Thread.State state;

    public ThreadInfo(Thread thread) {
        this.id = thread.getId();
        this.interrupted = thread.isInterrupted();
        this.alive = thread.isAlive();
        this.daemon = thread.isDaemon();
        this.priority = thread.getPriority();
        this.name = thread.getName();
        this.stackTrace = Stream.of(thread.getStackTrace()).map(ThreadInfo.StackInfo::new).collect(Collectors.toList());
        this.state = thread.getState();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isInterrupted() {
        return interrupted;
    }

    public void setInterrupted(boolean interrupted) {
        this.interrupted = interrupted;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public boolean isDaemon() {
        return daemon;
    }

    public void setDaemon(boolean daemon) {
        this.daemon = daemon;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ThreadInfo.StackInfo> getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(List<ThreadInfo.StackInfo> stackTrace) {
        this.stackTrace = stackTrace;
    }

    public Thread.State getState() {
        return state;
    }

    public void setState(Thread.State state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "[" + id + ", " + priority + ", " + name + ", " + state + ']';
    }

    public static class StackInfo {

        private String classLoaderName;
        private String moduleName;
        private String moduleVersion;
        private String methodName;
        private String fileName;
        private int lineNumber;
        private String className;
        private boolean nativeMethod;

        public StackInfo(StackTraceElement stackTraceElement) {
            this.classLoaderName = stackTraceElement.getClassLoaderName();
            this.moduleName = stackTraceElement.getModuleName();
            this.moduleVersion = stackTraceElement.getModuleVersion();
            this.methodName = stackTraceElement.getMethodName();
            this.fileName = stackTraceElement.getFileName();
            this.lineNumber = stackTraceElement.getLineNumber();
            this.className = stackTraceElement.getClassName();
            this.nativeMethod = stackTraceElement.isNativeMethod();
        }

        public String getClassLoaderName() {
            return classLoaderName;
        }

        public void setClassLoaderName(String classLoaderName) {
            this.classLoaderName = classLoaderName;
        }

        public String getModuleName() {
            return moduleName;
        }

        public void setModuleName(String moduleName) {
            this.moduleName = moduleName;
        }

        public String getModuleVersion() {
            return moduleVersion;
        }

        public void setModuleVersion(String moduleVersion) {
            this.moduleVersion = moduleVersion;
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public void setLineNumber(int lineNumber) {
            this.lineNumber = lineNumber;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public boolean isNativeMethod() {
            return nativeMethod;
        }

        public void setNativeMethod(boolean nativeMethod) {
            this.nativeMethod = nativeMethod;
        }
    }
}

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
package com.serotonin.mango.web.dwr.beans;

/**
 * Checks every 10 seconds to see if it's last query time is older than one minute. If so, the shutOff method is called.
 * 
 * @author Matthew Lohbihler
 */
abstract public class AutoShutOff extends Thread {
    public static final long DEFAULT_TIMEOUT = 60000;

    private long lastQuery;
    private volatile boolean running;
    private final long timeout;

    public AutoShutOff() {
        this(DEFAULT_TIMEOUT);
    }

    public AutoShutOff(long timeout) {
        this.timeout = timeout;
        update();
        start();
    }

    public void update() {
        lastQuery = System.currentTimeMillis();
    }

    @Override
    public void run() {
        running = true;

        while (running) {
            if (System.currentTimeMillis() - lastQuery > timeout) {
                running = false;
                shutOff();
                break;
            }

            synchronized (this) {
                try {
                    wait(10000);
                }
                catch (InterruptedException e) {
                    // no op
                }
            }
        }
    }

    public void cancel() {
        running = false;
        synchronized (this) {
            notify();
        }
    }

    abstract void shutOff();
}

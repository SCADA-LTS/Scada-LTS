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
package com.serotonin.mango.util;

import com.serotonin.mango.vo.User;

/**
 * @author Matthew Lohbihler
 */
public class BackgroundContext {
    /**
     * The ThreadLocal instance that will contain the various BackgroundContext objects.
     */
    private static ThreadLocal<BackgroundContext> contextStore = new ThreadLocal<BackgroundContext>();

    /**
     * Creates the BackgroundContext instance for this thread and adds it to the store.
     */
    public static void set(User user) {
        contextStore.set(new BackgroundContext(user));
    }

    public static void set(String processDescriptionKey) {
        contextStore.set(new BackgroundContext(processDescriptionKey));
    }

    /**
     * Used within user code to access the context objects.
     * 
     * @return the BackgroundContext object found for this thread.
     */
    public static BackgroundContext get() {
        return contextStore.get();
    }

    /**
     * Removes the BackgroundContext object from this thread once we are done with it.
     */
    public static void remove() {
        contextStore.remove();
    }

    private final User user;
    private final String processDescriptionKey;

    /**
     * Constructor
     */
    private BackgroundContext(User user) {
        this.user = user;
        this.processDescriptionKey = null;
    }

    private BackgroundContext(String processDescriptionKey) {
        this.user = null;
        this.processDescriptionKey = processDescriptionKey;
    }

    public User getUser() {
        return user;
    }

    public String getProcessDescriptionKey() {
        return processDescriptionKey;
    }
}

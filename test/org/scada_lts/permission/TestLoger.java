/*
 * (c) 2017 Abil'I.T. http://abilit.eu/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.scada_lts.permission;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

/**
 * @author Grzegorz Bylica grzegorz.bylica@gmail.com
 **/
public class TestLoger {

    static {
        Logger.getRootLogger().addAppender(new TestAppender());
    }

    public static class TestAppender extends AppenderSkeleton {
        public void close() {}
        public boolean requiresLayout() {return false;}
        @Override
        protected void append(LoggingEvent event) {
            System.out.println(""+event.getMessage());
        }
    }

}

/*
 * Copyright (C) 2020 Abil'I.T.
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

package org.scada_lts.web.mvc.api.alarms;

import java.util.regex.Pattern;

/**
 * Create by at Mateusz Hyski
 *
 * Contain ONLY regex definitions
 *
 *
 * @author 02-06-2020 on behalf of Abil'I.T. (code owner) email: sdt@abilit.eu
 */
class RegexSyntax {

    final static Pattern VALUE_NOT_NUMERIC = Pattern.compile("[^0-9]");
    final static Pattern VALUE_BETWEEN_0_TO_9999 = Pattern.compile("[0-9]{1,4}");
    final static Pattern DATE_FORMAT = Pattern.compile("^[0-9]{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$");
}

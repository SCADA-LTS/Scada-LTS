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
package com.serotonin.mango.rt.event.compound;

/**
 * @author Matthew Lohbihler
 */
public class AndOperator extends BinaryOperator {
    public AndOperator(LogicalOperator operand1, LogicalOperator operand2) {
        super(operand1, operand2);
    }

    @Override
    public boolean evaluate() {
        return operand1.evaluate() && operand2.evaluate();
    }

    @Override
    public String toString() {
        return "AND(" + operand1 + "," + operand2 + ")";
    }
}

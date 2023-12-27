/*
 * (c) 2015 Abil'I.T. http://abilit.eu/
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
package org.scada_lts.dao;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.exception.SerializationDataException;
import org.scada_lts.exception.StreamDaoException;

/** 
 * Serialization data from database
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 */
public class SerializationData {
	
	private static final Log LOG = LogFactory.getLog(SerializationData.class);
	 
	public Object readObject(final InputStream is) {
        
		if (is != null) {
			synchronized (is) {
				try {
					return new ObjectInputStream(is).readObject();
				} catch (ClassNotFoundException | IOException e) {
					LOG.error(e);
					throw new StreamDaoException(e);
				}
			}
		}
		return null;
	}
	
	public ByteArrayInputStream writeObject(final Object o) {
		if (o == null) {
			return null;
		}
		
		byte[] toWriteAsByte;
		try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            new ObjectOutputStream(baos).writeObject(o);
            toWriteAsByte = baos.toByteArray();
        }
        catch (IOException e) {
            throw new SerializationDataException(e);
        }
		
		return new ByteArrayInputStream(toWriteAsByte);
	}
	
	 
}

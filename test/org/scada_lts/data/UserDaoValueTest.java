/*
 * (c) 2016 Abil'I.T. http://abilit.eu/
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
package org.scada_lts.data;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import java.sql.SQLException;
import java.util.Random;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.dao.EmptyResultDataAccessException;
import com.serotonin.mango.vo.User;
import org.scada_lts.dao.UserDAO;

/**
 * @version 0.1
 * @author 	Radosław Jajko
 *
 */

public class UserDaoValueTest extends TestDAO {
	
	private static UserDAO userDao = null;
	
	private static TestDAO testDao = new TestDAO();
	private static int bound = 5;
	
	
	@BeforeClass
	public static void setUp() throws ClassNotFoundException, SQLException{
		
		//Initialize all database
		testDao.initialize();
		System.out.println("-- Database initialized succesfull!");
		
		userDao = new UserDAO();
		for (int i = 1; i <= bound; i++){
			userDao.insert(genUser());
		}
		
	}
	
	@Test
	public void getExistingUser(){
		
		for (int i = 1; i<=bound; i++){
			assertNotNull(userDao.getUser(i));
		}
	}
	
	@Test
	public void getNotExistingUser(){
		
		try{
			assertNull(userDao.getUser(100));
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			Assert.fail("Catched Exception "+ e + " instead expected NULL value");
		}
	}
	
	@AfterClass
	public static void tearDown() throws ClassNotFoundException, SQLException{
		testDao.relax();
	}
	
	/**
	 * generateRandomUser
	 * 
	 * @return user (User) - random generated User 
	 */
	
	private static User genUser(){
		
		User user = new User();
		
		Random randomGenerator = new Random();
		Integer genUsername = randomGenerator.nextInt(100)+1;
		
		user.setUsername("User_"+genUsername.toString());
		user.setPassword(genUsername.toString());
		user.setEmail(genUsername.toString()+"@test.com");
		user.setPhone(genUsername.toString());
		user.setAdmin(false);
		user.setDisabled(false);
		user.setHomeUrl("www."+genUsername.toString()+".com");
		user.setReceiveAlarmEmails(0);
		user.setReceiveOwnAuditEvents(false);
		
		return user;
		
	}

}

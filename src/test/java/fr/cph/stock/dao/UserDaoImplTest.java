/**
 * Copyright 2013 Carl-Philipp Harmant
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.cph.stock.dao;

import fr.cph.stock.entities.User;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class UserDaoImplTest {

	@Test
	public void testCRUDUser(){
		UserDAO dao = UserDAO.INSTANCE;

		String login = "carlzacdscdcsssscs";
		String password = "password";
		String email = "carl@gmail.com";
		User user = new User(login,password);
		user.setEmail(email);
		dao.insert(user);

		user = dao.selectWithLogin(login);
		assertEquals(login, user.getLogin());
		assertEquals(password, user.getPassword());
		assertEquals(email, user.getEmail());

		user.setEmail("caca@gmail.com");
		dao.update(user);

		user = dao.selectWithLogin(login);
		assertEquals(login, user.getLogin());
		assertEquals(password, user.getPassword());
		assertEquals("caca@gmail.com", user.getEmail());

		dao.delete(user);
		user = dao.selectWithLogin(login);
		assertNull(user);
	}

}

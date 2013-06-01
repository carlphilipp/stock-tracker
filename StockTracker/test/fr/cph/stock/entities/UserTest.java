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

package fr.cph.stock.entities;

import junit.framework.Assert;

import org.junit.Test;

public class UserTest {

	@Test
	public void testUser() {
		String login = "carl";
		String password = "mypassword";
		String email = "mail@gmail.com";
		User user = new User(login, password);
		Assert.assertEquals(login, user.getLogin());
		user.setEmail(email);
		Assert.assertEquals(email, user.getEmail());
	}

}

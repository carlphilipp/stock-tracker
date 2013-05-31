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

package fr.cph.stock.security;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import junit.framework.Assert;

import org.junit.Test;

public class SecurityTest {

	@Test
	public void testEncodeToSha256() throws UnsupportedEncodingException, NoSuchAlgorithmException {
		String password = "myPassword";
		String hashedPasswordExpected = "76549b827ec46e705fd03831813fa52172338f0dfcbd711ed44b81a96dac51c6";
		String actualHashedPassword = Security.encodeToSha256(password);
		Assert.assertEquals(hashedPasswordExpected, actualHashedPassword);
	}

}

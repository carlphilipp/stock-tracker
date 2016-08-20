/**
 * Copyright 2016 Carl-Philipp Harmant
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.cph.stock.security;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertEquals;

public class SecurityServiceTest {

	@Test
	public void testEncodeToSha256() throws UnsupportedEncodingException, NoSuchAlgorithmException {
		String password = "myPassword";
		String hashedPasswordExpected = "76549b827ec46e705fd03831813fa52172338f0dfcbd711ed44b81a96dac51c6";
		String actualHashedPassword = SecurityService.INSTANCE.encodeToSha256(password);
		assertEquals(hashedPasswordExpected, actualHashedPassword);
	}
}

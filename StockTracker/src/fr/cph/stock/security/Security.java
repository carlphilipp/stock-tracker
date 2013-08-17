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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.apache.commons.codec.binary.Hex;

/**
 * Class that takes care of encoding password and generate salt
 * 
 * @author Carl-Philipp Harmant
 * 
 */
public final class Security {

	/**
	 * Constructor
	 */
	private Security() {
	}

	/**
	 * Encode to sha256 the user password
	 * 
	 * @param str
	 *            the password to encode
	 * @return an encoded string
	 * @throws NoSuchAlgorithmException
	 *             the NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 *             the UnsupportedEncodingException
	 */
	public static String encodeToSha256(final String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hash = digest.digest(str.getBytes("UTF-8"));
		String encoded = Hex.encodeHexString(hash);
		return encoded;
	}

	/**
	 * Generate a salt, a random key, en encrypt it
	 * 
	 * @return a key encrypted
	 * @throws NoSuchAlgorithmException
	 *             the NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 *             the UnsupportedEncodingException
	 */
	public static String generateSalt() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		SecureRandom random = new SecureRandom();
		return Security.encodeToSha256(random.toString());
	}
}

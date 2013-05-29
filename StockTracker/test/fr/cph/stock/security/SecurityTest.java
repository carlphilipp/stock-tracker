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

package fr.cph.stock.security;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public interface SecurityService {

	String encodeToSha256(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException;

	String generateSalt() throws NoSuchAlgorithmException, UnsupportedEncodingException;
}

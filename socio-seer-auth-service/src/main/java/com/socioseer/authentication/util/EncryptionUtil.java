package com.socioseer.authentication.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

/**
 * <b>Encryption Utility</b>
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Component
public class EncryptionUtil {

	private MessageDigest messageDigest;
/**
 * 
 * @throws NoSuchAlgorithmException
 */
	@PostConstruct
	public void init() throws NoSuchAlgorithmException {
		messageDigest = MessageDigest.getInstance("SHA");
	}

	/**
	 * <b>Encode Password</b>
	 * @param plainText
	 * @return returns String
	 */
	public String encode(String plainText) {
		messageDigest.update(plainText.getBytes());
		String encodePassword = byteArrayToHexString(messageDigest.digest());
		messageDigest.reset();
		return encodePassword;
	}
/**
 * <b>Compare Password</b>
 * @param encodedPassword
 * @param plainPassword
 * @return returns boolean
 */
	public boolean matchPassword(String encodedPassword, String plainPassword) {
		return encodedPassword.equals(encode(plainPassword));
	}

	/**
	 * <b>Convert Byte Array to Hex String</b>
	 * @param b
	 * @return returns String
	 */
	private String byteArrayToHexString(byte[] b) {
		StringBuffer sb = new StringBuffer(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			int v = b[i] & 0xff;
			if (v < 16) {
				sb.append('0');
			}
			sb.append(Integer.toHexString(v));
		}
		return sb.toString().toUpperCase();
	}

}

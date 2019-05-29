package net.etfbl.cryptocoin.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.bouncycastle.util.encoders.Hex;

import net.etfbl.cryptocoin.blockchain.Blockchain;

public class Util {

	public static byte[] computeHash(String input) {
		try {
			return computeHash(input.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] computeHash(byte[] input) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("SHA-256");
			byte[] hash = messageDigest.digest(input);

			return hash;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] concatenateByteArrays(byte[] ... arrays) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		for (byte[] array: arrays) {
			try {
				outputStream.write(array);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return outputStream.toByteArray();
	}

	public static byte[] getByteArray(long input) {
		return ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(input).array();
	}

	public static boolean checkFirstZeroBytes(byte[] bytes) {
		int counter = 0;
		String hexBytes = Hex.toHexString(bytes);

		for (int i = 0; i < hexBytes.length(); i++) {
			if (hexBytes.charAt(i) == '0')
				counter++;
			else {
				if (counter == Blockchain.DIFFICULTY) {
					if ((i + 1) == hexBytes.length())
						return true;
					return !(hexBytes.charAt(i + 1) == '0');
				}
				else
					return false;
			}
		}

		return false;
	}
}

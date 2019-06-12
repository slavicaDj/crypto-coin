package net.etfbl.cryptocoin.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Crypto {

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

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

	public static KeyPair computeKeyPair() {
		try {
			KeyPairGenerator generator = KeyPairGenerator.getInstance("ECDSA","BC");
			SecureRandom random = new SecureRandom();
			ECGenParameterSpec parameterSpec = new ECGenParameterSpec("prime192v1");

			generator.initialize(parameterSpec, random);
			KeyPair keyPair = generator.generateKeyPair();

			return keyPair;
		}
		catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static byte[] signData(PrivateKey privateKey, byte[] data) {
		Signature dsa;
		try {
			dsa = Signature.getInstance("ECDSA","BC");
			dsa.initSign(privateKey);
			dsa.update(data);

			return dsa.sign();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean verifySignature(PublicKey publicKey, byte[] data, byte[] signature) {
		try {
			Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
			ecdsaVerify.initVerify(publicKey);
			ecdsaVerify.update(data);
			
			return ecdsaVerify.verify(signature);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}


}

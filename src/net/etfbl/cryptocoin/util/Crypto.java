package net.etfbl.cryptocoin.util;

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyFactory;
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
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMEncryptor;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaMiscPEMGenerator;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.bouncycastle.openssl.jcajce.JcePEMEncryptorBuilder;

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
			ECGenParameterSpec parameterSpec = new ECGenParameterSpec("secp256k1");

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

	public static void saveKeyInPemFile(Key key, char[] passphrase, String filePath) {
		try {
			JcaPEMWriter jcaPEMWriter = new JcaPEMWriter(new PrintWriter(filePath));

			if (passphrase != null) {
				PEMEncryptor pemEncryptor = new JcePEMEncryptorBuilder("AES-128-CBC").build(passphrase);
				JcaMiscPEMGenerator pemGenerator = new JcaMiscPEMGenerator(key, pemEncryptor);
				jcaPEMWriter.writeObject(pemGenerator);
			}
			else
				jcaPEMWriter.writeObject(key);
			
			jcaPEMWriter.flush();
			jcaPEMWriter.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static KeyPair readKeysFromPemFile(char[] passphrase, String filePath) {
		KeyPair keyPair = null;
		try {
			PEMParser pemParser = new PEMParser(new FileReader(new File(filePath)));
			Object object = pemParser.readObject();
			PEMDecryptorProvider decProv = new JcePEMDecryptorProviderBuilder().build(passphrase);
			JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
	
			if (object instanceof PEMEncryptedKeyPair)
				keyPair = converter.getKeyPair(((PEMEncryptedKeyPair) object).decryptKeyPair(decProv));
			else
				keyPair = converter.getKeyPair((PEMKeyPair) object);
	
			pemParser.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return keyPair;
	}

	public static PublicKey getPublicKeyFromBytes(byte[] pubKey) {
		PublicKey publicKey = null;

		try {
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pubKey);
			KeyFactory kf = KeyFactory.getInstance("EC");
			publicKey = kf.generatePublic(keySpec);
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return publicKey;
	}

}

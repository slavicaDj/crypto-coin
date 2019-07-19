package net.etfbl.cryptocoin.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
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

import net.etfbl.cryptocoin.blockchain.Blockchain;

public class Util {

	public static final String filePath = "D:\\workspace\\CryptoCoin\\resources\\";

	public static byte[] concatenateByteArrays(byte[] ... arrays) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		for (byte[] array: arrays) {
			try {
				if (array != null)
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

	public static void saveKeyInPemFile(Key key, String passphrase, String filePath) {
		try {
			JcaPEMWriter jcaPEMWriter = new JcaPEMWriter(new PrintWriter(filePath));

			if (passphrase != null) {
				PEMEncryptor pemEncryptor = new JcePEMEncryptorBuilder("AES-128-CBC").build(passphrase.toCharArray());
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
//		JcaPEMWriter pemWriter = null;
//
//		try {
//			pemWriter = new JcaPEMWriter(new OutputStreamWriter(new FileOutputStream(filePath)));
//			PemObject pemObject = new PemObject(description, key.getEncoded());
//
//			pemWriter.writeObject(pemObject);
//			pemWriter.close();
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//		}
	}

	public static KeyPair readKeysFromPemFile(String passphrase, String filePath) {
		KeyPair keyPair = null;
		try {
			PEMParser pemParser = new PEMParser(new FileReader(new File(filePath)));
			Object object = pemParser.readObject();
			PEMDecryptorProvider decProv = new JcePEMDecryptorProviderBuilder().build(passphrase.toCharArray());
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
//		try {
//			PemReader pemReader = new PemReader(new FileReader(filePath));
//			PemObject pemObject = pemReader.readPemObject();
//			byte[] pemObjectContent = pemObject.getContent();
//
//			pemReader.close();
//
//			PKCS8EncodedKeySpec encodedKeySpec = new PKCS8EncodedKeySpec(pemObjectContent);
//			KeyFactory keyFactory = KeyFactory.getInstance("ECDSA", "BC");
//			PrivateKey privateKey = keyFactory.generatePrivate(encodedKeySpec);
//			
//			return privateKey;
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
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

	public static <T> byte[] getBytes(T t) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(t);
			oos.close();
			baos.close();

			return baos.toByteArray();
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getObject(byte[] bytes) {
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bais);

			T t = null;
			t = (T) ois.readObject();
			ois.close();

			return t;
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}

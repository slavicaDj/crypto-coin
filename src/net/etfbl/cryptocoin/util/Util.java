package net.etfbl.cryptocoin.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;

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

	public static void saveKeyInPemFile(Key key, String description, String filePath) {
		PemWriter pemWriter = null;

		try {
			pemWriter = new PemWriter(new OutputStreamWriter(new FileOutputStream(filePath)));
			PemObject pemObject = new PemObject(description, key.getEncoded());

			pemWriter.writeObject(pemObject);
			pemWriter.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static PrivateKey readKeyFromPemFile(String filePath) {
		try {
			PemReader pemReader = new PemReader(new FileReader(filePath));
			PemObject pemObject = pemReader.readPemObject();
			byte[] pemObjectContent = pemObject.getContent();

			pemReader.close();

			PKCS8EncodedKeySpec encodedKeySpec = new PKCS8EncodedKeySpec(pemObjectContent);
			KeyFactory keyFactory = KeyFactory.getInstance("ECDSA", "BC");
			PrivateKey privateKey = keyFactory.generatePrivate(encodedKeySpec);
			
			return privateKey;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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

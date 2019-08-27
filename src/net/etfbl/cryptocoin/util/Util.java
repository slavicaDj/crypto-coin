package net.etfbl.cryptocoin.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import org.bouncycastle.util.encoders.Hex;

public class Util {

	public static byte[] concatenateByteArrays(byte[] ... arrays) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		try {
			for (byte[] array: arrays) {
				if (array != null)
					outputStream.write(array);
			}

			outputStream.close();
		}
		catch (IOException e) {
			e.printStackTrace();
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
				if (counter == Consts.DIFFICULTY) {
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
			if (bytes == null)
				return null;

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

	public static String loadConfig(String key) {
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(new File(Consts.CONFIG_PATH)));
			String line = null;

			while ((line = reader.readLine()) != null) {
				if (line.split(Consts.CONFIG_SEPARATOR)[0].equals(key)) {
					reader.close();
					return line.split(Consts.CONFIG_SEPARATOR)[1];
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static String convertDateToString(Date date) {
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");  
		return dateFormat.format(date);
	}

	public static boolean isDecimal(String input) {
		Scanner scanner = new Scanner(input);
		if (scanner.hasNextDouble()) {
			scanner.close();
			return true;
		}

		scanner.close();
		return false;
	}
}

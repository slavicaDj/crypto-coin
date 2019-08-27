package net.etfbl.cryptocoin.blockchain;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import net.etfbl.cryptocoin.util.Consts;
import net.etfbl.cryptocoin.util.Crypto;

public class Wallet {

	private PublicKey publicKey;

	public Wallet() {
	}

	public void register(char[] passphrase, String filePath) {
		KeyPair keyPair = Crypto.computeKeyPair();
		publicKey = keyPair.getPublic();

		Crypto.saveKeyInPemFile(keyPair.getPrivate(), passphrase, filePath);
	}

	public boolean login(char[] passphrase) {
		KeyPair keyPair = Crypto.readKeysFromPemFile(passphrase, Consts.MY_WALLET_PATH + Consts.WALLET_FILE_NAME);

		if (keyPair == null)
			return false;

		publicKey = keyPair.getPublic();
		return true;
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public PrivateKey getPrivateKey(char[] passphrase) {
		return Crypto.readKeysFromPemFile(passphrase, Consts.MY_WALLET_PATH + Consts.WALLET_FILE_NAME).getPrivate();
	}

	
}
package net.etfbl.cryptocoin.blockchain;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.bouncycastle.util.encoders.Hex;

import net.etfbl.cryptocoin.util.Crypto;
import net.etfbl.cryptocoin.util.Util;

public class Wallet {

	private PublicKey publicKey;

	public Wallet() {
	}

	public void register() {
		KeyPair keyPair = Crypto.computeKeyPair();
		publicKey = keyPair.getPublic();

		//later on, use password to encrypt the pem file
		Util.saveKeyInPemFile(keyPair.getPrivate(), "prik", "" + Util.filePath + Hex.toHexString(publicKey.getEncoded()));
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public PrivateKey getPrivateKey() {
		return Util.readKeyFromPemFile(Util.filePath + Hex.toHexString(publicKey.getEncoded()));
	}
}

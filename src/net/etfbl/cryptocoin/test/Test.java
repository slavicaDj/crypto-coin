package net.etfbl.cryptocoin.test;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;

import javax.xml.bind.DatatypeConverter;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import net.etfbl.cryptocoin.blockchain.Wallet;
import net.etfbl.cryptocoin.util.Util;

public class Test {
	
	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException{
		
		/* block mining */
//		Block genesisBlock = new Block(new byte[256], new Date(), "Hi, I'm the genesis block!");
//		genesisBlock.mine();
//		Block secondBlock = new Block(genesisBlock.getHash(), new Date(), "Hi, I'm the second block!");
//		secondBlock.mine();
//		Block thirdBlock = new Block(secondBlock.getHash(), new Date(), "Hi, I'm the third block!");
//		thirdBlock.mine();
//
//		System.out.println(Hex.toHexString(genesisBlock.getHash()));
//		System.out.println(Hex.toHexString(secondBlock.getHash()));
//		System.out.println(Hex.toHexString(thirdBlock.getHash()));

		/* generating key pair, encrypting and verifying data */
//		try {
//			String input = "Hello crypto world!";
//
//			KeyPair keyPair = Crypto.computeKeyPair();
//
//			byte[] sign = Crypto.signData(keyPair.getPrivate(), input.getBytes());
//			System.out.println(Crypto.verifySignature(keyPair.getPublic(), input.getBytes(), sign));
//
//			
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//		}

		Wallet wallet = new Wallet();
		wallet.register();
		System.out.println(Hex.toHexString(wallet.getPublicKey().getEncoded()));
		PublicKey pk = Util.getPublicKeyFromBytes(DatatypeConverter.parseHexBinary(Hex.toHexString(wallet.getPublicKey().getEncoded())));
		System.out.println(Hex.toHexString(pk.getEncoded()));
	}

}

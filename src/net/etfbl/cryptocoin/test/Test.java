package net.etfbl.cryptocoin.test;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Test {
	
	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	public static void main(String[] args){
		
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
	}

}

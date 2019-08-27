package net.etfbl.cryptocoin.test;

import java.math.BigDecimal;
import net.etfbl.cryptocoin.util.*;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import javax.xml.bind.DatatypeConverter;

import org.bouncycastle.util.encoders.Hex;

import net.etfbl.cryptocoin.blockchain.Wallet;
import net.etfbl.cryptocoin.leveldb.LevelDBHandler;
import net.etfbl.cryptocoin.util.Util;

public class Test {

	public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException{
//		Wallet walletCopy = new Wallet();
//
//		System.out.println(walletCopy.login("test".toCharArray()));
//		System.out.println(walletCopy.getPublicKey().getEncoded());
//		System.out.println(walletCopy.getPrivateKey("test".toCharArray()).getEncoded());

		System.out.println(LevelDBHandler.getBlock(DatatypeConverter.parseHexBinary("000d65af9395f9e2f529ebb258d50f57d24ddb78f340c9743bbeb0c93d539af7")));
//		System.out.println(Hex.toHexString(wallet.getPublicKey().getEncoded()));
//		PublicKey pk = Util.getPublicKeyFromBytes(DatatypeConverter.parseHexBinary(Hex.toHexString(wallet.getPublicKey().getEncoded())));
//		System.out.println(Hex.toHexString(pk.getEncoded()));
		
//		BigDecimal bd1 = new BigDecimal(100);
//		BigDecimal bd2 = new BigDecimal(5);
//		BigDecimal bd3 = new BigDecimal(0.1);
//		BigDecimal bd4 = bd1.subtract(bd2).subtract(bd3);
//		System.out.println("bd4: " + bd4.doubleValue());
//
//		BigDecimal bd5 = new BigDecimal(0);
//		for (int i = 0; i < 5; i++)
//			bd5 = bd5.add(new BigDecimal(1));
//		System.out.println(bd5.intValue());
//
//		BigDecimal outputSum = new BigDecimal(5);
//		BigDecimal inputSum = new BigDecimal(10);
//		System.out.println(outputSum.compareTo(inputSum));
//
//		System.out.println(new BigDecimal(0.1));
	}

}

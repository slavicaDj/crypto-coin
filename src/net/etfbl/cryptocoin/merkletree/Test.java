package net.etfbl.cryptocoin.merkletree;

import java.util.Date;

import org.bouncycastle.util.encoders.Hex;

import net.etfbl.cryptocoin.blockchain.Block;

public class Test {

	public static void main(String[] args) {
		
		Block genesisBlock = new Block(new byte[256], new Date(), "Hi, I'm the genesis block!");
		genesisBlock.mine();
		Block secondBlock = new Block(genesisBlock.getHash(), new Date(), "Hi, I'm the second block!");
		secondBlock.mine();
		Block thirdBlock = new Block(secondBlock.getHash(), new Date(), "Hi, I'm the third block!");
		thirdBlock.mine();
		
		for (int i = 0; i < genesisBlock.getHash().length; i++) {
			String s1 = String.format("%8s", Integer.toBinaryString(genesisBlock.getHash()[i] & 0xFF)).replace(' ', '0');
			System.out.print(s1);
		}
		System.out.println();
		for (int i = 0; i < secondBlock.getHash().length; i++) {
			String s2 = String.format("%8s", Integer.toBinaryString(secondBlock.getHash()[i] & 0xFF)).replace(' ', '0');
			System.out.print(s2);
		}
		System.out.println();
		for (int i = 0; i < thirdBlock.getHash().length; i++) {
			String s3 = String.format("%8s", Integer.toBinaryString(thirdBlock.getHash()[i] & 0xFF)).replace(' ', '0');
			System.out.print(s3);
		}
		System.out.println();
		System.out.println(Hex.toHexString(genesisBlock.getHash()));
		System.out.println(Hex.toHexString(secondBlock.getHash()));
		System.out.println(Hex.toHexString(thirdBlock.getHash()));
	}
}

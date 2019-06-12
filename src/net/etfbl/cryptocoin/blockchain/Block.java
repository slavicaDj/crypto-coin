package net.etfbl.cryptocoin.blockchain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import org.bouncycastle.util.encoders.Hex;

import net.etfbl.cryptocoin.merkletree.MerkleNode;
import net.etfbl.cryptocoin.merkletree.MerkleTree;
import net.etfbl.cryptocoin.util.Crypto;
import net.etfbl.cryptocoin.util.Util;

public class Block {

	private byte[] hash;
	private byte[] previousBlockHash;
	private Date timestamp;
	private byte[] merkleTreeRootHash;
	private int nonce;

	private Transaction coinbaseTransaction;
	private ArrayList<Transaction> transactions; /* Transactions won't be included in the block hash, only the merkle root hash */

	public Block() {
		transactions = new ArrayList<>();
	}

	public Block(byte[] previousBlockHash, Date timestamp, ArrayList<Transaction> transactions) {
		super();
		this.previousBlockHash = Arrays.copyOf(previousBlockHash, previousBlockHash.length);
		this.timestamp = timestamp;
		this.transactions = transactions;
		computeHash();
	}
	
	private void computeHash() {

		byte[] blockBytes = Util.concatenateByteArrays(previousBlockHash,
				  Util.getByteArray(timestamp.getTime()),
				  merkleTreeRootHash,
				  Util.getByteArray(nonce));

		hash = Crypto.computeHash(blockBytes);
	}

	private void computeMerkleTreeRootHash() {
		if (transactions == null || transactions.size() < 1)
			return;
			
		ArrayList<MerkleNode> leaves = new ArrayList<>();

		for (int i = 0; i < transactions.size(); i++)
			leaves.add(new MerkleNode(transactions.get(i).getHash()));
			
		MerkleTree tree = new MerkleTree(leaves);
		merkleTreeRootHash = tree.build().getHash();
	}

	public void mine() {
		if (transactions == null || transactions.size() < 1) {
			System.out.println("Cannot mine an empty block!"); //Make custom exception
			return;
		}

		Random random = new Random(new Date().getTime());
		computeMerkleTreeRootHash();

		while(!Util.checkFirstZeroBytes(hash)) {
			nonce = random.nextInt(Integer.MAX_VALUE);
			computeHash();
		}
	}

	public byte[] getHash() {
		return hash;
	}

	public byte[] getPreviousBlockHash() {
		return previousBlockHash;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public byte[] getMerkleRootHash() {
		return merkleTreeRootHash;
	}

	@Override
	public String toString() {
		return "hash=" + Hex.toHexString(hash) + ",\npreviousBlockHash=" + Hex.toHexString(previousBlockHash)
				+ ",\ntimestamp=" + timestamp + ",\nmerkleTreeRootHash=" + Hex.toHexString(merkleTreeRootHash)
				+ ",\nnonce=" + nonce;
	}

	
	
}

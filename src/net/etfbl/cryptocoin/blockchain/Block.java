package net.etfbl.cryptocoin.blockchain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.bouncycastle.util.encoders.Hex;

import net.etfbl.cryptocoin.merkletree.MerkleNode;
import net.etfbl.cryptocoin.merkletree.MerkleTree;
import net.etfbl.cryptocoin.util.Crypto;
import net.etfbl.cryptocoin.util.Util;

public class Block implements Serializable, Comparable<Block> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3265660258469067295L;
	private byte[] hash;
	private byte[] previousBlockHash;
	private Date timestamp;
	private byte[] merkleTreeRootHash;
	private int nonce;

	private ArrayList<Transaction> transactions; /* Transactions won't be included in the block hash, only the Merkle root hash */

	public Block(byte[] previousBlockHash, Date timestamp, ArrayList<Transaction> transactions) {
		super();
		this.previousBlockHash = Arrays.copyOf(previousBlockHash, previousBlockHash.length);
		this.timestamp = timestamp;
		this.transactions = transactions;

		computeHash();
	}
	
	public void computeHash() {

		byte[] blockBytes = Util.concatenateByteArrays(previousBlockHash,
				  Util.getByteArray(timestamp.getTime()),
				  merkleTreeRootHash,
				  Util.getByteArray(nonce));

		hash = Crypto.computeHash(blockBytes);
	}

	public void computeMerkleTreeRootHash() {
		if (transactions == null || transactions.size() < 1)
			return;
			
		ArrayList<MerkleNode> leaves = new ArrayList<>();

		for (int i = 0; i < transactions.size(); i++)
			leaves.add(new MerkleNode(transactions.get(i).getHash()));

		MerkleTree tree = new MerkleTree(leaves);
		merkleTreeRootHash = tree.build().getHash();
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

	public byte[] getMerkleTreeRootHash() {
		return merkleTreeRootHash;
	}

	public int getNonce() {
		return nonce;
	}

	public ArrayList<Transaction> getTransactions() {
		return transactions;
	}

	public void setNonce(int nonce) {
		this.nonce = nonce;
	}

	@Override
	public String toString() {
		return "hash=" + Hex.toHexString(hash) + ",\npreviousBlockHash=" + Hex.toHexString(previousBlockHash)
				+ ",\ntimestamp=" + timestamp + ",\nmerkleTreeRootHash=" + Hex.toHexString(merkleTreeRootHash)
				+ ",\nnonce=" + nonce;
	}

	@Override
	public int compareTo(Block b) {
		return this.getTimestamp().compareTo(b.getTimestamp());
	}

}

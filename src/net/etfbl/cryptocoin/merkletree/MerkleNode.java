package net.etfbl.cryptocoin.merkletree;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bouncycastle.util.encoders.Hex;

import net.etfbl.cryptocoin.util.Crypto;
import net.etfbl.cryptocoin.util.Util;

public class MerkleNode {
	
	private byte[] hash;
	private MerkleNode leftChild;
	private MerkleNode rightChild;
	
	public MerkleNode() {
		
	}

	public MerkleNode(MerkleNode leftChild, MerkleNode rightChild) {
		this.leftChild = leftChild;
		this.rightChild = rightChild;
		
		this.computeHash();
	}

	public MerkleNode(byte[] hash) {
		this.hash = Arrays.copyOf(hash, hash.length);
	}

	public MerkleNode(String input) {
		this.hash = Crypto.computeHash(input);
	}

	public boolean isLeaf() {
		if (leftChild == null && rightChild == null)
			return true;
		return false;
	}

	public void computeHash() {
		if (rightChild == null)
			hash = leftChild.hash;
		else {
			byte[] concat = Util.concatenateByteArrays(leftChild.hash, rightChild.hash);
			hash = Crypto.computeHash(concat);
		}
	}

	public byte[] getHash() {
		return hash;
	}

	public MerkleNode getLeftChild() {
		return leftChild;
	}

	public MerkleNode getRightChild() {
		return rightChild;
	}

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
		MerkleNode leaf1 = new MerkleNode(Crypto.computeHash("a"));
		MerkleNode leaf2 = new MerkleNode(Crypto.computeHash("b"));
		MerkleNode leaf3 = new MerkleNode(Crypto.computeHash("c"));
		MerkleNode leaf4 = new MerkleNode(Crypto.computeHash("d"));
		MerkleNode leaf5 = new MerkleNode(Crypto.computeHash("e"));

		List<MerkleNode> leaves = new ArrayList<>();
		leaves.add(leaf1);
		leaves.add(leaf2);
		leaves.add(leaf3);
		leaves.add(leaf4);
		leaves.add(leaf5);

		MerkleTree tree = new MerkleTree(leaves);
		tree.build();

		System.out.println(Hex.toHexString(tree.getRoot().getHash()));
	}
}

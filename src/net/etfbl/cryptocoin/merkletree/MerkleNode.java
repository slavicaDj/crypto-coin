package net.etfbl.cryptocoin.merkletree;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.util.encoders.Hex;

public class MerkleNode {
	
	private byte[] hash;
	private MerkleNode leftChild;
	private MerkleNode rightChild;
	
	public MerkleNode() {
		
	}

	public MerkleNode(MerkleNode leftChild, MerkleNode rightChild) {
		this.leftChild = leftChild;
		this.rightChild = rightChild;
		
		this.computeSignature();
	}

	public MerkleNode(byte[] hash) {
		this.hash = hash;
	}

	public MerkleNode(String input) {
		this.hash = computeHash(input);
	}

	public boolean isLeaf() {
		if (leftChild == null && rightChild == null)
			return true;
		return false;
	}

	public void computeSignature() {
		if (rightChild == null)
			hash = leftChild.hash;
		else
			hash = computeHash(concatenateByteArrays(leftChild.hash, rightChild.hash));
	}

	private static byte[] computeHash(String input) {
		return computeHash(input.getBytes(StandardCharsets.UTF_8));
	}

	public static byte[] computeHash(byte[] input) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("SHA-1");
			byte[] hash = messageDigest.digest(input);

			return hash;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	private byte[] concatenateByteArrays(byte[] firstArray, byte[] secondArray) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			outputStream.write(firstArray);
			outputStream.write(secondArray);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return outputStream.toByteArray( );
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
		MerkleNode leaf1 = new MerkleNode(computeHash("11111111111111111111"));
		MerkleNode leaf2 = new MerkleNode(computeHash("2"));
		MerkleNode leaf3 = new MerkleNode(computeHash("3"));
		MerkleNode leaf4 = new MerkleNode(computeHash("4"));
		MerkleNode leaf5 = new MerkleNode(computeHash("5"));

		List<MerkleNode> leaves = new ArrayList<>();
		leaves.add(leaf1);
		leaves.add(leaf2);
		leaves.add(leaf3);
		leaves.add(leaf4);
		leaves.add(leaf5);

		MerkleTree tree = new MerkleTree(leaves);
		System.out.println("root: " + Hex.toHexString(tree.build().hash));
	}
}

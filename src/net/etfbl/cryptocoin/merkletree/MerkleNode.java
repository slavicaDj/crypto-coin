package net.etfbl.cryptocoin.merkletree;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.util.encoders.Hex;

public class MerkleNode {
	
	private String hash;
	private MerkleNode leftChild;
	private MerkleNode rightChild;
	
	public MerkleNode() {
		
	}

	public MerkleNode(MerkleNode leftChild, MerkleNode rightChild) {
		this.leftChild = leftChild;
		this.rightChild = rightChild;
		
		this.computeSignature();
	}

	public MerkleNode(String hash) {
		this.hash = hash;
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
			hash = computeHash(leftChild.hash + rightChild.hash);
	}

	public static String computeHash(String input) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("SHA-1");
			byte[] hash = messageDigest.digest(input.getBytes(StandardCharsets.UTF_8));

			String hexHash = new String(Hex.encode(hash));
			System.out.println(hexHash + " " + "[" + input + "]");
			return hexHash;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public MerkleNode getLeftChild() {
		return leftChild;
	}

	public void setLeftChild(MerkleNode leftChild) {
		this.leftChild = leftChild;
	}

	public MerkleNode getRightChild() {
		return rightChild;
	}

	public void setRightChild(MerkleNode rightChild) {
		this.rightChild = rightChild;
	}

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
		MerkleNode leaf1 = new MerkleNode(computeHash("111111"));
		MerkleNode leaf2 = new MerkleNode(computeHash("2"));
		MerkleNode leaf3 = new MerkleNode(computeHash("3"));
		MerkleNode leaf4 = new MerkleNode(computeHash("4"));
		MerkleNode leaf5 = new MerkleNode(computeHash("5"));
//		MerkleNode leaf6 = new MerkleNode(computeHash("6"));
//		MerkleNode leaf7 = new MerkleNode(computeHash("7"));

		List<MerkleNode> leaves = new ArrayList<>();
		leaves.add(leaf1);
		leaves.add(leaf2);
		leaves.add(leaf3);
		leaves.add(leaf4);
		leaves.add(leaf5);
//		leaves.add(leaf6);
//		leaves.add(leaf7);
//		leaves.add(leaf8);

		MerkleTree tree = new MerkleTree(leaves);
		System.out.println(tree.build().hash);
		
		byte[] leaf1Bytes = new MerkleNode("3d4f2bf07dc1be38b20cd6e46949a1071f9d0e3d").hash.getBytes(StandardCharsets.UTF_8);
		byte[] leaf2Bytes = new MerkleNode("da4b9237bacccdf19c0760cab7aec4a8359010b0").hash.getBytes(StandardCharsets.UTF_8);
		
		byte[] leaf12Bytes = new byte[leaf1Bytes.length + leaf2Bytes.length];
	    System.arraycopy(leaf1Bytes, 0, leaf12Bytes, 0, leaf1Bytes.length);
	    System.arraycopy(leaf2Bytes, 0, leaf12Bytes, leaf1Bytes.length, leaf2Bytes.length);

		MessageDigest messageDigest = null;
		messageDigest = MessageDigest.getInstance("SHA-1");
		byte[] hash = messageDigest.digest(leaf12Bytes);

		String hexHash = new String(Hex.encode(hash));
		System.out.println("*******");
		System.out.println(hexHash);
	}
}

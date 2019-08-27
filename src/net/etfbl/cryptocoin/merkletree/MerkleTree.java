package net.etfbl.cryptocoin.merkletree;

import java.util.ArrayList;
import java.util.List;

public class MerkleTree {

	private MerkleNode root;
	private List<MerkleNode> nodes = new ArrayList<>();
	private List<MerkleNode> leaves = new ArrayList<>();

	public MerkleTree() {
		
	}

	public MerkleTree(List<MerkleNode> leaves) {
		this.leaves = leaves;
	}

	public MerkleNode build() {
		build(leaves);
		return root;
	}

	private void build(List<MerkleNode> localNodes) {
		if (localNodes.size() == 1)
			this.root = localNodes.get(0);
		else {
			List<MerkleNode> parentNodes = new ArrayList<>();
			for (int i = 0; i < localNodes.size(); i += 2) {
				MerkleNode rightNode = (i + 1 < localNodes.size()) ? localNodes.get(i + 1) : null;
				MerkleNode parentNode = new MerkleNode(localNodes.get(i), rightNode);
				parentNodes.add(parentNode);
				nodes.add(parentNode);
			}
			build(parentNodes);
		}
	}

	public MerkleNode getRoot() {
		return root;
	}

	public List<MerkleNode> getNodes() {
		return nodes;
	}

	public List<MerkleNode> getLeaves() {
		return leaves;
	}

}

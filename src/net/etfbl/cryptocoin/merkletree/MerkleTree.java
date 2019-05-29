package net.etfbl.cryptocoin.merkletree;

import java.util.ArrayList;
import java.util.List;

public class MerkleTree {

	private MerkleNode root;
	private List<MerkleNode> nodes;
	private List<MerkleNode> leaves;

	public MerkleTree() {
		
	}

	public MerkleTree(List<MerkleNode> leaves) {
		this.leaves = leaves;
	}

	public MerkleNode build() {
    	build(leaves);
    	return root;
    }

    public void build(List<MerkleNode> nodes) {
    	if (nodes.size() == 1)
    		this.root = nodes.get(0);
        else {
        	List<MerkleNode> parents = new ArrayList<>();
        	for (int i = 0; i < nodes.size(); i += 2) {
        		MerkleNode right = (i + 1 < nodes.size()) ? nodes.get(i + 1) : null;
        		MerkleNode parent = new MerkleNode(nodes.get(i), right);
        		parents.add(parent);
        	}
        	build(parents);
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

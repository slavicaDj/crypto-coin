package net.etfbl.cryptocoin.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.bouncycastle.util.encoders.Hex;

import net.etfbl.cryptocoin.blockchain.Block;

public class CustomTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5759868364866619844L;
	private static final String[] columnNames = {"Hash", "Merkle tree root hash", "Nonce", "Previous block hash", "Timestamp"};
	private List<Block> blocks;

	public CustomTableModel() {
		blocks = new ArrayList<>();
	}

	public void addElement(Block block) {
		blocks.add(block);
		fireTableRowsInserted(blocks.size()-1, blocks.size()-1);
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		return blocks.size();
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch(columnIndex) {
			case 0:
				return Hex.toHexString(blocks.get(rowIndex).getHash());
			case 1:
				return Hex.toHexString(blocks.get(rowIndex).getMerkleTreeRootHash());
			case 2:
				return blocks.get(rowIndex).getNonce();
			case 3:
				return Hex.toHexString(blocks.get(rowIndex).getPreviousBlockHash());
			case 4:
				return blocks.get(rowIndex).getTimestamp().toString();
		}

		return null;
	}
}

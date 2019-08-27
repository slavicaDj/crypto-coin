package net.etfbl.cryptocoin.gui;

import java.math.BigDecimal;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.bouncycastle.util.encoders.Hex;

import net.etfbl.cryptocoin.blockchain.Transaction;
import net.etfbl.cryptocoin.blockchain.Transaction.Input;
import net.etfbl.cryptocoin.blockchain.Transaction.Output;
import net.etfbl.cryptocoin.blockchain.UnspentTx;
import net.etfbl.cryptocoin.leveldb.LevelDBHandler;

public class TableModelTxs extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5759868364866619844L;
	private static final String[] columnNames = {"Hash", "Pošiljalac", "Primalac", "Poslata sredstva", "Fee", "Visina bloka"};
	private List<Transaction> transactions;

	public TableModelTxs() {
		transactions = new ArrayList<>();
	}

	public void addElement(Transaction t) {
		transactions.add(t);
		fireTableRowsInserted(transactions.size()-1, transactions.size()-1);
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		return transactions.size();
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		PublicKey pkRecepient = null;
		PublicKey pkSender = null;

		if (transactions.get(rowIndex).getOutputs().size() > 0) {
			Output out = transactions.get(rowIndex).getOutputs().get(0);
			pkRecepient = out.getPkRecipient();
		}

		if (transactions.get(rowIndex).getInputs().size() > 0) {
			Input in = transactions.get(rowIndex).getInputs().get(0);
			UnspentTx unspentTx = new UnspentTx(in.getPreviousTxHash(), in.getOutputIndex(), in.getPreviousBlockHeight());
			Output out = LevelDBHandler.getOutputFromHistory(unspentTx);
			if (out != null)
				pkSender = out.getPkRecipient();
		}

		switch(columnIndex) {
			case 0:
				return Hex.toHexString(transactions.get(rowIndex).getHash());
			case 1:
				return pkSender == null? null : Hex.toHexString(pkSender.getEncoded());
			case 2:
				return pkRecepient == null? null : Hex.toHexString(pkRecepient.getEncoded());
			case 3:
				return transactions.get(rowIndex).getAmount(pkRecepient);
			case 4:
				BigDecimal fee = transactions.get(rowIndex).getFee();
				return fee == null? 0 : fee;
			case 5:
				return transactions.get(rowIndex).getBlockHeight();
		}

		return null;
	}
}

package net.etfbl.cryptocoin.blockchain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class UnspentTxPool implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4083980934184548108L;
	private HashMap<UnspentTx, Transaction.Output> unspentTxs;

	public UnspentTxPool() {
		this.unspentTxs = new HashMap<>();
	}

	public UnspentTxPool(HashMap<UnspentTx, Transaction.Output> unspentTxs) {
		this.unspentTxs = new HashMap<>(unspentTxs);
	}

	public Transaction.Output getUnspentTxOutput(UnspentTx ut) {
		return unspentTxs.get(ut);
	}

	public void putUnspentTxOutput(UnspentTx ut, Transaction.Output out) {
		unspentTxs.put(ut, out);
	}

	public void removeUnspentTxOutput(UnspentTx ut) {
		unspentTxs.remove(ut);
	}

	public boolean contains(UnspentTx ut) {
		return unspentTxs.containsKey(ut);
	}

	public ArrayList<UnspentTx> getAllUnspentTxs() {
		Set<UnspentTx> setUTXO = unspentTxs.keySet();
		ArrayList<UnspentTx> allUTXO = new ArrayList<UnspentTx>();

		for (UnspentTx ut : setUTXO)
			allUTXO.add(ut);

		return allUTXO;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		for (UnspentTx utx : unspentTxs.keySet()) {
			sb.append(utx).append("\n");
			sb.append(unspentTxs.get(utx)).append("\n");
			sb.append("----\n");
		}
		
		return sb.toString();
	}

}

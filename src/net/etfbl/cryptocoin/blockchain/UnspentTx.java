package net.etfbl.cryptocoin.blockchain;

import java.util.Arrays;

import org.bouncycastle.util.encoders.Hex;

public class UnspentTx {

	private byte[] txHash;
	private int index;

	public UnspentTx(byte[] txHash, int index) {
		this.txHash = Arrays.copyOf(txHash, txHash.length);
		this.index = index;
	}
	
	public byte[] getTxHash() {
		return txHash;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
		result = prime * result + Arrays.hashCode(txHash);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UnspentTx other = (UnspentTx) obj;
		if (index != other.index)
			return false;
		if (!Arrays.equals(txHash, other.txHash))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "[txHash=" + Hex.toHexString(txHash) + ", index=" + index + "]";
	}

	
}

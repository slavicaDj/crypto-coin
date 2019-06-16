package net.etfbl.cryptocoin.blockchain;

import java.util.Arrays;
import java.util.Date;

import org.bouncycastle.util.encoders.Hex;

public class UnspentTx {

	private byte[] txHash;
	private int index;
	private int blockHeight;

	public UnspentTx(byte[] txHash, int index, int blockHeight) {
		this.txHash = Arrays.copyOf(txHash, txHash.length);
		this.index = index;
		this.blockHeight = blockHeight;
	}
	
	public byte[] getTxHash() {
		return txHash;
	}

	public int getIndex() {
		return index;
	}

	public int getBlockHeight() {
		return blockHeight;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + blockHeight;
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
		if (blockHeight != other.blockHeight)
			return false;
		if (index != other.index)
			return false;
		if (!Arrays.equals(txHash, other.txHash))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UnspentTx [txHash=" + Hex.toHexString(txHash) + ", index=" + index + ", blockHeight=" + blockHeight
				+ "]";
	}

	
}

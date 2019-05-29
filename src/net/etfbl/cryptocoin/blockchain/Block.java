package net.etfbl.cryptocoin.blockchain;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Random;

import net.etfbl.cryptocoin.util.Util;

public class Block {

	private byte[] hash;
	private byte[] previousBlockHash;
	private Date timestamp;
	private String data; // This will be changed to Transactions in the later implementation
	private int nonce;

	public Block(byte[] previousBlockHash, Date timestamp, String data) {
		super();
		this.previousBlockHash = previousBlockHash;
		this.timestamp = timestamp;
		this.data = data;
		
		computeHash();
	}
	
	private void computeHash() {
		byte[] blockBytes = Util.concatenateByteArrays(previousBlockHash,
				  Util.getByteArray(timestamp.getTime()),
				  data.getBytes(StandardCharsets.UTF_8),
				  Util.getByteArray(nonce));
		hash = Util.computeHash(blockBytes);
	}

	public void mine() {
		Random random = new Random(new Date().getTime());

		while(!Util.checkFirstZeroBytes(hash)) {
			nonce = random.nextInt(Integer.MAX_VALUE);
			computeHash();
		}
	}

	public byte[] getHash() {
		return hash;
	}

	public byte[] getPreviousBlockHash() {
		return previousBlockHash;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public String getData() {
		return data;
	}

	
}

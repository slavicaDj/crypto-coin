package net.etfbl.cryptocoin.leveldb;

import static org.fusesource.leveldbjni.JniDBFactory.factory;

import java.io.File;
import java.io.IOException;
import java.security.PublicKey;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.DBIterator;

import net.etfbl.cryptocoin.blockchain.Block;
import net.etfbl.cryptocoin.blockchain.Transaction.Output;
import net.etfbl.cryptocoin.blockchain.UnspentTx;
import net.etfbl.cryptocoin.util.Util;

public class LevelDBHandler {

	private static final String DB_BLOCKS = "blocks";
	private static final String DB_UNSPENT_TX_POOL = "unspentTxPool";

	public static void put(Block block) {
		Options options = new Options();
		options.createIfMissing(true);
		try {
			DB db = factory.open(new File(DB_BLOCKS), options);
			db.put(block.getHash(), Util.getBytes(block));

			db.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void put(UnspentTx unspentTx, Output output) {
		Options options = new Options();
		options.createIfMissing(true);

		try {
			DB db = factory.open(new File(DB_UNSPENT_TX_POOL), options);
			db.put(Util.getBytes(unspentTx), Util.getBytes(output));

			db.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void deleteOutput(UnspentTx unspentTx) {
		Options options = new Options();
		options.createIfMissing(true);

		try {
			DB db = factory.open(new File(DB_UNSPENT_TX_POOL), options);
			db.delete(Util.getBytes(unspentTx));

			db.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Block getBlock(byte[] hash) {
		Block block = null;
		Options options = new Options();
		options.createIfMissing(true);

		try {
			DB db = factory.open(new File(DB_BLOCKS), options);
			byte[] blockBytes = db.get(hash);
			block = Util.getObject(blockBytes);

			db.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return block;
	}

	public static Output getOutput(UnspentTx unspentTx) {
		Output output = null;
		Options options = new Options();
		options.createIfMissing(true);

		try {
			DB db = factory.open(new File(DB_UNSPENT_TX_POOL), options);
			byte[] outputBytes = db.get(Util.getBytes(unspentTx));
			output = Util.getObject(outputBytes);

			db.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return output;
	}

	public static double getBalance(PublicKey pk) {
		double balance = 0;
		Options options = new Options();
		options.createIfMissing(true);

		try {
			DB db = factory.open(new File(DB_UNSPENT_TX_POOL), options);
			DBIterator iterator = db.iterator();

			for(iterator.seekToFirst(); iterator.hasNext(); iterator.next()) {
				byte[] value = iterator.peekNext().getValue();
				Output output = Util.getObject(value);

				if (output != null && output.getPkRecipient().equals(pk))
					balance += output.getValue();
			}

			iterator.close();
			db.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return balance;
	}

	public static int getMaxHeight() {
		int maxHeight = 0;
		Options options = new Options();
		options.createIfMissing(true);

		try {
			DB db = factory.open(new File(DB_BLOCKS), options);
			DBIterator iterator = db.iterator();

			for(iterator.seekToFirst(); iterator.hasNext(); iterator.next())
				maxHeight++;

			iterator.close();
			db.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return maxHeight;
	}
}

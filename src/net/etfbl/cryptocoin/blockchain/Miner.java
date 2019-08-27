package net.etfbl.cryptocoin.blockchain;

import java.math.BigDecimal;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import javax.xml.bind.DatatypeConverter;

import net.etfbl.cryptocoin.blockchain.Transaction.Input;
import net.etfbl.cryptocoin.blockchain.Transaction.Output;
import net.etfbl.cryptocoin.exception.BlockException;
import net.etfbl.cryptocoin.exception.TransactionException;
import net.etfbl.cryptocoin.leveldb.LevelDBHandler;
import net.etfbl.cryptocoin.util.Consts;
import net.etfbl.cryptocoin.util.Crypto;
import net.etfbl.cryptocoin.util.Util;

public class Miner {

	private static PublicKey publicKey;
	private static final String CONFIG_KEY = "miner_pk";
	
	static {
		String configValue = Util.loadConfig(CONFIG_KEY);
		publicKey = Crypto.getPublicKeyFromBytes(DatatypeConverter.parseHexBinary(configValue));
	}


	public static void initBlockchain(PublicKey pk) {
		Transaction genesisTx = new Transaction(new BigDecimal(0), 0);
		Transaction.Output output = new Transaction.Output(new BigDecimal(100), pk);
		genesisTx.getOutputs().add(output);
		genesisTx.computeHash();

		ArrayList<Transaction> genTxs = new ArrayList<>();
		genTxs.add(genesisTx);

		LevelDBHandler.put(new UnspentTx(genesisTx.getHash(), 0, genesisTx.getBlockHeight()), output);

		Block genesisBlock = new Block(new byte[32], new Date(), genTxs);
		try {
			mine(genesisBlock);
		}
		catch (Exception e) {
			System.out.println("Error! Unable to mine empty block.");
			return;
		}
		LevelDBHandler.put(genesisBlock);
	}

	public static void mineBlock(ArrayList<Transaction> txs) throws TransactionException {
		BigDecimal fees = new BigDecimal(0);
		for (Transaction t : txs)
			fees = fees.add(t.getFee());
		Transaction coinbaseTx = new Transaction(fees.add(Consts.COINBASE_VALUE), publicKey, LevelDBHandler.getMaxHeight());
		txs.add(0, coinbaseTx);

		Block block = new Block(LevelDBHandler.getLastBlock().getHash(), new Date(), handleTxs(txs));
		try {
			mine(block);
		}
		catch (Exception e) {
			System.out.println("Error! Unable to mine empty block.");
			return;
		}
		LevelDBHandler.put(block);
	}

	private static ArrayList<Transaction> handleTxs(ArrayList<Transaction> txs) throws TransactionException {
		ArrayList<Transaction> filteredTxs = new ArrayList<>();

		for (Transaction transaction : txs) {
			if (transaction.isCoinbase() || transaction.isValid())
				filteredTxs.add(transaction);
		}

		ArrayList<UnspentTx> usedTxs = new ArrayList<>();

		/* Check if some of transactions reference the same unspent transaction */
		for (Transaction transaction : filteredTxs) {
			for (Transaction.Input input : transaction.getInputs()) {
				UnspentTx utxo = new UnspentTx(input.getPreviousTxHash(), input.getOutputIndex(), input.getPreviousBlockHeight());

				if (usedTxs.contains(utxo))
					filteredTxs.remove(transaction);
				else
					usedTxs.add(utxo);
			}
		}

		/* Send back change to the sender */
		for (Transaction transaction : filteredTxs) {
			if (!transaction.isCoinbase()) {
				BigDecimal diff = calculateDiff(transaction);
				Input input = transaction.getInputs().get(0);
				UnspentTx utx = new UnspentTx(input.getPreviousTxHash(), input.getOutputIndex(), input.getPreviousBlockHeight());
				Output out = LevelDBHandler.getOutput(utx);
				transaction.getOutputs().add(new Output(diff, out.getPkRecipient()));
			}
		}

		/* Cleanup - remove now spent unspent transactions */
		for (Transaction transaction : filteredTxs) {
			for (Transaction.Input input : transaction.getInputs()) {
				UnspentTx utxo = new UnspentTx(input.getPreviousTxHash(), input.getOutputIndex(), input.getPreviousBlockHeight());
				
				if (LevelDBHandler.getOutput(utxo) != null)
					LevelDBHandler.deleteOutput(utxo);
			}
		}

		/* Cleanup - put newly generated unspent transactions */
		for (Transaction transaction : filteredTxs) {
			ArrayList<Transaction.Output> outputs = transaction.getOutputs();
			for (int j = 0; j < outputs.size(); j++) {
				Transaction.Output output = outputs.get(j);
				UnspentTx utxo = new UnspentTx(transaction.getHash(), j, LevelDBHandler.getMaxHeight());
				LevelDBHandler.put(utxo, output);
			}
		}

		return filteredTxs;

	}

	private static BigDecimal calculateDiff(Transaction t) {
		BigDecimal inputSum = new BigDecimal(0);
		BigDecimal outputSum = new BigDecimal(0);
		BigDecimal diff = new BigDecimal(0);

		for (Input in : t.getInputs()) {
			UnspentTx unspentTx = new UnspentTx(in.getPreviousTxHash(), in.getOutputIndex(), in.getPreviousBlockHeight());
			Output out = LevelDBHandler.getOutput(unspentTx);
			inputSum = inputSum.add(out.getAmount());
		}

		for (Output out: t.getOutputs())
			outputSum = outputSum.add(out.getAmount());

		diff = inputSum.subtract(outputSum).subtract(t.getFee());
		return diff;
	}

	private static int mine(Block block) throws Exception {
		int nonce = 0;

		if (block == null || block.getTransactions() == null || block.getTransactions().size() < 1) {
			throw new BlockException("Cannot mine empty block!");
		}

		Random random = new Random(new Date().getTime());
		block.computeMerkleTreeRootHash();

		while(!Util.checkFirstZeroBytes(block.getHash())) {
			block.setNonce(random.nextInt(Integer.MAX_VALUE));
			block.computeHash();
		}

		return nonce;
	}

}

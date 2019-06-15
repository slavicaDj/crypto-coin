package net.etfbl.cryptocoin.blockchain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.bouncycastle.util.encoders.Hex;

import net.etfbl.cryptocoin.blockchain.Transaction.Input;
import net.etfbl.cryptocoin.blockchain.Transaction.Output;

public class Blockchain {

	private static ArrayList<Block> blockchain = new ArrayList<>();
	private static UnspentTxPool unspentTxPool = new UnspentTxPool();
	private static Wallet walletA = new Wallet();
	private static Wallet walletB = new Wallet();
	
	public static int DIFFICULTY = 3;

	public static ArrayList<Block> getBlockchain() {
		return blockchain;
	}

	public static UnspentTxPool getUnspentTxPool() {
		return unspentTxPool;
	}

	public static void main(String[] args) {
		walletA.register();
		walletB.register();

		Transaction genesisTx = new Transaction(Transaction.DEFAULT_FEE);
		Transaction.Output output = new Transaction.Output(100, walletA.getPublicKey());
		genesisTx.getOutputs().add(output);
		genesisTx.computeHash();

		ArrayList<Transaction> genTxs = new ArrayList<>();
		genTxs.add(genesisTx);

		unspentTxPool.putUnspentTxOutput(new UnspentTx(genesisTx.getHash(), 0), output);

		Block genesisBlock = new Block(new byte[32], new Date(), genTxs, walletA.getPublicKey());
		genesisBlock.mine();
		blockchain.add(genesisBlock);

		System.out.println("Genesis block:");
		System.out.println(genesisBlock + "\n");
		
		System.out.println("Wallet A private: " + Hex.toHexString(walletA.getPrivateKey().getEncoded()));
		System.out.println("Wallet A public: " + Hex.toHexString(walletA.getPublicKey().getEncoded()));

		Transaction transaction1 = new Transaction(Transaction.DEFAULT_FEE);
		Transaction.Input input1 = new Transaction.Input(genesisTx.getHash(), 0);
		Transaction.Output output1 = new Transaction.Output(10, walletB.getPublicKey());
		Transaction.Output output2 = new Transaction.Output(20, walletB.getPublicKey());
		transaction1.getInputs().add(input1);
		transaction1.getOutputs().add(output1);
		transaction1.getOutputs().add(output2);
		transaction1.computeSignature(0, walletA.getPrivateKey());
		transaction1.computeHash();

		Transaction coinbaseTx = new Transaction(Transaction.COINBASE_VALUE, walletA.getPublicKey());

		ArrayList<Transaction> txs1 = new ArrayList<>();
		txs1.add(coinbaseTx);
		txs1.add(transaction1);

		Block block1 = new Block(genesisBlock.getHash(), new Date(), handleTxs(txs1), walletA.getPublicKey());
		block1.mine();
		blockchain.add(block1);

		System.out.println("\nBlock 1: \n" + block1 + "\n");
		System.out.println("Wallet B private: " + Hex.toHexString(walletB.getPrivateKey().getEncoded()));
		System.out.println("Wallet B public: " + Hex.toHexString(walletB.getPublicKey().getEncoded()) + "\n");

		System.out.println("Wallet A balance: " + unspentTxPool.getBalance(walletA.getPublicKey()));
		System.out.println("Wallet B balance: " + unspentTxPool.getBalance(walletB.getPublicKey()));
	}

	private static ArrayList<Transaction> handleTxs(ArrayList<Transaction> txs) {
		ArrayList<Transaction> filteredTxs = new ArrayList<>();

		for (Transaction transaction : txs) {
			if (transaction.isCoinbase() || transaction.isValid(unspentTxPool)) {
				filteredTxs.add(transaction);
			}
		}

		ArrayList<UnspentTx> usedTxs = new ArrayList<>();

		for (Transaction transaction : filteredTxs) {
			for (Transaction.Input input : transaction.getInputs()) {
				UnspentTx utxo = new UnspentTx(input.getPreviousTxHash(), input.getOutputIndex());

				if (usedTxs.contains(utxo))
					filteredTxs.remove(transaction);
				else
					usedTxs.add(utxo);
			}
		}

		for (Transaction transaction : filteredTxs) {
			if (!transaction.isCoinbase()) {
				double diff = calculateDiff(transaction);
				/* to do */
				transaction.getOutputs().add(new Output(diff, walletA.getPublicKey()));
			}
		}

		//cleanup
		for (Transaction transaction : filteredTxs) {
			for (Transaction.Input input : transaction.getInputs()) {
				UnspentTx utxo = new UnspentTx(input.getPreviousTxHash(), input.getOutputIndex());
				
				if (unspentTxPool.contains(utxo))
					unspentTxPool.removeUnspentTxOutput(utxo);
			}
		}

		int i = 0;
		for (Transaction transaction : filteredTxs) {
			ArrayList<Transaction.Output> outputs = transaction.getOutputs();
			for (int j = 0; j < outputs.size(); j++) {
				Transaction.Output output = outputs.get(j);
				UnspentTx utxo = new UnspentTx(transaction.getHash(), j);
				unspentTxPool.putUnspentTxOutput(utxo, output);
			}
		}

		return filteredTxs;

	}

	private static double calculateDiff(Transaction t) {
		double inputSum = 0;

		for (Input in : t.getInputs()) {
			UnspentTx unspentTx = new UnspentTx(in.getPreviousTxHash(), in.getOutputIndex());
			Output out = unspentTxPool.getUnspentTxOutput(unspentTx);
			inputSum += out.getValue();
		}

		double outputSum = 0;

		for (Output out: t.getOutputs())
			outputSum += out.getValue();

		return inputSum - outputSum - t.getFee();
	}
}

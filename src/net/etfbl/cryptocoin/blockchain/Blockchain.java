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
	private static Wallet walletC = new Wallet();
	private static Wallet walletD = new Wallet();
	
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
		walletC.register();
		walletD.register();

		System.out.println("WalletA: " + Hex.toHexString(walletA.getPublicKey().getEncoded()));
		System.out.println("WalletB: " + Hex.toHexString(walletB.getPublicKey().getEncoded()));
		System.out.println("WalletC: " + Hex.toHexString(walletC.getPublicKey().getEncoded()));
		System.out.println("WalletD: " + Hex.toHexString(walletD.getPublicKey().getEncoded()));


		Transaction genesisTx = new Transaction(Transaction.DEFAULT_FEE, 0);
		Transaction.Output output = new Transaction.Output(100, walletA.getPublicKey());
		genesisTx.getOutputs().add(output);
		genesisTx.computeHash();
//		System.out.println("genesisTx: " + genesisTx);

		ArrayList<Transaction> genTxs = new ArrayList<>();
		genTxs.add(genesisTx);

		unspentTxPool.putUnspentTxOutput(new UnspentTx(genesisTx.getHash(), 0, blockchain.size()), output);

		Block genesisBlock = new Block(new byte[32], new Date(), genTxs);
		genesisBlock.mine();
		blockchain.add(genesisBlock);

		System.out.println("Genesis block:");
		System.out.println(genesisBlock + "\n");

		System.out.println("Wallet A balance: " + unspentTxPool.getBalance(walletA.getPublicKey()));
		System.out.println("Wallet B balance: " + unspentTxPool.getBalance(walletB.getPublicKey()));
		System.out.println("Wallet C balance: " + unspentTxPool.getBalance(walletC.getPublicKey()));
		System.out.println("Wallet D balance: " + unspentTxPool.getBalance(walletD.getPublicKey()) + "\n");

		Transaction transaction1 = new Transaction(Transaction.DEFAULT_FEE, blockchain.size());
		Transaction.Input input1 = new Transaction.Input(genesisTx.getHash(), 0, 0);
		Transaction.Output output1 = new Transaction.Output(10, walletB.getPublicKey());
		Transaction.Output output2 = new Transaction.Output(20, walletB.getPublicKey());
		transaction1.getInputs().add(input1);
		transaction1.getOutputs().add(output1);
		transaction1.getOutputs().add(output2);

		transaction1.computeSignature(0, walletA.getPrivateKey());
		transaction1.computeHash();

//		System.out.println("transaction1: " + transaction1);
		Transaction coinbaseTx = new Transaction(Transaction.COINBASE_VALUE, walletA.getPublicKey(), blockchain.size());
//		System.out.println("coinbaseTx: " + coinbaseTx);

		ArrayList<Transaction> txs1 = new ArrayList<>();
		txs1.add(coinbaseTx);
		txs1.add(transaction1);

		Block block1 = new Block(genesisBlock.getHash(), new Date(), handleTxs(txs1));
		block1.mine();
		blockchain.add(block1);

		System.out.println("\nBlock 1: \n" + block1 + "\n");

		System.out.println("Wallet A balance: " + unspentTxPool.getBalance(walletA.getPublicKey()));
		System.out.println("Wallet B balance: " + unspentTxPool.getBalance(walletB.getPublicKey()));
		System.out.println("Wallet C balance: " + unspentTxPool.getBalance(walletC.getPublicKey()));
		System.out.println("Wallet D balance: " + unspentTxPool.getBalance(walletD.getPublicKey()));

		System.out.println("\n***Pool***\n");
		System.out.println(unspentTxPool);
		System.out.println("\n***Pool***\n");

		Transaction transaction2 = new Transaction(Transaction.DEFAULT_FEE, blockchain.size());
		transaction2.getInputs().add(new Input(transaction1.getHash(), 0, transaction1.getBlockHeight()));
		transaction2.getOutputs().add(new Output(5, walletC.getPublicKey()));
		transaction2.getInputs().add(new Input(transaction1.getHash(), 1, transaction1.getBlockHeight()));
		transaction2.getOutputs().add(new Output(5, walletD.getPublicKey()));

		transaction2.computeSignature(0, walletB.getPrivateKey());
		transaction2.computeSignature(1, walletB.getPrivateKey());
		transaction2.computeHash();

		Transaction coinbaseTx2 = new Transaction(Transaction.COINBASE_VALUE, walletA.getPublicKey(), blockchain.size());

		ArrayList<Transaction> txs2 = new ArrayList<>();
		txs2.add(coinbaseTx2);
		txs2.add(transaction2);

//		System.out.println("transaction2: " + transaction2);
//		System.out.println("coinbaseTx2: " + coinbaseTx2);

		Block block2 = new Block(block1.getHash(), new Date(), handleTxs(txs2));
		block2.mine();
		blockchain.add(block2);

		System.out.println("\nBlock 2: \n" + block2 + "\n");

		System.out.println("Wallet A balance: " + unspentTxPool.getBalance(walletA.getPublicKey()));
		System.out.println("Wallet B balance: " + unspentTxPool.getBalance(walletB.getPublicKey()));
		System.out.println("Wallet C balance: " + unspentTxPool.getBalance(walletC.getPublicKey()));
		System.out.println("Wallet D balance: " + unspentTxPool.getBalance(walletD.getPublicKey()));

		System.out.println("\n***Pool***\n");
		System.out.println(unspentTxPool);
		System.out.println("\n***Pool***\n");	
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
				UnspentTx utxo = new UnspentTx(input.getPreviousTxHash(), input.getOutputIndex(), input.getPreviousBlockHeight());

				if (usedTxs.contains(utxo))
					filteredTxs.remove(transaction);
				else
					usedTxs.add(utxo);
			}
		}

		for (Transaction transaction : filteredTxs) {
			if (!transaction.isCoinbase()) {
				double diff = calculateDiff(transaction);
				Input input = transaction.getInputs().get(0);
				UnspentTx utx = new UnspentTx(input.getPreviousTxHash(), input.getOutputIndex(), input.getPreviousBlockHeight());
				Output out = unspentTxPool.getUnspentTxOutput(utx);
				transaction.getOutputs().add(new Output(diff, out.getPkRecipient()));
			}
		}

		//cleanup
		for (Transaction transaction : filteredTxs) {
			for (Transaction.Input input : transaction.getInputs()) {
				UnspentTx utxo = new UnspentTx(input.getPreviousTxHash(), input.getOutputIndex(), input.getPreviousBlockHeight());
				
				if (unspentTxPool.contains(utxo))
					unspentTxPool.removeUnspentTxOutput(utxo);
			}
		}

		for (Transaction transaction : filteredTxs) {
			ArrayList<Transaction.Output> outputs = transaction.getOutputs();
			for (int j = 0; j < outputs.size(); j++) {
				Transaction.Output output = outputs.get(j);
				UnspentTx utxo = new UnspentTx(transaction.getHash(), j, blockchain.size());
				unspentTxPool.putUnspentTxOutput(utxo, output);
			}
		}

		return filteredTxs;

	}

	private static double calculateDiff(Transaction t) {
		double inputSum = 0;

		for (Input in : t.getInputs()) {
			UnspentTx unspentTx = new UnspentTx(in.getPreviousTxHash(), in.getOutputIndex(), in.getPreviousBlockHeight());
			Output out = unspentTxPool.getUnspentTxOutput(unspentTx);
			inputSum += out.getValue();
		}

		double outputSum = 0;

		for (Output out: t.getOutputs())
			outputSum += out.getValue();

		return inputSum - outputSum - t.getFee();
	}
}

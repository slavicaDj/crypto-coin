package net.etfbl.cryptocoin.blockchain;

import java.math.BigDecimal;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Date;

import org.bouncycastle.util.encoders.Hex;

import net.etfbl.cryptocoin.blockchain.Transaction.Input;
import net.etfbl.cryptocoin.blockchain.Transaction.Output;
import net.etfbl.cryptocoin.leveldb.LevelDBHandler;

public class Blockchain {

//	private static Wallet walletA = new Wallet();
//	private static Wallet walletB = new Wallet();
//	private static Wallet walletC = new Wallet();
//	private static Wallet walletD = new Wallet();
	
	public static int DIFFICULTY = 3;

	public static void init(PublicKey pk) {
		Transaction genesisTx = new Transaction(Transaction.DEFAULT_FEE, 0);
		Transaction.Output output = new Transaction.Output(100, pk);
		genesisTx.getOutputs().add(output);
		genesisTx.computeHash();

		ArrayList<Transaction> genTxs = new ArrayList<>();
		genTxs.add(genesisTx);

		LevelDBHandler.put(new UnspentTx(genesisTx.getHash(), 0, genesisTx.getBlockHeight()), output);

		Block genesisBlock = new Block(new byte[32], new Date(), genTxs);
		genesisBlock.mine();
		LevelDBHandler.put(genesisBlock);
	}

//	public static void main(String[] args) {
//		walletA.register();
//		walletB.register();
//		walletC.register();
//		walletD.register();
//
//		System.out.println("WalletA: " + Hex.toHexString(walletA.getPublicKey().getEncoded()));
//		System.out.println("WalletB: " + Hex.toHexString(walletB.getPublicKey().getEncoded()));
//		System.out.println("WalletC: " + Hex.toHexString(walletC.getPublicKey().getEncoded()));
//		System.out.println("WalletD: " + Hex.toHexString(walletD.getPublicKey().getEncoded()));
//
//		Transaction genesisTx = new Transaction(Transaction.DEFAULT_FEE, 0);
//		Transaction.Output output = new Transaction.Output(100, walletA.getPublicKey());
//		genesisTx.getOutputs().add(output);
//		genesisTx.computeHash();
//
//		ArrayList<Transaction> genTxs = new ArrayList<>();
//		genTxs.add(genesisTx);
//
//		LevelDBHandler.put(new UnspentTx(genesisTx.getHash(), 0, genesisTx.getBlockHeight()), output);
//
//		Block genesisBlock = new Block(new byte[32], new Date(), genTxs);
//		genesisBlock.mine();
//		LevelDBHandler.put(genesisBlock);
//
//		System.out.println("Genesis block:");
//		System.out.println(genesisBlock + "\n");
//
//		System.out.println("Wallet A balance: " + LevelDBHandler.getBalance(walletA.getPublicKey()));
//		System.out.println("Wallet B balance: " + LevelDBHandler.getBalance(walletB.getPublicKey()));
//		System.out.println("Wallet C balance: " + LevelDBHandler.getBalance(walletC.getPublicKey()));
//		System.out.println("Wallet D balance: " + LevelDBHandler.getBalance(walletD.getPublicKey()) + "\n");
//
//		Transaction transaction1 = new Transaction(Transaction.DEFAULT_FEE, LevelDBHandler.getMaxHeight());
//		Transaction.Input input1 = new Transaction.Input(genesisTx.getHash(), 0, 0);
//		Transaction.Output output1 = new Transaction.Output(10, walletB.getPublicKey());
//		Transaction.Output output2 = new Transaction.Output(20, walletB.getPublicKey());
//		transaction1.getInputs().add(input1);
//		transaction1.getOutputs().add(output1);
//		transaction1.getOutputs().add(output2);
//
//		transaction1.computeSignature(0, walletA.getPrivateKey());
//		transaction1.computeHash();
//
//		Transaction coinbaseTx = new Transaction(Transaction.COINBASE_VALUE, walletA.getPublicKey(), LevelDBHandler.getMaxHeight());
//
//		ArrayList<Transaction> txs1 = new ArrayList<>();
//		txs1.add(coinbaseTx);
//	 	txs1.add(transaction1);
//
//		Block block1 = new Block(genesisBlock.getHash(), new Date(), handleTxs(txs1));
//		block1.mine();
//		LevelDBHandler.put(block1);
//
//		System.out.println("\nBlock 1: \n" + block1 + "\n");
//
//		System.out.println("Wallet A balance: " + LevelDBHandler.getBalance(walletA.getPublicKey()));
//		System.out.println("Wallet B balance: " + LevelDBHandler.getBalance(walletB.getPublicKey()));
//		System.out.println("Wallet C balance: " + LevelDBHandler.getBalance(walletC.getPublicKey()));
//		System.out.println("Wallet D balance: " + LevelDBHandler.getBalance(walletD.getPublicKey()));
//
//		Transaction transaction2 = new Transaction(Transaction.DEFAULT_FEE, LevelDBHandler.getMaxHeight());
//		transaction2.getInputs().add(new Input(transaction1.getHash(), 0, transaction1.getBlockHeight()));
//		transaction2.getOutputs().add(new Output(5, walletC.getPublicKey()));
//		transaction2.getInputs().add(new Input(transaction1.getHash(), 1, transaction1.getBlockHeight()));
//		transaction2.getOutputs().add(new Output(5, walletD.getPublicKey()));
//
//		transaction2.computeSignature(0, walletB.getPrivateKey());
//		transaction2.computeSignature(1, walletB.getPrivateKey());
//		transaction2.computeHash();
//
//		Transaction coinbaseTx2 = new Transaction(Transaction.COINBASE_VALUE, walletA.getPublicKey(), LevelDBHandler.getMaxHeight());
//
//		ArrayList<Transaction> txs2 = new ArrayList<>();
//		txs2.add(coinbaseTx2);
//		txs2.add(transaction2);
//
//		Block block2 = new Block(block1.getHash(), new Date(), handleTxs(txs2));
//		block2.mine();
//		LevelDBHandler.put(block2);
//
//		System.out.println("\nBlock 2: \n" + block2 + "\n");
//		System.out.println("Seek last block:");
//
//		System.out.println("Wallet A balance: " + LevelDBHandler.getBalance(walletA.getPublicKey()));
//		System.out.println("Wallet B balance: " + LevelDBHandler.getBalance(walletB.getPublicKey()));
//		System.out.println("Wallet C balance: " + LevelDBHandler.getBalance(walletC.getPublicKey()));
//		System.out.println("Wallet D balance: " + LevelDBHandler.getBalance(walletD.getPublicKey()));
//	}

	public static void sendFunds(Wallet myWallet, PublicKey receiverPk, double amount, double fee) {
		if (LevelDBHandler.getBalance(myWallet.getPublicKey()) < (amount + fee))
			return;

		double sum = amount + fee;
		double sumBalance = 0;
		UnspentTxPool unspentTxPool = LevelDBHandler.getUnspentTxPool(myWallet.getPublicKey());
		ArrayList<Input> inputs = new ArrayList<>();

		for (UnspentTx unspentTx : unspentTxPool.getAllUnspentTxs()) {
			Output output = unspentTxPool.getUnspentTxOutput(unspentTx);
			Input input = new Input(unspentTx.getTxHash(), unspentTx.getIndex(), unspentTx.getBlockHeight());
			inputs.add(input);

			sumBalance += output.getValue();
			if (sumBalance >= sum)
				break;
		}
		Output output = new Output(amount, receiverPk);

		Transaction transaction = new Transaction(Transaction.DEFAULT_FEE, LevelDBHandler.getMaxHeight());
		transaction.getInputs().addAll(inputs);
		transaction.getOutputs().add(output);

		for (int i = 0; i < transaction.getInputs().size(); i++)
			transaction.computeSignature(i, myWallet.getPrivateKey());
		transaction.computeHash();

		Transaction coinbaseTx = new Transaction(Transaction.COINBASE_VALUE, myWallet.getPublicKey(), LevelDBHandler.getMaxHeight());

		ArrayList<Transaction> txs = new ArrayList<>();
		txs.add(coinbaseTx);
		txs.add(transaction);

		Block block = new Block(LevelDBHandler.getLastBlock().getHash(), new Date(), handleTxs(txs));
		block.mine();
		LevelDBHandler.put(block);
	}

	private static ArrayList<Transaction> handleTxs(ArrayList<Transaction> txs) {
		ArrayList<Transaction> filteredTxs = new ArrayList<>();

		for (Transaction transaction : txs) {
			if (transaction.isCoinbase() || transaction.isValid()) {
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
				Output out = LevelDBHandler.getOutput(utx);
				transaction.getOutputs().add(new Output(diff, out.getPkRecipient()));
			}
		}

		//cleanup
		for (Transaction transaction : filteredTxs) {
			for (Transaction.Input input : transaction.getInputs()) {
				UnspentTx utxo = new UnspentTx(input.getPreviousTxHash(), input.getOutputIndex(), input.getPreviousBlockHeight());
				
				if (LevelDBHandler.getOutput(utxo) != null)
					LevelDBHandler.deleteOutput(utxo);
			}
		}

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

	private static double calculateDiff(Transaction t) {
		double inputSum = 0;

		for (Input in : t.getInputs()) {
			UnspentTx unspentTx = new UnspentTx(in.getPreviousTxHash(), in.getOutputIndex(), in.getPreviousBlockHeight());
			Output out = LevelDBHandler.getOutput(unspentTx);
			inputSum += out.getValue();
		}

		double outputSum = 0;

		for (Output out: t.getOutputs())
			outputSum += out.getValue();

		BigDecimal diff = new BigDecimal(0);
		BigDecimal bdInputSum = new BigDecimal(inputSum);
		BigDecimal bdOutputSum = new BigDecimal(outputSum);
		BigDecimal bdFee = new BigDecimal(t.getFee());

		diff = bdInputSum.subtract(bdOutputSum).subtract(bdFee);
		System.out.println("diff: " + diff.doubleValue());
		return diff.doubleValue();
	}
}

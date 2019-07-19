package net.etfbl.cryptocoin.blockchain;

import java.math.BigDecimal;
import java.security.PublicKey;
import java.util.ArrayList;

import net.etfbl.cryptocoin.blockchain.Transaction.Input;
import net.etfbl.cryptocoin.blockchain.Transaction.Output;
import net.etfbl.cryptocoin.leveldb.LevelDBHandler;

public class Blockchain {
	
	public static int DIFFICULTY = 3;

	public static void init(PublicKey pk) {
		Miner.initBlockchain(pk);
	}

	public static void sendFunds(Wallet myWallet, PublicKey receiverPk, BigDecimal amount, BigDecimal fee) {
		BigDecimal sum = new BigDecimal(0);
		sum.add(amount).add(fee);
		if (LevelDBHandler.getBalance(myWallet.getPublicKey()).compareTo(sum) < 0)
			return;

		BigDecimal sumBalance = new BigDecimal(0);
		UnspentTxPool unspentTxPool = LevelDBHandler.getUnspentTxPool(myWallet.getPublicKey());
		ArrayList<Input> inputs = new ArrayList<>();

		for (UnspentTx unspentTx : unspentTxPool.getAllUnspentTxs()) {
			Output output = unspentTxPool.getUnspentTxOutput(unspentTx);
			Input input = new Input(unspentTx.getTxHash(), unspentTx.getIndex(), unspentTx.getBlockHeight());
			inputs.add(input);

			sumBalance = sumBalance.add(output.getAmount());
			if (sumBalance.compareTo(sum) >= 0)
				break;
		}
		Output output = new Output(amount, receiverPk);

		Transaction transaction = new Transaction(fee, LevelDBHandler.getMaxHeight());
		transaction.getInputs().addAll(inputs);
		transaction.getOutputs().add(output);

		for (int i = 0; i < transaction.getInputs().size(); i++)
			transaction.computeSignature(i, myWallet.getPrivateKey());
		transaction.computeHash();

		ArrayList<Transaction> txs = new ArrayList<>();
		txs.add(transaction);

		Miner.mineBlock(txs);
	}

}

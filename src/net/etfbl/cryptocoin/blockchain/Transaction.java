package net.etfbl.cryptocoin.blockchain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bouncycastle.util.encoders.Hex;

import net.etfbl.cryptocoin.exception.TransactionException;
import net.etfbl.cryptocoin.leveldb.LevelDBHandler;
import net.etfbl.cryptocoin.util.Consts;
import net.etfbl.cryptocoin.util.Crypto;
import net.etfbl.cryptocoin.util.Util;

public class Transaction implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5926299142781689209L;

	public static class Input implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 4577379126272158863L;
		private byte[] previousTxHash;
		private int outputIndex;
		private int previousBlockHeight;
		private byte[] signature;

		public Input(byte[] previousTxHash, int outputIndex, int previousBlockHeight) {
			this.previousTxHash = Arrays.copyOf(previousTxHash, previousTxHash.length);
			this.outputIndex = outputIndex;
			this.previousBlockHeight = previousBlockHeight;
		}

		public byte[] getPreviousTxHash() {
			return previousTxHash;
		}

		public int getOutputIndex() {
			return outputIndex;
		}

		public int getPreviousBlockHeight() {
			return previousBlockHeight;
		}

		public byte[] getSignature() {
			return signature;
		}

		@Override
		public String toString() {
			return "Input [previousTxHash=" + Hex.toHexString(previousTxHash) + ", outputIndex=" + outputIndex
					+ ", previousBlockHeight=" + previousBlockHeight + "]";
		}

	}

	public static class Output implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 6483601312507253277L;
		private BigDecimal amount;
		private PublicKey pkRecipient;

		public Output(BigDecimal amount, PublicKey pkRecipient) {
			this.amount = amount;
			this.pkRecipient = pkRecipient;
		}

		public BigDecimal getAmount() {
			return amount;
		}

		public PublicKey getPkRecipient() {
			return pkRecipient;
		}

		@Override
		public String toString() {
			return "Output [amount=" + amount + ",\npkRecipient=" + Hex.toHexString(pkRecipient.getEncoded()) + "]\n";
		}

	}

	private byte[] hash;
	private ArrayList<Input> inputs;
	private ArrayList<Output> outputs;
	private boolean coinbase;
	private BigDecimal fee;
	private int blockHeight;

	public Transaction(BigDecimal fee, int blockHeight) {
		this.fee = fee;
		this.blockHeight = blockHeight;
		inputs = new ArrayList<>();
		outputs = new ArrayList<>();

		computeHash();
	}

	public Transaction(BigDecimal amount, PublicKey publicKey, int blockHeight) {
		coinbase = true;
		this.blockHeight = blockHeight;
		inputs = new ArrayList<>();
		outputs = new ArrayList<>();

		Output output = new Output(amount, publicKey);
		outputs.add(output);

		computeHash();
	}

	public byte[] getHash() {
		return hash;
	}

	public ArrayList<Input> getInputs() {
		return inputs;
	}

	public ArrayList<Output> getOutputs() {
		return outputs;
	}

	public boolean isCoinbase() {
		return coinbase;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public int getBlockHeight() {
		return blockHeight;
	}

	public void computeHash() {
		hash = Crypto.computeHash(Util.getBytes(this));
	}

	public byte[] getInputBytes(int index) {
		ArrayList<Byte> bytes = new ArrayList<Byte>();

		if (index > inputs.size())
			return null;

		Input input = inputs.get(index);
		byte[] prevTxHash = input.previousTxHash;
		ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.SIZE / 8);
		byteBuffer.putInt(input.outputIndex);
		byte[] outputIndex = byteBuffer.array();

		if (prevTxHash != null)
			for (int i = 0; i < prevTxHash.length; i++)
				bytes.add(prevTxHash[i]);

		for (int i = 0; i < outputIndex.length; i++)
			bytes.add(outputIndex[i]);

		ByteBuffer byteBuf = ByteBuffer.allocate(Integer.SIZE / 8);
		byteBuf.putInt(input.previousBlockHeight);
		byte[] blockHeightBytes = byteBuf.array();
		for (int i = 0; i < blockHeightBytes.length; i++)
			bytes.add(blockHeightBytes[i]);

		for (Output output : outputs) {
			ByteBuffer byteBufferOutput = ByteBuffer.allocate(Double.SIZE / 8);
			byteBufferOutput.putDouble(output.amount.doubleValue());
			byte[] value = byteBufferOutput.array();
			byte[] address = output.pkRecipient.getEncoded();

			for (int i = 0; i < value.length; i++)
				bytes.add(value[i]);
			for (int i = 0; i < address.length; i++)
				bytes.add(address[i]);
		}

		byte[] bytesArray = new byte[bytes.size()];
		int i = 0;
		for (Byte b : bytes)
			bytesArray[i++] = b;

		return bytesArray;
	}

	public boolean isValid() throws TransactionException {
		Set<UnspentTx> usedUnspentTx = new HashSet<>();
		BigDecimal inputSum = new BigDecimal(0);

		for (int i = 0; i < inputs.size(); i++) {
			Input input = inputs.get(i);
			byte[] prevTxHash = input.getPreviousTxHash();
			int outIndex = input.getOutputIndex();
			int prevBlockHeight = input.getPreviousBlockHeight();

			UnspentTx unspentTx = new UnspentTx(prevTxHash, outIndex, prevBlockHeight);

			if (LevelDBHandler.getOutput(unspentTx) == null) {
				throw new TransactionException(Consts.ERR_NON_EX_OUTPUT);
//				return false;
			}

			if (!verifySignature(getInputBytes(i), input.getSignature(), LevelDBHandler.getOutput(unspentTx).getPkRecipient())) {
				throw new TransactionException(Consts.ERR_SIGN_CORRUPT);
//				return false;
			}

			if (!usedUnspentTx.add(unspentTx)) {
				throw new TransactionException(Consts.ERR_DOUBLE_SPENDING);
//				return false;
			}

			inputSum = inputSum.add(LevelDBHandler.getOutput(unspentTx).amount);
		}

		BigDecimal outputSum = new BigDecimal(0);

		for (Output output : outputs) {
			if(output.amount.compareTo(new BigDecimal(0)) <= 0) {
				throw new TransactionException(Consts.ERR_NEG_AMOUNT);
//				return false;
			}
			outputSum = outputSum.add(output.amount);
		}

		if (outputSum.compareTo(inputSum) >= 0) {
			throw new TransactionException(Consts.ERR_SUM);
//			return false;
		}

		return true;
	}

	public BigDecimal getAmount(PublicKey pkRecepient) {
		if (pkRecepient == null)
			return  null;

		BigDecimal outputSum = new BigDecimal(0);

		for (Output output : outputs) {
			if (output.getPkRecipient().equals(pkRecepient))
				outputSum = outputSum.add(output.amount);
		}

		return  outputSum;
	}

	public void computeSignature(int index, PrivateKey privateKey) {
		inputs.get(index).signature = Crypto.signData(privateKey, getInputBytes(index));
	}

	private boolean verifySignature(byte[] data, byte[] signature, PublicKey publicKey) {
		return Crypto.verifySignature(publicKey, data, signature);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("Transaction basic info: [hash=" + Hex.toHexString(hash) + ", coinbase=" + coinbase + ", fee=" + fee + ", ");
		sb.append("blockHeight = " + blockHeight + "]\n");

		for (Input in : inputs)
			sb.append(in);

		sb.append("\n");

		for (Output out: outputs)
			sb.append(out);

		return sb.toString();
	}

}

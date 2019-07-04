package net.etfbl.cryptocoin.blockchain;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bouncycastle.util.encoders.Hex;

import net.etfbl.cryptocoin.leveldb.LevelDBHandler;
import net.etfbl.cryptocoin.util.Crypto;
import net.etfbl.cryptocoin.util.Util;

public class Transaction implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5926299142781689209L;
	public static double DEFAULT_FEE = 0.1;
	public static double COINBASE_VALUE = 10;

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
		private double amount;
		private PublicKey pkRecipient;

		public Output(double value, PublicKey pkRecipient) {
			this.amount = value;
			this.pkRecipient = pkRecipient;
		}

		public double getValue() {
			return amount;
		}

		public PublicKey getPkRecipient() {
			return pkRecipient;
		}

		@Override
		public String toString() {
			return "[amount=" + amount + ",\npkRecipient=" + Hex.toHexString(pkRecipient.getEncoded()) + "]\n";
		}

	}

	private byte[] hash;
	private ArrayList<Input> inputs;
	private ArrayList<Output> outputs;
	private boolean coinbase;
	private double fee;
	private int blockHeight;

	public Transaction(double fee, int blockHeight) {
		this.fee = fee;
		this.blockHeight = blockHeight;
		inputs = new ArrayList<>();
		outputs = new ArrayList<>();

		computeHash();
	}

	public Transaction(double value, PublicKey publicKey, int blockHeight) {
		coinbase = true;
		this.blockHeight = blockHeight;
		inputs = new ArrayList<>();
		outputs = new ArrayList<>();

		Output output = new Output(value, publicKey);
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

	public double getFee() {
		return fee;
	}

	public int getBlockHeight() {
		return blockHeight;
	}

	public void computeHash() {
		hash = Crypto.computeHash(getBytes());
	}

	private byte[] getBytes() {
		ArrayList<Byte> bytes = new ArrayList<Byte>();

		for (Input input : inputs) {
			byte[] previousTxHash = input.previousTxHash;
			ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.SIZE / 8);
			byteBuffer.putInt(input.outputIndex);
			byte[] outputIndex = byteBuffer.array();
			byte[] signature = input.signature;
			byte[] prevBlockHeight = Util.getByteArray(input.getPreviousBlockHeight());

			if (previousTxHash != null)
				for (int i = 0; i < previousTxHash.length; i++)
					bytes.add(previousTxHash[i]);

			for (int i = 0; i < outputIndex.length; i++)
				bytes.add(outputIndex[i]);

			for (int i = 0; i < prevBlockHeight.length; i++)
				bytes.add(prevBlockHeight[i]);

			if (signature != null)
				for (int i = 0; i < signature.length; i++)
					bytes.add(signature[i]);
		}

		for (Output output : outputs) {
			ByteBuffer byteBuffer = ByteBuffer.allocate(Double.SIZE / 8);
			byteBuffer.putDouble(output.amount);
			byte[] value = byteBuffer.array();
			byte[] address = (output.getPkRecipient()).getEncoded();

			for (int i = 0; i < value.length; i++)
				bytes.add(value[i]);

			for (int i = 0; i < address.length; i++)
				bytes.add(address[i]);
		}

		byte[] bytesArr = new byte[bytes.size()];
		for (int i = 0; i < bytes.size(); i++)
			bytesArr[i] = bytes.get(i);

		return bytesArr;
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
			byteBufferOutput.putDouble(output.amount);
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

	public boolean isValid() {
		Set<UnspentTx> usedUnspentTx = new HashSet<>();
		int inputSum = 0;

		for (int i = 0; i < inputs.size(); i++) {
			Input input = inputs.get(i);
			byte[] prevTxHash = input.getPreviousTxHash();
			int outIndex = input.getOutputIndex();
			int prevBlockHeight = input.getPreviousBlockHeight();

			UnspentTx unspentTx = new UnspentTx(prevTxHash, outIndex, prevBlockHeight);

			if (LevelDBHandler.getOutput(unspentTx) == null) {
				System.out.println("Invalid transaction! Transaction trying to use nonexisting unspent output!");
				System.out.println("\n*********This transaction****************");
				System.out.println(this);
				System.out.println("***********This transaction**************\n");
				System.out.println("\n*********Unspent transaction****************");
				System.out.println(unspentTx);
				System.out.println("***********Unspent transaction**************\n");
				return false;
			}

			if (!verifySignature(getInputBytes(i), input.getSignature(), LevelDBHandler.getOutput(unspentTx).getPkRecipient())) {
				System.out.println("Invalid transaction! Signature is corrupt (" + i + ")!" + this);
				return false;
			}

			if (!usedUnspentTx.add(unspentTx)) {
				System.out.println("Invalid transaction! Transaction trying to use the same unspent output more than once.");
				return false;
			}

			inputSum += LevelDBHandler.getOutput(unspentTx).amount;
		}

		int outputSum = 0;

		for (Output output : outputs) {
			if(output.amount <= 0) {
				System.out.println("Invalid transaction! Transaction contains negative output amounts!");
				return false;
			}
			outputSum += output.amount;
		}

		if (outputSum > inputSum) {
			System.out.println("Invalid transaction! Output sum is larger than input sum!");
			return false;
		}

		return true;
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
		sb.append("Transaction basic info: [hash=" + Hex.toHexString(hash) + ", coinbase=" + coinbase + ", fee=" + fee + "]\n");
		for (Input in : inputs) {
			sb.append(in);
		}
		sb.append("\n");
		for (Output out: outputs) {
			sb.append(out);
		}
		return sb.toString();
	}

}

package net.etfbl.cryptocoin.blockchain;

import java.nio.ByteBuffer;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bouncycastle.util.encoders.Hex;

import net.etfbl.cryptocoin.util.Crypto;
import net.etfbl.cryptocoin.util.Util;

public class Transaction {

	public static class Input {

		private byte[] previousTxHash;
		private int outputIndex;
		private byte[] signature;

		public Input(byte[] previousTxHash, int outputIndex) {
			this.previousTxHash = Arrays.copyOf(previousTxHash, previousTxHash.length);
			this.outputIndex = outputIndex;
		}

		public byte[] getPreviousTxHash() {
			return previousTxHash;
		}

		public int getOutputIndex() {
			return outputIndex;
		}

		public byte[] getSignature() {
			return signature;
		}

	}

	public static class Output {

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
			return "[amount=" + amount + ",\npkRecipient=" + Hex.toHexString(pkRecipient.getEncoded()) + "]";
		}

	}

	private byte[] hash;
	private ArrayList<Input> inputs;
	private ArrayList<Output> outputs;
	private boolean coinbase;

	public Transaction() {
		inputs = new ArrayList<>();
		outputs = new ArrayList<>();
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

			if (previousTxHash != null)
				for (int i = 0; i < previousTxHash.length; i++)
					bytes.add(previousTxHash[i]);

			for (int i = 0; i < outputIndex.length; i++)
				bytes.add(outputIndex[i]);

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

	public boolean isValid(UnspentTxPool unspentTxPool) {
		Set<UnspentTx> usedUnspentTx = new HashSet<>();
		int inputSum = 0;

		for (int i = 0; i < inputs.size(); i++) {
			Input input = inputs.get(i);
			byte[] prevTxHash = input.getPreviousTxHash();
			int outIndex = input.getOutputIndex();

			UnspentTx unspentTx = new UnspentTx(prevTxHash, outIndex);

			if (!unspentTxPool.contains(unspentTx)) {
				System.out.println("Invalid transaction! Transaction trying to use nonexisting unspent output!");
				return false;
			}

			if (!verifySignature(getInputBytes(i), input.getSignature(), unspentTxPool.getUnspentTxOutput(unspentTx).getPkRecipient())) {
				System.out.println("Invalid transaction! Signature is corrupt!");
				return false;
			}

			if (!usedUnspentTx.add(unspentTx)) {
				System.out.println("Invalid transaction! Transaction trying to use the same unspent output more than once.");
				return false;
			}

			inputSum += unspentTxPool.getUnspentTxOutput(unspentTx).amount;
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
}

package net.etfbl.cryptocoin.exception;

public class TransactionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3485721502960581035L;

	public TransactionException() {
		super();
	}

	public TransactionException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public TransactionException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public TransactionException(String arg0) {
		super(arg0);
	}

	public TransactionException(Throwable arg0) {
		super(arg0);
	}

	

}

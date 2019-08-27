package net.etfbl.cryptocoin.exception;

public class BlockException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7387728958481051921L;

	public BlockException() {
		super();
	}

	public BlockException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public BlockException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public BlockException(String arg0) {
		super(arg0);
	}

	public BlockException(Throwable arg0) {
		super(arg0);
	}
}

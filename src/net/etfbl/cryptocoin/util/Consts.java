package net.etfbl.cryptocoin.util;

import java.math.BigDecimal;

public class Consts {

	public static final String CONFIG_SEPARATOR = ":";

	public static final String CONFIG_PATH = "D:\\workspace\\CryptoCoin\\resources\\config.txt";
	public static final String RESOURCES_PATH = "D:\\workspace\\CryptoCoin\\resources\\";
	public static final String MY_WALLET_PATH = "D:\\workspace\\CryptoCoin\\resources\\myWallet\\";
	public static final String OTHER_WALLETS_PATH = "D:\\workspace\\CryptoCoin\\resources\\otherWallets\\";
 
	public static final String WALLET_FILE_NAME = "wallet";

	public static final String ADDRESS_REGEX = "^[a-zA-Z0-9]{176}$";

	public static final BigDecimal DEFAULT_FEE = BigDecimal.valueOf(0.1);
	public static final BigDecimal COINBASE_VALUE = new BigDecimal(10);

	public static final int DIFFICULTY = 3;

	public static final String ERR_NON_EX_OUTPUT = "Invalid transaction! Transaction trying to use nonexisting unspent output!";
	public static final String ERR_SIGN_CORRUPT = "Invalid transaction! Signature is corrupt!";
	public static final String ERR_DOUBLE_SPENDING = "Invalid transaction! Transaction trying to use the same unspent output more than once.";
	public static final String ERR_NEG_AMOUNT = "Invalid transaction! Transaction contains negative output amounts!";
	public static final String ERR_SUM = "Invalid transaction! Output sum is larger than input sum!";
}

package com.bitcoin.termwallet;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.utils.BtcFormat;
import org.bitcoinj.params.*;
import java.math.BigInteger;
import java.io.File;

public class Constants {

	public static NetworkParameters params = MainNetParams.get(); // To go live, change to "MainNetParams.get()"
	public static File fileLocation = new File(System.getProperty("user.home") + "/.termwallet/"); 
	public static int CONFIRMATIONS_TO_WAIT = 1; // 1 right now to make it faster, but to be safe it should be 6 when live
	public static BtcFormat bf = BtcFormat.getInstance();
	public static boolean useTor = false;

}

package com.bitcoin.termwallet;

import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.params.*;
import java.math.BigInteger;

public class Constants {

	public static NetworkParameters params = TestNet3Params.get(); // To go live, change to "MainNetParams.get()"
	public static int CONFIRMATIONS_TO_WAIT = 1; // 1 right now to make it faster, but to be safe it should be 6 when live

}

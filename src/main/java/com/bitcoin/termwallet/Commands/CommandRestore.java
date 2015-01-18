package com.bitcoin.termwallet;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Address;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Create a wallet based on a BIP-39 mnemonic seed")
public class CommandRestore {

	@Parameter(names = { "-t", "--time" }, description = "Replay blockchain starting from this time (UNIX epoch time)")
	long time = System.currentTimeMillis() / 1000L;
	
}


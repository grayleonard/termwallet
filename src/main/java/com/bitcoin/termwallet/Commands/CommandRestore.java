package com.bitcoin.termwallet;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Address;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Remove a key from your watched addresses - destroy imported private keys")
public class CommandRestore {

	@Parameter(names = { "-t", "--time" }, description = "Replay blockchain starting from this time (UNIX epoch time)")
	long time = System.currentTimeMillis() / 1000L;
	@Parameter(names = { "-p", "--password" }, description = "Seed password (True or false)")
	boolean passRequired;
}


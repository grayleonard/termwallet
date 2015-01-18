package com.bitcoin.termwallet;

import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.store.UnreadableWalletException;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.io.PrintStream;

@Parameters(commandDescription = "Create a wallet based on a BIP-39 mnemonic seed")
public class CommandRestore {

	@Parameter(names = { "-t", "--time" }, description = "Replay blockchain starting from this time (UNIX epoch time)")
	long time = System.currentTimeMillis() / 1000L;

	public void call(boolean verbose) {
		System.setOut(Constants.original);
		System.out.print("Seed: ");
		String seedCode = System.console().readLine();
		long seedTime = time; 
		try {
			DeterministicSeed seed = new DeterministicSeed(seedCode, null, "", seedTime);
			System.out.println("Restoring seed \"" + seedCode + "\", from time " + seedTime);
			if(!verbose)
				System.setOut(Constants.nullStream);
			App.getKit().restoreWalletFromSeed(seed);
		} catch(UnreadableWalletException e) {
			e.printStackTrace();
		}
	}	
}


package com.bitcoin.termwallet;

import org.bitcoinj.core.NetworkParameters;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.IStringConverter;

import org.bitcoinj.core.Address;
 
import java.util.List;
import java.util.ArrayList;


public class Arguments {

	@Parameter(names = { "-t", "--tor" }, description = "Use Tor to connect to bitcoin network")
	public boolean tor = false;

	@Parameter(names = { "--testnet" }, description = "Use testnet network")
	public boolean testnet = false;

	@Parameter(names = { "-l", "--listen" }, description = "Passively listen for transactions")
	public boolean listen = false;

	@Parameter(names = { "-v", "--verbose" }, description = "Verbose output")
	public boolean verbose = false;

	@Parameter(names = {"-h", "--help" }, help=true)
	public boolean help = false;
}



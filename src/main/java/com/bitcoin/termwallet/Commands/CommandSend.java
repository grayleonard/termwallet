package com.bitcoin.termwallet;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Address;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "  Add file contents to the index")
public class CommandSend {
 
	@Parameter(names = { "-a", "--amount" }, description = "Amount to Send (in BTC)", required=true, converter=CoinConverter.class)
	public Coin sendAmount;

  	@Parameter(names = { "-t", "--to" }, description = "Address to send to", required=true, converter=AddressConverter.class)
	public Address toAddress;

	@Parameter(names = { "-f", "--from" }, description = "Address to send from", converter=AddressConverter.class)
	public Address fromAddress = null;

	@Parameter(names = { "-c", "--change" }, description = "Change address", converter=AddressConverter.class)
	public Address changeAddress = null;
}


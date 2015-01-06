package com.bitcoin.termwallet;

import org.bitcoinj.wallet.KeyChain.KeyPurpose;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Create a new address with purpose 'change' or 'receive', defaults to 'receive'")
public class CommandNew {

	@Parameter(names = { "-p", "--purpose" }, description = "Address purpose", converter=KeyPurposeConverter.class)
	public KeyPurpose purpose = KeyPurpose.RECEIVE_FUNDS;

}

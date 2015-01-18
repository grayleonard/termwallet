package com.bitcoin.termwallet;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Address;

import org.spongycastle.crypto.params.KeyParameter;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Send all BTCs in wallet to address, optionally delete wallet")
public class CommandPanic {

	@Parameter(names = { "-t", "--to" }, description = "Address to send to", converter=AddressConverter.class)
	public Address toAddress = null;

	@Parameter(names = { "-sd", "--self-destruct" }, description = "Self-Destruct; Erase all private keys and wallet file")
	public boolean selfDestruct = false;

	public void call() {
		System.setOut(Constants.original);
		//Handle password input / decryption
		KeyParameter aeskey = null;
		String password = null;
		if(App.getKit().wallet().isEncrypted()) {
			password = Utils.promptPassword();
			while(password == null) {
				password = Utils.promptPassword();
			}
			aeskey = App.getKit().wallet().getKeyCrypter().deriveKey(password);
		}
		App.getEngine().emptyWalletRequest(toAddress, selfDestruct, aeskey);
	}

}

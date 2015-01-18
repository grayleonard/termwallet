package com.bitcoin.termwallet;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Address;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Remove a key from your watched addresses - destroy imported private keys")
public class CommandDelete {

	@Parameter(names = { "-a", "--address" }, description = "Address to delete", required=true, converter=AddressConverter.class)
	Address addressToDelete;

	public void call() {
		ECKey toDelete = App.getKit().wallet().findKeyFromPubHash(addressToDelete.getHash160());
		App.getEngine().deleteKey(toDelete);
	}
}

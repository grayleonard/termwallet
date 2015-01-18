package com.bitcoin.termwallet;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Address;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.util.List;
import java.util.Arrays;

@Parameters(commandDescription = "Add a private key to your wallet")
public class CommandImport {

	public void call() {
		ECKey eckey = Utils.promptPrivKey();
		System.out.println("Creating ECKey " + eckey + "...");
		eckey.setCreationTimeSeconds(140606150L); //Ensure retroactive checking of balance
		if(App.getKit().wallet().isEncrypted()) {
			String pass = Utils.promptPassword();	
			if(App.getKit().wallet().checkPassword(pass)) {
				List<ECKey> eckeys = Arrays.asList(eckey);
				App.getKit().wallet().importKeysAndEncrypt(eckeys, pass);
				System.out.println("Imported and encrypted key with address " + eckey.toAddress(Constants.params));
			} else {
				System.out.println("Wrong password, try again.");
			}
		} else {
		App.getKit().wallet().importKey(eckey);
		System.out.println("Added new ECKey, address " + eckey.toAddress(Constants.params) + " to wallet...");
		}
	}
}


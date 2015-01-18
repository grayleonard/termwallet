package com.bitcoin.termwallet;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Decrypt private keys and seed in wallet")
public class CommandDecrypt {

	public void call() {
		if(App.getKit().wallet().isEncrypted()) {
					String password = null;
					while(password == null)
						password = Utils.promptPassword();
					App.getKit().wallet().decrypt(App.getKit().wallet().getKeyCrypter().deriveKey(password));
					System.out.println("Success! Decrypted wallet!");
				} else {
					System.out.println("Wallet is not encrypted.");
				}
				System.out.println("Exiting...");
	}

}

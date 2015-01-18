package com.bitcoin.termwallet;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Export your wallet")
public class CommandExport {

	public void call() {
		System.out.println("Exporting wallet:");
			if(App.getKit().wallet().isEncrypted()) {
				System.out.print("Wallet is encrypted, decrypt now? If not, no private keys will be shown (yes/no):");
				String result = System.console().readLine();
				if(result.equals("yes") || result.equals("y")) {
					String password = null;
					while(password == null)
						password = Utils.promptPassword();
					App.getKit().wallet().decrypt(App.getKit().wallet().getKeyCrypter().deriveKey(password));
					System.out.println(App.getKit().wallet().toString(true, false, false, null));
					App.getKit().wallet().encrypt(password);
				} else {
				System.out.println(App.getKit().wallet().toString(false, false, false, null));
				}
			} else {
				System.out.println(App.getKit().wallet().toString(true, false, false, null));
			}
	}

}

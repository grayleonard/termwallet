package com.bitcoin.termwallet;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Encrypt private keys and seed in wallet")
public class CommandEncrypt {

	public void call() {
		if(App.getKit().wallet().isEncrypted()) {
			System.out.println("Wallet already encrypted.");
			return;
		}
		System.out.print("Password: ");
		String password = System.console().readLine();
		System.out.print("Again: ");
		String passwordCheck = System.console().readLine();
		if(password.equals(passwordCheck)) {
			App.getKit().wallet().encrypt(password);
			System.out.println("Success! Private keys have been encrypted.");
		} else {
			System.out.println("Passwords didn't match!");
		}
		return;
	}
}

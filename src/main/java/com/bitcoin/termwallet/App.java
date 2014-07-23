package com.bitcoin.termwallet;

import com.google.bitcoin.core.*;
import com.google.bitcoin.wallet.CoinSelector;
import com.google.bitcoin.crypto.KeyCrypter;
import com.google.bitcoin.crypto.KeyCrypterException;
import com.google.bitcoin.kits.WalletAppKit;
import com.google.bitcoin.params.MainNetParams;
import com.google.bitcoin.params.RegTestParams;
import com.google.bitcoin.params.TestNet3Params;
import com.google.bitcoin.utils.BriefLogFormatter;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;

import org.slf4j.helpers.NOPLogger;

import java.util.Iterator;
import java.util.List;
import java.io.File;
import java.io.PrintStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.lang.StringBuilder;
import java.util.logging.*;
import java.text.MessageFormat;

import static com.google.common.base.Preconditions.checkNotNull;

public class App {
    private static Address forwardingAddress;
    private static WalletAppKit kit;
    private static WalletEngine walletEngine;
    public static void main(String[] args) throws Exception {
	if(args.length == 0)
		printHelp();
	if(args[0].equals("help")) 
		printHelp();	
	
        // Figure out which network we should connect to. Each one gets its own set of files.
        try {
		System.out.println("\r\nConnecting to and downloading blockchain...");
		PrintStream nullStream = new PrintStream(new OutputStream() {
			public void write(int b) {} 
			});
		PrintStream original = System.out;
		System.setOut(nullStream); //RIP beautiful programming
		System.setErr(nullStream);
		String filePrefix;
		if (Constants.params.equals(TestNet3Params.get())) {
		System.out.println("connecting to TestNet");
			filePrefix = "termwallet-testnet";
		} else {
			filePrefix = "termwallet";
		}

		// Start up a basic app using a class that automates some boilerplate.
		kit = new WalletAppKit(Constants.params, new File(System.getProperty("user.home")), filePrefix);
		// Download the blockchain and wait until it's done.
		kit.startAndWait();

		// Initiate the WalletEngine class
		walletEngine = new WalletEngine();
		// Forwards all events to the wallet engine to handle them.
		kit.wallet().addEventListener(walletEngine);
		// Start it all up, baby!
		System.setOut(original); //Allow console output again
		System.out.println("\r\nFinished downloading blockchain, starting process...\r\n");
		//TODO: Handle this using something better than if cases	
		if(args[0].equals("status")) {
			System.out.println("Your addresses and their balances:");
			List<ECKey> walletPubkeys = kit.wallet().getKeys();
			Iterator<ECKey> iterator = walletPubkeys.iterator();
			while(iterator.hasNext()) {
				Address tempAddr = iterator.next().toAddress(Constants.params);
				BigInteger addrBalance = kit.wallet().getBalance(new IndividualCoinSelector(tempAddr));
				String format = "%-40s%s%n";
				System.out.printf(format, tempAddr, "Balance: " + addrBalance);
			}
			System.out.println("\r\nConfirmed: " + Utils.bitcoinValueToFriendlyString(kit.wallet().getBalance()) + "BTC");
			System.out.println("Unconfirmed: " + Utils.bitcoinValueToFriendlyString(kit.wallet().getBalance(Wallet.BalanceType.ESTIMATED).subtract(kit.wallet().getBalance())) + "BTC");
		}
		if(args[0].equals("newaddress")) {
			System.out.println(getEngine().newAddress());
		}
		if(args[0].equals("send")) {
			System.setOut(original);
			System.out.println(args[2]);
			if(args.length == 3) {
				getEngine().createSendRequest(args[1], new BigInteger(args[2]));
			}
			if(args.length == 4) {
				getEngine().createSendRequest(new Address(Constants.params, args[2]), args[1], new BigInteger(args[3]));
			}
		}
		if(args[0].equals("encrypt")) {
			System.out.print("Password: ");
			String password = System.console().readLine();
			System.out.print("Again: ");
			String passwordCheck = System.console().readLine();
			if(password.equals(passwordCheck)) {
				getKit().wallet().encrypt(password);
				System.out.println("Success! Private keys have been encrypted.");
			}
		}
		if(args[0].equals("decrypt")) {
			System.out.print("Password: ");
			String password = System.console().readLine();
			if(getKit().wallet().checkPassword(password)) {
				getKit().wallet().decrypt(getKit().wallet().getKeyCrypter().deriveKey(password));
				System.out.println("Success! Decrypted wallet!");
			} else {
				System.out.println("Error! Wrong password!");
			}
		}
		if(args[0].equals("export")) {
			System.out.println("Exporting wallet:");
			System.out.println(getKit().wallet().toString(true, false, false, null));
		}
		if(args[0].equals("add")) {
			System.out.println("Creating ECKey from privkey " + args[1] + "...");
			ECKey toAdd = new ECKey(args[1].getBytes(), null);
			toAdd.setCreationTimeSeconds(140606150L);
			getKit().wallet().addKey(toAdd);
			System.out.println("Added new ECKey, address " + toAdd.toAddress(Constants.params) + " to wallet...");
		}
		
		System.out.println("\r\n");
		if(!args[0].equals("listen")) {
			System.exit(0);
		} else {
			System.out.println("OK, listening for new transactions...");
			System.setOut(original);
		}
	}
	catch (Exception e) {
	    e.printStackTrace();
	}
    }
    
    public static WalletAppKit getKit() {
        return kit;
    }

    public static WalletEngine getEngine() {
        return walletEngine;
    }
    static void printHelp() {
		System.out.println("\r\nCommands:\r\n\r\nhelp, Prints this\r\n");
		System.out.println("status, Prints overview of your wallet. Includes your addresses and their balances\r\n");
		System.out.println("newaddress, Add a new, random address to your wallet\r\n");
		System.out.println("add [privkey], Adds designated private key to your wallet\r\n");
		System.out.println("send [to] [satoshis], send a new transaction. Chooses from all outputs in your wallet\r\n");
		System.out.println("send [to] [from] [satoshis], send a new transaction. Designating the 'from' address limits output selection to only that address\r\n");
		System.out.println("listen, Listen passively for new transactions");
		System.out.println("encrypt, You will be prompted for a password (make sure it is long and secure) to encrypt the private keys in your wallet with\r\n");
		System.out.println("decrypt, You will be prompted for your encryption password.  You must decrypt before sending a transaction (and ideally encrypt after you send the transaction)\r\n");
		System.out.println("export, Export all keys associated with your wallet.  You must decrypt your wallet first if you want your private keys");
		System.exit(0);
	}
}

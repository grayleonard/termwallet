package com.bitcoin.termwallet;

import org.bitcoinj.core.*;
import org.bitcoinj.wallet.CoinSelector;
import org.bitcoinj.wallet.*;
import org.bitcoinj.wallet.KeyChain.KeyPurpose;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.KeyCrypter;
import org.bitcoinj.crypto.KeyCrypterException;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.utils.BriefLogFormatter;
import org.bitcoinj.utils.BtcFormat;
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

import com.beust.jcommander.Parameter;
import com.beust.jcommander.JCommander;

import static com.google.common.base.Preconditions.checkNotNull;

public class App {
	private static Address forwardingAddress;
	private static WalletAppKit kit;
	private static WalletEngine walletEngine;

	private static JCommander jc;

	private static PrintStream nullStream = new PrintStream(new OutputStream() {
				public void write(int b) {} 
			});
	private static PrintStream original = System.out; 

	private static BtcFormat bf = Constants.bf;

	public static void main(String[] args) throws Exception {

		Arguments jcargs = new Arguments();
		JCommander jc = new JCommander(jcargs);
		CommandStatus status = new CommandStatus();
		CommandSend send = new CommandSend();
		CommandEncrypt encrypt = new CommandEncrypt();
		CommandDecrypt decrypt = new CommandDecrypt();
		CommandExport export = new CommandExport();
		CommandMaintenance maintenance = new CommandMaintenance();
		CommandImport commandImport = new CommandImport();
		CommandDelete delete = new CommandDelete();
		CommandPanic panic = new CommandPanic();
		CommandNew commandNew = new CommandNew();

		jc.addCommand("status", status);
		jc.addCommand("new", commandNew);
		jc.addCommand("import", commandImport);
		jc.addCommand("send", send);
		jc.addCommand("encrypt", encrypt);
		jc.addCommand("decrypt", decrypt);
		jc.addCommand("export", export);
		jc.addCommand("maintenance", maintenance);
		jc.addCommand("delete", delete);
		jc.addCommand("panic", panic);

		jc.parse(args);

		if(args.length == 0) {
			printHelp(jc);
			System.exit(0);
		} if(jcargs.help) {
			printHelp(jc);
			System.exit(0);
		}	

		if(jcargs.testnet)
			Constants.params = TestNet3Params.get(); //Yikes. Do better later

		// Figure out which network we should connect to. Each one gets its own set of files.
		try {
			System.out.println("Connecting to and downloading blockchain...");
			if(!jcargs.verbose) {
				System.setOut(nullStream); //RIP beautiful programming
				System.setErr(nullStream);
			}

			String filePrefix;
			
			if (Constants.params.equals(TestNet3Params.get())) {
				System.out.println("connecting to TestNet");
				filePrefix = ".termwallet-testnet";
			} else {
				filePrefix = ".termwallet";
			}
			kit = new WalletAppKit(Constants.params, Constants.fileLocation, filePrefix);
			if(Constants.useTor || jcargs.tor) {
				System.out.println("Using Tor...");
				kit.useTor(); //For the future...
			}
			// Download the blockchain and wait until it's done.
			kit.startAndWait();
			// Initiate the WalletEngine class
			walletEngine = new WalletEngine();
			// Forwards all events to the wallet engine to handle them.
			kit.wallet().addEventListener(walletEngine);
			// Start it all up, baby!
			System.setOut(original); //Allow console output again
			System.out.println("Finished downloading blockchain...\r\n");
			//TODO: Handle this using something better than if cases	
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Call status command
		if(jc.getParsedCommand() == "status") {
			printStatus();
		}


		if(jc.getParsedCommand() == "new") {
			System.out.println("New Address " + getEngine().newAddress(commandNew.purpose) + " with purpose " + commandNew.purpose);
		}

		if(jc.getParsedCommand() == "send" || jc.getParsedCommand() == "panic") {
			System.setOut(original);
			boolean shouldEncryptOnceSent = false;
			String password = "";
			if(getKit().wallet().isEncrypted()) {
				password = promptDecrypt();
				if(password == "") {
					System.out.println("Wrong password! Exiting...");
					return;
				}
				shouldEncryptOnceSent = true;
			}
			if(jc.getParsedCommand() == "send") {
				getEngine().createSendRequest(send.toAddress, send.sendAmount, send.fromAddress, send.changeAddress);
			} else if(jc.getParsedCommand() == "panic") {
				getEngine().emptyWalletRequest(panic.toAddress, panic.selfDestruct);
			}
			if(shouldEncryptOnceSent) {
				getKit().wallet().encrypt(password);
			}

		}

		if(jc.getParsedCommand() == "encrypt") {
			if(!getKit().wallet().isEncrypted()) {
				promptEncrypt();
			} else {
				System.out.println("Wallet is already encrypted.");
			}
			System.out.println("Exiting...");
		}

		if(jc.getParsedCommand() == "decrypt") {
			if(getKit().wallet().isEncrypted()) {
				promptDecrypt();
			} else {
				System.out.println("Wallet is not encrypted.");
			}
			System.out.println("Exiting...");
		}

		if(jc.getParsedCommand() == "export") {
			System.out.println("Exporting wallet:");
			if(getKit().wallet().isEncrypted()) {
				System.out.print("Wallet is encrypted, decrypt now? If not, no private keys will be shown (yes/no):");
				String result = System.console().readLine();
				if(result.equals("yes") || result.equals("y")) {
					promptDecrypt();
					System.out.println(getKit().wallet().toString(true, false, false, null));
				} else {
				System.out.println(getKit().wallet().toString(false, false, false, null));
				}
			} else {
				System.out.println(getKit().wallet().toString(true, false, false, null));
			}
		}

		if(jc.getParsedCommand() == "maintenance") {
			getKit().wallet().doMaintenance(null, true);
		}

		if(jc.getParsedCommand() == "delete") {
			ECKey toDelete = getKit().wallet().findKeyFromPubHash(delete.addressToDelete.getHash160());
			getEngine().deleteKey(toDelete);
		}

		if(jc.getParsedCommand() == "import") {
			ECKey eckey = promptPrivKey();
			System.out.println("Creating ECKey " + eckey + "...");
			eckey.setCreationTimeSeconds(140606150L); //Ensure retroactive checking of balance
			getKit().wallet().importKey(eckey);
			System.out.println("Added new ECKey, address " + eckey.toAddress(Constants.params) + " to wallet...");
		}

		//TODO: figure out a better way to do this?
		if(!jcargs.listen) {
			System.exit(0);
		} else {
			System.out.println("OK, listening for new transactions...");
			System.setOut(original);
		}
	}

	public static WalletAppKit getKit() {
		return kit;
	}

	public static WalletEngine getEngine() {
		return walletEngine;
	}
	static void printHelp(JCommander jc) {
		jc.usage();
		System.exit(0);
	}

	static void promptEncrypt() {
		System.out.print("Password: ");
		String password = System.console().readLine();
		System.out.print("Again: ");
		String passwordCheck = System.console().readLine();
		if(password.equals(passwordCheck)) {
			getKit().wallet().encrypt(password);
			System.out.println("Success! Private keys have been encrypted.");
		} else {
			System.out.println("Passwords didn't match!");
		}
		return;
	}

	static String promptDecrypt() {
		System.out.print("Password: ");
		String password = System.console().readLine();
		if(getKit().wallet().checkPassword(password)) {
			getKit().wallet().decrypt(getKit().wallet().getKeyCrypter().deriveKey(password));
			System.out.println("Success! Decrypted wallet!");
			return password;
		} else {
			System.out.println("Error! Wrong password!");
			return "";
		}
	}

	static ECKey promptPrivKey() {
		System.out.print("Private key: ");
		ECKey eckey = new ECKey(System.console().readLine().getBytes(), null);
		return eckey;
	}

	static void printStatus() {
		System.out.println("Your addresses and their balances:");
			List<Address> determKeys = kit.wallet().getWatchedAddresses();
			Iterator<Address> determIterator = determKeys.iterator();

			List<ECKey> importedKeys = kit.wallet().getImportedKeys();
			Iterator<ECKey> importedIterator = importedKeys.iterator();
			
			if(importedKeys.size() > 0) {
				System.out.println("\r\nImported Keys:");
				while(importedIterator.hasNext()) {
					Address tempAddr = importedIterator.next().toAddress(Constants.params);
					Coin addrBalance = kit.wallet().getBalance(new IndividualCoinSelector(tempAddr));
					String format ="%-40s%s%n";
					System.out.printf(format,tempAddr, "Balance: " + bf.format(addrBalance));
				}
			System.out.println();
			}
			if(determKeys.size() > 0) {
				System.out.println("Deterministic Keys:");
				while(determIterator.hasNext()) {
					Address tempAddr = determIterator.next();
					Coin addrBalance = kit.wallet().getBalance(new IndividualCoinSelector(tempAddr));
					String format = "%-40s%s%n";
					System.out.printf(format, tempAddr, "Balance: " + bf.format(addrBalance));
				}
				System.out.println("\r\nConfirmed: " + bf.format(kit.wallet().getBalance()));
				System.out.println("Unconfirmed: " + bf.format(kit.wallet().getBalance(Wallet.BalanceType.ESTIMATED).subtract(kit.wallet().getBalance())));
			}
	}
}

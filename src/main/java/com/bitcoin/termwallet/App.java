package com.bitcoin.termwallet;

import java.util.Iterator;
import java.util.List;
import java.util.Arrays;
import java.io.File;
import java.io.PrintStream;
import java.io.OutputStream;

import org.bitcoinj.core.Address;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.utils.BtcFormat;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.MissingCommandException;

public class App {

	private static Address forwardingAddress;
	private static WalletAppKit kit;
	private static WalletEngine walletEngine;

	private static JCommander jc;

	private static BtcFormat bf = Constants.bf;

	enum Commands {
		STATUS,
		NEW,
		IMPORT,
		SEND,
		ENCRYPT,
		DECRYPT,
		EXPORT,
		MAINTENANCE,
		DELETE,
		PANIC,
		RESTORE;
	}

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
		CommandRestore restore = new CommandRestore();

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
		jc.addCommand("restore", restore);

		try {
			jc.parse(args);
		} catch(MissingCommandException e) {
			System.out.println("Unknown command, printing help");
			printHelp(jc);
		}

		Commands c = Commands.valueOf(jc.getParsedCommand().toUpperCase());

		if(args.length == 0) {
			printHelp(jc);
			System.exit(0);
		} if(jcargs.help) {
			printHelp(jc);
			System.exit(0);
		}	

		if(jcargs.testnet)
			Constants.params = TestNet3Params.get(); //Yikes. Do better later

		if(!jcargs.verbose) {
			System.setOut(Constants.nullStream); //RIP beautiful programming
			System.setErr(Constants.nullStream);
		}

		String filePrefix;
		
		if (Constants.params.equals(TestNet3Params.get())) {
			System.out.println("Connecting to TestNet");
			filePrefix = ".termwallet-testnet";
		} else {
			filePrefix = ".termwallet";
		}

		try {
			// Start up wallet
			kit = new WalletAppKit(Constants.params, Constants.fileLocation, filePrefix);
			if(Constants.useTor || jcargs.tor) {
				System.out.println("Using Tor...");
				kit.useTor(); //For the future...
			}

			if(c == Commands.RESTORE) {
				restore.call(jcargs.verbose);
			}

			// Download the blockchain and wait until it's done.
			System.setOut(Constants.original);
			System.out.println("Connecting to and downloading blockchain...");
			System.setOut(Constants.nullStream);
			kit.startAndWait();
			// Initiate the WalletEngine class
			walletEngine = new WalletEngine();
			// Forwards all events to the wallet engine to handle them.
			kit.wallet().addEventListener(walletEngine);
			// Start it all up, baby!
			System.setOut(Constants.original); //Allow console output again
			System.out.println("Finished downloading blockchain...\r\n");
			//TODO: Handle this using something better than if cases	
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Iterate through all possible commands 
		switch(c) {
			case STATUS:
				Utils.printStatus();
			break;

			case NEW:
				System.out.println("New Address " + getEngine().newAddress(commandNew.purpose) + " with purpose " + commandNew.purpose);
			break;

			case SEND:
				send.call();
			break;

			case PANIC:
				panic.call();	
			break;

			case ENCRYPT:
				encrypt.call();	
			break;

			case DECRYPT:
				decrypt.call();	
			break;

			case EXPORT:
				export.call();	
			break;

			case MAINTENANCE:
				getKit().wallet().doMaintenance(null, true);
			break;

			case DELETE:
				delete.call();
			break;

			case IMPORT:
				commandImport.call();	
			break;
			}

		//TODO: figure out a better way to do this?
		if(!jcargs.listen) {
			System.exit(0);
		} else {
			System.out.println("OK, listening for new transactions...");
			System.setOut(Constants.original);
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
}

package com.bitcoin.termwallet;


import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.utils.BtcFormat;
import java.util.Iterator;
import java.util.List;
public class Utils {

	private static BtcFormat bf = Constants.bf;

	static String promptPassword() {
		System.out.print("Password: ");
		String password = System.console().readLine();
		if(App.getKit().wallet().checkPassword(password)) {
			return password;
		} else {
			System.out.println("Wrong Password! Try Again...");
			return null;
		}
	}

	static ECKey promptPrivKey() {
		System.out.print("Private key: ");
		ECKey eckey = new ECKey(System.console().readLine().getBytes(), null);
		return eckey;
	}

	static void printStatus() {
		System.out.println("Your addresses and their balances:");
		List<Address> determKeys = App.getKit().wallet().getWatchedAddresses();
		Iterator<Address> determIterator = determKeys.iterator();

		List<ECKey> importedKeys = App.getKit().wallet().getImportedKeys();
		Iterator<ECKey> importedIterator = importedKeys.iterator();
		
		if(importedKeys.size() > 0) {
			System.out.println("\r\nImported Keys:");
			while(importedIterator.hasNext()) {
				Address tempAddr = importedIterator.next().toAddress(Constants.params);
				Coin addrBalance = App.getKit().wallet().getBalance(new IndividualCoinSelector(tempAddr));
				String format ="%-40s%s%n";
				System.out.printf(format,tempAddr, "Balance: " + bf.format(addrBalance));
			}
		System.out.println();
		}
		if(determKeys.size() > 0) {
			System.out.println("Deterministic Keys:");
			while(determIterator.hasNext()) {
				Address tempAddr = determIterator.next();
				Coin addrBalance = App.getKit().wallet().getBalance(new IndividualCoinSelector(tempAddr));
				String format = "%-40s%s%n";
				System.out.printf(format, tempAddr, "Balance: " + bf.format(addrBalance));
			}
			System.out.println("\r\nConfirmed: " + bf.format(App.getKit().wallet().getBalance()));
			System.out.println("Unconfirmed: " + bf.format(App.getKit().wallet().getBalance(Wallet.BalanceType.ESTIMATED).subtract(App.getKit().wallet().getBalance())));
		}
	}
}

package com.bitcoin.termwallet;

import org.bitcoinj.core.*;
import org.bitcoinj.wallet.CoinSelector;
import org.bitcoinj.wallet.KeyChain.KeyPurpose;
import org.bitcoinj.wallet.*;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.utils.*;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;
import static com.google.common.base.Preconditions.checkNotNull;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.util.concurrent.*;
import static java.util.concurrent.TimeUnit.*;

public class WalletEngine extends AbstractWalletEventListener {

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private BtcFormat bf = Constants.bf;

	// Handle all incoming transactions, create TransactionFutures for each of them.
	@Override
	public void onCoinsReceived(Wallet w, Transaction tx, Coin prevBalance, Coin newBalance) {
		final Coin value = tx.getValueSentToMe(w);
		System.out.println("Received tx for " + bf.format(value) + ": " + tx);
		System.out.println("Transaction will be added to your balance after it confirms! Congrats!");
		System.out.println("If you keep termWallet running a confirmation message will be displayed.");

		// Wait until it's made it into the block chain (may run immediately if it's already there).
		Futures.addCallback(tx.getConfidence().getDepthFuture(1), new FutureCallback<Transaction>() {
			@Override
			public void onSuccess(Transaction result) {
				// check if the transaction is internal - if it is, handle it
				System.out.println("Transaction confirmed:" + result.toString());
			}

			@Override
			public void onFailure(Throwable t) {
				throw new RuntimeException(t);
			}
		});
	}

	@Override
	public void onKeysAdded(List<ECKey> keys) {
	}

	public Address newAddress(KeyPurpose purpose) {
		// Generates new keypair, adds private key to wallet, and then returns the corresponding address.
		Address freshAddress = App.getKit().wallet().freshAddress(purpose);
		App.getKit().wallet().addWatchedAddress(freshAddress);
		return freshAddress;
	}

	public void createSendRequest(Address destination, Coin amount, Address from, Address change) {
		try {
			System.out.println("Sending " + bf.format(amount) + " with TX FEE: " + bf.format(Transaction.REFERENCE_DEFAULT_MIN_TX_FEE));
			final Coin amountToSend = amount;
			Coin amountAvailable;
			// Checks if param. address has been specified
			if(from == null) {
				amountAvailable = App.getKit().wallet().getBalance();
			} else {
				amountAvailable = App.getKit().wallet().getBalance(new IndividualCoinSelector(from));
				App.getKit().wallet().setCoinSelector(new IndividualCoinSelector(from));
			}
			// Check balance prior to sending request (sendResult will also throw error if balance < amount)
			if(amountAvailable.compareTo(amountToSend.add(Transaction.REFERENCE_DEFAULT_MIN_TX_FEE)) == -1) {
				System.out.println("You don't have enough funds! You need " + amountToSend.add(Transaction.REFERENCE_DEFAULT_MIN_TX_FEE).subtract(amountAvailable) + " satoshis more!");
				return;
			}
			// Creates the send request and designates the change address (where change will be sent).
			Wallet.SendRequest sendRequest = Wallet.SendRequest.to(destination, amountToSend.add(Transaction.REFERENCE_DEFAULT_MIN_TX_FEE));
			if(change == null) {
				Address newChangeAddress = newAddress(KeyPurpose.CHANGE);
				App.getKit().wallet().addWatchedAddress(newChangeAddress);
				sendRequest.changeAddress = newChangeAddress; //Temporary
			}
			else {
				sendRequest.changeAddress = change;
			}
			final Wallet.SendResult sendResult = App.getKit().wallet().sendCoins(App.getKit().peerGroup(), sendRequest);
			checkNotNull(sendResult);  // We should never try to send more coins than we have!
			System.out.println("Sending...");
			// Register a callback that is invoked when the transaction has propagated across the network.
			sendResult.broadcastComplete.addListener(new Runnable() {
				@Override
				public void run() {
					// The wallet has changed now, it'll get auto saved shortly or when the app shuts down.
					System.out.println("Sent coins! Transaction hash is " + sendResult.tx.getHashAsString());
				}
			}, MoreExecutors.sameThreadExecutor());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void emptyWalletRequest(Address destination, final boolean selfDestruct) {
		try {
			if(destination == null) {
				if(selfDestruct) {
					deleteWallet();
					return;
				}
				return;
			}
			System.out.println("Panicking! Sending BTC to " + destination.toString());
			Wallet.SendRequest sendRequest = Wallet.SendRequest.emptyWallet(destination);
			final Wallet.SendResult sendResult = App.getKit().wallet().sendCoins(App.getKit().peerGroup(), sendRequest);
			sendResult.broadcastComplete.addListener(new Runnable() {
				@Override
				public void run() {
					// The wallet has changed now, it'll get auto saved shortly or when the app shuts down.
					System.out.println("Sent coins! Transaction hash is " + sendResult.tx.getHashAsString());
					if(selfDestruct) {
						deleteWallet();
					}
				}
			}, MoreExecutors.sameThreadExecutor());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean deleteKey(ECKey key) {
		if(key instanceof DeterministicKey) {
			//TODO However, predicated on bitcoinj maven repo being updated to include this
			//App.getKit().wallet().removeWatchedAddress(key.toAddress(Constants.params));
			System.out.println("Determinstic keys not yet supported, sorry.");
			return false;
		} else if(key instanceof ECKey) {
			App.getKit().wallet().removeKey(key);
			System.out.println("Removed key " + key);
			return true;
		}
		return false;
	}

	public boolean deleteWallet() {
		for(File file: Constants.fileLocation.listFiles())
			file.deleteOnExit();
		if(Constants.fileLocation.listFiles().length == 0)
			return true;
		else
			return false;
	}

	// Get Balance of an array of bitcoin addresses that you control
	public Coin getCurrentBalance(ArrayList<Address> addresses) { 
		App.getKit().wallet().setCoinSelector(new MultipleCoinSelector(addresses));
		return App.getKit().wallet().getBalance();
	}

	// Get Balance of one bitcoin address that you control
	public Coin getCurrentBalance(Address address) {
		App.getKit().wallet().setCoinSelector(new IndividualCoinSelector(address));
		return App.getKit().wallet().getBalance();
	}
}

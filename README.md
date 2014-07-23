TermWallet
==========

**Still in development, and although it works it is in rough shape, so don't trust with your bitcoins!**

An easy-to-use, bitcoinj-based wallet accessible through the command line.  Because it uses bitcoinj it doesn't need to download the blockchain.  Small servers with limited hard-drive space are thus the ideal use-case for TermWallet - you can run it with only terminal access.

TermWallet includes a novel adaption of bitcoinj's CoinSelector method, IndividualCoinSelector.  Previously, bitcoinj (and subsequently bitcoinj-based wallets such as Multibit) have not been able to create transactions from a single address's outputs.  IndividualCoinSelector provides this capability. Test it out with the `send [to] [from] [amount]` command, where `[from]` is an address in your wallet.


Instructions
============

You Need:
Maven
Java 1.6 (bitcoinj requirement)

Compile with `mvn package appassembler:assemble`.

To install, move 'termwallet' and the 'target' folder to /usr/local/bin (or your folder of preference).

To run, execute 'termwallet <command>' in the terminal.


Commands
========

help, Prints this

status, Prints overview of your wallet.  Includes your addresses and their balances.

newaddress, Add a new, random address to your wallet

add [privkey], Adds designated private key to your wallet

send [to] [satoshis], send a new transaction. Chooses from all outputs in your wallet

send [to] [from] [satoshis], send a new transaction. Designating the 'from' address limits output selection to only that address

listen, Listen passively for new transactions

encrypt, You will be prompted for a password (make sure it is long and secure) to encrypt the private keys in your wallet with

decrypt, You will be prompted for your encryption password.  You must decrypt before sending a transaction (and ideally encrypt after you send the transaction)

export, Export all keys associated with your wallet.  You must decrypt your wallet first if you want your private keys

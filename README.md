TermWallet
==========

An easy-to-use, bitcoinj-based wallet accessible through the command line.  It doesn't need to download the whole blockchain, and the blockchain headers only take up about a megabyte of space.  Small servers with limited hard-drive space are thus the ideal use-case for TermWallet.  You can run it with only terminal access.

TermWallet includes a novel adaption of bitcoinj's CoinSelector method, IndividualCoinSelector.  Previously, bitcoinj (and subsequently bitcoinj-based wallets such as Multibit) have not been able to create transactions from a single address's outputs.  IndividualCoinSelector provides this capability. 

Further, TermWallet provides a 'panic' command that can send all the BTC's in the wallet to an external address and then delete the wallet (including all files). This should be used if the computer is compromised or at risk.

Instructions
============

You Need:
Java 1.6 (bitcoinj requirement)

To change from testnet to mainnet, open Constants.java and change `params` to `MainNetParams.get()`.

Compile with `mvn package appassembler:assemble`.

To install, move 'termwallet' and the 'target' folder to /usr/local/bin (or your folder of preference).

To run, execute 'termwallet <command>' in the terminal.


Usage
========
```
Usage: <main class> [options] [command] [command options]
  Options:
    -h, --help
       
       Default: false
    -l, --listen
       Passively listen for transactions
       Default: false
    -t, --tor
       Use Tor to connect to bitcoin network
       Default: false
    -v, --verbose
       Verbose output
       Default: false
  Commands:
    status      Prints wallet addresses and balances
      Usage: status [options]

    send        Add file contents to the index
      Usage: send [options]
        Options:
        * -a, --amount
             Amount to Send (in BTC)
          -c, --change
             Change address
          -f, --from
             Address to send from
        * -t, --to
             Address to send to

    encrypt      Encrypt private keys and seed in wallet
      Usage: encrypt [options]

    decrypt      Decrypt private keys and seed in wallet
      Usage: decrypt [options]

    export      Export your wallet
      Usage: export [options]

    maintenance      Export your wallet
      Usage: maintenance [options]

    import      Add a private key to your wallet
      Usage: import [options]

    delete      Remove a key from your watched addresses - destroy imported private keys
      Usage: delete [options]
        Options:
        * -a, --address
             Address to delete

    panic      Send all BTCs in wallet to address, optionally delete wallet
      Usage: panic [options]
        Options:
          -sd, --self-destruct
             Self-Destruct; Erase all private keys and wallet file
             Default: false
          -t, --to
             Address to send to

    new      Create a new address with purpose 'change' or 'receive', defaults to 'receive'
      Usage: new [options]
        Options:
          -p, --purpose
             Address purpose
             Default: RECEIVE_FUNDS
```

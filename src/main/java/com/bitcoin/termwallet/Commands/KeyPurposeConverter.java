package com.bitcoin.termwallet;

import org.bitcoinj.wallet.KeyChain.KeyPurpose;

import com.beust.jcommander.IStringConverter;

public class KeyPurposeConverter implements IStringConverter<KeyPurpose> {
	@Override
	public KeyPurpose convert(String value) {
		if(value.equalsIgnoreCase("receive")) {
			return KeyPurpose.RECEIVE_FUNDS;
		}

		if(value.equalsIgnoreCase("change")) {
			return KeyPurpose.CHANGE;
		}
		return KeyPurpose.RECEIVE_FUNDS;
	}
}


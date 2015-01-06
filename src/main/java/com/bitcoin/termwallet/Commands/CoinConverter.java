package com.bitcoin.termwallet;

import org.bitcoinj.core.Coin;
import com.beust.jcommander.IStringConverter;

public class CoinConverter implements IStringConverter<Coin> {
	@Override
	public Coin convert(String value) {
		try {
			return Coin.parseCoin(value);
		} catch(Exception e) {
			System.out.println("Error parsing address " + value);
			e.printStackTrace();
			return null;
		}
	}
}

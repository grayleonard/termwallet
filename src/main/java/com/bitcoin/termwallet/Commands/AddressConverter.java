package com.bitcoin.termwallet;

import org.bitcoinj.core.Address;
import com.beust.jcommander.IStringConverter;

public class AddressConverter implements IStringConverter<Address> {
	@Override
	public Address convert(String value) {
		try {
			return new Address(Constants.params, value);
		} catch(Exception e) {
			System.out.println("Error parsing address " + value);
			e.printStackTrace();
			return null;
		}
	}
}

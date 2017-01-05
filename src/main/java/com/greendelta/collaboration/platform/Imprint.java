package com.greendelta.collaboration.platform;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class Imprint {

	public final String company;
	public final String ceo;
	public final String street;
	public final String zipCode;
	public final String city;
	public final String country;
	public final String phone;
	public final String fax;
	public final String email;
	public final String website;
	public final String registration;
	public final String vat;

	@Inject
	public Imprint(@Named("imprint.company") String company,
			@Named("imprint.ceo") String ceo,
			@Named("imprint.street") String street,
			@Named("imprint.zipCode") String zipCode,
			@Named("imprint.city") String city,
			@Named("imprint.country") String country,
			@Named("imprint.phone") String phone,
			@Named("imprint.fax") String fax,
			@Named("imprint.email") String email,
			@Named("imprint.website") String website,
			@Named("imprint.registration") String registration,
			@Named("imprint.vat") String vat) {
		this.company = company;
		this.ceo = ceo;
		this.street = street;
		this.zipCode = zipCode;
		this.city = city;
		this.country = country;
		this.phone = phone;
		this.fax = fax;
		this.email = email;
		this.website = website;
		this.registration = registration;
		this.vat = vat;
	}

}

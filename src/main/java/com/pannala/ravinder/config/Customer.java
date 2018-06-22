package com.pannala.ravinder.config;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Customer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	private int id;
	@Column(name="H_NO")
	private String hNo;
	private String contactPerson;
	private String customerNumber;
	private String postcode;
	private String cityName;
	private String country;
	private String telephoneNumber;
	private String emailAddress;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String gethNo() {
		return hNo;
	}

	public void sethNo(String hNo) {
		this.hNo = hNo;
	}

	public String getCustomerNumber() {
		return customerNumber;
	}

	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public String getTelephoneNumber() {
		return telephoneNumber;
	}

	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}


	public Customer(int id, String hNo, String contactPerson, String customerNumber, String postcode, String cityName,
			String country, String telephoneNumber, String emailAddress) {
		super();
		this.id = id;
		this.hNo = hNo;
		this.contactPerson = contactPerson;
		this.customerNumber = customerNumber;
		this.postcode = postcode;
		this.cityName = cityName;
		this.country = country;
		this.telephoneNumber = telephoneNumber;
		this.emailAddress = emailAddress;
	}

	public Customer() {
		super();
	}

}

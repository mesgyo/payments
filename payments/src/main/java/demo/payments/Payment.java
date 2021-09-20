package demo.payments;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="payment")
public class Payment {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private int id;
	
	@Column(name="UserID")
	private String userID;
	
	@Column(name="UnixTimestamp")
	private long unixTimestamp;
	
	@Column(name="Country")
	private String country;
	
	@Column(name="Currency")
	private String currency;
	
	@Column(name="AmountInCents")
	private int amountInCents;
	
	@Column(name="AmountInEuro")
	private double amountInEuro;
	
	public Payment(String userID, long unixTimestamp, String country, String currency, int amountInCents, double amountInEuro) {
		this.userID = userID;
		this.unixTimestamp = unixTimestamp;
		this.country = country;
		this.currency = currency;
		this.amountInCents = amountInCents;
		this.amountInEuro = amountInEuro;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public long getUnixTimestamp() {
		return unixTimestamp;
	}

	public void setUnixTimestamp(long unixTimestamp) {
		this.unixTimestamp = unixTimestamp;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public int getAmountInCents() {
		return amountInCents;
	}

	public void setAmountInCents(int amountInCents) {
		this.amountInCents = amountInCents;
	}

	public double getAmountInEuro() {
		return amountInEuro;
	}

	public void setAmountInEuro(double amountInEuro) {
		this.amountInEuro = amountInEuro;
	}	
}


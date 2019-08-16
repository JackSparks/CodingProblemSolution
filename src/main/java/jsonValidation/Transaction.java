package jsonValidation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.TimeZone;

public class Transaction {

	private String id;
	private String customer_id;
	private String time;
	private double load_amount;

	public Transaction() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(String customer_id) {
		this.customer_id = customer_id;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String date) {
		this.time = date;
	}

	public double getLoad_amount() {
		return load_amount;
	}

	public void setLoad_amount(double d) {
		this.load_amount = d;
	}

}

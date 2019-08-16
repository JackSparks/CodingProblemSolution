package jsonValidation;

import java.awt.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

public class Validater {

	Map<String, ArrayList> map = new HashMap<String, ArrayList>();

	public Validater() {

	}

	// performs validation on each transaction
	public boolean validate(Transaction transaction) {

		Double load_amount_today = (double) 0;
		Double load_amount_this_week = (double) 0;
		int number_of_loads_today = 0;

		// if there is no customer yet existing, add it.
		if (map.get(transaction.getCustomer_id()) == null) {

			// don't bother adding the transaction since its not a valid transaction because
			// the amount is too high...
			// in reality we'd probably still add the customer though, just ignore the
			// transaction...but its an interview and time is short
			if (transaction.getLoad_amount() > 5000) {
				return false;
			}

			load_amount_today += transaction.getLoad_amount();
			load_amount_this_week += transaction.getLoad_amount();
			number_of_loads_today += 1;

			// creates a list of customer transactions
			ArrayList customerTransactions = new ArrayList();
			customerTransactions.add(transaction);

			// adds the customer transaction
			map.put(transaction.getId(), customerTransactions);

			// customer already exists in system and has previous transactions
		} else {

			if (transaction.getLoad_amount() > 5000) {
				return false;
			}

			ArrayList<Transaction> customerTransactions = map.get(transaction.getCustomer_id());

			// using the customerTransactions, we want to look at the past load amounts and
			// times that transactions were held in the past
			// using that info we can determine whether a transaction should be added or not

			Calendar cal1 = Calendar.getInstance();
			Calendar cal2 = Calendar.getInstance();

			Date date = java.sql.Date.valueOf(transaction.getTime());
			cal1.setTime(date);

			// Looks at transactions and takes metrics that will later be used to determine
			// if the transaction can be processed or not.
			// metrics being how large a load amount and number of times
			for (Transaction t : customerTransactions) {
				Date date2 = java.sql.Date.valueOf(t.getTime());
				cal2.setTime(date2);

				// checks if transactions are on the same day
				boolean sameDay = cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
						&& cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);

				if (sameDay) {
					load_amount_today += transaction.getLoad_amount();
					load_amount_this_week += transaction.getLoad_amount();
					number_of_loads_today += 1;
				}

				boolean sameWeek = cal1.get(Calendar.DAY_OF_WEEK) == cal2.get(Calendar.DAY_OF_WEEK)
						&& cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);

				if (sameWeek) {
					load_amount_this_week += transaction.getLoad_amount();
				}

			}

			// performs validation checks
			if (load_amount_today > 5000) {
				return false;
			}

			if (load_amount_this_week > 20000) {
				return false;
			}

			if (number_of_loads_today > 2) {
				return false;
			}

			customerTransactions.add(transaction);

			// adds the customer transaction
			map.put(transaction.getId(), customerTransactions);

		}

		return true;
	}
}

/*
 * The purpose of this program is as follows:
 * 
 * In finance, it's common for accounts to have so-called "velocity limits". In this task, you'll write a program that accepts or declines attempts to load funds into customers' accounts in real-time.

Each attempt to load funds will come as a single-line JSON payload, structured as follows:

```json
{ "id": "1234", "customer_id": "1234", "load_amount": "$123.45", "time": "2018-01-01T00:00:00Z" }
```

Each customer is subject to three limits:

- A maximum of $5,000 can be loaded per day
- A maximum of $20,000 can be loaded per week
- A maximum of 3 loads can be performed per day, regardless of amount

As such, a user attempting to load $3,000 twice in one day would be declined on the second attempt, as would a user attempting to load $400 four times in a day.

For each load attempt, you should return a JSON response indicating whether the fund load was accepted based on the user's activity, with the structure:

```json
{ "id": "1234", "customer_id": "1234", "accepted": true }
```

You can assume that the input arrives in ascending chronological order and that if a load ID is observed more than once for a particular user, all but the first instance can be ignored. Each day is considered to end at midnight UTC, and weeks start on Monday (i.e. one second after 23:59:59 on Sunday).

Your program should process lines from `input.txt` and return output in the format specified above, either to standard output or a file. Expected output given our input data can be found in `output.txt`. 

You're welcome to write your program in a general-purpose language of your choosing, but as we use Go we do have a preference towards solutions written in it.

We value well-structured, self-documenting code with sensible test coverage. Descriptive function and variable names are appreciated, as is isolating your business logic from the rest of your code.

 */

package jsonValidation;

import java.awt.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONValidation {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException, ParseException, java.text.ParseException {

		/////////////////
		JSONParser jsonParser = new JSONParser();
		try {

			// for each json object, do an operation on the json object
			BufferedReader br = new BufferedReader(new FileReader("input.txt"));
			String line = null;
			while ((line = br.readLine()) != null) {
				line = br.readLine();

				// One validater needs to be used throughout (singleton class) because it
				// contains
				// records of all the transactions.
				// in reality you'd want to use an SQL database with all the records, but its an
				// interview and I just need this to run for now
				Validater validater = new Validater();

				Object obj = jsonParser.parse(line);
				parseTransactionObject((JSONObject) obj, validater);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	// receives a JSON object like the following
	// { "id": "1234", "customer_id": "1234", "load_amount": "$123.45", "time":
	// "2018-01-01T00:00:00Z" }
	private static void parseTransactionObject(JSONObject transaction_record, Validater validater)
			throws java.text.ParseException

	{

		Transaction transaction = new Transaction();
		// Get employee object within list
		// JSONObject employeeObject = (JSONObject) employee.get("employee");

		String id = (String) transaction_record.get("id");
		transaction.setId(id);
		// System.out.println(id);

		String customer_id = (String) transaction_record.get("customer_id");
		transaction.setCustomer_id(customer_id);
		// System.out.println(customer_id);

		String load_amount = (String) transaction_record.get("load_amount");
		// removes dollar sign...that took me a while to figure out this was messing up
		// the conversion to double
		load_amount = load_amount.substring(1);
		try {

			double load_amount_value = Double.parseDouble(load_amount);
			transaction.setLoad_amount(load_amount_value);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		transaction.setTime((String) transaction_record.get("time"));

		// validates the transaction
		try {
			validationOutput(id, customer_id, validater.validate(transaction));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// receives a some values and will output a JSON object like the following into
	// the output file:
	// { "id": "1234", "customer_id": "1234", "accepted": true }
	@SuppressWarnings("unchecked")
	private static void validationOutput(String id, String customer_id, boolean accepted) throws FileNotFoundException {

		JSONObject transactionVerdict = new JSONObject();

		transactionVerdict.put("accepted", accepted);
		transactionVerdict.put("customer_id", customer_id);
		transactionVerdict.put("id", id);

		// Write JSON file with output

		try (FileWriter fw = new FileWriter("output.txt", true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {
			out.println(transactionVerdict.toJSONString());
		} catch (IOException e) {
		}
	}
}
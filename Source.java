import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

public class Source {

	/**
	 * This method loops through the given CSV file and retrieves all quotes from
	 * it. It then returns an Arraylist
	 */
	public static ArrayList<String> getQuotesFromTextFile(String file_name) {
		ArrayList<String> list_quotes = new ArrayList<String>();
		try {
			File file = new File(file_name);
			Scanner reader = new Scanner(file);
			while (reader.hasNextLine()) {
				String line = reader.nextLine();
				System.out.println(line);
				String[] array = line.split(",");
				String quote = array[2];
				if (line.contains("\"")) {
					String[] array2 = line.split("\"");
					quote = array2[1];
					quote = quote.replaceAll("\\s", "");
					quote = quote.replaceAll("[\\p{Punct}]", "");
					list_quotes.add(quote);
				} else {
					quote = quote.replaceAll("\\s", "");
					quote = quote.replaceAll("[\\p{Punct}]", "");
					list_quotes.add(quote);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return list_quotes;
	}

	/**
	 * @author AutoCap This method establishes a connection with the database and
	 *         inquires the user for two parameters that must be provide for the
	 *         script to work properly. Once completed the method returns an
	 *         arraylist back to the Puzzle class.
	 *
	 */

	public static ArrayList<String> getQuotesFromDatabase(String statementExe) throws SQLException {

		ArrayList<String> phrase_from_db = new ArrayList<String>();

		Connection connect = null;

		String database = "quotes_db";
		String url = "jdbc:mysql://localhost/" + database;
		String username = "root";
		String password = "";

		try {
			connect = DriverManager.getConnection(url, username, password);

			Statement statement = connect.createStatement();

			ResultSet results = statement.executeQuery(statementExe);

			while (results.next()) {
				String quote_temp = results.getString("quote");
				String quote = quote_temp.replaceAll("\\s", "");
				quote = quote.replaceAll("[\\p{Punct}]", "");
				phrase_from_db.add(quote);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return phrase_from_db;
	}

	public static void printQuotes(ArrayList<String> list_of_quotes) {
		System.out.println("Number of Quotes: " + list_of_quotes.size());

		for (String x : list_of_quotes) {
			System.out.println(x);
		}
	}

	/**
	 * A wrapper method that returns a list of quotes either from a DATABASE or from
	 * a TEXTFILE based on a preference value
	 * 
	 * @return
	 */

	public static ArrayList<String> getQuotes() throws Exception {

		ArrayList<String> list_quotes = new ArrayList<String>();
		String data_source = Preferences.SOURCE;

		if (data_source.equalsIgnoreCase("DATABASE")) {
			Scanner scan = new Scanner(System.in);

			System.out.println("Enter the starting ID: ");
			int start_id = scan.nextInt();

			System.out.println("Enter the ending ID: ");
			int end_id = scan.nextInt();

			String statement = "SELECT quote FROM quote_table WHERE id BETWEEN " + start_id + " AND " + end_id;
			list_quotes = Source.getQuotesFromDatabase(statement);
			System.out.println("Fetched the quotes from Database!\n");
			// Source.printQuotes(list_quotes);
		} else {
			System.out.println("Fetching the quotes from Text File!\n");
			String txtfile_name = Preferences.TEXT_FILE_NAME;
			list_quotes = Source.getQuotesFromTextFile(txtfile_name);
			System.out.println("Number of quotes: " + list_quotes.size());
			System.out.println("Fetched the quotes from the Text File!\n");
			// Source.printQuotes(list_quotes);
		}

		return list_quotes;

	}
}

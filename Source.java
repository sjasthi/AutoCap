import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

public class Source {

	/**
	 * @author AutoCap
	 * This method loops through the given CSV file and retrieves all quotes from the from it to then return an arraylist back to the Puzzle class.
	 * 
	 */
	public static ArrayList<String> getQuotesFromtxt(String file_name) {
		ArrayList<String> list_quotes = new ArrayList<String>();
		try {
			File file = new File(file_name);
			Scanner reader = new Scanner(file);
			while (reader.hasNextLine()) {
				String line = reader.nextLine();
				String[] array = line.split(",");
				String quote = array[3];
				if (line.contains("\"")) {
					String[] array2 = line.split("\"");
					quote = array2[1];
					quote = quote.replaceAll("\\s",""); 
					quote = quote.replaceAll("[\\p{Punct}]", "");
					list_quotes.add(quote);
				}
				else {
					quote = quote.replaceAll("\\s",""); 
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
	 * @author AutoCap
	 * This method establishes a connection with the database and inquires the user for two parameters that must be provide for the
	 * script to work properly. Once completed the method returns an arraylist back to the Puzzle class.
	 *
	 */
	
	public static ArrayList<String> connect_to_db() throws SQLException {

		ArrayList<String> phrase_from_db = new ArrayList<String>();

		Connection connect = null;

		String database = "quotes_db_2";
		String url = "jdbc:mysql://localhost/" + database;
		String username = "root";
		String password = "";

		Puzzle user = new Puzzle();
		int start_ID = user.getStartID();
		int end_ID = user.getEndID();

		try {
			connect = DriverManager.getConnection(url, username, password);

			System.out.println("Connected to Database!\n");

			Statement statement = connect.createStatement();

			ResultSet results = statement
					.executeQuery("SELECT quote FROM quote_table WHERE id BETWEEN " + start_ID + " AND " + end_ID);

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
}

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
	
	public static ArrayList<String> getQuotesFromtxt(String file_name) {
		ArrayList<String> list_quotes = new ArrayList<String>();
		try {
			File file = new File(file_name);
			Scanner reader = new Scanner(file);
			while (reader.hasNextLine()) {
				String storethis = reader.nextLine();
				String[] array = storethis.split(",");
				String quote1 = array[3];
				String quote = quote1.replaceAll("\\s",""); 
				quote = quote.replaceAll("[\\p{Punct}]", "");
				list_quotes.add(quote);
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		return list_quotes;
	}
	
	public static ArrayList<String> connect_to_db() throws SQLException {

		ArrayList<String> phrase_from_db = new ArrayList<String>();

		Connection connect = null;
		
		String database = "quotes_db";
		String url = "jdbc:mysql://localhost/" + database;
		String username = "root";
		String password = "";
		
		Puzzle user = new Puzzle();
		int start_ID = user.getStartID();
		int end_ID = user.getEndID();

		try {
			connect = DriverManager.getConnection(url, username, password);

			System.out.println("Connected!");

			Statement statement = connect.createStatement();

			ResultSet results = statement
					.executeQuery("SELECT quote FROM quote_table WHERE id BETWEEN " + start_ID + " AND " + end_ID);

			while (results.next()) {
				String quote_temp = results.getString("quote");
				String quote = quote_temp.replaceAll("\\s",""); 
				quote = quote.replaceAll("[\\p{Punct}]", "");
				phrase_from_db.add(quote);
			}
			
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return phrase_from_db;
	}
}


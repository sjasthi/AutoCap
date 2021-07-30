import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class quotes_DB {

	public static ArrayList<String> connect_to_db() throws SQLException {

		ArrayList<String> phrase_from_db = new ArrayList<String>();

		Connection connect = null;
		
		String database = "quotes_db";
		String url = "jdbc:mysql://localhost/" + database;
		String username = "root";
		String password = "";
		
		Iteration7 user = new Iteration7();
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

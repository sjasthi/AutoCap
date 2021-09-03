import java.sql.SQLException;
import java.util.ArrayList;

public class Analyzer {

	/**
	 * This method prints the limits of AutoCap application
	 */
	public void printSystemLimits() {
		System.out.println("System Limits:");

		System.out.println("Grid Size (Column x Row) | Max Phrase Length");
		System.out.println("        16x12 (DEFAULT)          74        ");
		System.out.println("        18x12                    83        ");
		System.out.println("        20x12                    88        ");
		System.out.println("        16x14                    85        ");
		System.out.println("        18x14                    90        ");
		System.out.println("        20x14                    100        ");
		System.out.println("        16x16                    100        ");
		System.out.println("        18x16                    102        ");
		System.out.println("        20x16                    105        ");
		System.out.println("        16x18                    106        ");
		System.out.println("        18x18                    107        ");
		System.out.println("        20x18                    112        ");
	}

	/**
	 * @author AutoCap This method grabs the length of the longest quote from the
	 *         connected database and passes it into suggestion method.
	 * 
	 */
	public int getMaxLength(String txtfile_name) throws SQLException {

		ArrayList<String> quote_array = new ArrayList<String>();

		String source = Preferences.SOURCE;

		if (source.equalsIgnoreCase("TEXTFILE")) {
			quote_array = Source.getQuotesFromTextFile(txtfile_name);
		} else {
			String statement = "SELECT quote FROM quote_table";
			quote_array = Source.getQuotesFromDatabase(statement);
		}

		int max_length = 0;

//		for (int i = 0; i < quote_array.size(); i++) {
//			if (quote_array.get(i).length() > max_length) {
//				max_length = quote_array.get(i).length();
//			}
//		}

		for (String quote : quote_array) {
			int q_length = quote.length();
			if (q_length > max_length) {
				max_length = q_length;
			}
		}

		return max_length;
	}

	/**
	 * @author AutoCap This method collects a list of quotes that exceed the tested
	 *         amount of tested quote sizes and returns it.
	 * 
	 */

	public ArrayList<String> getBadQuotes(String txtfile_name) throws SQLException {
		ArrayList<String> arrayBadQuotes = new ArrayList<String>();

		ArrayList<String> quote_array = new ArrayList<String>();

		String data_source = Preferences.SOURCE;

		if (data_source.equalsIgnoreCase("DATABASE")) {
			String statement = "SELECT quote FROM quote_table";
			quote_array = Source.getQuotesFromDatabase(statement);
		} else {
			quote_array = Source.getQuotesFromTextFile(txtfile_name);
		}

		for (int i = 0; i < quote_array.size(); i++) {
			if (quote_array.get(i).length() > 74) {
				String quote = quote_array.get(i);
				arrayBadQuotes.add(quote);
			}
		}

		return arrayBadQuotes;
	}

	/**
	 * @author AutoCap This method suggest's the optimal grid sizes for the user to
	 *         use when generating.
	 * 
	 */
	public void getSuggestion(int max_length) {
		System.out.println("Grid Size Suggested: ");

		if (max_length > 74 && max_length <= 112) {
			System.out.println("18x12\n20x12\n16x14\n18x14\n20x14\n16x16\n18x16\n20x16\n16x18\n18x18\n20x18");
		} else {
			System.out.println(
					"Your Max Phrase Length is over 112 characters! Max Grid Size: 20x18 (Supports up to 112 characters)");
		}
	}

	/**
	 * main method for the Analyzer
	 */
	public static void main(String[] args) throws Exception {

		String txtfile_name = "quotes_table.csv";

		Analyzer analyzer = new Analyzer();
		int max_length = analyzer.getMaxLength(txtfile_name);
		System.out.println("Max Phrase Length from your SOURCE: " + max_length + "\n");
		analyzer.printSystemLimits();
		System.out.println();

		analyzer.getSuggestion(max_length);
		System.out.println();

		ArrayList<String> badQuotesArray = analyzer.getBadQuotes(txtfile_name);
		System.out.println(badQuotesArray.size());
		System.out.println("Default grid size CAN NOT accommodate these phrases: ");
		// Print the list of quotes that can not be accommodated
		for (String line : badQuotesArray) {
			System.out.println(line);
		}
		System.out.println();

	}

}

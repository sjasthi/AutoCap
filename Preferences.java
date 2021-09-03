
public class Preferences {

	/*
	 * Notes for default table size of 16x12: Good table size: table_width = 30
	 * table_height = 30 place in middle of power-point coordinates: tableMoveX =
	 * 140 tableMoveY = 130
	 */

	// side_label_width should stay at 30 at all times in order to not overlap with
	// table
	// or else table X and Y coordinates will have to be adjusted.

	// side_label_width should stay at 30 at all times in order to not overlap with
	// table
	// or else table X and Y coordinates will have to be adjusted.

	// Source can be either DATABASE or TEXTFILE
	// public final static String SOURCE = "DATABASE";
	public final static String SOURCE = "TEXTFILE";
	public final static String TEXT_FILE_NAME = "autocap_quotes.csv";

	// Default number of rows for the puzzle grid
	public static int NO_OF_ROWS = 12;

	// Default number of columns for the puzzle grid
	public static int NO_OF_COLUMNS = 16;

	// What is the maximum length this app can accommodate
	public static int MAX_LENGTH_OF_PHRASE = 100;

	// Power Point Rendering Preferences
	public static String PPT_FILE_NAME = "Catch_A_Phrase" + System.currentTimeMillis() + ".ppt";
	public static int ROW_HEIGHT = 30;
	public static int COL_WIDTH = 42;
	public static int SIDE_LABEL_WIDTH = 30; // what is this?
	public static String FONT_NAME = "NATS";
	public static double GRID_FONT_SIZE = 18.0;
	public static double TITLE_FONT_SIZE = 24.0;
	public static boolean SHOW_LABELS = true;
	public static boolean SHOW_BORDERS = true;
	public static int STARTING_X = 40;
	public static int STARTING_Y = 110;

	// the following are set to true are for demo purposes
	// Typically, these would come from a master slide
	// Hence, these would normally be set to false
	public static boolean RENDER_LOGO = true;
	public static boolean RENDER_SLIDE_NUMBER = true;
	public static boolean RENDER_TITLE = true;

	// TBD: This should be fetched from the text file or database
	// For now, it is hard-coded.
	public static String PUZZLE_TITLE = "యండమూరి వీరేంద్రనాథ్";

}

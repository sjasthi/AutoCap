import java.awt.Color;
import java.awt.Desktop;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import org.apache.poi.hslf.usermodel.HSLFFill;
import org.apache.poi.hslf.usermodel.HSLFLine;
import org.apache.poi.hslf.usermodel.HSLFPictureData;
import org.apache.poi.hslf.usermodel.HSLFPictureShape;
import org.apache.poi.hslf.usermodel.HSLFSlide;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFTable;
import org.apache.poi.hslf.usermodel.HSLFTableCell;
import org.apache.poi.hslf.usermodel.HSLFTextBox;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.hslf.usermodel.HSLFTextRun;
import org.apache.poi.sl.usermodel.TableCell.BorderEdge;
import org.apache.poi.sl.usermodel.TextParagraph.TextAlign;
import org.apache.poi.sl.usermodel.VerticalAlignment;
import org.apache.poi.util.IOUtils;

/**
 * @author AutoCap (Xeng Xiong and Seng Khang) The purpose of this program is to
 *         mass generate a snake puzzle in power point. There will be two slides
 *         generated for one puzzle (puzzle and solution). The program will work
 *         for CSV files and a database (myPHPadmin), parameters must be
 *         adjusted accordingly - CSV file must be located in the project
 *         folder, will only work with the given format
 *         "ID,Author,Category,Quote". - Database must be up and running, please
 *         adjust any difference in naming accordingly. - PowerPoint must be
 *         closed out before running the program else the program throws an
 *         error. - When running the code existing files with the same name and
 *         type will be overwritten please backup any mass generated puzzle
 *         accordingly.
 *
 */

public class Driver {

	// side_label_width should stay at 30 at all times
	// in order to not overlap with table
	// or else table X and Y coordinates will have to be adjusted.
	private final static int tableMoveX = Preferences.STARTING_X;
	private final static int tableMoveY = Preferences.STARTING_Y;
	private final static int table_width = Preferences.COL_WIDTH;
	private final static int table_height = Preferences.ROW_HEIGHT;
	private final static int side_label_width = Preferences.SIDE_LABEL_WIDTH;
	private final static double table_fontSize = Preferences.GRID_FONT_SIZE;

	private static int num_column = Preferences.NO_OF_COLUMNS;
	private static int num_row = Preferences.NO_OF_ROWS;
	private static int char_limit = Preferences.MAX_LENGTH_OF_PHRASE;

	/**
	 * @author AutoCap This method sets the the color of a the cells.
	 */

	public static void setBorders(HSLFTableCell cell) {
		cell.setBorderColor(BorderEdge.bottom, Color.black);
		cell.setBorderColor(BorderEdge.top, Color.black);
		cell.setBorderColor(BorderEdge.right, Color.black);
		cell.setBorderColor(BorderEdge.left, Color.black);
	}

	/**
	 * Method for setting the background. Not used!
	 */

	public static void changeBackground(HSLFSlideShow ppt, HSLFSlide slide) throws IOException {
		slide.setFollowMasterBackground(false);
		HSLFFill fill = slide.getBackground().getFill();
		HSLFPictureData pd = ppt.addPicture(new File("white.png"), HSLFPictureData.PictureType.PNG);
		fill.setFillType(2);
		fill.setPictureData(pd);
	}

	/**
	 * Method for setting the footer. Not used!
	 */
	public static void createFooter(HSLFSlide slide, String name) {
		HSLFTextBox footer = slide.createTextBox();
		HSLFTextParagraph p = footer.getTextParagraphs().get(0);
		p.setTextAlign(TextAlign.CENTER);
		footer.setFillColor(Color.green);
		HSLFTextRun r = p.getTextRuns().get(0);
		r.setText("www.telugupuzzles.com\nusername: " + name);
		r.setFontFamily(Preferences.FONT_NAME);
		r.setFontSize(Preferences.GRID_FONT_SIZE);
		footer.setAnchor(new Rectangle(0, 490, 720, 50));
	}

	/**
	 * @author AutoCap This method creates and controls the title text box that runs
	 *         across each slide.
	 */

	public static void createTitle(HSLFSlide slide, String puzzleName) {
		if (Preferences.RENDER_TITLE) {
			HSLFTextBox title = slide.createTextBox();
			HSLFTextParagraph p = title.getTextParagraphs().get(0);
			p.setTextAlign(TextAlign.CENTER);
			HSLFTextRun r = p.getTextRuns().get(0);
			// r.setBold(true);
			r.setFontColor(Color.black);
			r.setText(puzzleName.toUpperCase());
			r.setFontFamily(Preferences.FONT_NAME);
			r.setFontSize(Preferences.TITLE_FONT_SIZE);
			title.setAnchor(new Rectangle(240, 10, 400, 200));
		}
	}

	/**
	 * @author AutoCap This method creates and controls the slide numbers that are
	 *         in each slide.
	 */

	public static void renderSlideNumber(HSLFSlide slide, int slide_num) {
		if (Preferences.RENDER_SLIDE_NUMBER) {
			HSLFTextBox slide_number = slide.createTextBox();
			HSLFTextParagraph p = slide_number.getTextParagraphs().get(0);
			p.setTextAlign(TextAlign.CENTER);
			// slide_number.setFillColor(Color.green);
			HSLFTextRun r = p.getTextRuns().get(0);
			r.setText("" + slide_num + "");
			r.setFontFamily(Preferences.FONT_NAME);
			r.setFontSize(30.);
			slide_number.setAnchor(new Rectangle(220, 10, 50, 30));
			// create text box lines
			createLine(slide, 220, 5, 50, 0); // top line
			createLine(slide, 270, 5, 0, 50); // right line
			createLine(slide, 220, 55, 50, 0); // bottom line
			createLine(slide, 220, 5, 0, 50); // left line
		}
	}

	/**
	 * @author AutoCap This method gives the illusion of the slide number having a
	 *         box with a black border.
	 */

	public static void createLine(HSLFSlide slide, int x, int y, int width, int height) {
		HSLFLine line = new HSLFLine();
		line.setAnchor(new Rectangle(x, y, width, height));
		line.setLineColor(Color.black);
		slide.addShape(line);
	}

	/**
	 * @author AutoCap This method inserts the "logo.png" into the top left corner
	 *         of every puzzle's slide.
	 */

	public static void renderLogo(HSLFSlideShow ppt, HSLFSlide slide) throws IOException {
		if (Preferences.RENDER_LOGO == true) {
			byte[] picture = IOUtils.toByteArray(new FileInputStream(new File("logo.png")));
			HSLFPictureData pd = ppt.addPicture(picture, HSLFPictureData.PictureType.PNG);
			HSLFPictureShape pic_shape = slide.createPicture(pd);
			pic_shape.setAnchor(new Rectangle(0, 0, 174, 65));
		}
	}

	/**
	 * @author AutoCap This method generates and controls the top and side labels of
	 *         the grid.
	 */

	public static void getLabels(HSLFSlide slide, int num_row, int num_column) {
		String[] top_label = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
				"S", "T", "U", "V", "W", "X", "Y", "Z" };

		String[] side_label = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16",
				"17", "18", "19", "20", "21", "22", "23", "24", "25", "26" };

		// create labels
		HSLFTable top_row = slide.createTable(1, num_column);
		HSLFTable side_row = slide.createTable(num_row, 1);

		for (int i = 0; i < num_row; i++) {
			// side column labels
			HSLFTableCell side_cell = side_row.getCell(i, 0);
			side_cell.setText(side_label[i]);
			setBorders(side_cell);
			HSLFTextRun rts1 = side_cell.getTextParagraphs().get(0).getTextRuns().get(0);
			rts1.setFontFamily(Preferences.FONT_NAME);
			rts1.setFontSize(table_fontSize - 5); // labels' font size are 5 less than table font size
			rts1.setBold(true);
			side_cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
			side_cell.setHorizontalCentered(true);

			for (int j = 0; j < num_column; j++) {
				// top row labels
				HSLFTableCell top_cell = top_row.getCell(0, j);
				top_cell.setText(top_label[j]);
				setBorders(top_cell);
				HSLFTextRun rt2s1 = top_cell.getTextParagraphs().get(0).getTextRuns().get(0);
				rt2s1.setFontFamily(Preferences.FONT_NAME);
				rt2s1.setFontSize(table_fontSize - 5);
				rt2s1.setBold(true);
				top_cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
				top_cell.setHorizontalCentered(true);
			}
		}

		for (int i = 0; i < num_column; i++) {
			top_row.setColumnWidth(i, table_width);
			side_row.setColumnWidth(0, side_label_width);
		}

		for (int i = 0; i < num_row; i++) {
			side_row.setRowHeight(i, table_height);
			top_row.setRowHeight(0, table_height);
		}

		top_row.moveTo(tableMoveX, tableMoveY - 30); // y - 20 to match table
		side_row.moveTo(tableMoveX - 30, tableMoveY); // x - 30 to match table
	}

	/**
	 * @author AutoCap This method ensures that the reordering of slides works. use
	 *         for empty slides in order to store puzzles and not be out of bound
	 */

	public static String[][] genGrid(String filler) throws UnsupportedEncodingException, SQLException {
		Random rand = new Random();
		num_row = Preferences.NO_OF_ROWS;
		num_column = Preferences.NO_OF_COLUMNS;

		API api = new API();

		// get filler characters and store in an array
		ArrayList<String> filler_array1 = api.getFillerCharacters(filler);
		String[] filler_array = filler_array1.toArray(new String[0]);

		String[][] grid = new String[num_row][num_column];

		for (int i = 0; i < num_row; i++) {
			for (int j = 0; j < num_column; j++) {
				int x = rand.nextInt(filler_array.length);
				grid[i][j] = filler_array[x];
			}
		}

		return grid;
	}

	public static String[][] genGrid(HSLFTable table, int length, boolean solution, String filler, String quote)
			throws UnsupportedEncodingException, SQLException {

		int num_row = Preferences.NO_OF_ROWS;
		int num_column = Preferences.NO_OF_COLUMNS;

		Random rand = new Random();

		API api = new API();

		// process phrase through logicalChars API and store in an array
		ArrayList<String> list_quotes1 = api.getLogicalChars(quote);
		String[] quote_char_array = list_quotes1.toArray(new String[0]);
		System.out.println("Processing phrase '" + quote + "' into API...");

		// get filler characters and store in an array
		ArrayList<String> filler_array1 = api.getFillerCharacters(filler);
		String[] filler_array = filler_array1.toArray(new String[0]);
		System.out.println("Getting fillers from API to fill the grid...\n");

		String[][] grid = new String[num_row][num_column];

		for (int i = 0; i < num_row; i++) {
			for (int j = 0; j < num_column; j++) {
				int x = rand.nextInt(filler_array.length);
				grid[i][j] = filler_array[x];
			}
		}

		int[][] locations = chooseLocations(num_row, num_column, length);

		for (int i = 0; i < length; i++) {
			int x = locations[i][0];
			int y = locations[i][1];
			grid[x][y] = quote_char_array[i];
			String char_string = String.valueOf(grid[x][y]);
			HSLFTableCell cell = table.getCell(x, y);
			cell.setText(char_string);

			if (solution == true) {
				cell.setFillColor(Color.green); // set solution to color green
			}
		}

		return grid;
	}

	/**
	 * @author AutoCap This method is the logic behind where each of the letters in
	 *         the solution is placed on the grid itself.
	 * 
	 */

	public static int[][] chooseLocations(int num_row, int num_column, int length) {
		boolean legitimatePlacement = false;
		boolean startOver = true;
		Random rand = new Random();
		int[][] locations = new int[1][2];

		// continue until a full set of legitimate placements have been found
		while (startOver) {
			startOver = false;

			locations[0][0] = (int) Math.floor(rand.nextDouble() * num_row);
			locations[0][1] = (int) Math.floor(rand.nextDouble() * num_column);

			// choose random locations for the other characters such that no character
			// touches an earlier character
			for (int i = 1; i < length; i++) {
				int[] newLocation = {};
				int placementOptions = 8;
				ArrayList<Object> badPlacements = new ArrayList<Object>();

				while (!legitimatePlacement && !startOver) {
					// pick a random location for the next character
					int placement = (int) Math.floor(rand.nextDouble() * placementOptions);

					// adjust placement based on which placements have been determined to be bad
					for (int j = 0; j < badPlacements.size(); j++) {
						if (j <= placement) {
							if (Boolean.parseBoolean(badPlacements.get(j).toString()) == true) {
								placement += 1;
							}
						}
					}

					// the location in vertical and horizontal coordinates of the grid
					newLocation = new int[2];
					if (placement < 8) {

						switch (placement) {
						case 0:
							newLocation[0] = locations[i - 1][0] - 1;
							newLocation[1] = locations[i - 1][1] - 1;
							break;
						case 1:
							newLocation[0] = locations[i - 1][0] - 1;
							newLocation[1] = locations[i - 1][1];
							break;
						case 2:
							newLocation[0] = locations[i - 1][0] - 1;
							newLocation[1] = locations[i - 1][1] + 1;
							break;
						case 3:
							newLocation[0] = locations[i - 1][0];
							newLocation[1] = locations[i - 1][1] + 1;
							break;
						case 4:
							newLocation[0] = locations[i - 1][0] + 1;
							newLocation[1] = locations[i - 1][1] + 1;
							break;
						case 5:
							newLocation[0] = locations[i - 1][0] + 1;
							newLocation[1] = locations[i - 1][1];
							break;
						case 6:
							newLocation[0] = locations[i - 1][0] + 1;
							newLocation[1] = locations[i - 1][1] - 1;
							break;
						case 7:
							newLocation[0] = locations[i - 1][0];
							newLocation[1] = locations[i - 1][1] - 1;
							break;
						}
						// check if the location chosen is legitimate (is not outside the grid or
						// touching a previous character in the phrase)
						legitimatePlacement = legitimate(locations, newLocation, num_row, num_column);
					} else {
						startOver = true;
					}

					// if the placement is not legitimate, record it as bad
					if (!legitimatePlacement) {
						if (badPlacements.size() < (placement + 1)) {
							int n = (placement + 1) - badPlacements.size();
							for (int r = 0; r < n + 1; r++) {
								badPlacements.add("false");
							}
						}

						badPlacements.set(placement, true);
						placementOptions -= 1;
					}
				}

				// if all possible locations have been checked and none of them are legitimate,
				// start over
				if (startOver) {
					locations = new int[1][2];
					break;
				}

				// when a legitimate placement is found, add it to the locations list
				if (locations.length <= i) {
					locations = ResizeArray(locations);
				}

				locations[i][0] = newLocation[0];
				locations[i][1] = newLocation[1];
				legitimatePlacement = false;
			}
		}

		return locations;
	}

	/**
	 * @author AutoCap This method is used to determine if the solution meets the
	 *         requirement to form a snake.
	 * 
	 */

	public static boolean legitimate(int[][] locations, int[] newLocation, int num_row, int num_column) {
		// check if new location out of bounds
		if (newLocation[0] < 0 || newLocation[0] >= num_row || newLocation[1] < 0 || newLocation[1] >= num_column) {
			return false;
		}

		// check if new location touches an old location
		for (int i = 0; i < locations.length - 1; i++) {
			// the new location touches an old location if it is within the 3x3 square
			// surrounding the old location
			if (newLocation[0] <= (locations[i][0] + 1) && newLocation[0] >= (locations[i][0] - 1)
					&& newLocation[1] <= (locations[i][1] + 1) && newLocation[1] >= (locations[i][1] - 1)) {
				return false;
			}
		}

		// otherwise, placement is legitimate
		return true;
	}

	public static int[][] ResizeArray(int[][] location) {
		int[][] new_loc = new int[location.length + 1][2];

		for (int i = 0; i < new_loc.length; i++) {
			new_loc[i][0] = 0;
			new_loc[i][1] = 0;
		}

		for (int j = 0; j < location.length; j++) {
			new_loc[j][0] = location[j][0];
			new_loc[j][1] = location[j][1];
		}
		return new_loc;
	}

	/**
	 * @author AutoCap This method is used to process all the quotes and generate
	 *         the Power-Point with puzzle and solution puzzle slides.
	 * 
	 */

	public static void genPowerPoint(ArrayList<String> list_quotes) throws IOException, SQLException {
		// get the Power Point file name
		File ppt_file_name = new File(Preferences.PPT_FILE_NAME);

		// convert arraylist to array
		String[] quote_array = list_quotes.toArray(new String[0]);

		int puzzle_slide_no = 1;
		int solution_slide_no = 1;

		// to store grid in order to use later
		ArrayList<String[][]> puzzles = new ArrayList<String[][]>();

		// create power-point
		HSLFSlideShow ppt = new HSLFSlideShow();

		// create the API instance
		API api = new API();

		// mass generate slides for a quote starting at index 0
		for (int index = 0; index < quote_array.length; index++) {

			String a_quote = quote_array[index];
			int length = api.getLength(a_quote);

			if (length < char_limit) {
				HSLFSlide slide = ppt.createSlide();
				String title_name = Preferences.PUZZLE_TITLE;
				createTitle(slide, title_name);
				// createFooter(slide, name);
				renderLogo(ppt, slide);

				HSLFTable table = slide.createTable(num_row, num_column); // create a table of 12 rows and 16 columns
				getLabels(slide, num_row, num_column); // create labels for slide1

				String grid[][] = genGrid(table, length, true, quote_array[index], quote_array[index]); // call method
																										// to generate
																										// Grid

				for (int i = 0; i < num_column; i++) {
					for (int j = 0; j < num_row; j++) {
						// writes values from puzzle into tables
						String char_string = String.valueOf(grid[j][i]);
						HSLFTableCell cell1 = table.getCell(j, i);
						cell1.setText(char_string);

						// formats each cell on slide 1
						setBorders(cell1);
						HSLFTextRun rt1 = cell1.getTextParagraphs().get(0).getTextRuns().get(0);
						rt1.setFontFamily(Preferences.FONT_NAME);
						rt1.setFontSize(Preferences.GRID_FONT_SIZE);
						cell1.setVerticalAlignment(VerticalAlignment.MIDDLE);
						cell1.setHorizontalCentered(true);
					}
				}

				for (int i = 0; i < num_column; i++) {
					table.setColumnWidth(i, table_width);
				}

				for (int i = 0; i < num_row; i++) {
					table.setRowHeight(i, table_height);
				}

				table.moveTo(tableMoveX, tableMoveY);

				renderSlideNumber(slide, puzzle_slide_no);
				puzzles.add(grid);
			} else { // if the length of the quote is beyond system limit
				HSLFSlide slide = ppt.createSlide();
				String title_name = Preferences.PUZZLE_TITLE;
				// create template for slide1: puzzle no solution
				createTitle(slide, title_name);
				// createFooter(slide, name);
				renderLogo(ppt, slide); // add logo picture
				renderSlideNumber(slide, solution_slide_no);

				String grid[][] = genGrid(quote_array[index]);
				puzzles.add(grid);
			}

			solution_slide_no = solution_slide_no + 1;
		}

		for (int index = 0; index < quote_array.length; index++) {

			int length = api.getLength(quote_array[index]);

			if (length < char_limit) {
				HSLFSlide slide2 = ppt.createSlide();
				String title_name = Preferences.PUZZLE_TITLE;
				createTitle(slide2, title_name); // create template for slide2: puzzle solution
				// createFooter(slide, name);
				renderLogo(ppt, slide2);

				HSLFTable table2 = slide2.createTable(num_row, num_column); // create a table of n rows and n columns
				getLabels(slide2, num_row, num_column); // create labels for slide2

				String grid[][] = puzzles.get(index);

				for (int i = 0; i < num_column; i++) {
					for (int j = 0; j < num_row; j++) {
						// writes values from puzzle into tables
						String char_string = String.valueOf(grid[j][i]);
						HSLFTableCell cell1 = table2.getCell(j, i);
						cell1.setText(char_string);

						// formats each cell on slide 2
						setBorders(cell1);
						HSLFTextRun rt1 = cell1.getTextParagraphs().get(0).getTextRuns().get(0);
						rt1.setFontFamily(Preferences.FONT_NAME);
						rt1.setFontSize(Preferences.GRID_FONT_SIZE);
						cell1.setVerticalAlignment(VerticalAlignment.MIDDLE);
						cell1.setHorizontalCentered(true);
					}
				}

				for (int i = 0; i < num_column; i++) {
					table2.setColumnWidth(i, table_width);
				}

				for (int i = 0; i < num_row; i++) {
					table2.setRowHeight(i, table_height);
				}

				renderSlideNumber(slide2, puzzle_slide_no);

				table2.moveTo(tableMoveX, tableMoveY);
			} else {
				HSLFSlide slide = ppt.createSlide();
				String title_name = Preferences.PUZZLE_TITLE;
				createTitle(slide, title_name); // create template for slide1: puzzle no solution
				// createFooter(slide, name);
				renderLogo(ppt, slide); // add logo picture
				renderSlideNumber(slide, puzzle_slide_no);
			}

			puzzle_slide_no = puzzle_slide_no + 1;
		}

		// reorder slides so solution slides goes on the bottom half
		int size = quote_array.length;
		int o = 1;
		int n = quote_array.length + 1;
		for (int index = 0; index < size; index++) {
			ppt.reorderSlide(o, n);
			o++;
			n++;
		}

		// write the contents of the ppt to the ppt_file_name
		FileOutputStream out = new FileOutputStream(ppt_file_name);
		ppt.write(out);
		out.close();

		// Launch the Power Point
		System.out.println("Puzzle is created: " + ppt_file_name);
		System.out.println("Loading...");
		Desktop.getDesktop().browse(ppt_file_name.toURI());
		System.out.println("Done.");

		// Close the power point
		ppt.close();
	}

	/**
	 * main method for driving the Puzzle and Power Point Generation
	 * 
	 * @param args
	 * @throws IOException
	 * @throws SQLException
	 */

	public static void main(String[] args) throws Exception, IOException, SQLException {

		// Create a new driver
		Driver driver = new Driver();

		// Get the list of quotes
		ArrayList<String> list_quotes = Source.getQuotes();

		// Now generate the power point
		driver.genPowerPoint(list_quotes);

	}
}

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
import java.util.Scanner;
import org.apache.commons.compress.utils.IOUtils;
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

/**
 * @author AutoCap (Xeng Xiong and Seng Khang)
 * The purpose of this program is to mass generate a snake puzzle in power point.
 * There will be two slides generated for one puzzle (puzzle and solution).
 * The program will work for CSV files and a database (myPHPadmin), parameters must be adjusted accordingly
 * - CSV file must be located in the project folder, will only work with the given format "ID,Author,Category,Quote".
 * - Database must be up and running, please adjust any difference in naming accordingly.
 * - PowerPoint must be closed out before running the program else the program throws an error. 
 * - When running the code existing files with the same name and type will be overwritten please backup any mass generated puzzle accordingly.
 *
 */

public class Puzzle {
	
	/*
	Notes for default table size of 16x12:
		Good table size:
		table_width = 30
		table_height = 30
		place in middle of power-point coordinates:
		tableMoveX = 140
		tableMoveY = 130
	*/
	
	//side_label_width should stay at 30 at all times in order to not overlap with table
	//or else table X and Y coordinates will have to be adjusted.
	private final static int tableMoveX = 140; 
	private final static int tableMoveY = 130; 
	private final static int table_width = 30;
	private final static int table_height = 30;
	private final static int side_label_width = 30;
	private final static double table_fontSize = 15.0;
	private final static String table_font = "Arial";
	private final static String content_font = "Arial";

	//16x12 default grid size
	private static int num_column = 16; //default columns
	private static int num_row = 12; //default rows
	private static int char_limit = 74; //max phrase length for default grid size
	public static String SOURCE = "DATABASE"; //DATABASE or TEXTFILE
	
	private int startID;
	private int endID;
	
	public int getCharLimit(){
		return char_limit;
	}
	
	public int getStartID() {
		return startID;
	}
	
	public int getEndID() {
		return endID;
	}
	
	public void setStartID(int startID) {
		this.startID = startID;
	}
	
	public void setEndID(int endID) {
		this.endID = endID;
	}
	
	public int getNumColumn() {
		return num_column;
	}
	
	public int getNumRow() {
		return num_row;
	}
	
	/**
	 * @author AutoCap
	 * This method sets the the color of a the cells.
	 */
	
	public static void setBorders(HSLFTableCell cell) {
		cell.setBorderColor(BorderEdge.bottom, Color.black);
		cell.setBorderColor(BorderEdge.top, Color.black);
		cell.setBorderColor(BorderEdge.right, Color.black);
		cell.setBorderColor(BorderEdge.left, Color.black);
	}
	
	/**
	 * @author AutoCap
	 * These two methods are unused, but they set the background of the power point slide
	 * and creates a footer with the data retrieved from the database.
	 */
	
	/*
	public static void changeBackground(HSLFSlideShow ppt, HSLFSlide slide) throws IOException {
		slide.setFollowMasterBackground(false);
		HSLFFill fill = slide.getBackground().getFill();
		HSLFPictureData pd = ppt.addPicture(new File("white.png"), HSLFPictureData.PictureType.PNG);
		fill.setFillType(2);
		fill.setPictureData(pd);
	}
	*/
	
	/*
	public static void createFooter(HSLFSlide slide, String name) {
		HSLFTextBox footer = slide.createTextBox();
		HSLFTextParagraph p = footer.getTextParagraphs().get(0);
		p.setTextAlign(TextAlign.CENTER);
		footer.setFillColor(Color.green);
		HSLFTextRun r = p.getTextRuns().get(0);
		r.setText("www.telugupuzzles.com\nusername: " + name);
		r.setFontFamily(content_font);
		r.setFontSize(18.);
		footer.setAnchor(new Rectangle(0,490,720,50));
	}
	*/
	
	/**
	 * @author AutoCap
	 * This method creates and controls the title text box that runs across each slide.
	 */
	
	public static void createTitle(HSLFSlide slide, String title_name, int width, int height) {
		HSLFTextBox title = slide.createTextBox();
		HSLFTextParagraph p = title.getTextParagraphs().get(0);
		p.setTextAlign(TextAlign.CENTER);
		HSLFTextRun r = p.getTextRuns().get(0);
		r.setBold(true);
		r.setFontColor(Color.black);
		r.setText(title_name);
		r.setFontFamily(content_font);
		r.setFontSize(35.);
		title.setAnchor(new Rectangle(270,5,width,height));
	}
	
	/**
	 * @author AutoCap
	 * This method creates and controls the slide numbers that are in each slide.
	 */
	
	public static void createSlideNum(HSLFSlide slide, int slide_num, int width, int height) {
		HSLFTextBox slide_number = slide.createTextBox();
		HSLFTextParagraph p = slide_number.getTextParagraphs().get(0);
		p.setTextAlign(TextAlign.CENTER);
		slide_number.setFillColor(Color.green);
		HSLFTextRun r = p.getTextRuns().get(0);
		r.setText("" + slide_num + "");
		r.setFontFamily(content_font);
		r.setFontSize(30.);
		slide_number.setAnchor(new Rectangle(221,6,width,height));
	}
	
	/**
	 * @author AutoCap
	 * This method gives the illusion of the slide number having a box with a black border.
	 */
	
	public static void createLine(HSLFSlide slide, int x, int y, int width, int height) {
		HSLFLine line = new HSLFLine();
		line.setAnchor(new Rectangle(x,y,width,height));
		line.setLineColor(Color.black);
		slide.addShape(line);
	}
	/**
	 * @author AutoCap
	 * This method inserts the "logo.png" into the top left corner of every puzzle's slide.
	 */
	
	public static void createPic(HSLFSlideShow ppt, HSLFSlide slide) throws IOException {
		byte[] picture = IOUtils.toByteArray(new FileInputStream(new File("logo.png")));
		HSLFPictureData pd = ppt.addPicture(picture, HSLFPictureData.PictureType.PNG);
		HSLFPictureShape pic_shape = slide.createPicture(pd); 
		pic_shape.setAnchor(new Rectangle(0, 0, 174, 65));
	}
	
	/**
	 * @author AutoCap
	 * This method generates and controls the top and side labels of the grid. 
	 */
	
	public static void getLabels(HSLFSlide slide, int num_row, int num_column) {
		String[] top_label = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
				"S", "T", "U", "V", "W", "X", "Y", "Z" };

		String[] side_label = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16",
				"17", "18", "19", "20", "21", "22", "23", "24", "25", "26" };
		
		//create labels
		HSLFTable top_row = slide.createTable(1, num_column);
		HSLFTable side_row = slide.createTable(num_row, 1);
		
		for (int i = 0; i < num_row; i++) {
			//side column labels
			HSLFTableCell side_cell = side_row.getCell(i, 0);
			side_cell.setText(side_label[i]);
			setBorders(side_cell);
			HSLFTextRun rts1 = side_cell.getTextParagraphs().get(0).getTextRuns().get(0);
			rts1.setFontFamily(table_font);
			rts1.setFontSize(table_fontSize - 5); //labels' font size are 5 less than table font size
			rts1.setBold(true);
			side_cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
			side_cell.setHorizontalCentered(true);
			
			for (int j = 0; j < num_column; j++) {
				//top row labels
				HSLFTableCell top_cell = top_row.getCell(0, j);
				top_cell.setText(top_label[j]);
				setBorders(top_cell);
				HSLFTextRun rt2s1 = top_cell.getTextParagraphs().get(0).getTextRuns().get(0);
				rt2s1.setFontFamily(table_font);
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
			top_row.setRowHeight(0, 10);
		}
		
		top_row.moveTo(tableMoveX, tableMoveY - 20); // y - 20 to match table
		side_row.moveTo(tableMoveX - 30, tableMoveY); // x - 30 to match table
	}

	/**
	 * @author AutoCap
	 * This method ensures that the reordering of slides works.
	 * use for empty slides in order to store puzzles and not be out of bound
	 */
	
	public static String[][] genGrid(String filler, int num_row, int num_column) throws UnsupportedEncodingException, SQLException {
		Random rand = new Random();
		
		API api = new API();
		
		//get filler characters and store in an array
		ArrayList<String> filler_array1 = api.getFillers(filler);
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

	public static  String[][] genGrid(HSLFTable table, int num_row, int num_column, int length, boolean solution, String filler, String quote) throws UnsupportedEncodingException, SQLException {
		Random rand = new Random();
		
		API api = new API();
		
		//process phrase through logicalChars API and store in an array
		ArrayList<String> list_quotes1 = api.parseLogicalChars(quote);
		String[] quote_char_array = list_quotes1.toArray(new String[0]);
		System.out.println("Processing phrase '" + quote + "' into API...");
		
		//get filler characters and store in an array
		ArrayList<String> filler_array1 = api.getFillers(filler);
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
				cell.setFillColor(Color.green); //set solution to color green 
			}
		}
		
		return grid;
	}
	
	/**
	 * @author AutoCap
	 * This method is the logic behind where each of the letters in the solution is placed on the grid itself.
	 * 
	 */
	
	public static int[][] chooseLocations(int num_row, int num_column, int length) {
		boolean legitimatePlacement = false;
		boolean startOver = true;
		Random rand = new Random();
		int[][] locations = new int[1][2];

		//continue until a full set of legitimate placements have been found
		while (startOver) {
			startOver = false;

			locations[0][0] = (int)Math.floor(rand.nextDouble() * num_row);
			locations[0][1] = (int)Math.floor(rand.nextDouble() * num_column);

			//choose random locations for the other characters such that no character touches an earlier character
			for (int i = 1; i < length; i++) { 
				int[] newLocation = {};
				int placementOptions = 8;
				ArrayList<Object> badPlacements = new ArrayList<Object>();

				while (!legitimatePlacement && !startOver) {
					//pick a random location for the next character
					int placement = (int)Math.floor(rand.nextDouble() * placementOptions);

					//adjust placement based on which placements have been determined to be bad
					for (int j = 0; j < badPlacements.size(); j++) {
						if (j <= placement) {
							if (Boolean.parseBoolean(badPlacements.get(j).toString()) == true) {
								placement += 1;
							}
						}
					}

					//the location in vertical and horizontal coordinates of the grid
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
						//check if the location chosen is legitimate (is not outside the grid or touching a previous character in the phrase)
						legitimatePlacement = legitimate(locations, newLocation, num_row, num_column);
					}
					else {
						startOver = true;
					}

					//if the placement is not legitimate, record it as bad
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

				//if all possible locations have been checked and none of them are legitimate, start over
				if (startOver) {
					locations = new int[1][2];
					break;
				}

				//when a legitimate placement is found, add it to the locations list
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
	 * @author AutoCap
	 * This method is used to determine if the solution meets the requirement to form a snake.
	 * 
	 */
	
	public static boolean legitimate(int[][] locations, int[] newLocation, int num_row, int num_column) {
		//check if new location out of bounds
		if (newLocation[0] < 0 || newLocation[0] >= num_row || newLocation[1] < 0 || newLocation[1] >= num_column) {
			return false;
		}

		//check if new location touches an old location
		for (int i = 0; i < locations.length - 1; i++) {
			//the new location touches an old location if it is within the 3x3 square surrounding the old location
			if (newLocation[0] <= (locations[i][0] + 1) && newLocation[0] >= (locations[i][0] - 1) && newLocation[1] <= (locations[i][1] + 1) && newLocation[1] >= (locations[i][1] - 1)) {
				return false;
			}
		}

		//otherwise, placement is legitimate
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
	 * @author AutoCap
	 * This method is used to process all the quotes and generate the Power-Point with puzzle and solution puzzle slides.
	 * 
	 */
	
	public static void genPowerPoint(ArrayList<String> list_quotes) throws IOException, SQLException {
		File f = new File("Puzzle.ppt");
		
		String[] quote_array = list_quotes.toArray(new String[0]); //convert arraylist to array
		
		int no_solution_slide = 1;
		int solution_slide = 1;
		
		ArrayList<String[][]> puzzles = new ArrayList<String[][]>(); //to store grid in order to use later
		
		//create power-point
		HSLFSlideShow ppt = new HSLFSlideShow();
		
		API api = new API();
		
		//mass generate slides for a quote starting at index 0
		for (int index = 0; index < quote_array.length; index++) {
	
			int length = api.getLength(quote_array[index]);

			if (length < char_limit) {
				HSLFSlide slide = ppt.createSlide();
				String title_name = "Puzzle Solution";
				createTitle(slide, title_name, 320, 60); //create template for slide1: puzzle no solution
				//createFooter(slide, name);
				createPic(ppt, slide); //add logo picture
		
				HSLFTable table = slide.createTable(num_row, num_column); //create a table of 12 rows and 16 columns
				getLabels(slide, num_row, num_column); //create labels for slide1
				
				String grid[][] = genGrid(table, num_row, num_column, length, true, quote_array[index], quote_array[index]); //call method to generate Grid
		
				for(int i = 0; i < num_column; i++) {
					for(int j = 0; j < num_row; j++) {
						//writes values from puzzle into tables
						String char_string = String.valueOf(grid[j][i]);
						HSLFTableCell cell1 = table.getCell(j, i);
						cell1.setText(char_string);
				
						//formats each cell on slide 1
						setBorders(cell1);
						HSLFTextRun rt1 = cell1.getTextParagraphs().get(0).getTextRuns().get(0);
						rt1.setFontFamily("Arial");
						rt1.setFontSize(10.);
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
			
				if (solution_slide >= 100) {
					createSlideNum(slide, no_solution_slide, 65, 49);
				
					//create text box lines   
					createLine(slide, 220, 5, 65, 0); //top line
					createLine(slide, 285, 5, 0, 50); //right line
					createLine(slide, 220, 55, 65, 0); //bottom line
					createLine(slide, 220, 5, 0, 50); //left line
				}
				else {
					createSlideNum(slide, solution_slide, 49, 49);
				
					//create text box lines   
					createLine(slide, 220, 5, 50, 0); //top line
					createLine(slide, 270, 5, 0, 50); //right line
					createLine(slide, 220, 55, 50, 0); //bottom line
					createLine(slide, 220, 5, 0, 50); //left line
				}
				puzzles.add(grid);
			}
			else {
				HSLFSlide slide = ppt.createSlide();
				String title_name = "Empty Slide";
				createTitle(slide, title_name, 320, 60); //create template for slide1: puzzle no solution
				//createFooter(slide, name);
				createPic(ppt, slide); //add logo picture
				if (solution_slide >= 100) {
					createSlideNum(slide, solution_slide, 65, 49);
				
					//create text box lines   
					createLine(slide, 220, 5, 65, 0); //top line
					createLine(slide, 285, 5, 0, 50); //right line
					createLine(slide, 220, 55, 65, 0); //bottom line
					createLine(slide, 220, 5, 0, 50); //left line
				}
				else {
					createSlideNum(slide, solution_slide, 49, 49);
				
					//create text box lines   
					createLine(slide, 220, 5, 50, 0); //top line
					createLine(slide, 270, 5, 0, 50); //right line
					createLine(slide, 220, 55, 50, 0); //bottom line
					createLine(slide, 220, 5, 0, 50); //left line
				}
				
				String grid[][] = genGrid(quote_array[index], num_row, num_column);
				puzzles.add(grid);
			}
			
			solution_slide = solution_slide + 1;
		}
		
		for (int index = 0; index < quote_array.length; index++) {
			
			int length = api.getLength(quote_array[index]);
			
			if (length < char_limit) {
				HSLFSlide slide2 = ppt.createSlide();
				String title_name = "Puzzle";
				createTitle(slide2, title_name, 200, 60); //create template for slide2: puzzle solution
				//createFooter(slide, name);
				createPic(ppt, slide2); //add logo picture

				HSLFTable table2 = slide2.createTable(num_row, num_column); //create a table of n rows and n columns
				getLabels(slide2, num_row, num_column); //create labels for slide2
				
				String grid[][] = puzzles.get(index);
	
				for(int i = 0; i < num_column; i++) {
					for(int j = 0; j < num_row; j++) {
						//writes values from puzzle into tables
						String char_string = String.valueOf(grid[j][i]);
						HSLFTableCell cell1 = table2.getCell(j, i);
						cell1.setText(char_string);
					
						//formats each cell on slide 2
						setBorders(cell1);
						HSLFTextRun rt1 = cell1.getTextParagraphs().get(0).getTextRuns().get(0);
						rt1.setFontFamily("Arial");
						rt1.setFontSize(10.);
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
			
				if (solution_slide >= 100) {
					createSlideNum(slide2, no_solution_slide, 65, 49);
				
					//create text box lines   
					createLine(slide2, 220, 5, 65, 0); //top line
					createLine(slide2, 285, 5, 0, 50); //right line
					createLine(slide2, 220, 55, 65, 0); //bottom line
					createLine(slide2, 220, 5, 0, 50); //left line
				}
				else {
					createSlideNum(slide2, no_solution_slide, 49, 49);
				
					//create text box lines   
					createLine(slide2, 220, 5, 50, 0); //top line
					createLine(slide2, 270, 5, 0, 50); //right line
					createLine(slide2, 220, 55, 50, 0); //bottom line
					createLine(slide2, 220, 5, 0, 50); //left line
				}
				
				table2.moveTo(tableMoveX, tableMoveY);
			}
			else {
				HSLFSlide slide = ppt.createSlide();
				String title_name = "Empty Slide";
				createTitle(slide, title_name, 320, 60); //create template for slide1: puzzle no solution
				//createFooter(slide, name);
				createPic(ppt, slide); //add logo picture
				if (no_solution_slide >= 100) {
					createSlideNum(slide, no_solution_slide, 65, 49);
					
					//create text box lines   
					createLine(slide, 220, 5, 65, 0); //top line
					createLine(slide, 285, 5, 0, 50); //right line
					createLine(slide, 220, 55, 65, 0); //bottom line
					createLine(slide, 220, 5, 0, 50); //left line
				}
				else {
					createSlideNum(slide, no_solution_slide, 49, 49);
					
					//create text box lines   
					createLine(slide, 220, 5, 50, 0); //top line
					createLine(slide, 270, 5, 0, 50); //right line
					createLine(slide, 220, 55, 50, 0); //bottom line
					createLine(slide, 220, 5, 0, 50); //left line
				}
			}
			
			no_solution_slide = no_solution_slide + 1; 
		}
	
		//reorder slides so solution slides goes on the bottom half
		int size = quote_array.length;
		int o = 1;
		int n = quote_array.length + 1;
		for (int index = 0; index < size; index++) {
			ppt.reorderSlide(o, n);
			o++;
			n++;
		}
	
		FileOutputStream out = new FileOutputStream(f);
		ppt.write(out);
		out.close();
		
		System.out.println("Puzzle is created: " + f);
		System.out.println("Loading...");
		
		Desktop.getDesktop().browse(f.toURI());
		System.out.println("Done.");
		
		ppt.close();
	}
	
	public static void main(String[] args) throws IOException, SQLException {
		
		Scanner scan = new Scanner(System.in);
		
		String txtfile_name = "quotes_table.csv";
		
		Puzzle user = new Puzzle();
		
		Analyzer analyzer = new Analyzer();
		int max_length = analyzer.getMaxLength(txtfile_name);
		System.out.println("Max Phrase Length from your SOURCE: " + max_length + "\n");
		analyzer.printSystemLimits();
		System.out.println();
		
		analyzer.getSuggestion(max_length);
		System.out.println();
		
		ArrayList<String> badQuotesArray = analyzer.getBadQuotes(txtfile_name);
		System.out.println("Default grid size CAN NOT accommodate these phrases: ");
		System.out.println(badQuotesArray);
		System.out.println();
		
		String answer = "";
		if (max_length > 112) {
			System.out.println("Do you still want to continue creating the slides?(Y/N) or Enter 'C' to change grid size: ");
			answer = scan.nextLine();
		}
		else {
			answer = "Y";
		}
		
		if (answer.equalsIgnoreCase("C")) {
			System.out.println("Enter number of columns: ");
			int column = scan.nextInt();
			num_column = column;
			
			System.out.println("Enter number of rows: ");
			int row = scan.nextInt();
			num_row = row;
			answer = "Y";
			
			if (column == 18 && row == 12) {
				char_limit = 83;
			}
			if (column == 20 && row == 12) {
				char_limit = 88;
			}
			if (column == 16 && row == 14) {
				char_limit = 85;
			}
			if (column == 18 && row == 14) {
				char_limit = 90;
			}
			if (column == 20 && row == 14) {
				char_limit = 100;
			}
			if (column == 16 && row == 16) {
				char_limit = 100;
			}
			if (column == 18 && row == 16) {
				char_limit = 102;
			}
			if (column == 20 && row == 16) {
				char_limit = 105;
			}
			if (column == 16 && row == 18) {
				char_limit = 106;
			}
			if (column == 18 && row == 18) {
				char_limit = 107;
			}
			if (column == 20 && row == 18) {
				char_limit = 112;
			}
			
			System.out.println("Grid Size has been changed!");
		}
		
		if (answer.equalsIgnoreCase("Y")) {
			System.out.println("Slides for phrases over 112 characters will not be created! Will be replaced with Empty Slides!");
			System.out.println();
			
			ArrayList<String> list_quotes = new ArrayList<String>();
			
			if (SOURCE.equalsIgnoreCase("DATABASE")) {
				System.out.println("Enter the starting ID: ");
				int startID = scan.nextInt();
				user.setStartID(startID);
				
				System.out.println("Enter the ending ID: ");
				int endID = scan.nextInt();
				user.setEndID(endID);
				
				String statement = "SELECT quote FROM quote_table WHERE id BETWEEN " + user.getStartID() + " AND " + user.getEndID();
				list_quotes = Source.connect_to_db(statement);
				System.out.println("Connected to Database!\n");
			}
			else {
				list_quotes = Source.getQuotesFromtxt(txtfile_name);
			}
			
			genPowerPoint(list_quotes);
		}
		else {
			System.out.println("End.");
		}
	}
}

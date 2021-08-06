import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;


public class API {
	
	/**
	 * @author AutoCap
	 * This method passes a quote to the API which then returns the quote's length. After retrieving the length we pass it back into the Puzzle
	 * class to be used in the grid generation.
	 */
	
	public int getLength(String quote) throws UnsupportedEncodingException {
		
		String lang = chooseLang(quote);
		
		String URL = "http://indic-wp.thisisjava.com/api/getLength.php?string=" + URLEncoder.encode(quote, "UTF-8") + "&language='" + lang + "'";
		String newURL = URL.replaceAll(" ", "%20");
    
		Client client = Client.create();
    		WebResource resource = client.resource(newURL);
    		String response = resource.get(String.class);
    	
    		int index = response.indexOf("{");
    		response = response.substring(index);
    		JSONObject myObject = new JSONObject(response.trim()); 

    		Number length = myObject.getNumber("data");
    	
    		int q_length = length.intValue();

    		return q_length;
	}
	
	/**
	 * @author AutoCap
	 * This method checks the given quote and determines whether it is English or Telugu, it then passes the language into the 
	 * other methods for them to adjust accordingly.
	 */
	
	public String chooseLang(String quote) {
		if (quote.matches(".*[a-zA-Z]+.*")) {
			return "English";
		}
		else {
			return "Telugu";
		}
	}
	
	/**
	 * @author AutoCap
	 * This method passes the array that was retrieved from the Sources class into the given API where it is then broken down
	 * into individual characters that will be used to generate the solution.
	 */
	
	public ArrayList<String> parseLogicalChars(String quote) throws SQLException, UnsupportedEncodingException {
		
		ArrayList<String> quote_array = new ArrayList<String>();
	
		String lang = chooseLang(quote);
		String URL = "http://indic-wp.thisisjava.com/api/getLogicalChars.php?string=" + URLEncoder.encode(quote, "UTF-8") + "&language='" + lang + "'";
		String newURL = URL.replaceAll(" ", "%20");
        
		Client client = Client.create();
       		WebResource resource = client.resource(newURL);
        	String response = resource.get(String.class);
        
       		int index = response.indexOf("{");
       	 	response = response.substring(index);
       	 	JSONObject myObject = new JSONObject(response); 
        	
        	JSONArray jsonArray = myObject.getJSONArray("data");
            
        	for (int j = 0; j < jsonArray.length(); j++) {
        		quote_array.add(jsonArray.getString(j));
        	}
	
		return quote_array;
	}
	
	/**
	 * @author AutoCap
	 * This method retrieves filler characters for each generated grid and returns it to the Puzzle class.
	 */
	
	public ArrayList<String> getFillers (String quote) throws SQLException, UnsupportedEncodingException {
		
		ArrayList<String> filler_array = new ArrayList<String>();
	
		String lang = chooseLang(quote);
		String URL = "https://indic-wp.thisisjava.com/api/getFillerCharacters.php?count=160&type=CONSONANT&language=" + lang;
		String newURL = URL.replaceAll(" ", "%20");
        
		Client client = Client.create();
       		WebResource resource = client.resource(newURL);
        	String response = resource.get(String.class);
        
       		int index = response.indexOf("{");
        	response = response.substring(index);
       		JSONObject myObject = new JSONObject(response.trim()); 
        	
       		JSONArray jsonArray = myObject.getJSONArray("data");
            
       		for (int j = 0; j < jsonArray.length(); j++) {
        		filler_array.add(jsonArray.getString(j));
       		}
		
		return filler_array;
	}

}


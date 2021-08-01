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
	
	public int getLength(char[] phraseChars) throws UnsupportedEncodingException {
		String quote1 = String.valueOf(phraseChars);
		String URL = "http://indic-wp.thisisjava.com/api/getLength.php?string=" + URLEncoder.encode(quote1, "UTF-8") + "&language='telugu'";
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
	
	public String chooseLang(String quote) {
		if (quote.matches(".*[a-zA-Z]+.*")) {
			return "English";
		}
		else {
			return "Telugu";
		}
	}
	
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
        	
        System.out.println(response);
        	
        JSONArray jsonArray = myObject.getJSONArray("data");
            
        for (int j = 0; j < jsonArray.length(); j++) {
        	quote_array.add(jsonArray.getString(j));
        }
		
        System.out.println(quote_array);
		return quote_array;
	}
	
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
        	
        System.out.println(response);
        	
        JSONArray jsonArray = myObject.getJSONArray("data");
            
        for (int j = 0; j < jsonArray.length(); j++) {
        	filler_array.add(jsonArray.getString(j));
        }
		
		return filler_array;
	}

}


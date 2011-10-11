package ch.hyperdoc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import processing.core.PApplet;
import processing.net.Client;

public class Controller {
	private ArrayList<Entry> entrys = new ArrayList<Entry>();
	private ArrayList<String> tags = new ArrayList<String>();
	private PApplet parent;
	private String ip;
	private int port;
	private boolean debug = false;

	public Controller(PApplet parent, String ip, int port) {
		this.parent = parent;
		this.ip = ip;
		this.port = port;
	}
	
	public Controller(PApplet parent) {
		this.parent = parent;
		this.ip = "127.0.0.1";
		this.port = 8000;
	}
	
	public Controller(PApplet parent, boolean debug) {
		this.debug = debug;
		if(!debug) {
			this.parent = parent;
			this.ip = "127.0.0.1";
			this.port = 8000;
		} else {
			System.out.println("using debug-mode");
		}
		
	}

	private ObjectMapper mapper = new ObjectMapper();
	private String jsonString = new String(), pureJson;
	private String[] allTheStrings;
	private ArrayList<Entry> jsonObjects = new ArrayList<Entry>();
	private Client c;
	
	/**
	 * This Method retrieves all Data from the Webserver.
	 * Always run this Method before running something else
	 * @param void
	 */
	public void getData() {
		if(!debug) {
			System.out.println("connecting to " + ip +":" + Integer.toString(port));
			c = new Client(parent, ip, port); // Connect to server on port 8000
			c.write("GET /json/ HTTP/1.1\n"); // Use the HTTP "GET" command to ask for a Web page
			c.write("Host: ch.hyperdoc\n\n"); // Be polite and say who we are
			while(!jsonString.contains("}}]"))	{
				if (c.available() > 0) { // If there's incoming data from the client...
					jsonString += c.readString(); // ...then grab it and print it
				}
			}
		}
		else {
			jsonString = Debug.jsonString;
		}
		
		int splitAt = jsonString.indexOf("{");
		pureJson = jsonString.substring(splitAt);
		allTheStrings = pureJson.split("(?<=})\\, ");
		
		
		for(int i = 0; i < allTheStrings.length;i++) {
			try {
				addEntry(mapper.readValue(allTheStrings[i], Entry.class));
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		setTags();
	}
	
	/**
	 * This Getter-Method returns an ArrayList with all Entrys
	 * @param void
	 * @return ArrayList<Entry> with all Entrys
	 */
	public ArrayList<Entry> getEntrys() {
		return entrys;
	}

	/**
	 * Extract all Tags from the input data and put it the arraylist.
	 * Does this by running all the inputTags through all the registered tags via a inner for-clause.
	 * @param void
	 */
	public void setTags() {
		for(Entry e : entrys) {
			String[] inputTags = e.getTags();
			for(int i = 0;i < inputTags.length;i++) {
				if(tags.size() == 0 && inputTags.length > 0)
				{
					tags.add(inputTags[i]);
				} else {
					boolean add = true;
					for(int innerI = 0; innerI < tags.size(); innerI++) {
						String tag = tags.get(innerI);
						if(inputTags[i].equalsIgnoreCase(tag)) {
							add = false;
						}
					}
					if(add) { tags.add(inputTags[i]); }
					
				}
			}
		}
	}
	
	/**
	 * Returns an ArrayList<String> containig all Tags
	 * @param void
	 * @return ArrayList<String> with all Tags
	 */
	public ArrayList<String> getTags() {
		return tags;
	}
	
	/**
	 * Adds an Entry to the Entry ArrayList<Entry>
	 * @param Entry e
	 */
	public void addEntry(Entry e) {
		entrys.add(e);
	}
	/**
	 * This methods returns either an empty arraylist or an arraylist with the objects on which all tags matched.
	 * At first it searchs trough all the entrys and uses that that subset to check against the other tags.
	 * @param tags ArrayList containig Tags as String, ArrayList<Entry> inputEntrys
	 * @return ArrayList with Entrys or empty ArrayList
	 */

	public ArrayList<Entry> getEntryswithTags(ArrayList<String> tags, ArrayList<Entry> inputEntrys) {
		ArrayList<Entry> result = new ArrayList<Entry>();
		boolean firstRun = true;
		for (String questionTag : tags)
		{
			if(result.size() == 0 && firstRun) {
				for (Entry e : inputEntrys) {
					if(e.containsTag(questionTag)) {
						result.add(e);
					}
				}
				firstRun = false;
			} else {
				if(result.size() == 0) {
					return result;
				} else {
					for(Iterator<Entry> i = result.iterator();i.hasNext();)
					{
						Entry currentEntry = i.next();
						if(!currentEntry.containsTag(questionTag)) {
							i.remove();
						}
					}
				}
			}
		}

		return result;
	}
	
	/**
	 * This methods returns either an empty arraylist or an arraylist with the objects on which all usernames matched.
	 * At first it searchs trough all the entrys and uses that that subset to check against the other usernames.
	 * @param tags ArrayList containig usernames as String, ArrayList<Entry> inputEntrys
	 * @return ArrayList with Entrys or empty ArrayList
	 */
	public ArrayList<Entry> getEntryswithUsername(ArrayList<String> usernames, ArrayList<Entry> inputEntrys) {
		ArrayList<Entry> result = new ArrayList<Entry>();
		boolean firstRun = true;
		for (String questionName : usernames)
		{
			if(result.size() == 0 && firstRun) {
				for (Entry e : inputEntrys) {
					if(e.containsTag(questionName)) {
						result.add(e);
					}
				}
				firstRun = false;
			} else {
				if(result.size() == 0) {
					return result;
				} else {
					for(Iterator<Entry> i = result.iterator();i.hasNext();)
					{
						Entry currentEntry = i.next();
						if(!currentEntry.containsTag(questionName)) {
							i.remove();
						}
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * This method checks for any entrys which were added on that date, but checks only for day, month and year.
	 * @param java.util.Date date, ArrayList<Entry> inputEntrys
	 * @return ArrayList with Entrys or empty ArrayList
	 */
	public ArrayList<Entry> getEntrysOnDay(Date date, ArrayList<Entry> inputEntrys){
		ArrayList<Entry> result = new ArrayList<Entry>();
		Calendar inputDate = Calendar.getInstance();
		inputDate.setTime(date);

		for (Entry e : inputEntrys) {
			if(e.isOnDate(inputDate)) {
				result.add(e);
			}
		}
		return result;
	}
	
	/**
	 * This method checks for any entrys which were added on before the given date, but checks only for day, month and year.
	 * @param java.util.Date date, ArrayList<Entry> inputEntrys
	 * @return ArrayList with Entrys or empty ArrayList
	 */
	public ArrayList<Entry> getEntrysBeforeDate(Date date, ArrayList<Entry> inputEntrys) {
		ArrayList<Entry> result = new ArrayList<Entry>();
		Calendar inputDate = Calendar.getInstance();
		inputDate.setTime(date);

		for (Entry e : inputEntrys) {
			if(e.isBeforeDate(inputDate)) {
				result.add(e);
			}
		}
		return result;
	}
	/**
	 * This method checks for any entrys of the originaly retrived entrys which were added after the given date, but checks only for day, month and year.
	 * @param java.util.Date date, ArrayList<Entry> inputEntrys
	 * @return ArrayList with Entrys or empty ArrayList
	 */
	public ArrayList<Entry> getEntrysAfterDate(Date date, ArrayList<Entry> inputEntrys) {
		ArrayList<Entry> result = new ArrayList<Entry>();
		Calendar inputDate = Calendar.getInstance();
		inputDate.setTime(date);

		for (Entry e : inputEntrys) {
			if(e.isAfterDate(inputDate)) {
				result.add(e);
			}
		}
		return result;
	}
}

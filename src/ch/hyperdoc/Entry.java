package ch.hyperdoc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Entry {
	public int pk;
	public String model;
	public Fields _fields;

	public static class Fields {
		public String text;
		public Calendar date = Calendar.getInstance();
		public String[] tags;
		public String username;
		public int tagsLength;
		SimpleDateFormat sdfToDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		public void setTags(String[] tags) {
			this.tags = tags;
			tagsLength = tags.length;
		}
		
		public void setDate(String dateString) throws ParseException {
			date.setTime(sdfToDate.parse(dateString));
		}
	}

	public String[] getTags() { return _fields.tags; }
	public String getText() { return _fields.text; }
	public Calendar getDate() { return _fields.date; }
	public String getUsername() { return _fields.username; }
	
	public boolean containsTag(String inputTag) {
		for(int i = 0; i < _fields.tagsLength; i++) {
			if(_fields.tags[i].equalsIgnoreCase(inputTag)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isOnDate(Calendar inputDate) {
		if(_fields.date.get(Calendar.DAY_OF_MONTH) == inputDate.get(Calendar.DAY_OF_MONTH) && 
				_fields.date.get(Calendar.MONTH) == inputDate.get(Calendar.MONTH) &&
				_fields.date.get(Calendar.YEAR) == inputDate.get(Calendar.YEAR)) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isBeforeDate(Calendar inputDate) {
		if(_fields.date.get(Calendar.DAY_OF_MONTH) == inputDate.get(Calendar.DAY_OF_MONTH) && 
				_fields.date.get(Calendar.MONTH) == inputDate.get(Calendar.MONTH) &&
				_fields.date.get(Calendar.YEAR) == inputDate.get(Calendar.YEAR)) {
			return false;
		} else {
			return(_fields.date.before(inputDate));
		}
	}
	
	public boolean isAfterDate(Calendar inputDate) {
		if(_fields.date.get(Calendar.DAY_OF_MONTH) == inputDate.get(Calendar.DAY_OF_MONTH) && 
				_fields.date.get(Calendar.MONTH) == inputDate.get(Calendar.MONTH) &&
				_fields.date.get(Calendar.YEAR) == inputDate.get(Calendar.YEAR)) {
			return false;
		} else {
			return(_fields.date.after(inputDate));
		}
	}
	
	public boolean belongsToUser(String inputUsername) {
		if(_fields.username.equalsIgnoreCase(inputUsername)) {
			return true;
		} else {
			return false;
		}
	}

	public Fields getFields() {return _fields; }
	public void setFields(Fields f) {_fields = f; }
	//  {"pk": 35, "model": "blub.mess", "fields": {"date": "2011-05-07 11:59:14", "text": "sadly, no recursion #supsi", "tags": ["supsi"]}}
	// 2011-06-12 12:17:36
}

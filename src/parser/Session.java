package parser;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public class Session {
	LocalDate date;
	String title;
	List<Message> messages;
	public Session(LocalDate date, List<Message> messages) {
		super();
		this.date = date;
		this.title = date.toString();
		this.messages = messages;
	}
	
	public Session(LocalDate date, String title, List<Message> messages) {
		super();
		this.date = date;
		this.title = title;
		this.messages = messages;
	}
	
	public Session(LocalDate date) {
		super();
		this.date = date;
		this.title = date.toString();
		this.messages = new LinkedList<Message>();
	}
	
	public Session(LocalDate date, String title) {
		super();
		this.date = date;
		this.title = title;
		this.messages = new LinkedList<Message>();
	}
	
	public void addMessage(Message message) {
		messages.add(message);
	}
	
	public int getNumMessages() {
		return messages.size();
	}
	
	public String getTitle() {
		return title;
	}

	public List<Message> getMessages() {
		return messages;
	}

}

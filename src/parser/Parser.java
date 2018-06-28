package parser;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.LinkedList;
import java.util.List;

public class Parser {
	
	final static long sessionBreak = 3;
	private List<Session> sessions;
	private Session workingSession;
	private Message workingMessage;
	
	public Parser() {
		this.sessions = new LinkedList<Session>();
		this.workingSession = null;
		this.workingMessage = null;
	}
	
	private LocalDateTime parseTimestamp(String timestamp, LocalDateTime previous) {
		int day;
		int year;
		int hour;
		int minutes;
		Month month;
		System.out.print(timestamp);
		if (timestamp.length() > 7) {
			String noComma = timestamp.replaceAll(",", "");
			String[] strings = noComma.split(" ");
			month = Month.valueOf(strings[0].toUpperCase());
			day = Integer.parseInt(strings[1]);
			year = Integer.parseInt(strings[2]);
			hour = Integer.parseInt(strings[3].split(":")[0]);
			if (strings[3].indexOf("PM") >= 0 && hour != 12)
				hour += 12;
			else if (hour==12)
				hour -= 12;
			minutes = Integer.parseInt(strings[3].split(":")[1].substring(0, 2));
		} else {
			day = previous.getDayOfMonth();
			year = previous.getYear();
			month = previous.getMonth();
			
			hour = Integer.parseInt(timestamp.split(":")[0]);
			if (timestamp.indexOf("PM") >= 0){
				if (hour != 12) 
					hour += 12;
			} else {
				if (previous.getHour() >= 12){
					day++;
				}
				if (hour == 12) {
					hour = 0;
				}
			}
			minutes = Integer.parseInt(timestamp.split(":")[1].substring(0, 2));
		}
		System.out.println(": " + year + " " + month + " " + day + " " + hour + " " + minutes);
		return LocalDateTime.of(year, month, day, hour, minutes);
	}
	
	private boolean isSessionBreak(LocalDateTime first, LocalDateTime second) {
		//System.out.println(first + " " + second + " " + (Duration.between(first, second).toHours() > sessionBreak));
		return Duration.between(first, second).toHours() > sessionBreak;
	}
	
	private void nextSession(LocalDate newDate) {
		//System.out.println("workingSession initialized");
		if (workingSession != null) {
			sessions.add(workingSession);
		}
		
		workingSession = new Session(newDate);
	}
	
	private void addMessageToSession(Message message, LocalDateTime previous) {
		
		if(isSessionBreak(previous, message.getTimestamp())) {
			nextSession(message.getTimestamp().toLocalDate());
		}
		workingSession.addMessage(message);
	}
	
	public Log parse(Document doc, String title) {
		Log log = null;
		
		Elements messages = doc.getElementsByClass("message");
		LocalDateTime previous = LocalDateTime.of(1, 1, 1, 0, 0);
		LocalDateTime lastMessageTime = LocalDateTime.of(1, 1, 1, 0, 0);
		
		sessions = new LinkedList<Session>();
		
		for (Element message: messages) {
			String by = null;
			String avatar = null;
			LocalDateTime tstamp = null;
			String text;
						
			for (Element timestamp : message.getElementsByClass("tstamp")) {
				tstamp = parseTimestamp(timestamp.text(), previous);	
				timestamp.remove();
			}
			
			if (workingSession == null) {
				workingSession = new Session(tstamp.toLocalDate());
			}
			
			for (Element byline : message.getElementsByClass("by")) {
				by = byline.html();
				by = by.substring(0, by.length()-1);
				byline.remove();
			}
			
			for (Element avatarLine : message.getElementsByClass("avatar")) {
				avatar = avatarLine.html().replace("<img src=\"", "").replace("\">", "");
				avatarLine.remove();
			}
			
			if (message.hasClass("emote")) {
				if (workingMessage != null) {
					addMessageToSession(workingMessage, lastMessageTime);
					lastMessageTime = workingMessage.getTimestamp();
				}
				workingMessage = new Message(by, avatar, lastMessageTime);
				workingMessage.emote = true;
				text = message.html();
				workingMessage.addLine(text);
				addMessageToSession(workingMessage, lastMessageTime);
				workingMessage = null;
			}
			
			if (message.hasClass("desc")) {
				if (workingMessage != null) {
					addMessageToSession(workingMessage, lastMessageTime);
					lastMessageTime = workingMessage.getTimestamp();
				}
				workingMessage = new Message(null, null, lastMessageTime);
				workingMessage.desc = true;
				text = message.html();
				workingMessage.addLine(text);
				addMessageToSession(workingMessage, lastMessageTime);
				workingMessage = null;
			}
			
			text = message.html();
						
			if (by != null && tstamp != null) {
				if (workingMessage != null) {
					addMessageToSession(workingMessage, lastMessageTime);
					lastMessageTime = workingMessage.getTimestamp();
				}
					
				workingMessage = new Message(by, avatar, tstamp);
			}
			
			if(workingMessage != null) {
				workingMessage.addLine(text);
			}
			
			if (tstamp != null) {
				previous = tstamp;
			}
		}
		addMessageToSession(workingMessage, lastMessageTime);
		nextSession(workingMessage.getTimestamp().toLocalDate());
		
		log = new Log(sessions, title);
		
		sessions = new LinkedList<Session>();
		title = null;
		workingSession = null;
		workingMessage = null;
		
		return log;
	}
	
	public Document load(File htmlFile) {
		FileInputStream file = null;
		
		try {
			file = new FileInputStream(htmlFile);
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
			return null;
		}
		
		Document doc = null;
		
		try {
			doc = Jsoup.parse(file, null, "");
		} catch (IOException e) {
			System.out.println("Parse failed.");
			return null;
		}
		
		return doc;
	}
}

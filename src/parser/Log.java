package parser;

import java.util.List;

public class Log {
	List<Session> sessions;
	String title;
	public Log(List<Session> sessions, String title) {
		super();
		this.sessions = sessions;
		this.title = title;
	}
	public String getTitle() {
		return title;
	}
	public List<Session> getSessions() {
		return sessions;
	}

}

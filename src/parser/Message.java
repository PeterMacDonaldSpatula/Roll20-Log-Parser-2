package parser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Message {
	String speaker;
	String avatarAddress;
	LocalDateTime timestamp;

	List<String> messages;
	boolean emote;
	boolean desc;
	
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	
	public Message(String speaker, String avatarAddress, LocalDateTime timestamp, boolean emote, boolean desc) {
		super();
		this.speaker = speaker;
		this.avatarAddress = avatarAddress;
		this.timestamp = timestamp;
		this.emote = emote;
		this.desc = desc;
		this.messages = new ArrayList<String>();
	}

	public Message(String speaker, String avatarAddress, LocalDateTime timestamp, List<String> messages, boolean emote,
			boolean desc) {
		super();
		this.speaker = speaker;
		this.avatarAddress = avatarAddress;
		this.timestamp = timestamp;
		this.messages = messages;
		this.emote = emote;
		this.desc = desc;
	}

	public Message(String speaker, String avatarAddress, LocalDateTime timestamp, List<String> messages) {
		super();
		this.speaker = speaker;
		this.avatarAddress = avatarAddress;
		this.timestamp = timestamp;
		this.messages = messages;
		this.emote = false;
		this.desc = false;
	}

	public Message(String speaker, String avatarAddress, LocalDateTime timestamp) {
		super();
		this.speaker = speaker;
		this.avatarAddress = avatarAddress;
		this.timestamp = timestamp;
		this.messages = new ArrayList<String>();
		this.emote = false;
		this.desc = false;
	}

	public String getSpeaker() {
		return speaker;
	}

	public String getAvatarAddress() {
		return avatarAddress;
	}

	public List<String> getMessages() {
		return messages;
	}

	public boolean isEmote() {
		return emote;
	}

	public boolean isDesc() {
		return desc;
	}

	public void addLine(String line) {
		messages.add(line);
	}
	
	public void modifyAvatarPath(String oldPath) {
		//System.out.println(oldPath);
		avatarAddress = avatarAddress.replace(oldPath, "res/img");
	}

	@Override
	public String toString() {
		return "Message [speaker=" + speaker + ", avatarAddress=" + avatarAddress + ", timestamp=" + timestamp
				+ ", messages=" + messages + ", emote=" + emote + ", desc=" + desc + "]";
	}
	
	
}

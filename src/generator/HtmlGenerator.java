package generator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import parser.Log;
import parser.Message;
import parser.Parser;
import parser.Session;

public class HtmlGenerator {
	private String generateHeader(String title, String styleSheet) {
		String output = 
				"<head>\n"
					+ "<title>" + title +"</title>\n"
					+ "<link rel=\"stylesheet\" href=\"res/style/" + styleSheet + "\">"
				+ "</head>\n";
		
		return output;
	}
	
	private String generateMessage(Message message) {
		if (message.isDesc()) {
			return generateDescMessage(message);
		} else if (message.isEmote()) {
			return generateEmoteMessage(message);
		} else {
			return generateNormalMessage(message);
		}
	}
	
	private String generateEmoteMessage(Message message) {
		String output;
		if (message.getAvatarAddress().isEmpty()) {
			output = "<div class=\"message emote\">\n";
		} else {
			output = 
					"<div class=\"message emote\">\n"
							+ "<div class=\"avatar\">\n"
							+ "<img class=\"avatar\" src=\"" + message.getAvatarAddress() + "\">\n"
							+ "</div>\n";
		}

		List<String> messageText = message.getMessages();
		
		for (String text : messageText) {
			output += text + "<br>\n";
		}
		output += "</div>\n";
		return output;
	}
	
	private String generateDescMessage(Message message) {
		String output = 
				"<div class=\"message desc\">\n";

		List<String> messageText = message.getMessages();
		
		for (String text : messageText) {
			output += text + "<br>\n";
		}
		output += "</div>\n";
		return output;
	}
	
	private String generateNormalMessage(Message message) {
		String output;
		
		if (message.getAvatarAddress().isEmpty()) {
			output = 
					"<div class=\"message normal\">\n"
							+ "<span class=\"speaker\">" + message.getSpeaker() + ":</span>";
		} else {
			output = 
					"<div class=\"message normal\">\n"
							+ "<div class=\"avatar\">\n"
								+ "<img src=\"" + message.getAvatarAddress() + "\">\n"
							+ "</div>"
							+ "<span class=\"speaker\">" + message.getSpeaker() + ":</span>";
		}

		List<String> messageText = message.getMessages();
		
		for (String text : messageText) {
			output += text + "<br>\n";
		}
		output += "</div>\n";
		return output;
	}
	
	private String generateBody(List<Message> messages) {
		String output = "<body>\n";
		for(Message message: messages) {
			output += generateMessage(message);
		}
		output += "</body>\n";
		return output;
	}
	
	private String generateFooter(int sessionNum, boolean first, boolean last) {
		String output = "<div class =\"navigation\">";
		if (!first) {
			output += "<a href=\"" + String.format("%04d.html", sessionNum - 1) + "\">Previous</a> |";
		}
		output += "<a href=\"index.html\">Table of Contents</a>";
		if (!last) {
			output += " | <a href=\"" + String.format("%04d.html", sessionNum + 1) + "\">Next</a>";
		}
		output += "</div>";
		
		return output;
	}
	
	private String generateSession(Session session, String logTitle, String styleSheet, int sessionNum, boolean first, boolean last) {
		String output = "<html>\n" + generateHeader(logTitle + " - " + session.getTitle(), styleSheet);
		output += generateBody(session.getMessages());
		output += generateFooter(sessionNum, first, last);
		output += "</html>";
		
		return output;
	}
	
	private String getImagePath(String avatar) {
		if (avatar == null)
			return null;
				
		return avatar.substring(0, avatar.indexOf('/'));
	}
	
	private void moveImages(Log log) throws IOException {
		List<Session> sessions = log.getSessions();
		String path = null;
		int sessionCount = 0, messageCount = 0;
		while(path == null) {
			if(messageCount < sessions.get(sessionCount).getMessages().size()-1) {
				messageCount++;
				continue;
			} else {
				if (sessionCount < sessions.size()-1) {
					sessionCount++;
					messageCount = 0;
				} else {
					return;
				}
			}
			System.out.println(sessions.get(sessionCount).getMessages().size());
			path = getImagePath(sessions.get(sessionCount).getMessages().get(messageCount).getAvatarAddress());
		}
		
		System.out.println(path);
		
		for(Session session : sessions) {
			for(Message message : session.getMessages()) {
				if (message.getAvatarAddress() != null) {
					message.modifyAvatarPath(path);
				}
			}
		}
		
		File folder = new File(path);
		File[] files = folder.listFiles();
		
		Files.createDirectories(Paths.get("output/" + log.getTitle() + "/res/img"));
		
		for(File image : files) {
			Files.copy(Paths.get(image.getPath()), Paths.get("output/" + log.getTitle() + "/res/img/" + image.getName()), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
		}
	}
	
	private void moveStyleSheet(String title, String styleSheet) {
		try {
			Files.createDirectories(Paths.get("output/" + title + "/res/style"));
		} catch (IOException e) {
			System.out.println("Error establishing /res/style");
		}
		
		try {
			Files.copy(Paths.get(styleSheet), Paths.get("output/" + title + "/res/style/" + Paths.get(styleSheet).getFileName().toString()));
		} catch (IOException e) {
			System.out.println("Error copying stylesheet");
		}
	}
	
	private String generateTocHeader(String title, String styleSheet) {
		String output = 
				"<head>\n"
					+ "<title>" + title +"</title>\n"
					+ "<link rel=\"stylesheet\" href=\"res/style/" + styleSheet + "\">"
				+ "</head>\n"
				+ "<body>";
		
		return output;
	}
	
	private String generateTocEntry(int sessionNum, String title) {
		return sessionNum + ". <a href=\"" +String.format("%04d.html", sessionNum)+  "\">" + title + "</a><br>\n";
	}
	
	private String generateTocFooter() {
		return "</body>\n</html>";
	}
	
	public void generate(Log log, String styleSheet) throws IOException{
		moveImages(log);
		moveStyleSheet(log.getTitle(), styleSheet);
		
		List<Session> sessions = log.getSessions();
		
		int counter = 0;
		
		String toc = generateTocHeader(log.getTitle(), styleSheet);
		
		for (Session session : sessions) {
			counter++;
			String html;
			if (counter == 1) {
				html = generateSession(session, log.getTitle(), styleSheet, counter, true, false);
			} else if (counter == sessions.size()) {
				html = generateSession(session, log.getTitle(), styleSheet, counter, false, true);
			} else {
				html = generateSession(session, log.getTitle(), styleSheet, counter, false, false);
			}
			String fileName = String.format("output/%s/%04d.html", log.getTitle(), counter);
			FileWriter file = new FileWriter(fileName);
			file.write(html);
			file.close();
			
			toc += generateTocEntry(counter, session.getTitle());
		}
		
		toc += generateTocFooter();
		
		FileWriter tocFile = new FileWriter(String.format("output/%s/index.html", log.getTitle()));
		tocFile.write(toc);
		tocFile.close();
	}
	
	public void zip(File destination, String dir) {
		List<File> fileList = new ArrayList<File>();
		File dirToZip = new File(dir);
		getAllFiles(dirToZip, fileList);
		
		try {
			FileOutputStream fos = new FileOutputStream(destination);
			ZipOutputStream zos = new ZipOutputStream(fos);
			
			for (File file : fileList) {
				if (!file.isDirectory()) {
					addToZip(dirToZip, file, zos);
				}
			}
			zos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteTempDir(String dir) {
		File file = new File(dir);
		try {
			delete(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void delete(File file) throws IOException {
		if (file.isDirectory()) {
			if (file.list().length==0) {
				file.delete();
			} else {
				String files[] = file.list();
				
				for (String temp : files) {
					File fileDelete = new File(file, temp);
					
					delete(fileDelete);
				}
				if (file.list().length==0) {
					file.delete();
				}
			}
		} else {
			file.delete();
		}
	}
	
	private void addToZip(File dirToZip, File file, ZipOutputStream zos) throws FileNotFoundException, IOException {
		FileInputStream fis = new FileInputStream(file);
		
		String zipFilePath = file.getCanonicalPath().substring(dirToZip.getCanonicalPath().length() + 1, file.getCanonicalPath().length());
		
		ZipEntry zipEntry = new ZipEntry(zipFilePath);
		zos.putNextEntry(zipEntry);
		
		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zos.write(bytes, 0, length);
		}

		zos.closeEntry();
		fis.close();
	}
	
	private void getAllFiles(File dir, List<File> fileList) {
		File[] files = dir.listFiles();
		for (File file : files) {
			fileList.add(file);
			if (file.isDirectory()) {
				getAllFiles(file, fileList);
			}
		}
	}
	
	public static void main(String [] args) throws IOException {
		Parser parser = new Parser();
		
		if (args.length == 0) {
			System.out.println("Please run this program with the file name as the first parameter.");
			System.exit(0);
		}
		
		FileInputStream file = null;
		
		try {
			file = new FileInputStream(args[0]);
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
			System.exit(0);
		}
		
		Document doc = null;
		
		try {
			doc = Jsoup.parse(file, null, "");
		} catch (IOException e) {
			System.out.println("Parse failed.");
			System.exit(0);
		}
		
		Log log = parser.parse(doc, "VXM");
		
		HtmlGenerator generator = new HtmlGenerator();
		
		generator.generate(log, "style.css");
		generator.zip(new File("output.zip"), "output/VXM");
		generator.deleteTempDir("output/VXM");
		
		//System.out.println(generator.generateSession(log.getSessions().get(3)));
	}
}
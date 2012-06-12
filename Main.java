import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextArea;


public class Main extends Thread{
	URLConnection url, loginUrl;
	BufferedReader in , inpsd;
	ArrayList<String> usernames = new ArrayList<String>();
	ArrayList<String> wordlist = new ArrayList<String>();
	
	private String isOnline = "true";   // Leave blank for both. true for online and false for offline only
	private int sex = 1;   // Should be set. 1 for men and 2 for woman
	private String username = "";
	private String password = "";
	private final String dummy = "Kode";
	private String subject;
	
	//\n\r == linjeskift
	private String besked = "";
	private boolean usesProxy = false;
			
	
	Pattern pattern = 
		    Pattern.compile("<b>[a-zA-Z\\w\\d\\søæåØÆÅ]+</b>");
	
	public Main(String username, String password, int sex, String isOnline, String besked, String subject, boolean proxy){
		this.username = username;
		this.password = password; 
		this.sex = sex;
		this.isOnline = isOnline;
		this.besked = besked + "\r\n\r\nDenne besked er sendt med NS Mailsender. www.nhhacks.x10.mx";    // This makes sure my commercial gets added to every message
		this.subject = subject;
		this.usesProxy = proxy;
		
	}
	
	
	
	@Override
	public void run(){
		System.out.println("NS Mailsender");
		startf();
	}
	
	public void startf(){
		// "http://www.n.dk/community/mail/frMailList.asp?M=2");
		//<>
		besked = URLEncoder.encode(besked);
		username = URLEncoder.encode(username);
		password = URLEncoder.encode(password);
		subject = URLEncoder.encode(subject);
		
		
		
		java.net.CookieManager cm = new java.net.CookieManager();
		java.net.CookieHandler.setDefault(cm);
		
		initWordlist();
		login();
		connect();
		sendMails();
		cleanAndClose();
	}
	private void initWordlist(){
		
		// Word list initializing for searching. If you want to search for users with special characters in their names you just add them here
		wordlist.add("1");
		wordlist.add("2");
		wordlist.add("3");
		wordlist.add("4");
		wordlist.add("5");
		wordlist.add("6");
		wordlist.add("7");
		wordlist.add("8");
		wordlist.add("9");
		wordlist.add("a");
		wordlist.add("b");
		wordlist.add("c");
		wordlist.add("d");
		wordlist.add("e");
		wordlist.add("f");
		wordlist.add("g");
		wordlist.add("h");
		wordlist.add("i");
		wordlist.add("j");
		wordlist.add("k");
		wordlist.add("l");
		wordlist.add("m");
		wordlist.add("n");
		wordlist.add("o");
		wordlist.add("p");
		wordlist.add("q");
		wordlist.add("r");
		wordlist.add("s");  
		wordlist.add("t");
		wordlist.add("u");
		wordlist.add("v");
		wordlist.add("w");
		wordlist.add("x");
		wordlist.add("y");
		wordlist.add("z");
		wordlist.add("æ");
		wordlist.add("ø");
		wordlist.add("å");
	}
	
	private void connect(){
		int fetchedUsers = 0;
		try {
			nsmailsendUI.jTextArea2.append("\nTilslutter");
			
			for(int b = 0; b<wordlist.size() ; b++){
				
				String let = wordlist.get(b);
				
					
				// nsmailsendUI.jTextArea2
				for(int a = 1; a<150 ; a++){
					fetchedUsers = usernames.size();
					nsmailsendUI.jTextArea2.append("\nGår igennem side: " + a  + "  Char: " + let);
					nsmailsendUI.jTextArea2.setCaretPosition(nsmailsendUI.jTextArea2.getDocument().getLength());
					url = new URL("http://www.n.dk/community/search/adSearch.asp?name="+let+"&age=&sex="+sex+"&isOnline="+isOnline +
							"&region=&hasApp=undefined&hasPic=undefined&hasDiary=undefined&sort=0&page=" + a).openConnection();
					in = new BufferedReader(new InputStreamReader(url.getInputStream()));
					harvestUsernames();
					
					if(usernames.size()==fetchedUsers){
						break;
					}
				
				}
			
			}
			
			
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void harvestUsernames(){
		
		String inputline;
		
		
		try {
			while((inputline = in.readLine()) != null){
		
			            Matcher matcher = 
					            pattern.matcher(inputline);
			            if(matcher.find()){
			            	String username = matcher.group();
			            	
			            	
			            	username = username.substring(3, username.length()-4);
			            	username = URLEncoder.encode(username);
			            	
			            
			            	usernames.add(username);
			            	nsmailsendUI.jTextArea2.append("\nTilføjer: " + username);
			            	nsmailsendUI.jTextArea2.setCaretPosition(nsmailsendUI.jTextArea2.getDocument().getLength());
			            }
				
			}
		
			nsmailsendUI.jTextArea2.append("\nBrugere fundet: " + usernames.size());
			nsmailsendUI.jTextArea2.setCaretPosition(nsmailsendUI.jTextArea2.getDocument().getLength());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void sendMails(){
		nsmailsendUI.jTextArea2.append("\nStarter med at sende mails...");
		nsmailsendUI.jTextArea2.setCaretPosition(nsmailsendUI.jTextArea2.getDocument().getLength());
		int succesfull = 0;
		try {
			for(String username : usernames){
			
				nsmailsendUI.jTextArea2.append("\nSender mail til: " + username );
				nsmailsendUI.jTextArea2.setCaretPosition(nsmailsendUI.jTextArea2.getDocument().getLength());
				String data = "txtTil=" +username+ "&subject="+subject+"&msg="+besked+"&image1.x=13&image1.y=13";
			
				loginUrl = new URL("http://www.n.dk/community/mail/newMsg.asp").openConnection();
				loginUrl.setDoOutput(true);
				OutputStreamWriter writer = new OutputStreamWriter(loginUrl.getOutputStream());
				
					//Write parameters to url
					writer.write(data);
					writer.flush();
					
					// Only for debugging purpose!
					String inputline;
					inpsd = new BufferedReader(new InputStreamReader(loginUrl.getInputStream()));
					while((inputline = inpsd.readLine()) != null){
						//System.out.println(inputline);  For debugging only	
					}
					succesfull += 1;
			}
			nsmailsendUI.jTextArea2.append("\nHar succesfuldt sendt til: " + succesfull + " brugere");
			nsmailsendUI.jTextArea2.setCaretPosition(nsmailsendUI.jTextArea2.getDocument().getLength());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	private void login(){
		try {
			nsmailsendUI.jTextArea2.append("\nLogger ind på netstationen");
			nsmailsendUI.jTextArea2.setCaretPosition(nsmailsendUI.jTextArea2.getDocument().getLength());
			if(usesProxy){
				nsmailsendUI.jTextArea2.append("\nBruger proxy");
				nsmailsendUI.jTextArea2.setCaretPosition(nsmailsendUI.jTextArea2.getDocument().getLength());
				String ip = nsmailsendUI.jTextField2.getText();
				int port = Integer.parseInt(nsmailsendUI.jTextField3.getText());
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
				url = new URL("http://www.n.dk/community/login/login.asp?username=" + username + "&pdummy=" + dummy + "&password=" + password).openConnection(proxy);
				
			}else{
				nsmailsendUI.jTextArea2.append("\nBruger ikke proxy");
				nsmailsendUI.jTextArea2.setCaretPosition(nsmailsendUI.jTextArea2.getDocument().getLength());
				url = new URL("http://www.n.dk/community/login/login.asp?username=" + username + "&pdummy=" + dummy + "&password=" + password).openConnection();
			}
			inpsd = new BufferedReader(new InputStreamReader(url.getInputStream()));
			String inline;
			while((inline = inpsd.readLine()) != null){
				//System.out.println(inline);   //UNCOMMENT FOR DEBUGGING   // Prints response from login site
			}
			
			
			
			// There is no actual check that you really are logged on. 
			nsmailsendUI.jTextArea2.append("\nSuccesfuldt logget på!");
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void cleanAndClose(){
		try {
			inpsd.close();
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}


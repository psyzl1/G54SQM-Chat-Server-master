package g54ubi.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class Client implements Runnable {
	private static final String address = "localhost";
	private static final int port = 9000;
	private String userName = null;
	private String message = null;
	private String helloMSG = null;
	private BufferedReader in;
	private PrintWriter out;
	private Socket serverSocket = null;
	private String serverResult = null;
	
	public Client(String name){
		this.userName = name;
		try {
			serverSocket = new Socket(address,port);
			in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
			out = new PrintWriter(serverSocket.getOutputStream(),true);
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void run(){
		String result = null;
		try {
			do{
				this.helloMSG = in.readLine();
				System.out.println("The client get the helloMSG of " + this.helloMSG);
			}while(helloMSG == null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(true){
			try {
				result = in.readLine();
				if(result != null){
					this.serverResult = result;
					System.out.println(this.userName + ": Client get the response of: " + serverResult);
					Thread.sleep(100);
				}
//				System.out.println("Client get response of " + result);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}
	
	public String getHelloMSG(){return this.helloMSG;}
	
	public void setMessage(String msg){
		System.out.println(this.userName + ": Client has accept the message of " + msg);
		out.println(msg);
	}
		
	public String getServerResponese(){return this.serverResult;}
}


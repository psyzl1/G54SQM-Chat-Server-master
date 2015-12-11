package g54ubi.chat.server;

import static org.junit.Assert.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

public class Test1 {
	private static Server myServer = null;
	private static Thread serverThread = null;
	
	@BeforeClass
	public static void setUp(){
		serverThread = new Thread(){
			public void run(){myServer = new Server(9000);}
		};
		serverThread.start();
		System.out.println("Server Socket hase been opened.");	
	}
	
	@Test
	public void helloMSGTest() {
		System.out.println("+++++++++++++++++++++++++helloMSGTest++++++++++++++++++++++++++++++++++++++");
		Client user = new Client("user1");
		Thread connection = new Thread(user);
		connection.start();
		Client user2 = new Client("user2");
		Thread connection2 = new Thread(user2);
		connection2.start();
		waitForHello(user);
		waitForHello(user2);
		String resultFinal = user.getHelloMSG();
		String resultFinal2 = user2.getHelloMSG();
		System.out.println("Here is the result: " + resultFinal);
		System.out.println("Here is the result2 " + resultFinal2);
		user2.setMessage("QUIT");
		user.setMessage("QUIT");
		boolean correct = (resultFinal.equals("OK Welcome to the chat server, there are currelty 1 user(s) online")
						&& resultFinal2.equals("OK Welcome to the chat server, there are currelty 2 user(s) online"));
		assertTrue("True",correct);
	}

	@Test 
	public void lessMSGTest(){
		System.out.println("++++++++++++++++++++++++++LessMSGTest++++++++++++++++++++++++++++++++++++++");
		Client user = new Client("user2");
		Thread connection = new Thread(user);
		connection.start();
		waitForHello(user);
		user.setMessage("AAA");
		String result = null;
		result = getResponse(user);
		System.out.println("LessMSGTEST get respone of " + result);
		user.setMessage("QUIT");
		assertEquals("BAD invalid command to server",result);
	}
	
	@Test
	public void badMSGTest(){
		System.out.println("+++++++++++++++++++++++++++badMSGTest++++++++++++++++++++++++++++++++++++++");
		Client user = new Client("user3");
		Thread connection = new Thread(user);
		connection.start();
		waitForHello(user);
		user.setMessage("ASDFGASADF");
		String result = null;
		result = getResponse(user);	 
		System.out.println("BadMSGTEST get respone of " + result);
		user.setMessage("QUIT");
		assertEquals("BAD command not recognised",result);
	}
	
	@Test 
	public void statusUnRegistered(){
		System.out.println("+++++++++++++++++++++++++++statusUnRegistered++++++++++++++++++++++++++++++++");
		Client user = new Client("user4");
		Thread connection = new Thread(user);
		connection.start();
		waitForHello(user);
		user.setMessage("STAT");
		String result = null;
		result = getResponse(user);	
		System.out.println("statusUnRegistered get the response of " + result);
		String prediectResult = "OK There are currently 1 user(s) on the server You have not logged in yet";
		user.setMessage("QUIT");
		assertEquals(prediectResult,result);
	}
	
	@Test 
	public void statusRegistered(){
		System.out.println("+++++++++++++++++++++++++++statusRegistered++++++++++++++++++++++++++++++++++");
		Client user = new Client("user5");
		Thread connection = new Thread(user);
		connection.start();
		waitForHello(user);
		user.setMessage("IDEN user5");
		String result = null;
		result = getResponse(user);	
		System.out.println("statusRegistered registering : " + result);
		user.setMessage("STAT");
		String result2 = result;
		while(result2.equals(result)){
			result2 = user.getServerResponese();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
		System.out.println("statusRegistered get the response of " + result);
		String predictedResult = "OK There are currently 1 user(s) on the server You are logged im and have sent 0 message(s)";
		user.setMessage("QUIT");
		assertEquals(predictedResult,result2);
	}
	
	@Test
	public void listUnRegistered(){
		System.out.println("+++++++++++++++++++++++++++listUnRegistered++++++++++++++++++++++++++++++++++");
		Client user = new Client("user6");
		Thread connection = new Thread(user);
		connection.start();
		waitForHello(user);
		user.setMessage("LIST");
		String result = null;
		result = getResponse(user);	
		System.out.println("listUnRegistered get the response of " + result);
		user.setMessage("QUIT");
		assertEquals("BAD You have not logged in yet" , result);
		
	}
	
	
	
	@Test
	public void listRegistered(){
		System.out.println("+++++++++++++++++++++++++++listRegistered++++++++++++++++++++++++++++++++++++");
		Client user1 = new Client("user7");
		Client user2 = new Client("user8");
		Thread connection1 = new Thread(user1);
		Thread connection2 = new Thread(user2);
		connection1.start();
		connection2.start();
		waitForHello(user1);
		waitForHello(user2);
		String user1result = null;
		String user2result = null;
		user1.setMessage("IDEN user7");
		user2.setMessage("IDEN user8");
		user1result = getResponse(user1);	
		user2result = getResponse(user2);	
		System.out.println("The user1result is " + user1result + "\nThe user2result is " + user2result);
		user1.setMessage("LIST");
		String user1result2 = user1result;
		while(user1result2.equals(user1result)){
			user1result2 = user1.getServerResponese();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
		System.out.println("The user1 list result is " + user1result2);
		String predictedResult1 = "OK user7, user8, ";
		String predictedResult2 = "OK user8, user7, ";
		System.out.println("Just for test: " + predictedResult1.indexOf("," , 17 ));
		boolean result = user1result2.equals(predictedResult1) || user1result2.equalsIgnoreCase(predictedResult2);
		user1.setMessage("QUIT");
		user2.setMessage("QUIT");
		assertTrue(user1result , result);
		
	}
	
	@Test 
	public void idenOneThreadOneUserName(){
		System.out.println("+++++++++++++++++++++++++++idenOneThreadOneUserName+++++++++++++++++++++++++");
		Client user = new Client("user9");
		Thread connection = new Thread(user);
		connection.start();
		waitForHello(user);
		user.setMessage("IDEN User4");
		String result = null;
		result = getResponse(user);		
		System.out.println("idenOneThreadOneUserName get respone of " + result);
		user.setMessage("QUIT");
		assertEquals("OK Welcome to the chat server User4",result);
		
	}
	
	@Test
	public void idenOneThreadTwoUserName(){
		System.out.println("+++++++++++++++++++++++++++idenOneThreadTwoUserName+++++++++++++++++++++++++");
		Client user = new Client("user10");
		Thread connection = new Thread(user);
		connection.start();
		waitForHello(user);
		user.setMessage("IDEN User5");
		String firstResult = null;
		firstResult = getResponse(user);	
		System.out.println("This is the oneThreadTwoUserName first result: " + firstResult);
		user.setMessage("IDEN User6");
		String secondResult = firstResult;
		while(firstResult.equals(secondResult)){
			secondResult = user.getServerResponese();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
		System.out.println("idenOneThreadTwoUserName get respone of " + secondResult);
		user.setMessage("QUIT");
		assertEquals("BAD you are already registerd with username User5" , secondResult);
		
	}
	
	@Test
	public void idenTwoThreadOneUserName(){
		System.out.println("+++++++++++++++++++++++++++idenTwoThreadOneUserName++++++++++++++++++++++++++");
		Client user1 = new Client("user11");
		Client user2 = new Client("user12");
		Thread connection1 = new Thread(user1);
		Thread connection2 = new Thread(user2);
		connection1.start();
		connection2.start();
		waitForHello(user1);
		waitForHello(user2);
		user1.setMessage("IDEN User7");
		String user1result = null;
		user1result = getResponse(user1);	
		System.out.println("idenTwoThreadOneUserName get the first responese of "+user1result );
		user2.setMessage("IDEN User7");
		String user2result = null;
		user2result = getResponse(user2);
		System.out.println("idenTwoThreadOneUserName get the second responese of "+user2result );
		user1.setMessage("QUIT");
		user2.setMessage("QUIT");
		assertEquals("BAD username is already taken",user2result);
		
	}
	
	@Test
	public void hailNoRegistered(){
		System.out.println("+++++++++++++++++++++++++++hailNoRegistered++++++++++++++++++++++++++++++++++");
		Client user = new Client("user13");
		Thread connection = new Thread(user);
		connection.start();
		waitForHello(user);
		user.setMessage("HAIL Hello World!");
		String result = null;	
		result = getResponse(user);
		System.out.println("hailNoRegistered get the response of " + result);
		user.setMessage("QUIT");
		assertEquals("BAD You have not logged in yet",result);
		
	}
	

	@Test
	public void hailRegistered(){
		System.out.println("+++++++++++++++++++++++++++hailRegistered++++++++++++++++++++++++++++++++++++");
		Client user1 = new Client("user14");
		Client user2 = new Client("user15");
		Client user3 = new Client("user16");
		Thread connection1 = new Thread(user1);
		Thread connection2 = new Thread(user2);
		Thread connection3 = new Thread(user3);
		connection1.start();
		connection2.start();
		connection3.start();
		waitForHello(user1);
		waitForHello(user2);
		waitForHello(user3);
		user1.setMessage("IDEN user14");
		String user1result = null;
		user1result = getResponse(user1);
		user2.setMessage("IDEN user15");
		String user2result = null;
		user2result = getResponse(user2);
		user3.setMessage("IDEN user15");
		String user3result = null;
		user3result = getResponse(user3);
		user1.setMessage("HAIL Hello World");
		String user1result2 = user1result;
		while(user1result2.equals(user1result))user1result2 = user1.getServerResponese();
		System.out.println("The second result of user1 is " + user1result2);
		String user2result2 = user2result;
		while(user2result2.equals(user2result))user2result2 = user2.getServerResponese();
		System.out.println("The second result of user2 is " + user2result2);
		String user3result2 = user3result;
		while(user3result2.equals(user3result))user3result2 = user3.getServerResponese();
		System.out.println("The second result of user3 is " + user3result2);
		String predited = "Broadcast from user14: Hello World";
		boolean bool = user1result2.equals(predited) && user2result2.equals(predited) && user3result2.equals(predited);
		user1.setMessage("QUIT");
		user2.setMessage("QUIT");
		user3.setMessage("QUIT");
		assertTrue(user1result2,bool);
	}
	
	@Test
	public void quitUnRegistered(){
		System.out.println("+++++++++++++++++++++++++++quitUnRegistered++++++++++++++++++++++++++++++++++");
		Client user = new Client("user17");
		Thread connection = new Thread(user);
		connection.start();
		waitForHello(user);
		user.setMessage("QUIT");
		String result = getResponse(user);
		System.out.println("quitUnRegistered get the response of " + result);
		assertEquals("OK goodbye" , result);
	}
	
	@Test 
	public void quitRegistered(){
		System.out.println("+++++++++++++++++++++++++++quitRegistered++++++++++++++++++++++++++++++++++++");
		Client user = new Client("user18");
		Thread connection = new Thread(user);
		connection.start();
		waitForHello(user);
		user.setMessage("IDEN user18");
		String result1 = getResponse(user);
		System.out.println("quitRegistered got the response of registeration： " + result1);
		String result2 = result1;
		user.setMessage("QUIT");
		while(result2.equals(result1))result2 = getResponse(user);
		System.out.println("quitRegistered got he response of quit: " + result2);
		assertEquals("OK thank you for sending 0 message(s) with the chat service, goodbye. " , result2);
	}
	
	@Test 
	public void msgUnRegistered(){
		System.out.println("+++++++++++++++++++++++++++msgUnRegistered++++++++++++++++++++++++++++++++++++");
		Client user = new Client("user19");
		Thread connection = new Thread(user);
		connection.start();
		waitForHello(user);
		user.setMessage("MESG user18 HelloWorl");
		String result = getResponse(user);
		System.out.println("msgUnRegistered got the response of： " + result);
		user.setMessage("QUIT");
		assertEquals("BAD You have not logged in yet" , result);
	}
	
	@Test 
	public void msgBadMessage(){
		System.out.println("+++++++++++++++++++++++++++msgUnRegistered++++++++++++++++++++++++++++++++++++");
		Client user = new Client("user20");
		Thread connection = new Thread(user);
		connection.start();
		waitForHello(user);
		user.setMessage("IDEN user20");
		String result1 = getResponse(user);
		System.out.println("msgBadMessage get the response of registeration of :" + result1);
		String result2 = result1;
		user.setMessage("MESG user20helloworld");
		while(result2.equals(result1))result2 = getResponse(user);
		System.out.println("msgBadMessage get the response of:" + result2);
		user.setMessage("QUIT");
		assertEquals("BAD Your message is badly formatted" , result2);
	}
	
	@Test
	public void msgUserUnFound(){
		System.out.println("+++++++++++++++++++++++++++msgUserUnFound+++++++++++++++++++++++++++++++++++++");
		Client user = new Client("user21");
		Thread connection = new Thread(user);
		connection.start();
		waitForHello(user);
		user.setMessage("IDEN user21");
		String result1 = getResponse(user);
		System.out.println("msgUserUnFound get the response of registeration of:" + result1);
		String result2 = result1;
		user.setMessage("MESG user200 HelloWorld");
		while(result2.equals(result1))result2 = getResponse(user);
		System.out.println("msgUserUnFound get the response of " + result2);
		user.setMessage("QUIT");
		assertEquals("BAD the user does not exist" , result2);
	}
	
	@Test 
	public void msgNormalMessage(){
		System.out.println("+++++++++++++++++++++++++++msgNormalMessage++++++++++++++++++++++++++++++++++++");
		Client user1 = new Client("user22");
		Client user2 = new Client("user23");
		Thread connection1 = new Thread(user1);
		Thread connection2 = new Thread(user2);
		connection1.start();
		connection2.start();
		waitForHello(user1);
		waitForHello(user2);
		user1.setMessage("IDEN user22");
		user2.setMessage("IDEN user23");
		String user1result1 = getResponse(user1);
		String user2result1 = getResponse(user2);
		System.out.println("msgNormalMessage get the response of registeration of user1 of: " + user1result1);
		System.out.println("msgNormalMessage get the response of registeration of user2 of: " + user2result1);
		user1.setMessage("MESG user23 HelloWorld!");
		String user1result2 = user1result1;
		String user2result2 = user2result1;
		while(user2result2.equals(user2result1))user2result2 = getResponse(user2);
		while(user1result2.equals(user1result1))user1result2 = getResponse(user1);
		System.out.println("msgNormalMessage get the response of user1 of: " + user1result2);
		System.out.println("msgNormalMessage get the response of user2 of: " + user2result2);
		user1.setMessage("QUIT");
		user2.setMessage("QUIT");
		String user1Predict = "OK your message has been sent";
		String user2Predict = "PM from user22:HelloWorld!";
		boolean finalresult = user1Predict.equals(user1result2) && user2Predict.equals(user2result2);
		assertTrue("True" , finalresult);
		
	}
	

	public String getResponse(Client clientTest){
		String instant = null;
		while(instant == null){
			instant = clientTest.getServerResponese();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return instant;
	}
	
	public void waitForHello(Client userTest){
		while(userTest.getHelloMSG() == null){try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}}
	}
	
	
	
}

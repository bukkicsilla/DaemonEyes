package org.photos.demoneyes;

public class FakeDemon extends Thread {

	public void run(){
			
		
		try {
			for (int i = 0; i < 30000; i++)	
				System.out.println("it is fake" + i + " ");
		}
		catch (Exception e){}
		}//run
}

package org.photos.demoneyes;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.apache.http.entity.mime.content.*;
import org.apache.http.entity.mime.*;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.view.KeyEvent;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import android.content.DialogInterface;
import android.os.Looper;
import android.app.ProgressDialog;
import android.os.Handler;

import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.app.PendingIntent;
import android.app.NotificationManager;

public class ConnectionDemon extends AsyncTask<String, Void, Uri> {

	//final static String serverUrl = "http://nymt-one.user32.com:12484/dface/imgproc/1.0/";
	//final static String serverCheck = "http://nymt-one.user32.com:12484/dface/imgproc/1.0/check.php";
	//final static String serverDownload = "http://nymt-one.user32.com:12484/dface/imgproc/1.0/download.php";
	//final static String serverUpload = "http://nymt-one.user32.com:12484/dface/imgproc/1.0/upload.php";
	//final static String serverWrong = "http://numt-one.user32.com:12484/dface/wrong.php";
        
    
	final static String serverUrl = "http://v20.api.daemoneyes.com/";
	final static String serverCheck = "http://v20.api.daemoneyes.com/check.php";
	final static String serverDownload = "http://v20.api.daemoneyes.com/download.php";
	final static String serverUpload = "http://v20.api.daemoneyes.com/upload.php";
	
	
    static boolean serverError;
	static boolean imgDownloaded;
	boolean isConnected;
	
	static final String boundary = "1c2s3i5l6l7a4b8u9k0i";
	Uri imageUri;
	public static Uri demoneyesUri;
	public static String newdemoneyes;
	public static String storageDirectory;
	public static long demoneyesCount;
	static final int BLOCKSIZE = 20;
	File file;
	ContentBody imgfile;
	
	String deviceId;
	long imgFilesize;
	String postData;
	final String parameters = "&parameters=";
	StringEntity se;
	boolean imgexists;
	String imgHash;
	long imgid;

	Context context;
	public static ProgressDialog progressDialog;
	int notifId = 112;
	int notifIdConnection = 113;
	 
	public ConnectionDemon(Context context, String deviceId , long imgFilesize, Uri imageUri){
		serverError = false;
		imgDownloaded = false;
		this.context = context;
		this.deviceId =  deviceId;
		this.imgFilesize =  imgFilesize;
		this.imageUri = imageUri;
		//System.out.println("count before loading "+ demoneyesCount);
		//if (demoneyesCount != 0){
			loadCount();
			
		//}
		//System.out.println("count " + demoneyesCount);
		//System.out.println("image uri "+ imageUri.toString());
		//System.out.println("" + this.imageUri.toString());
		progressDialog = new ProgressDialog(context);
		//progressDialog =  ProgressDialog.show(context, "Please wait", "Loading please wait..", true);
		//progressDialog.setCancelable(true);
	
		
	}
	
public void saveCount(){
		
		String str = demoneyesCount+"";
		try
		{
			FileOutputStream fileout = context.openFileOutput("countDemoneyes.txt", context.MODE_PRIVATE);
			OutputStreamWriter osw = new OutputStreamWriter(fileout);
			osw.write(str);
			osw.flush();
			osw.close();
		}
	
		catch (IOException ex){
			ex.printStackTrace();		
		}
	}
	
	public void loadCount(){
		try{
			
			FileInputStream fileinput = context.openFileInput("countDemoneyes.txt");
			InputStreamReader isr = new InputStreamReader(fileinput);
			char [] inputBuffer = new char[BLOCKSIZE];
			String str = "";
			int charNum;
		while ( (charNum = isr.read(inputBuffer)) > 0){
			//System.out.println("charNum " + charNum);
			String str1 = String.copyValueOf(inputBuffer, 0, charNum);
			str += str1;
			inputBuffer = new char[BLOCKSIZE];
		}//while
		demoneyesCount = Long.parseLong(str);
		
		
		}
		catch (IOException ex){
			ex.printStackTrace();		
		}
		
	}
	private void md5HashImage(){
		long startTime = System.nanoTime();
		//System.out.println("start time " + startTime);
		
		try{
   		 //System.out.println("md5hash ****************" + imageUri);
   		 
   		 	InputStream ins = context.getContentResolver().openInputStream(imageUri);
   		 	//System.out.println("7777777777777777777777777");
	 		byte[] byteImage = getBytes(ins);
	 		 
	 		//getting hashcode 
	 		try {
	 			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
	 			byte[] array = md.digest(byteImage);
	 			StringBuffer sb = new StringBuffer();
	 			for (int i = 0; i < array.length; ++i) {
	 				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
	 			}
	 			imgHash =  sb.toString();        
	 			//System.out.println("hashcode for image " + imgHash + " length " + imgHash.length());
	 		} 
   		 
   		
   		catch (java.security.NoSuchAlgorithmException e) {
   		    	}
		}
   
   	 	catch (FileNotFoundException ex) {
   	 		System.err.println("FileNotFoundException!!");
   	 	}
   	 	catch (IOException ex) {
   	 		System.err.println("IOException!!");
   	 	} 
		long estimatedTime = System.nanoTime() - startTime;
		//System.out.println("estimated time " + estimatedTime);	
	}
	
	private byte[] getBytes(InputStream inputStream) throws IOException {
	      ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
	      int bufferSize = 1024;
	      byte[] buffer = new byte[bufferSize];

	      int len = 0;
	      while ((len = inputStream.read(buffer)) != -1) {
	        byteBuffer.write(buffer, 0, len);
	      }
	      return byteBuffer.toByteArray();
	    }

	/*******************************************************************************************************************************************/
    /*********************************************************************************************************************************************/
	public synchronized void checkRequestWithoutHash(){
		
		 System.out.println("check request without hash");
		 //ckech with deviceid and imgfilesize
		 try{
			JSONObject jsonpara =  new JSONObject();
			jsonpara.put("deviceid", deviceId);
			jsonpara.put("imgfilesize", imgFilesize);
			
			postData = parameters + jsonpara.toString();
			
			//System.out.println("json parameters " + postData);
			
			se = new StringEntity(postData);
			
		 }
		 catch (Exception e){
			 e.printStackTrace();
             		 
		 }
	 
	      //send request
		 	 
		 Thread t = new Thread(){
	     public void run() {
				 
	     //Looper.prepare();
	  
		 //System.out.println("before sending request");
			 
			 try{
				 
			 //System.out.println("before sending request");
			 
			 HttpParams myParams = new BasicHttpParams();
			 HttpConnectionParams.setConnectionTimeout(myParams, 5000);
			 HttpConnectionParams.setSoTimeout(myParams, 5000);
			 HttpClient hc= new DefaultHttpClient(myParams);
			 
			 HttpPost hp = new HttpPost(serverCheck);
			 //HttpPost hp = new HttpPost(serverWrong);
			 hp.setHeader("Content-type", "application/x-www-form-urlencoded");
			 hp.setHeader("Accept", "application/json");
			 se.setContentType("application/x-www-form-urlencoded");
			 hp.setEntity(se);
			
			 
			 
			 //System.out.println("after sending request and before getting response");
			 //boolean serverError = false;
			 
			 try{
				 HttpResponse hr = hc.execute(hp);
			 
			 if (hr != null){
				 
				 	HttpEntity respEntity = hr.getEntity();
		
			        String content =  EntityUtils.toString(respEntity);
			        //System.out.println("content " + content);
			        
			        JSONObject jo = new JSONObject(content);
			        imgexists =  jo.getBoolean("imgexists");
			        //System.out.println("imgexists " + imgexists);

			    }
			 
			 }//try
			 catch (Exception e){
				 serverError = true;
				 
				
			 }
			 /*finally{
				 if(serverError){
					 System.out.println("Can not connect to server");
					 Toast toast =  Toast.makeText(context, "Can not connect to server", Toast.LENGTH_LONG);
					 toast.show();
					 Intent intent = new Intent(context, DemonEyesActivity.class);
			            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			            context.startActivity(intent);
				 }
			 }*/
			 //System.out.println("after getting response");
			
		 }
		 catch (Exception e){
			 e.printStackTrace();
			 
		 }
		 //Looper.loop();
		 }
};
	t.start();
	
	//join thread
	try{
		t.join();
	}
	catch (InterruptedException ie){
	}
	

}
 
	/***************************************************************************************************************************/
	/****************************************************************************************************************************/
	
	
public synchronized void checkRequestWithHash(){
		
		System.out.println("check request with hash");
		
		 //ckech with deviceid and imgfilesize md5hash
		 try{
			JSONObject jsonpara =  new JSONObject();
			jsonpara.put("deviceid", deviceId);
			jsonpara.put("imghash", imgHash);
			jsonpara.put("imgfilesize", imgFilesize);
			
			postData = parameters + jsonpara.toString();
			
		//	System.out.println("json parameters " + postData);
			
			se = new StringEntity(postData);
			
		 }
		 catch (Exception e){
			 e.printStackTrace();
              		 
		 }
	 
	      //send request
		 	 
		 Thread t = new Thread(){
	     public void run() {
				 
	     //Looper.prepare();
	   
		 //System.out.println("before sending request");
			 
			 try{
				 
			// System.out.println("before sending request");
			 
			 HttpParams myParams = new BasicHttpParams();
			 HttpConnectionParams.setConnectionTimeout(myParams, 5000);
			 HttpConnectionParams.setSoTimeout(myParams, 5000);
			 HttpClient hc= new DefaultHttpClient(myParams);
			 
			 HttpPost hp = new HttpPost(serverCheck);
			 //HttpPost hp = new HttpPost(serverWrong);
			 hp.setHeader("Content-type", "application/x-www-form-urlencoded");
			 hp.setHeader("Accept", "application/json");
			 se.setContentType("application/x-www-form-urlencoded");
			 hp.setEntity(se);
			
			 
			 
			// System.out.println("after sending request and before getting response");
			   
			 try{
				 HttpResponse hr = hc.execute(hp);
			 
				 if (hr != null){
				 
				 		HttpEntity respEntity = hr.getEntity();
		
				 		String content =  EntityUtils.toString(respEntity);
				 //		System.out.println("content " + content);
			        
				 		JSONObject jo = new JSONObject(content);
				 		imgexists =  jo.getBoolean("imgexists");
				 	//	System.out.println("imgexists " + imgexists);
				 		imgid = jo.getLong("imgid");
				 		//System.out.println("imgid "+ imgid);

				 }
			 }
			 catch(Exception e){
				 serverError =  true;
			 }
		//	 System.out.println("after getting response");
			
		 }
		 catch (Exception e){
			 e.printStackTrace();
			 
		 }
		 //Looper.loop();
		 }
};
	t.start();
	
	//join thread
	try{
		t.join();
	}
	catch (InterruptedException ie){
	}
		 
	}
	
    /*****************************************************************************************************************************/
    /*****************************************************************************************************************************/

	public synchronized Uri downloadRequest(){
		
		//System.out.println("download request");
	
		//ckech with deviceid and imgfilesize
		 try{
			JSONObject jsonpara =  new JSONObject();
			jsonpara.put("deviceid", deviceId);
			jsonpara.put("imgid", imgid);
			//jsonpara.put("ops", "demonEyesCircular");
                        jsonpara.put("ops", "demonEyes");
			
			postData = parameters + jsonpara.toString();
			
			//System.out.println("json parameters " + postData);
			
			se = new StringEntity(postData);
			
		 }
		 catch (Exception e){
			 e.printStackTrace();
             		 
		 }
		//send request
		 /*progressDialog =  ProgressDialog.show(ServerActivity.context, "Please wait", "Loading please wait..", true);
	 		progressDialog.setCancelable(true);
	 		new Thread(new Runnable() {  
	             @Override
	             public void run() {
	                   // TODO Auto-generated method stub
	                   try
	                   {
	                   	//ConnectionDemon cd = new ConnectionDemon(ServerActivity.this, deviceId, imgFilesize, imageUri);
	                   	//cd.demonEyes();
	                         Thread.sleep(5000);
	                   }catch(Exception e){}
	                   //myPd_ring.dismiss();
	             }
	      }).start();*/
		 
		 Thread t = new Thread(){
	     public void run() {
				
	    	 
	    	 
	    	//Looper.prepare();
	    	
	    	 //progressDialog =  ProgressDialog.show(ServerActivity.context, "Please wait", "Loading please wait..", true);
	 		//progressDialog.setCancelable(true);
	 		/*new Thread(new Runnable() {  
	             @Override
	             public void run() {
	                   // TODO Auto-generated method stub
	                   try
	                   {
	                   	//ConnectionDemon cd = new ConnectionDemon(ServerActivity.this, deviceId, imgFilesize, imageUri);
	                   	//cd.demonEyes();
	                         Thread.sleep(5000);
	                   }catch(Exception e){}
	                   //myPd_ring.dismiss();
	             }
	      }).start();*/
	     
	   
		 //System.out.println("before sending request");
			 
		 String content = "";
		 boolean gotDemoneyes = false;
		 
			 try{//try3
				 
			// System.out.println("before sending request");
			 
			 HttpParams myParams = new BasicHttpParams();
			 HttpConnectionParams.setConnectionTimeout(myParams, 25000);
			 HttpConnectionParams.setSoTimeout(myParams, 25000);
			 HttpClient hc= new DefaultHttpClient(myParams);
			 
			 HttpPost hp = new HttpPost(serverDownload);
			 //HttpPost hp = new HttpPost(serverWrong);
			 hp.setHeader("Content-type", "application/x-www-form-urlencoded");
			 //hp.setHeader("Accept", "application/json");
			 se.setContentType("application/x-www-form-urlencoded");
			 hp.setEntity(se);
			
			 
			 
			 //System.out.println("after sending request and before getting response");
			 //String stringUri = "/storage/sdcard0/demoneyes/demoneyesCsilla.jpg";
			 //demoneyesUri = Uri.parse(stringUri);
			 
			 try{ //server error
				 HttpResponse hr = hc.execute(hp);
				 //System.out.println("6666666666666666666666666");
				 if (hr != null){
				 
					 //HttpEntity respEntity = hr.getEntity();
				 		InputStream is = hr.getEntity().getContent();
				 	
				    //imageResponse = BitmapFactory.decodeStream(is);
				 	
				 	
			        //String content =  EntityUtils.toString(respEntity);
			        //System.out.println("content " + content);
				 		//is.reset();
				 		FileOutputStream fop = null;
				 		File folder, file;
				 		try { //try1
						 
				 			//file = new File(stringUri);
				 			folder = new File(Environment.getExternalStorageDirectory().toString() +"/daemoneyes");
				 			folder.mkdirs();
				 			storageDirectory =  folder.toString();
				 			//System.out.println("directory " + storageDirectory); 
				 			//generate img number
				 			demoneyesCount++;
				 			saveCount();
				 			String demoneyesPath = "daemoneyes" + demoneyesCount + ".jpg";
				 			//System.out.println("image path before saving  "  + DaemonEyes.imagePath);
/********************************************************************************************************/
				 			 String k = "/";
				 		    int last = 0;
				 		    int index = DaemonEyes.imagePath.indexOf(k);
				 		    while (index >=0){
				 		       // System.out.println("Index : "+index);
				 		        last = index;
				 		        index = DaemonEyes.imagePath.indexOf(k, index+k.length())   ;
				 		        
				 		    }
				 		    
				 		    //System.out.println("last " + last);
				 	            //String first = s.substring(0, last);
				 		    //System.out.println("first " + first);
				 		    String nameimage = DaemonEyes.imagePath.substring(last+1);
				 	            //System.out.println("second " + nameimage);
				 	            //String newurl = first + "demon" + second;
				 	            //System.out.println("image name " + nameimage);
  /*******************************************************************************************************************************/

                                                    String kk = ".";
				 		    int last1 = 0;
				 		    int index1 = nameimage.indexOf(kk);
				 		    while (index1 >=0){
				 		       // System.out.println("Index : "+index);
				 		        last1 = index1;
				 		        index1 = nameimage.indexOf(kk, index1+kk.length())   ;
				 		        
				 		    }
				 		    
				 		    //System.out.println("last " + last1);
				 	            String first = nameimage.substring(0, last1);
				 		    //System.out.println("first " + first);
				 		    String second = nameimage.substring(last1);
				 	            //System.out.println("second " + second);
				 	            //String newpath = first + "demon" + second;
                                                    String newpath = first + demoneyesPath;
				 	            //System.out.println("new path " + newpath);
/***********************************************************************************************************************************/

				 			//file = new File(storageDirectory, demoneyesPath);
						
				 			//newdemoneyes = storageDirectory + "/" + demoneyesPath;
				 			file = new File(storageDirectory, newpath);
							
				 			newdemoneyes = storageDirectory + "/" + newpath;
				 			
				 			demoneyesUri = Uri.parse(newdemoneyes);	
				 		
				 			
				 			
				 			fop = new FileOutputStream(file);
			 
				 			// if file doesnt exists, then create it
				 			if (!file.exists()) {
				 				//System.out.println("File does not exists");
				 				file.createNewFile();
				 			}
				 			byte[] byteImage = getBytes(is);
				 			content =  new String(byteImage);
				 			// get the content in bytes
				 			//byte[] contentInBytes;
				 			//byte b[] = new byte[65536];
				 			//int nRead = 0;
				 			//while ((nRead = is.read(b)) > 0) {
				 			//System.out.println("size: " + nRead);
				 			// fop.write(b, 0, nRead);
				 			//	}
				 			fop.write(byteImage);	
				 			fop.flush();
				 			fop.close();
			 
				 			//System.out.println("Done");
			 
				 	} //try1
				 		catch (IOException e) {
				 			e.printStackTrace();
					}
				 	
			        //InputStream is = hr.getEntity().getContent();

			        //imageResponse = BitmapFactory.decodeStream(is);
			        
			       try{//try2
			        	JSONObject jo = new JSONObject(content);
			        
			            //System.out.println("json error " + jo.toString());
			        }//try2
			        catch (Exception e){}
			    }
				 
			 }//try server error
			 catch(Exception e){
				 serverError = true;
			 }
			 
			 
			
		 }//try3
			 
		 catch (Exception e){
			 e.printStackTrace();
			 
		 }
		 //Looper.loop();
		 }
		 
};
	t.start();
	
	//join thread
	try{
		t.join();
	}
	catch (InterruptedException ie){
	}
	//System.out.println(" I am in download: demoneyesUri " + demoneyesUri.toString());
		return demoneyesUri;
	}
	
	/***********************************************************************************************************************/
	/***********************************************************************************************************************/
	
        public synchronized void uploadRequest(){
		
        	//System.out.println("upload request");
        	 
		//ckech with deviceid and imgfilesize
		 try{
			JSONObject jsonpara =  new JSONObject();
			jsonpara.put("deviceid", deviceId);
			
			//postData = parameters + jsonpara.toString();
			postData =  jsonpara.toString();
			//System.out.println("json parameters " + postData);
			
			//sb =  new StringBody("json parameters string body "+ postData);
			//sb = new StringBody(postData, Charset.forName("UTF-8"));
			
			//sb = new StringBody(deviceId);
			
			//System.out.println("stringbody " + sb.toString());
			//se = new StringEntity(postData);
			InputStream ins = context.getContentResolver().openInputStream(imageUri);
			
		//	System.out.println("imageuri in upload "+ imageUri.toString());
			String stringUri = "/storage/sdcard0/tempFile.jpg";
			//tempFileUri = Uri.parse(stringUri);
			
			FileOutputStream fop = null;
			
			try {
				 
				file = new File(stringUri);
				fop = new FileOutputStream(file);
	 
				// if file doesnt exists, then create it
				if (!file.exists()) {
					file.createNewFile();
				}
	        byte[] byteImage = getBytes(ins);
				
			fop.write(byteImage);	
				fop.flush();
				fop.close();
			//	System.out.println("before uploading image " + file.toString());
				//imgfile = new FileBody(file, ContentType.DEFAULT_BINARY);
				//imgfile = new FileBody(file, ContentType.MULTIPART_FORM_DATA);
	            //imgfile = new FileBody(file, ContentType.APPLICATION_OCTET_STREAM);
				//System.out.println("Done");
	 
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		
		 }
		 catch (Exception e){
			 e.printStackTrace();
             		 
		 }
		//send request
	 	 
		 Thread t = new Thread(){
	     public void run() {
				 
	     //Looper.prepare();
	   
		 //System.out.println("before sending request");
			 
		 
			 try{//try1
				 
			// System.out.println("before sending request");
			 
			 HttpParams myParams = new BasicHttpParams();
			 HttpConnectionParams.setConnectionTimeout(myParams, 50000);
			 HttpConnectionParams.setSoTimeout(myParams, 50000);
			 HttpClient hc= new DefaultHttpClient(myParams);
			 
			 HttpPost hp = new HttpPost(serverUpload);
			 //HttpPost hp = new HttpPost(serverWrong);
			 hp.setHeader("Content-type", "multipart/form-data; boundary=" + boundary);
			 //hp.setHeader("Content-type", "multipart/form-data");
			 //hp.setHeader("Content-type", "application/octet-stream");
			 //hp.setHeader("Accept", "application/json");
			 //se.setContentType("application/x-www-form-urlencoded");
			 //hp.setEntity(se);
			 //MultipartEntity multipart = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			 //MultipartEntity multipart = new MultipartEntity(HttpMultipartMode.STRICT);
			 //MultipartEntity multipart = new MultipartEntity();
	
			 MultipartEntityBuilder multipart = MultipartEntityBuilder.create();
			 multipart.setMode(HttpMultipartMode.STRICT);
             multipart.setBoundary(boundary);
			 Charset chars = Charset.forName("UTF-8");
			 //Charset chars = Charset.forName("ASCII");
			 multipart.setCharset(chars);
			 multipart.addTextBody("parameters", postData);
			 //multipart.addPart("parameters", new StringBody(postData, ContentType.APPLICATION_JSON));
			 //multipart.addPart("parameters", sb);
			 //multipart.addPart("imgfile", imgfile);
			 multipart.addBinaryBody("imgfile", file, ContentType.create("image/jpeg"), "/storage/sdcard0/tempFile.jpg");
			 //System.out.println("multipart  ****  " + multipart.toString());
			 
			 //ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			 /*multipart.writeTo(bytes);
			 String con = bytes.toString();
			 for (int i = 0; i < 10; i++){
				 System.out.println("**********************************************");
				 
			 }
			 System.out.println("ouptput " + con);
			 for (int i = 0; i < 10; i++){
				 System.out.println("***********************************************");
				 
			 }*/
			 
			 ByteArrayOutputStream os = new ByteArrayOutputStream();
			 multipart.build().writeTo(os);
			 //PrintStream ps = new PrintStream(os);
			 String output = os.toString();
			 //String responseBody = EntityUtils.toString(multipart.build());
			 //System.out.println(output);
			 

			 hp.setEntity(multipart.build());
		     //MultipartEntityBuilder mpe =  MultipartEntityBuilder.create();	 
			 //System.out.println("after sending request and before getting response");
	         //System.out.println(hp.toString());
	         
	         try{ //try3
			 HttpResponse hr = hc.execute(hp);
			 
			 if (hr != null){//if1
				 
				 	HttpEntity respEntity = hr.getEntity();
				 	//InputStream is = hr.getEntity().getContent();
				 	//InputStream is = respEntity.getContent();
				    //imageResponse = BitmapFactory.decodeStream(is);
				 	
				 	String content =  EntityUtils.toString(respEntity);
			  //      System.out.println("content " + content);
				 	
			        //byte[] byteImage = getBytes(is);
				
			        
			       try{//try2
			        	JSONObject jo = new JSONObject(content);
			        
			   //         System.out.println("json error " + jo.toString());
			            imgid = jo.getLong("imgid");
				 //       System.out.println("imgid in upload "+ imgid);
			            
			        }//try2
			        catch(Exception e){}
			    }//if1
			 }//try3
			 catch(Exception e){
				 serverError = true;
			 }
		//	 System.out.println("after getting response");
			
		 }//try1
		 catch (Exception e){
			 e.printStackTrace();
			 
		 }
		 //Looper.loop();
		 }
		 
};
	t.start();
	
	//join thread
	try{
		t.join();
	}
	catch (InterruptedException ie){
	}
		
	}

        /********************************************************************************************************************/
        /**********************************************************************************************************************/
        //AsyncTask methods
        @Override
        protected void onPreExecute(){
        	super.onPreExecute();
        	
        	//ProgressDialog.show(context, "", "", false, false);
        	//progressDialog = ProgressDialog.show(context, "Please wait", "Loading photo...", true);
        	progressDialog = new ProgressDialog(context);
        	progressDialog.setMessage("Please wait...the modified photo will be in the 'daemoneyes' folder in Gallery and Photos after saving it.");
        	progressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        	progressDialog.getWindow().setGravity(Gravity.BOTTOM);
           
        	progressDialog.setCancelable(false);
        	progressDialog.setOnKeyListener(new Dialog.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface arg0, int keyCode,
                        KeyEvent event) {
                    // TODO Auto-generated method stub
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        //finish();
                        progressDialog.dismiss();
                        Intent intent = new Intent(context, DemonEyes.class);
			            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			            context.startActivity(intent);
                    }
                    return true;
                }
            });

        	progressDialog.show();
		
        
	}

        @Override
        protected Uri doInBackground(final String... args){
        	
        	try {
        		//System.out.println("checking connection");
				isConnected = haveNetworkConnection();
				//System.out.println("Start????  "+ isConnected);
        		
				//System.out.println("before demoneyes");
        		Uri uritry = demonEyes();
        		//System.out.println("after demoneyes");
        		//System.out.println("diInBackground Uri "+ demoneyesUri.toString());
        		//System.out.println("count after connection to server " + demoneyesCount);
				saveCount();
        		return uritry;
        	}
        catch (Exception e){ return null;}
        }
        
        
        @Override 
        protected void onPostExecute(final Uri uri ){
        	 if (progressDialog.isShowing()) {
                 progressDialog.dismiss();
                 //System.out.println("server error "+ serverError);
                 
                

                 if (isConnected){
                	 if (!serverError){
                		 //System.out.println("Uri " + uri);
                		 DaemonEyes.iv.setImageURI(uri);
                		 //System.out.println("count after connection to server " + demoneyesCount);
                		 DaemonEyes.dialogSaving.show();
                		 
                	 }
                	 else{
                                 //System.out.println("Can not reach server");
                		 cannotReachServer();
                	 }
                		 
                 
                 }
                 else{
			 //System.out.println("No Connection ");
                	 Toast toast =  Toast.makeText(context, "No Connection ", Toast.LENGTH_LONG);
					 toast.show();
					 NotificationCompat.Builder mBuilder =
						        new NotificationCompat.Builder(context)
						        .setSmallIcon(R.drawable.notification)
						        .setContentTitle("My daemoneyes notification")
						        .setContentText("No Connection !!!");
						Intent resultIntent = new Intent(context, DemonEyes.class);
						TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
						stackBuilder.addParentStack(DemonEyes.class);
						stackBuilder.addNextIntent(resultIntent);
						PendingIntent resultPendingIntent =
						        stackBuilder.getPendingIntent(
						            0,
						            PendingIntent.FLAG_UPDATE_CURRENT
						        );
						mBuilder.setContentIntent(resultPendingIntent);
						NotificationManager mNotificationManager =
							    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
							// mId allows you to update the notification later on.
							mNotificationManager.notify(notifIdConnection, mBuilder.build());
					 
					 Intent intent = new Intent(context, DemonEyes.class);
			            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			            context.startActivity(intent);
                 }
                	 
                 //serverError = false;
             }
        	
        }
       
        
        /************************************************************************************/
        /*************************************************************************************/
        
      public Uri demonEyes(){
        	//if (gotImage) {
				 //sending requests
				 //System.out.println("image uri " + imageUri);
    	  		Uri demonEyesUri;
				 //System.out.println("checking connection");
				 
					 
				 //isConnected = haveNetworkConnection();
				 
				 //System.out.println("Start????  "+ isConnected);
					
				
				 //isConnected = true; //only for testing
				 //if (isConnected){                 //if1
				 checkRequestWithoutHash();
				 //System.out.println("before send request with hashing");
				 if(serverError){ //if2  
					 cannotReachServer();
					return null;
				 }
				 
				
				 else{   //else2
					 //image exists with filesize
					 if (imgexists){ //if3
						 //System.out.println("image exists and hash check");
						 md5HashImage();
						 checkRequestWithHash();
						 // System.out.println("Image id and imgexists " + imgid + "   ,   " + imgexists);
	                     if (serverError){ //if5
	                    	 
	                    	 cannotReachServer();
	                    	 return null;
	                    	 
	                     }//if5
	                 else{ //else5         
	                    	 //image exists with filesize and hash
	                    	 if(imgexists){ //if4
	                    		 // System.out.println("download image");
	                    		 demonEyesUri = downloadRequest();
	                    		 
	                    		 if (serverError){ //if6
	                    			 cannotReachServer();
	                    			 return null;
	                    		 }//if6
	                    		 else{ //else5
	                    			 return demonEyesUri;
	                    		
	                    		 
	                    		 }//else5
	                    	 }//if4
						 //image exists with given filesize, but hash does not match
						 	else{ //else4
						 
						 		uploadRequest();
						 		if (serverError){ //if8
						 			cannotReachServer();
						 			return null;
						 		}//if8
						 		else{//else8
						 			demonEyesUri = downloadRequest();
						 			if (serverError){//if7
						 				cannotReachServer();
						 				return null;
						 			}//if7
						 			else{
						 				return demonEyesUri;
						 			}
						 		}//else8
						 	} //else4
	                 }//else5
	                     }//if3 
					 
					 
	                     //image does not exists
	                     else{//else3
					 
	                    	 uploadRequest();
	                    		if (serverError){ //if9
	                    			cannotReachServer();
	                    			return null;
						 		}//if9
	                    		else { //else9
	                    			demonEyesUri = downloadRequest();
	                    			if (serverError){ //if8
	                    				cannotReachServer();
	                    				return null;
	                    			}//if8
	                    			else{
	                    				return demonEyesUri;
	                    			}
	                    		}//else9
	                     }//else3
			 
				
					
					 
			      	 }//else2
				 
				 	//}//if1
				 /*
				 else{  //else1
					 System.out.println("No Connection *************** " + isConnected);
					 System.out.println("********************!!!!!!!!!!!!!!!!!!!");
					 Toast toast =  Toast.makeText(context, "No Connection ", Toast.LENGTH_LONG);
					 toast.show();
					 System.out.println("********************!!!!!!!!!!!!!!!!!!!");
					 //serverError = true;
					 System.out.println("where are we?");
					 Intent intent = new Intent(context, DemonEyes.class);
			            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			            context.startActivity(intent);
					 
				 } //else1
	 		
			*/
      }

      
      private boolean haveNetworkConnection() {
  	    boolean haveConnectedWifi = false;
  	    boolean haveConnectedMobile = false;
  	    //boolean haveConnectedBluetooth = false;
  	    
  	    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
  	    NetworkInfo[] netInfo = cm.getAllNetworkInfo();
  	    for (NetworkInfo ni : netInfo) {
  	 
  	    	if ( ni != null )
  	    	{
  	    	    if (ni.getType() == ConnectivityManager.TYPE_WIFI)
  	    	        if (ni.isConnectedOrConnecting()){
  	    	          //System.out.println("WiFi connected");
  	  	           //System.out.println("network info wifi " + ni.getState().toString());
  	    	        	haveConnectedWifi = true;
  	    	            
  	    	        }
  	    	    if (ni.getType() == ConnectivityManager.TYPE_MOBILE)
  	    	        if (ni.isConnectedOrConnecting()){
  	    	        	
  	    	         // System.out.println("Mobile connected");
  	  	            //System.out.println("network info mobile " + ni.getState().toString());
  	    	            haveConnectedMobile = true;
  	    		}
  	    	}
  	    	
  	    	/*if (ni.getTypeName().equalsIgnoreCase("WIFI")){
  	            if (ni.isConnected()){
  	            System.out.println("WiFi connected");
  	            System.out.println("network info " + ni.getState().toString());
  	                haveConnectedWifi = true;
  	            }
  	        }
  	           if (ni.getTypeName().equalsIgnoreCase("MOBILE")){
  	            if (ni.isConnected()){
  	            	
  	            System.out.println("Mobile connected");
  	                haveConnectedMobile = true;
  	            }
  	          }
  	             if (ni.getTypeName().equalsIgnoreCase("BLUETOOTH")){
  	        	if (ni.isConnectedOrConnecting()){
  	        		System.out.println("Bluetooth connected");
  	        		haveConnectedBluetooth = true;    		
  	        	}
  	            }*/
  	    }//for
  	 
  	    
  	    
  	    	return haveConnectedWifi || haveConnectedMobile;
  	        //return haveConnectedWifi;
  	}
      
      public void cannotReachServer(){
    	  
    	  //System.out.println("Cannot connect to server ");
			 Toast toast =  Toast.makeText(context, "Cannot connect to server", Toast.LENGTH_LONG);
			 toast.show();
			 NotificationCompat.Builder mBuilder =
				        new NotificationCompat.Builder(context)
				        .setSmallIcon(R.drawable.notification)
				        .setContentTitle("My daemoneyes notification")
				        .setContentText("Cannot connect to server !!!");
				Intent resultIntent = new Intent(context, DemonEyes.class);
				TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
				stackBuilder.addParentStack(DemonEyes.class);
				stackBuilder.addNextIntent(resultIntent);
				PendingIntent resultPendingIntent =
				        stackBuilder.getPendingIntent(
				            0,
				            PendingIntent.FLAG_UPDATE_CURRENT
				        );
				mBuilder.setContentIntent(resultPendingIntent);
				NotificationManager mNotificationManager =
					    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
					// mId allows you to update the notification later on.
					mNotificationManager.notify(notifId, mBuilder.build());
			
			 Intent intent = new Intent(context, DemonEyes.class);
	            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
	            context.startActivity(intent);
			
      }
}



package org.photos.demoneyes;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.UUID;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.Toast;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.app.AlertDialog;
import android.app.Dialog;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.graphics.drawable.ColorDrawable;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.content.DialogInterface;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;


import android.app.ProgressDialog;
//import android.os.Handler;
import android.widget.ProgressBar;
import android.graphics.drawable.Drawable;
import android.graphics.Color;
import android.os.Looper;
import android.os.AsyncTask;

public class DaemonEyes extends Activity {
	private static final int SELECT_PHOTO = 100;
	private static final int USE_CAMERA = 20;
	private static final int DIALOG_ALERT = 10;
	
	public static 	ImageView iv;
	Intent i;
	Bitmap bmp;
	Uri uri;
	Uri tempFileUri;
	public static Uri imageUri;
	public static String imagePath;
	
	boolean gotImage = false;
	long imgFilesize;
	String imgFilesizest;
	String imgHash;
	File file;
	String [] stringArray;
	ListView listView;
	public static Dialog dialogSending;
	public static Dialog dialogSaving;
	String imei;
	String simSerialNumber;
	String androidId;
	String deviceId;
	
	static final String synapse = "sn4ps3";
	static final String synapses = "s9n4ps";
	static final String synaps = "s9nps3";
	
	boolean camera;
	ProgressDialog myProgressDialog;
	//public static Context context;
	ProgressBar spinner;
	static boolean doneSending = false;
	Thread t;
	ProgressDialog myPd_ring;
	
	private long md5HashCode(String s){
		long md5hashcode = 0;
		 try{
		    	MessageDigest m = MessageDigest.getInstance("MD5");
		    	m.reset();
		    	m.update(s.getBytes());
		    	byte[] digest = m.digest();
		    	BigInteger bigInt = new BigInteger(1,digest);
		    	md5hashcode = bigInt.longValue();
		 
		 }
		 catch (java.security.NoSuchAlgorithmException e ){
		    	
		    }
		return md5hashcode;
		
	}
	
	
	
	private void getImgFilesize(){
		try{
   		 //System.out.println("stage 1");
   		 //System.out.println(imageUri.toString());
   		 
   		InputStream ins = getContentResolver().openInputStream(imageUri);
	 		imgFilesize = ins.available();
	 		 imgFilesizest =  "" + imgFilesize;
	 		 //System.out.println("file size " + imgFilesizest );
	 		 
	 		 
	 		 String stringUri = "/storage/sdcard0/tempFile.jpg";
			 tempFileUri = Uri.parse(stringUri);
			 FileOutputStream fop = null;
				File file;
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
		 
					
		 
				} catch (IOException e) {
					e.printStackTrace();
				}
	 		 
			
		}
   
   	 	catch (FileNotFoundException ex) {
   	 		//System.err.println("FileNotFoundException!!");
   	 	}
   	 	catch (IOException ex) {
   	 		//System.err.println("IOException!!");
   	 	} 
		System.out.println("I am here!!!");
		iv.setImageURI(tempFileUri);
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.server_demon);
		//spinner = (ProgressBar)findViewById(R.id.progressBar1);
		//spinner.setVisibility(View.VISIBLE);
		//context = ServerActivity.this;
		//System.out.println("picked " + DemonEyes.pickedPhoto);
		iv = (ImageView) findViewById(R.id.imageView);
		
		
		final TelephonyManager telman = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE); 
	    
		 imei = synapses + telman.getDeviceId();
		 //System.out.println("imei " + imei + " length " + imei.length());
		 long imei5md = md5HashCode(imei);  
		 long b =   imei5md << 32;
		 //simSerialNumber = "" + telman.getSimSerialNumber();
		 simSerialNumber = synapse +  android.os.Build.SERIAL;
		 //System.out.println("sim " + simSerialNumber + " length " + simSerialNumber.length());
		 long sim5md = md5HashCode(simSerialNumber);
		 androidId = synaps + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
		 //System.out.println("android " + androidId + " length " + androidId.length());
		 long android5md = md5HashCode(androidId);
   
		 //System.out.println("androidid hashcode " + androidId.hashCode());
		 long a = (long) imei.hashCode() << 32;
		 //System.out.println("imei hashcode " + a);
		 //System.out.println("sim hashcode " + simSerialNumber.hashCode());
   
		 UUID deviceUuid = new UUID(android5md, b | sim5md);
		 deviceId = deviceUuid.toString();
		 //deviceId = "123456";
		 //System.out.println("deviceid " + deviceId);
		 //System.out.println(deviceId.length());
		 
		
		
		if(DemonEyes.pickedPhoto){
			camera = false;
			Intent imagePicker = new Intent(Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(imagePicker, SELECT_PHOTO); 
		
		
		
		DemonEyes.pickedPhoto = false;
		
		}
		
		else if(DemonEyes.usedCamera){
			//Intent useCamera = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			
			//startActivityForResult(useCamera, USE_CAMERA);
			camera = true;
			File file = new File(Environment.getExternalStorageDirectory(),  "/tempFile.jpg");
		    if(!file.exists()){
		    try {
		        file.createNewFile();
		    } catch (IOException e) {
		    // TODO Auto-generated catch block
		        e.printStackTrace();
		    }
		    }else{
		       file.delete();
		    try {
		       file.createNewFile();
		    } catch (IOException e) {
		    // TODO Auto-generated catch block
		        e.printStackTrace();
		    }
		    }
		    Uri capturedImageUri = Uri.fromFile(file);
		    //System.out.println("captured image uri " + capturedImageUri.toString());
		    Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		    //save file
		    i.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
		    /*try{
		    InputStream ins = getContentResolver().openInputStream(capturedImageUri);
	 		imgFilesize = ins.available();
	 		 imgFilesizest =  "" + imgFilesize;
	 		 System.out.println("file size from camera " + imgFilesizest );
		    }
	 		 catch(Exception e){}
            */		    
		    //imgFilesize =  file.length();
            //System.out.println("camera image filesize" + imgFilesize);
		    
		    startActivityForResult(i, USE_CAMERA);
		       
			DemonEyes.usedCamera = false;
			//System.out.println("camera image saved");
			//imgFilesize =  file.length();
            //System.out.println("camera image filesize" + imgFilesize);

		}
		
		
		else{
			//get image 
			//System.out.println("sharing ***** ");
			camera = false;
			 Intent intent = getIntent();
			 String action = intent.getAction();
			 String type = intent.getType();
	    
			 if (Intent.ACTION_SEND.equals(action) && type != null) {
				 if ("text/plain".equals(type)) {
					 handleSendText(intent); // Handle text being sent
				 } else if (type.startsWith("image/")) {
					 handleSendImage(intent); // Handle single image being sent
				 }
			 } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
				 if (type.startsWith("image/")) {
					 handleSendMultipleImages(intent); // Handle multiple images being sent
				 }
			 } else {
				 // Handle other intents, such as being started from the home screen
			 }
			 
			openDialogForSending();
			
		}
		
		
		
		 
	}
	
	
	public void openDialogForSending(){
		
		Drawable d = new ColorDrawable(Color.BLACK);
		d.setAlpha(0);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.alertdialog);
		//AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Light);
		builder.setCancelable(false);
		
		//builder.setTitle("Select Color Mode");
        //AlertDialog.Builder builder = new AlertDialog.Builder(this);
		listView = new ListView(this);
		
		stringArray = new String[] { "Daemon Eyes", "Cancel" };
		ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, stringArray);
		listView.setAdapter(modeAdapter);
		//final Dialog dialog = builder.create();
		//listView.setText(Color.BLUE);
		listView.setBackgroundResource(android.graphics.Color.TRANSPARENT);
		listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                //do something on click
            	//System.out.println("click "+ arg2 );
            	
            	switch(arg2){
            	
            	case 0:
            		//System.out.println("deviceId " + deviceId + " filesize " + imgFilesize);
            		
                    //if (!camera){
                    
   					//System.out.println("server error " + ConnectionDemon.serverError);
   					
   					dialogSending.cancel();
                    //ConnectionDemon cd = new ConnectionDemon(ServerDemon.this, deviceId, imgFilesize, imageUri);
                    //cd.demonEyes();
   					AsyncTask task =  new ConnectionDemon(DaemonEyes.this, deviceId, imgFilesize, imageUri).execute();
                    //}//camera
                 
                    /*else{
                    	
                    	cd.demonEyesCamera();
                    }*/
                    
   					/*try{
                    task.wait(10000);
   					}
   					catch(Exception e){}
   					*/
                  
                    
                    if (!ConnectionDemon.serverError){
                    	//System.out.println("server error " + ConnectionDemon.serverError);
                    	
                    	openDialogForSaving();
                    	
                    }
                    
                    else{
                    	finish();
                    }
   					//openDialogForSaving();
   					//finish();
                    
            		break;
            	case 1:
            		finish();
            		
            	    //Intent intent = new Intent(ServerActivity.this, DemonEyesActivity.class);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //startActivity(intent);
                    
            		break;
            	
            	}
            }
        });
		
        builder.setView(listView);
        
		dialogSending = builder.create();
        //dialogSending.getWindow().setBackgroundDrawableResource(R.color.transparent);
		//dialogSending.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		//Drawable d = new ColorDrawable(Color.BLACK);
		//d.setAlpha(0);
		//dialogSending.getWindow().setBackgroundDrawable(d);
		dialogSending.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		//dialogSending.getWindow().setGravity(Gravity.BOTTOM);
		dialogSending.getWindow().getAttributes().gravity = Gravity.BOTTOM;
		//dialog.show();
		dialogSending.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                    KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    finish();
                    //dialog.dismiss();
                }
                return true;
            }
        });
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
		dialogSending.show();
		
		
	}
	
	
	public void openDialogForSaving(){
		//System.out.println("what about saving? " + doneSending);
		//ServerDemon.iv.setImageURI(ConnectionDemon.demoneyesUri);
                	  /*while(!doneSending){
                		  System.out.println("I am working");
                	  }*/
                	  /******************************************************************************/
                	  AlertDialog.Builder builder = new AlertDialog.Builder(DaemonEyes.this, R.style.alertdialog);
                	  builder.setCancelable(false);
              		//builder.setTitle("Select Color Mode");
                      //AlertDialog.Builder builder = new AlertDialog.Builder(this);
              		listView = new ListView(DaemonEyes.this);
              		
              		stringArray = new String[] { "Save", "Cancel" };
              		ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(DaemonEyes.this, android.R.layout.simple_list_item_1, android.R.id.text1, stringArray);
              		listView.setAdapter(modeAdapter);
              		//final Dialog dialog = builder.create();
              		listView.setOnItemClickListener(new OnItemClickListener() {
                          @Override
                          public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                              //do something on click
                          	//System.out.println("click "+ arg2 );
                          	//Toast.makeText((), ((TextView) arg1).getText(), Toast.LENGTH_SHORT).show();
                          	
                        	  //ServerDemon.iv.setImageURI(ConnectionDemon.demoneyesUri);
                          	switch(arg2){
                          	
                          	case 0:
                          		//System.out.println("deviceId " + deviceId + " filesize " + imgFilesize);
                          		//ConnectionDemon cd = new ConnectionDemon(ServerActivity.this, deviceId, imgFilesize, imageUri);
                                  //cd.demonEyes();        	
                                  //System.out.println("scan images in " + ConnectionDemon.storageDirectory);
                          		
                          			//Toast toast =  Toast.makeText(ServerDemon.this, "Demoneyezed photo in the demoneyes subfolder in Gallery.", Toast.LENGTH_LONG);
                          			//toast.show();
                          		//ServerDemon.iv.setImageURI(ConnectionDemon.demoneyesUri);
                                  File filesave = new File(ConnectionDemon.newdemoneyes);
                                  Uri contentUri = Uri.fromFile(filesave);
                                  Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,contentUri);
                                  sendBroadcast(mediaScanIntent);
                                  /*sendBroadcast (
                                  	    new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, 
                                  	                Uri.parse( ConnectionDemon.storageDirectory) )
                                  	);*/ 
                                  finish();
                          		break;
                          	case 1:
                          		//String stringUri = "/storage/sdcard0/demoneyes/demoneyesCsilla.jpg";
                          		File file = new File(ConnectionDemon.newdemoneyes);
                          		file.delete();
                          		finish();
                          		
                          	    //Intent intent = new Intent(ServerActivity.this, DemonEyesActivity.class);
                                  //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                  //startActivity(intent);
                                  
                          		break;
                          	}
                          }
                      });
              		
                      builder.setView(listView);
              		dialogSaving = builder.create();
                      //dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
              		//dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
              		dialogSaving.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
              		dialogSaving.getWindow().setGravity(Gravity.BOTTOM);
              		dialogSaving.setOnKeyListener(new Dialog.OnKeyListener() {

                        @Override
                        public boolean onKey(DialogInterface arg0, int keyCode,
                                KeyEvent event) {
                            // TODO Auto-generated method stub
                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                finish();
                               // dialog.dismiss();
                            }
                            return true;
                        }
                    });
              		//dialog.show();
              		
                      //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
              		//dialogSaving.show();
                	//dialogSaving.cancel();  
                	  /********************************************************************************/
                	  
          
		
		
		
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent returnedImage){
    	super.onActivityResult(requestCode, resultCode, returnedImage);
    	//Toast toastnot =  Toast.makeText(( this ).getBaseContext(), "NOT OK", Toast.LENGTH_SHORT);
    	
    	//if (requestCode == captureMomo){
    	switch(requestCode){
    	case SELECT_PHOTO:
    	 if (resultCode == Activity.RESULT_OK){
    		
    		 //Toast toast =  Toast.makeText(( this ).getBaseContext(), "OK", Toast.LENGTH_SHORT);
    		 //Uri selectedImage = returnedImage.getData();
    		 imageUri  = returnedImage.getData();
    		 //System.out.println("imageUri from gallery "  + imageUri.toString());
    		 imagePath = getPathFromUri(this, imageUri);
    		 //System.out.println("image path "  + imagePath);
    		 if (imageUri != null){
                  gotImage = true;

    			 getImgFilesize();
    			 
    		 }
    		 //iv.setImageURI(selectedImage);
    	     openDialogForSending();	 
    		        }//if
    	 else{
    		 //System.out.println("going back ");
    		 finish();
    	 }
    	 break;
    	 
    	 
    	    		 }//switch
    	   //}
    	//}
    }
	void handleSendText(Intent intent) {
 		String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
 		if (sharedText != null) {
 			// Update UI to reflect text being shared
 		}
 	}

 	void handleSendImage(Intent intent) {
 		//System.out.println("creating uri");
 		imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
 		//System.out.println("imageUri from sharing "  + imageUri.toString());
 		//imagePath = getPathFromUri(this, imageUri);
		 //System.out.println("image path "  + imagePath);
 		if (imageUri != null) {
 			//just for testing
 			gotImage = true;
 			
 			getImgFilesize();
 			
 		}
 	}

 	void handleSendMultipleImages(Intent intent) {
	    ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
	    if (imageUris != null) {
	        // Update UI to reflect multiple images being shared
	    }
	} 	
 	


public String getPathFromUri(Context context, Uri contentUri) {
  Cursor cursor = null;
  try { 
    String[] proj = { MediaStore.Images.Media.DATA };
    cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
    cursor.moveToFirst();
    return cursor.getString(column_index);
  } finally {
    if (cursor != null) {
      cursor.close();
    }
  }
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
 	@Override
 	public boolean onTouchEvent(MotionEvent event){
 		
 		return true;
 	}
 	@Override
 	public boolean onKeyDown(int keyCode, KeyEvent event){
 		 if ((keyCode == KeyEvent.KEYCODE_BACK)) { //Back key pressed
 	    	//System.out.println("eljut ide?");
 	    	//count--;
 		    ////startActivity(new Intent("org.education.rememberpattern.PlayPattern"));
 			 //System.out.println("I pushed the back button");
 			 Intent intent = new Intent(this, DemonEyes.class);
 	           intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
 	           startActivity(intent);
 	        return true;
 	    }
 	    return super.onKeyDown(keyCode, event);
 	}
 	
    /*    @Override
 	public void onBackPressed(){
        System.out.println("I pushed the back button");

		 Intent intent = new Intent(this, DemonEyes.class);
           intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
           startActivity(intent);
	if (ConnectionDemon.progressDialog!= null){
            System.out.println("progressDialog is on");

                ConnectionDemon.progressDialog.setCancelable(true);
		System.out.println("Cancel ???");
 		if(ConnectionDemon.progressDialog.isShowing())
 		{
 		ConnectionDemon.progressDialog.dismiss();
 		}
            }
 		}*/
 	
	
}

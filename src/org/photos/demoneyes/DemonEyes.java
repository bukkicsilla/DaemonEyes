package org.photos.demoneyes;

import android.os.Bundle;
import android.app.Activity;
import android.view.KeyEvent;
//import android.view.Menu;
import android.content.Intent;
import android.view.View;
import android.widget.Button; 
import android.graphics.Typeface;
//import android.widget.Toast;
//import android.content.Context;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
import android.net.Uri;
import android.webkit.WebView;
//import android.support.v4.app.NotificationCompat;
//import android.support.v4.app.TaskStackBuilder;
//import android.app.PendingIntent;
//import android.app.NotificationManager;

public class DemonEyes extends Activity {

	//private static final int SELECT_PHOTO = 100;
	public static boolean pickedPhoto = false;
	public static boolean usedCamera = false;
	//public static String webpage = "http://nymt-one.user32.com:12484/dface/imgproc/1.0/documentation.html";
	public static String webpage = "http://deeplookinc.com/";
    private WebView webView;	
	int notifId = 111;
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.demon_eyes);
		//webView = (WebView) findViewById(R.id.web_view);
		Button buttonText = (Button) findViewById(R.id.button_text);
		Typeface font = Typeface.createFromAsset(getAssets(), "helvetica-neue-bold.ttf");
		buttonText.setTypeface(font);
		pickedPhoto = false;
		//////////////////////////////////////*************************************
		
		/* NotificationCompat.Builder mBuilder =
			        new NotificationCompat.Builder(this)
			        .setSmallIcon(R.drawable.demoneyes_notification)
			        .setContentTitle("My demoneyes notification")
			        .setContentText("No Connection !!!");
			Intent resultIntent = new Intent(this, DemonEyes.class);
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
			stackBuilder.addParentStack(DemonEyes.class);
			stackBuilder.addNextIntent(resultIntent);
			PendingIntent resultPendingIntent =
			        stackBuilder.getPendingIntent(
			            0,
			            PendingIntent.FLAG_UPDATE_CURRENT
			        );
			mBuilder.setContentIntent(resultPendingIntent);
			NotificationManager mNotificationManager =
				    (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
				// mId allows you to update the notification later on.
				mNotificationManager.notify(notifId, mBuilder.build());
*/
		
		//usedCamera = false;
	}
    
	public void openWebpage(View view ){
	
	
	
  }
	public void onClickWebpage(View view){
		openBrowser();
		
	}

	public void onClickWebview(View view){
		//System.out.println("it should work");
		//webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl(webpage);

		
	}
	
	public void onClickPickPhoto(View view){
	pickedPhoto = true;
	//startActivity(new Intent("org.photos.demoneyes.ServerDemon"));
        startActivity(new Intent("org.photos.demoneyes.DaemonEyes"));
	
	}
	
	private void openBrowser(){
		Uri uri = Uri.parse(webpage);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);

		
	}
	
	@Override
 	public boolean onKeyDown(int keyCode, KeyEvent event){
 		 if ((keyCode == KeyEvent.KEYCODE_BACK)) { //Back key pressed
 	    	//System.out.println("eljut ide?");
 	    	//count--;
 		    //startActivity(new Intent("org.education.rememberpattern.PlayPattern"));
 			 //System.out.println("I exit now");
 			 //Intent intent = new Intent(this, DemonEyes.class);
 	           //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
 	          // startActivity(intent);

 			 Intent startMain = new Intent(Intent.ACTION_MAIN);
 			 startMain.addCategory(Intent.CATEGORY_HOME);
 			 startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
 			 startActivity(startMain);
 	        return true;
 	    }
 	    return super.onKeyDown(keyCode, event);
 	}
	
	
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.demon_eyes, menu);
		return true;
	}*/

}

package com.mehow.pirates.server;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

//call with
//Intent intent = new Intent(this, LevelService.class);
//intent.putExtra("MODE", "DONT MATTER for now");
//Log.i("MenuActivity","starting service");
//startService(intent);

public class LevelService extends IntentService{
	
	public LevelService(String name) {
		super(name);
	}

    public LevelService() {
        this("LevelService");
    }
	
	@Override
	protected void onHandleIntent(Intent intent) {
		//use this parameter to set the url and resulting processing
		intent.getStringExtra("MODE");
		DefaultHttpClient   httpclient = new DefaultHttpClient(new BasicHttpParams());
		HttpGet httpGet = new HttpGet("https://ter0.net:8080/pirates/levels");
		//HttpPost httppost = new HttpPost("https://ter0.net:8080/pirates/levels");
		// Depends on your web service
		//httppost.setHeader("Content-type", "application/json");
		httpGet.setHeader("Content-type", "application/json");
		
		InputStream inputStream = null;
		String result = null;
		try {
		    HttpResponse response = httpclient.execute(httpGet);           
		    HttpEntity entity = response.getEntity();

		    inputStream = entity.getContent();
		    // json is UTF-8 by default
		    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
		    StringBuilder sb = new StringBuilder();

		    String line = null;
		    while ((line = reader.readLine()) != null)
		    {
		        sb.append(line + "\n");
		    }
		    result = sb.toString();
		} catch (Exception e) { 
		    // Oops
		}
		finally {
		    try{if(inputStream != null)inputStream.close();}catch(Exception squish){}
		}
		Log.i("LevelService", "got levels: "+result);
	}

}

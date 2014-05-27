package org.games.outgresresloaded;

import java.io.InputStream;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class DescargaImagen extends AsyncTask<String, Void, Bitmap> {
	
	private String foto;
	
	public DescargaImagen (String pUrl) {
		foto = pUrl;
	}
	
	//@Override
	@Override
	protected Bitmap doInBackground(String... params) {
	    Bitmap bitmap = null;
	    
		try {
			URL url = new URL(foto);
	        
	        HttpGet httpRequest = null;

	        httpRequest = new HttpGet(url.toURI());

	        HttpClient httpclient = new DefaultHttpClient();
	        HttpResponse response = httpclient.execute(httpRequest);

	        HttpEntity entity = response.getEntity();
	        BufferedHttpEntity b_entity = new BufferedHttpEntity(entity);
	        InputStream input = b_entity.getContent();

	        bitmap = BitmapFactory.decodeStream(input);
	    } catch (Exception e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	        Log.e("Exception", e.getMessage());
	    }

    return bitmap;
	}

}

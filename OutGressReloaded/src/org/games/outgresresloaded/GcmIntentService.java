package org.games.outgresresloaded;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class GcmIntentService extends IntentService {

	public GcmIntentService() {
		super("GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		String messageType = gcm.getMessageType(intent);
		Bundle extras = intent.getExtras();
		if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
			if (!extras.isEmpty()) {
				///Hacer lo que se quiera hacer con la notificación y su payload
				try {
					JSONObject datos= new JSONObject(intent.getExtras().getString("com.parse.Data"));
					Toast.makeText(getApplicationContext(), "La notificacion ha funcionado!!:"+datos.getString("Asignatura"), Toast.LENGTH_LONG).show();

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}
}	
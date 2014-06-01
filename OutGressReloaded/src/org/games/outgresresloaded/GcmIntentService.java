package org.games.outgresresloaded;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
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
				///Hacer lo que se quiera hacer con la notificaci�n y su payload
				/*try {
					JSONObject datos= new JSONObject(intent.getExtras().getString("com.parse.Data"));

				} catch (JSONException e) {
					e.printStackTrace();
				}*/
				
				NotificationCompat.Builder mBuilder =
						new NotificationCompat.Builder(this)
				.setSmallIcon(android.R.drawable.stat_sys_warning)
				.setLargeIcon((((BitmapDrawable)getResources()
				.getDrawable(R.drawable.ic_launcher)).getBitmap()))
				.setContentTitle("Mensaje de Alerta")
				.setContentText("Ejemplo de notificación en DAS.")
				.setContentInfo("Información extra")
				.setTicker("Nueva notificación!!");
				NotificationManager mNotificationManager =
						(NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);

				mNotificationManager.notify(1, mBuilder.build());

			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}
}	
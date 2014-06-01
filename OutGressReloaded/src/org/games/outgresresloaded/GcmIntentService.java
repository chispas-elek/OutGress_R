package org.games.outgresresloaded;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;
import android.content.Intent;

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
				//Obtenemos los datos que nos llegan
				String nombre = extras.getString("nombre");
				String nick = extras.getString("nick");
				int idportal = extras.getInt("idportal");
				
				//Generamos el intent con los datos necesarios
				Intent i = new Intent(this,InfoPortal.class);
				i.putExtra("idportal", idportal);
				PendingIntent intentEnNoti= PendingIntent.getActivity(this, 0, i, 0);
				
				//Lanzamos una notificación en pantalla
				NotificationCompat.Builder mBuilder =
						new NotificationCompat.Builder(this)
				.setSmallIcon(android.R.drawable.stat_sys_warning)
				.setLargeIcon((((BitmapDrawable)getResources()
				.getDrawable(R.drawable.ic_launcher)).getBitmap()))
				.setContentTitle(nombre+" ha sido capturado!!!!")
				.setContentText(nick+" ha capturado tu portal")
				.setContentInfo("Pulsa para ver información")
				.setTicker("¡¡Has perdido un portal!!")
				.setContentIntent(intentEnNoti);
				NotificationManager mNotificationManager =
						(NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);

				mNotificationManager.notify(1, mBuilder.build());

			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}
}	
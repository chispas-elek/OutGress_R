package org.games.outgresresloaded;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class ServicioAvisador extends Service{

	private Timer mTimer;
	private String mejorProveedor;
	private LocationManager elManager;
	private NotificationCompat.Builder mBuilder;
	private String equipo;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		//Generamos el intent de la notificación
		Intent iMain = new Intent (ServicioAvisador.this,MainActivity.class);
		PendingIntent intentNotif = PendingIntent.getActivity(this, 0,iMain, 0);
		//Generamos la notificación
		mBuilder = new NotificationCompat.Builder(this).setContentIntent(intentNotif).setAutoCancel(true).setSmallIcon(android.R.drawable.stat_sys_warning).setLargeIcon((((BitmapDrawable)getResources().getDrawable(R.drawable.ic_launcher)).getBitmap())).setContentTitle("Outgress").setContentText("¡¡Portal encontrado!!").setContentInfo("¡Pulsa aquí para atacar!").setTicker("¡¡Estás cerca de un portal!!");
		//Leemos la información del fichero de configuracion
		SharedPreferences prefs = getSharedPreferences("preferenciasOR", Context.MODE_PRIVATE);
		equipo = prefs.getString("equipo", "undefinied");
		
		//Configuramos el mejor proveedor y miramos si está activo
		LocationListener listenerlocalizacion = new MiLocationListener();
		elManager = (LocationManager) getSystemService(LOCATION_SERVICE); 
		Criteria losCriterios = new Criteria();
		mejorProveedor = elManager.getBestProvider(losCriterios, true);
		if (!elManager.isProviderEnabled(mejorProveedor)) {
			Intent i= new Intent (android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(i);
		}
		elManager.requestLocationUpdates(mejorProveedor, 3000,0, listenerlocalizacion);
		//Creamos el temporizador
		this.mTimer = new Timer();
		this.mTimer.scheduleAtFixedRate(new TimerTask(){
		     
			@Override
		     public void run() {
		      ejecutarTarea();
		     }      
		    }
		    , 0, 1000 * 30);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		super.onStartCommand(intent, flags, startId);
		return 1;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	@Override
	public IBinder onBind(Intent intent) {
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	private void ejecutarTarea(){
		  Thread t = new Thread(new Runnable() {
		   public void run() {
			   boolean portalCercano = false;
			   //Obtenemos las coordenadas de la posición actual
			   Location pos = elManager.getLastKnownLocation(mejorProveedor);
			   Location posDestino = elManager.getLastKnownLocation(mejorProveedor);
			   //Conectamos a la base de datos y vamos comprobando la distancia minima
			   if(pos != null && posDestino != null) {
				   //Configuramos los parámetros adicionales
				   ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();
				   parametros.add(new BasicNameValuePair("equipo", equipo));
				   //Aquí lanzamos el execute para que se conecte a la BD remota y reciba el String
				   CumplePeticiones result = (CumplePeticiones) new CumplePeticiones(ServicioAvisador.this,parametros,"listaportales.php").execute();
				   //Aquí genero el JSON y hago las vueltas
				   try {
					   JSONArray jsonArray = new JSONArray(result.get());
					   int i = 0;
					   while(i < jsonArray.length() && portalCercano !=true) {
						   //Calculamos la distancia más cercana al usuario
						   posDestino.setLongitude(Float.parseFloat(jsonArray.getJSONObject(i).getString("Longitud")));
						   posDestino.setLatitude(Float.parseFloat(jsonArray.getJSONObject(i).getString("Latitud")));
						   float distancia = pos.distanceTo(posDestino);
						   if(distancia < 30) {
							   //existe un portal cercano.
							   portalCercano = true;
						   }
						   i++;
					   }
				   }catch (JSONException e) {
					   Log.e("JSONException", "Excepción a la hora de manejar el archivo JSON Array");
				   }catch (InterruptedException e) {
						Log.e("InterrupException", "Ha ocurrido una excepción a la hora de convertir el result");
						e.printStackTrace();
					} catch (ExecutionException e) {
						Log.e("ExecutionException", "Se ha producido una excepción de ejecución a la hora de manejar el JSON");
						e.printStackTrace();
					}
					if(portalCercano){
						//Hay un portal cercano. avisamos al usuario de su existencia.
						NotificationManager mNotificationManager = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
						mNotificationManager.notify(1,mBuilder.build());
					}
			   }
		   }
		  });  
		  t.start();
		 }
}
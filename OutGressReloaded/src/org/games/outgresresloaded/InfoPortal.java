package org.games.outgresresloaded;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.games.outgresresloaded.GestionarPortales;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

/**
 * InfoPortales mostrarÃ¡ en pantalla los datos del portal seleccionado, y permitirÃ¡ 
 * atacarlo si estÃ¡ a cierta distancia y el propietario es del equipo contrario.
 * @author Aitor Valle
 *
 */

public class InfoPortal extends Activity {

	private Timer mTimer;
	private JSONObject portal;
	private JSONArray array;
	private SharedPreferences prefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info_portal);
		this.prefs = getSharedPreferences("preferenciasOR", Context.MODE_PRIVATE);
		
		LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		Criteria crit = new Criteria();
		String bestP = lm.getBestProvider(crit, true);
		
		int idportal = 0;
		String foto;
		String nombre;
		String info;
		Double latPort;
		Double longPort;
		String ownerid;
		String owner;
		String fecha;
		
		Bitmap img;
		float[] results = new float[1];
		
		//Recogemos los datos del intent
		Bundle extras = getIntent().getExtras();
		if (extras != null){
			idportal = extras.getInt("idportal");
		}
		
		GestionarPortales gp = new GestionarPortales(InfoPortal.this);
		try {
			//Obtenemos el portal
			portal=gp.obtenerDetallePortal(idportal).getJSONObject(0);
			
			//Recogemos los valores del portal
			//idportal=portal.getInt("idportal");
			foto=portal.getString("foto");
			
			nombre = portal.getString("nombre");
			info = portal.getString("info");
			latPort = portal.getDouble("latitud");
			longPort = portal.getDouble("longitud");
			ownerid = portal.getString("owner");
			fecha = portal.getString("fecha");
			
			ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();
			parametros.add(new BasicNameValuePair("idusuario",ownerid));
			CumplePeticiones cp = (CumplePeticiones) new CumplePeticiones(InfoPortal.this,parametros,"detallejugador.php").execute();
			array = new JSONArray(cp.get());
			owner = array.getJSONObject(0).getString("nick");
			
			TextView infoNombrePortal = (TextView) findViewById(R.id.infoNombrePortal);
			infoNombrePortal.setText(nombre);
			TextView infoCapturadoPor = (TextView) findViewById(R.id.infoCapturadoPor);
			infoCapturadoPor.append(owner);
			TextView infoFechaCapturado = (TextView) findViewById(R.id.infoFechaCapturado);
			infoFechaCapturado.append(fecha);
			TextView infoPortalDescripcion = (TextView) findViewById(R.id.infoPortalDescripcion);
			infoPortalDescripcion.setText(info);
			
			//Calculamos la distancia
			TextView infoPortalDistancia = (TextView) findViewById(R.id.infoPortalDistancia);
			
			if (!lm.isProviderEnabled(bestP)) {
				Intent i= new Intent (android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(i);
			}
			 
			Location pos = lm.getLastKnownLocation(bestP);
			double posLat = pos.getLatitude();
			double posLong = pos.getLongitude();
			
			Location.distanceBetween(posLat, posLong, latPort, longPort, results);
			
			infoPortalDistancia.append(results[0] + " metros.");
			
			//Descargamos la foto y la guardamos la foto en img
			ImageView iv = (ImageView) findViewById(R.id.infoFotoPortal);
			DescargaImagen di = (DescargaImagen) new DescargaImagen(foto).execute();
			
			try {
				img=di.get();
				iv.setImageBitmap(img);
			} catch (InterruptedException e) {
				e.printStackTrace();
				Log.e("InterruptedException", "InterrupciÃ³n durante el procesamiento de la imagen");
			} catch (ExecutionException e) {
				e.printStackTrace();
				Log.e("ExecutionException", "Error durante la ejecuciÃ³n del procesamiento de la imagen");
			}
			
			//Creamos el temporizador
			this.mTimer = new Timer();
			this.mTimer.scheduleAtFixedRate(new TimerTask(){
			     
				@Override
				public void run() {
					actualizarPosicion();
				}      
			}
			, 0, 1000 * 10);
			
			final int numPortal = idportal;
			final int numOwner = prefs.getInt("idusuario", -4);
			Button atacar = (Button) findViewById(R.id.infoButAtacarPortal);
			atacar.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					atacarPortal(numPortal, numOwner);
				}
			});
			
		} catch (JSONException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
			Log.e("JSONException", "Error al procesar JSON");
		}
		
	}
	
	private void actualizarPosicion() {
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				LocationListener listenerlocalizacion = new MiLocationListener();
				LocationManager elManager = (LocationManager) getSystemService(LOCATION_SERVICE); 
				//Configuramos los criterios (Si lo deseamos)
				Criteria losCriterios = new Criteria();
				String mejorProveedor = elManager.getBestProvider(losCriterios, true);
				elManager.requestLocationUpdates(mejorProveedor, 3000,0, listenerlocalizacion);
				Location pos = elManager.getLastKnownLocation(mejorProveedor);
				//Calculamos las distancias. Si el jugador estï¿½ a menos de 50 metros podrï¿½ obtener el portal
				try {
					Location posicionDest = new Location("posicionDest");
					posicionDest.setLatitude((portal.getDouble("latitud")));
					posicionDest.setLongitude(portal.getDouble("longitud"));
					Float distancia = pos.distanceTo(posicionDest);
					if(Float.compare(distancia, 100) < 0 && !array.getJSONObject(0).getString("equipo").equals(prefs.getString("equipo", "fallo"))) {
						//La distancia es menor de 50 metros y el portal no pertenece al equipo del jugador
						Button atacar = (Button) findViewById(R.id.infoButAtacarPortal);
						atacar.setEnabled(true);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
		});
	}
	
	/**
	 * 
	 * Éste método ataca un portal, cambiando el poseedor al que lo ha atacado y desbancando al anterior.
	 * @param pIdPortal El identificador del portal
	 * @param pIDUsuario El identiicador del usuario
	 */
	
	private void atacarPortal(int pIdPortal, int pIDUsuario) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String fecha = df.format(new Date());
		
		String idportal = String.valueOf(pIdPortal);
		String idusuario = String.valueOf(pIDUsuario);
		
		ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();
		parametros.add(new BasicNameValuePair("idportal",idportal));
		parametros.add(new BasicNameValuePair("owner",idusuario));
		parametros.add(new BasicNameValuePair("fecha",fecha));
		
		//TODO GCM
		
		CumplePeticiones cp = (CumplePeticiones) new CumplePeticiones(InfoPortal.this,parametros,"atacarportal.php").execute();
		
		try {
			if(cp.get().contains("0")) {
				//Los datos se han actualizado correctamente
				//Mandamos la notificación push
				ArrayList<NameValuePair> parametros2 = new ArrayList<NameValuePair>();
				try {
					parametros.add(new BasicNameValuePair("gcm",array.getJSONObject(0).getString("gcm")));
					parametros.add(new BasicNameValuePair("nombre",portal.getString("nombre")));
					parametros.add(new BasicNameValuePair("nick",array.getJSONObject(0).getString("nick")));
					CumplePeticiones cp2 = (CumplePeticiones) new CumplePeticiones(InfoPortal.this,parametros,"gcm.php").execute();
					this.finish();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}else {
				Toast.makeText(getApplicationContext(), "Ha ocurrido algún error, por favor inténtalo de nuevo", Toast.LENGTH_LONG).show();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			Log.e("InterrupException", "Error de interrupción a la hora de capturar un portal");
		} catch (ExecutionException e) {
			e.printStackTrace();
			Log.e("ExecuteException", "Error de ejecución a la hora de capturar un portal");
		}
	}

}

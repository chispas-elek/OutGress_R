package org.games.outgresresloaded;

import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.games.outgresresloaded.GestionarPortales;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * InfoPortales mostrará en pantalla los datos del portal seleccionado, y permitirá 
 * atacarlo si está a cierta distancia y el propietario es del equipo contrario.
 * @author Aitor Valle
 *
 */

public class InfoPortal extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info_portal);
		
		LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		Criteria crit = new Criteria();
		String bestP = lm.getBestProvider(crit, true);
		
		int idportal = 0;
		JSONObject portal;
		String foto;
		String nombre;
		String info;
		Double latPort;
		Double longPort;
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
			owner = portal.getString("owner");
			fecha = portal.getString("fecha");
			
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
				Log.e("InterruptedException", "Interrupción durante el procesamiento de la imagen");
			} catch (ExecutionException e) {
				e.printStackTrace();
				Log.e("ExecutionException", "Error durante la ejecución del procesamiento de la imagen");
			}
			
			
			
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e("JSONException", "Error al procesar JSON");
		}
		
	}

}
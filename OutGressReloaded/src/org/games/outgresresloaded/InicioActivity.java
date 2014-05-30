package org.games.outgresresloaded;


import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import com.google.android.gms.maps.GoogleMap;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.NotificationManager;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.os.Build;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class InicioActivity extends FragmentActivity {
	
	private Timer mTimer;
	private String mejorProveedor;
	private LocationManager elManager;
	private GoogleMap mapa;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inicio);
		
		//Generamos el mapa de google Maps
		mapa = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.elmapa)).getMap();
		//Configuramos el mapa por defecto
		mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		//Configuramos el mejor proveedor y miramos si está activo
		LocationListener listenerlocalizacion = new MiLocationListener();
		elManager = (LocationManager) getSystemService(LOCATION_SERVICE); 
		//Configuramos los criterios (Si lo deseamos)
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
				actualizarPosicion();
			}      
		}
		, 0, 1000 * 40);
		
		//El usuario pulsa sobre un portal
		mapa.setOnMarkerClickListener(new OnMarkerClickListener() {
			public boolean onMarkerClick(Marker marker) {
				//qu� se quiere hacer
				return false;
			}
		}); 
		
	}
	
	/**
	 * Éste método actualiza una serie de parámetros en el juego
	 * 
	 * - Actualiza la posición en Google Maps
	 * - Actualiza los portales más cercanos al jugador
	 * - Marca cada portal con el color del equipo corresondiente
	 */
	
	private void actualizarPosicion(){
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(new Runnable(){

			@Override
			public void run() {
				Location pos = elManager.getLastKnownLocation(mejorProveedor);
				CameraUpdate actualizar = CameraUpdateFactory.newLatLngZoom(new LatLng(pos.getLatitude(), pos.getLongitude()), 14);
				mapa.animateCamera(actualizar);
				//Toast.makeText(getApplicationContext(), "Mapa actualizado Latitud: "+pos.getLatitude()+ " Longitud: "+pos.getLongitude(), Toast.LENGTH_LONG).show();
				//Actualizamos la lista de los portales m�s cercanos en base a mi posici�n y los marco en el mapa.
				LatLng longLatid = new LatLng(pos.getLatitude(), pos.getLongitude());
				GestionarPortales gestion = new GestionarPortales(InicioActivity.this);
				JSONArray jsonArr = gestion.listaPortales(longLatid);
				//A partir de los resultados, marcamos las posiciones en el mapa.
				for(int i=0;i<jsonArr.length();i++) {
					//Preguntamos a la BD a qué equipo pertenece el portal capturado para que ponga una marca representado su color
					JSONArray jsonInfoJug = null;
					BitmapDescriptor color = null;
					ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();
					try {
						parametros.add(new BasicNameValuePair("idusuario",Integer.toString(jsonArr.getJSONObject(i).getInt("owner"))));
						CumplePeticiones result = (CumplePeticiones) new CumplePeticiones(InicioActivity.this,parametros,"detallejugador.php").execute();
						jsonInfoJug = new JSONArray(result.get());
						if (jsonInfoJug.getJSONObject(0).getString("equipo").equals("azul")) {
							color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
						}else if (jsonInfoJug.getJSONObject(0).getString("equipo").equals("verde")) {
							color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
						}else {
							//Significa que es un portal sin capturar, esto es, no petenece a ningún equipo
							color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
						}
						
						//Generamos le marker con la información recopilada
						mapa.addMarker(new MarkerOptions().position(new LatLng(jsonArr.getJSONObject(i).getDouble("latitud"),jsonArr.getJSONObject(i).getDouble("longitud")))
								.title(jsonArr.getJSONObject(i).getString("nombre")).snippet("and snippet")
								.icon(color));
					} catch (JSONException e) {
						e.printStackTrace();
						Log.e("JSON Exception", "Error a la hora de manejar el JSON para obtener el detalle del jugador o el marker");
					} catch (InterruptedException e) {
						e.printStackTrace();
						Log.e("JSON Interrup", "Error de interrupción a la hora de manejar el JSON para obtene el detalle del jugador o el marker");
					} catch (ExecutionException e) {
						e.printStackTrace();
						Log.e("JSON Execute", "Error de ejecución del JSON para detalle del jugador o el marker");
					}
				}
			} 
		});
	}
		
	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		getMenuInflater().inflate(R.menu.inicio, menu);
		return true; 
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.anadir:
				Location pos = elManager.getLastKnownLocation(mejorProveedor);
				LatLng posicionJugador = new LatLng(pos.getLatitude(), pos.getLongitude());
				Intent i = new Intent(InicioActivity.this,InsertarPortalNuevoActivity.class);
				i.putExtra("posicion",posicionJugador);
				startActivity(i);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}

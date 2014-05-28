package org.games.outgresresloaded;

import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.location.Location;
import android.util.Log;

/**
 * El objetivo primordial de ésta clase es la gestión de los portales, para conseguir información relativa
 * desde cuántos portales hay cerca del usuario, hasta información específica de un portal
 * @author Rubén Mulero
 *
 */


public class GestionarPortales {

	private Context ctx;
	//La diferencia de distancia entre latitud y longitud es de 200 Metros.
	private static final Double DIFERENCIA_LATITUD = 0.0017978;
	private static final Double DIFERENCIA_LONGITUD = 0.002471;
	
	//Constructora
	public GestionarPortales(Context pCtx) {
		this.ctx = pCtx;
	}
	
	//Métodos
	
	/**
	 * Éste método obtiene los datos relativos a un portal para poder mostrar
	 * @param pIdPortal el identificador del portal
	 * @return JSON que contiene la informacion con los datos. En caso de no conseguir datos, devuelve null;
	 */
	public JSONArray obtenerDetallePortal(int pIdPortal) {
		JSONArray jsonA = null;
		ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();
		parametros.add(new BasicNameValuePair("idportal",Integer.toString(pIdPortal)));
		CumplePeticiones result = (CumplePeticiones) new CumplePeticiones(ctx,parametros,"detalleportal.php").execute();
		try {
			jsonA = new JSONArray(result.get());
		} catch (JSONException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
			Log.e("JSON Error", "Error al manejar el JSON de los detalles del portal");
		}
		if(jsonA == null) {
			Log.i("JSON Info", "Atención, el JSON está vacío, comprueba que la BD te devuelva datos de forma correcta");
		}else {
			Log.i("JSON Info", "El JSON devuelve correctamente los datos");
		}
		return jsonA;
	}
	
	/**
	 * Éste método lista los portales más cercanos al usuario de manera ordenada
	 * @param pLatLng La latitud y la longitud de la posición actual del usuario	
	 * @return JSON con los portales más cercanos a la posicion del usuario
	 */
	
	public JSONArray listaPortales(LatLng pLatl) {
		ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();
		//Realizamos el cálculo de un sector cuadrado desde la posición actual del jugador.
		Double latitud1 = pLatl.latitude + DIFERENCIA_LATITUD;
		Double latitud2 = pLatl.latitude - DIFERENCIA_LATITUD;
		Double longitud1 = pLatl.longitude + DIFERENCIA_LONGITUD;
		Double longitud2 = pLatl.longitude - DIFERENCIA_LONGITUD;
		//Añadimos los valores al array
		parametros.add(new BasicNameValuePair("latitud1",Double.toString(latitud1)));
		parametros.add(new BasicNameValuePair("latitud2",Double.toString(latitud2)));		
		parametros.add(new BasicNameValuePair("longitud1",Double.toString(longitud1)));
		parametros.add(new BasicNameValuePair("longitud2",Double.toString(longitud2)));
		//Ejecutamos la llamada a la BD
		CumplePeticiones result = (CumplePeticiones) new CumplePeticiones(ctx,parametros,"listaportales.php").execute();
		//Ordenamos los resultados y los devolvemos
		return this.ordenarPortales(pLatl, result);
	}
	
	
	/**
	 * El objetivo de éste método, es ordenar la lista de portales recibida, por cercania al usuario
	 * @param pLatl La latitud y la longitud de la posición actual del usuario
	 * @param pResult El resultado de los datos de la BD
	 * @return
	 */
	
	private JSONArray ordenarPortales(LatLng pLatl,CumplePeticiones pResult) {
		JSONArray jsonArr = null;
		JSONArray jsonOrdenado = new JSONArray();
		Vector<Integer> indexOrdenados = new Vector<Integer>();
		try {
			jsonArr = new JSONArray(pResult.get());
			
			//Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results);
			for(int i=0;i<jsonArr.length();i++) {
				Double latitudDest = jsonArr.getJSONObject(i).getDouble("latitud");
				Double longitudDest = jsonArr.getJSONObject(i).getDouble("longitud");
				Double latitudUsu = pLatl.latitude;
				Double longitudUsu = pLatl.longitude;
				float[] resultadoPortalActual = null;
				float[] resultadoPortalOrdenado = null;
				if(indexOrdenados.size() == 0) {
					//La lista de index est� vac�a inserto directamente
					indexOrdenados.add(i);
				}else {
					//La lista contiene elementos, comparo para saber cual es m�s peque�o
					int j = 0;
					boolean insertado = false;
					while(j < indexOrdenados.size() && !insertado) {
						Double latitudAct = jsonArr.getJSONObject(indexOrdenados.get(j)).getDouble("latitud");
						Double longitudAct = jsonArr.getJSONObject(indexOrdenados.get(j)).getDouble("longitud");
						Log.i("Valor latitudAct", "El valor actual de latitud es: "+latitudAct);
						Location.distanceBetween(latitudUsu, longitudUsu, latitudDest, longitudDest, resultadoPortalActual);
						Log.i("PortalActual", "El resultado del portal Actual es: "+resultadoPortalActual[0]);
						Location.distanceBetween(latitudUsu, longitudUsu, latitudAct, longitudAct, resultadoPortalOrdenado);
						Log.i("PortalOrdenado", "El resultado del portal Ordenado es: "+resultadoPortalOrdenado[0]);
						if(resultadoPortalActual[0] < resultadoPortalOrdenado[0]); {
							//El portal, actual est� mas cerca del usuario
							indexOrdenados.add(j,i );
							insertado = true;
						}
					}
					//Nuestro valor est� m�s alejado que el resto, asi que entra en el ultimo lugar.
					if(!insertado) {
						indexOrdenados.add(i);
					}
					
				}
			}
			//Una vez obtenido la lista ordenada, generamos el JSON
			for(int z=0;z<indexOrdenados.size();z++) {
				jsonOrdenado.put(jsonArr.getJSONObject(indexOrdenados.get(z)));
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e("Error JSON", "Ha ocurrido un error a la hora de manejar el JSON en GestionarPortales");
		} catch (InterruptedException e) {
			e.printStackTrace();
			Log.e("Error Interrupcion JSON", "Ha ocurrido un error de interrupción inesperado en el manejo de JSON");
		} catch (ExecutionException e) {
			e.printStackTrace();
			Log.e("Error Ejecución", "Ha ocurrido un error de ejecución con el JSON");
		}
		
		return jsonOrdenado;
	}
	
	/**
	 * Algoritmo que permita el cálculo de la  distancia entre 2 Puntos
	 * @param lat_a Latitud 1
	 * @param lng_a Longitud 1
	 * @param lat_b Latitud 2
	 * @param lng_b Longidud 2
	 * @return Distancia en metros.
	 */
	
	/*private float calcularDistancia (float lat_a, float lng_a, float lat_b, float lng_b ) {
		double earthRadius = 3958.75;
	    double latDiff = Math.toRadians(lat_b-lat_a);
	    double lngDiff = Math.toRadians(lng_b-lng_a);
	    double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
	    Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
	    Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double distance = earthRadius * c;

	    int meterConversion = 1609;

	    return new Float(distance * meterConversion).floatValue();
	}*/ 
}

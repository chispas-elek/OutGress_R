package org.games.outgresresloaded;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.util.Log;

/**
 * El objetivo primordial de ésta clase es la gestión de los portales, para conseguir información relativa
 * desde cuántos portales hay cerca del usuario, hasta información específica de un portal
 * @author Rubén Mulero
 *
 */


public class GestionarPortales {

	private Context ctx;
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
			Log.e("JSON Error", "Error al manejar el JSON de los detalles del portala");
		}
		if(jsonA == null) {
			Log.i("JSON Info", "Atención, el JSON está vacío, comprueba que la BD te devuelva datos de forma correcta");
		}else {
			Log.i("JSON Info", "El JSON devuelve correctamente los datos");
		}
		return jsonA;
	}
	
	/**
	 * Éste método lista los portales más cercanos al usuario
	 * @param pLatLng La latitud y la longitud de la posición actual del usuario
	 * @param pNumPort El número de portales que se quiere obtener
	 * @return JSON con los portales más cercanos a la posicion del usuario
	 */
	
	public JSONArray listaPortales(LatLng pLatl,int pNumPort) {
		JSONArray jsonArr = null;
		ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();
		parametros.add(new BasicNameValuePair("numerop",Integer.toString(pNumPort)));
		parametros.add(new BasicNameValuePair("latitud",Double.toString(pLatl.latitude)));
		parametros.add(new BasicNameValuePair("longitud",Double.toString(pLatl.longitude)));
		CumplePeticiones result = (CumplePeticiones) new CumplePeticiones(ctx,parametros,"listaportales.php").execute();
		
		
		
		
		
		
		return jsonArr;
	}
}

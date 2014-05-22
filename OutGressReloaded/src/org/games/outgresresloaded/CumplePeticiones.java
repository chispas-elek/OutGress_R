package org.games.outgresresloaded;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class CumplePeticiones extends AsyncTask<Void, Void, String> {

	
	private Context context;
	private ProgressDialog dialog;
	private HttpClient httpclient;
	private HttpPost httppost;
	private JSONObject jsonObject;
	private JSONArray jsonArray;
	private ArrayList<NameValuePair> parametros;
	private String pagina;
	
	public CumplePeticiones (Context cxt, ArrayList<NameValuePair> pParametros, String pPagina) {
	    context = cxt;
		//cambiar tipo DE DIALOGO
	    //this.dialog = new ProgressDialog(cxt);
	    this.parametros = pParametros;
	    this.pagina = pPagina;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		//Generamos el dialogo
		//dialog.setTitle("Espera por favor");
        //dialog.show();
        //COnfiguramos la conexion
        //Seteamos el tiempo de respuesta
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 15000);
        HttpConnectionParams.setSoTimeout(httpParameters, 15000);
        
        //Configuramos con los datos el client y el post  
        this.httpclient = new DefaultHttpClient(httpParameters);
        //10.0.2.2 es el localhost de Android
        this.httppost = new HttpPost("http://galan.ehu.es/rmulero001/DAS/"+pagina);
        try {
			httppost.setEntity(new UrlEncodedFormEntity(parametros));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			Log.e("ErrorEntity", "Ha ocurrido un error a la hora de asignarele el entity");
		}
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		// Aquí se hace que el dialogo se avance. Aquí va ese codigo
		super.onProgressUpdate(values);
		/*try {
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	@Override
	protected String doInBackground(Void... params) {
		//HTTPClient.excute aqui va todo eso
		//Ademas aqui ira la ejecucion de publishprogress para avanzar el progreso.
		//Configuramos el post y ejecutamos
		String result = new String();
		try {
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			result = EntityUtils.toString(entity);
			//Eliminar éstos dos JSON que sobran
			//jsonObject = new JSONObject(result);
			//jsonArray = new JSONArray(result);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("ClientProtocolException", "Se ha producido un error de protocolo");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//¿Se puede conectar bien? ¿Tiene la aplicacion permisos de Internet? (android.permission.INTERNET)
			Log.e("IOException","Se ha producido un error de IO en la BD");
		}
		
		return result;
	}
	
	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);		
		//Se deshabilida el dialogo y se cieera la conexion.
		//dialog.dismiss();
	}


}
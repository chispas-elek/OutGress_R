package org.games.outgresresloaded;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Build;
import android.preference.PreferenceManager;

public class MainActivity extends Activity {

	private JSONArray jsonArray;
	private GoogleCloudMessaging gcm;
	private static String SENDER_ID = "72974413367";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//Comprobamos si el dispositivo es compatible con GCM si lo es, que ejecute todo
		//if(checkPlayServices()) {;
		
		final SharedPreferences prefs = getSharedPreferences("preferenciasOR", Context.MODE_PRIVATE);
		//Comprobamos si existen datos del usuario guardados en el sistema
		String codigoUser = prefs.getString("validacion", "0");
		if(codigoUser != "0") {
			//El usuario se ha logueado previamente, comprobamos que esto sea así y saltamos a la pantalla principal
			ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();
			parametros.add(new BasicNameValuePair("validacion",codigoUser));
			CumplePeticiones result = (CumplePeticiones) new CumplePeticiones(MainActivity.this,parametros,"loginc.php").execute();
			//Recogemos la respuesta
			try {
				this.jsonArray = new JSONArray(result.get());
				//Comprobamos si el identificador que teníamos guardado coincide con el de la BD
				int idUsuario = prefs.getInt("idusuario", 0);
				if(jsonArray.getJSONObject(0).getInt("idusuario") == idUsuario) {
					//TODO Los datos son correctos, asi que saltamos a la pantalla principa////////////////////////////
					Toast.makeText(getApplicationContext(), "Saltamos", Toast.LENGTH_LONG).show();
				}else {
					//Algo no ha ido bien
					Log.e("Login", "Error a la hora de procesar los identificadores del login");
				}
			} catch (JSONException | InterruptedException | ExecutionException e) {
				e.printStackTrace();
				Log.e("Error JSON", "Error al procesar el archivo JSON");
			}
			
			
		}else {
			//El usuario aún no se ha logueado en el sistema, recogemos y tratamos los datos
			final EditText usu = (EditText) findViewById(R.id.usuario);
			final EditText pass = (EditText) findViewById(R.id.contrasena);
			Button login = (Button) findViewById(R.id.login);
			login.setOnClickListener(new View.OnClickListener() {
			
				@Override
				public void onClick(View v) {
					//Preparamos los datos y llamamos a la BD
					ArrayList<NameValuePair> parametros2 = new ArrayList<NameValuePair>();
					parametros2.add(new BasicNameValuePair("usuario", usu.getText().toString()));
					parametros2.add(new BasicNameValuePair("pass", pass.getText().toString()));
					CumplePeticiones result2 = (CumplePeticiones) new CumplePeticiones(MainActivity.this,parametros2,"login.php").execute();
					//Recogemos la respuesta
					try {
						jsonArray = new JSONArray(result2.get());
						//Comprobamos que hayamos recibido algun dato
						if(jsonArray.getJSONObject(0).getString("usuario") != null) {
							//El usuario logueado es correcto asi que procedemos a registrar los datos en el dispotivo.
							//Registramos el dispositivo en el GCM y guardamos el identificador
							registrarseGCM();
							//Guardaremos el idusuario y la validacion
							SharedPreferences.Editor editor = prefs.edit();
							editor.putInt("idusuario", jsonArray.getJSONObject(0).getInt("idusuario"));
							editor.putString("validacion", jsonArray.getJSONObject(0).getString("validacion"));
							editor.commit();
							//Una vez validado el usuario se carga la interfaz principal del sistema
							Toast.makeText(getApplicationContext(), "Login correcto, bienvenido", Toast.LENGTH_LONG).show();
							
							/*
							 * Aqui se incluyen los datos necesarios para cargar la interfaz del usuario.
							 * 
							 */
							
						}else {
							//Los datos introducidos son incorrectos
							Toast.makeText(getApplicationContext(), "Los datos introducidos son incorrectos", Toast.LENGTH_LONG).show();
						}
					} catch (JSONException | InterruptedException
							| ExecutionException e) {
						e.printStackTrace();
						Log.e("Error JSON","Error al manejar el JSON");
					}
					
				}
			});
		}
	}
	
	
	
	@Override
	protected void onResume() {
		super.onResume();
		//Comprobamos que nuestro dispositivo sea compatible con GCM
		//this.checkPlayServices();
	}

	/**
	 * Ésta clase sirve para comprobar si el teléfono móvil soporta Google play services y si los tiene activados
	 * @return True o False dependiendo de si tiene o no los Google Play Services
	 */

	private boolean checkPlayServices() {
		 int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		 if (resultCode != ConnectionResult.SUCCESS) {
			 if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				 //Dispositivo no configurado. Mostrar ventana de configuración de Google Play Services
				 //PLAY_SERVICES_RESOLUTION_REQUEST debe valer 9000
				 GooglePlayServicesUtil.getErrorDialog(resultCode,this,9000).show();
			 }else {
				 //Dispositivo no compatible. Terminar la aplicación
				 Log.i("Google Play Services", "This device is not supported.");
				 finish(); 
			 }
			 return false;
		 }
		 return true;
	}
	
	/**
	 * En ésta clase se registra un dispositivo en el GCM
	 * 
	 */
	
	
	private void registrarseGCM() { 
		new AsyncTask<Void,Void,String>(){ 
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
					//SENDER_ID contiene el número de registro del proyecto.
					String regid = gcm.register(SENDER_ID);
					msg  = "Dispositivo registrado correctamente con el regid: "+regid;
					//Guardamos el GCM en las preferencias
					SharedPreferences prefs = getSharedPreferences("preferenciasOR", Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = prefs.edit();
					editor.putString("gcm", regid);
					editor.commit();
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
				}
				return msg;
			}
			@Override
			protected void onPostExecute(String msg) {
				//Mostramos un toast con lo que ha sucedido a la hora de registrar el dispositivo
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
			}
		}.execute(null, null, null);
		
	}
}


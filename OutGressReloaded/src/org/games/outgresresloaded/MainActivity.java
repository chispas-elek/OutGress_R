package org.games.outgresresloaded;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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

	private JSONObject jsonObject;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//Comprobamos si existen datos del usuario guardados en el sistema
		
		final SharedPreferences prefs = getSharedPreferences("preferenciasOR", Context.MODE_PRIVATE);
		String codigoUser = prefs.getString("codigo", "0");
		if(codigoUser != "0") {
			//El usuario se ha logueado previamente, comprobamos que esto sea así y saltamos a la pantalla principal
			ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();
			parametros.add(new BasicNameValuePair("validacion",codigoUser));
			CumplePeticiones result = (CumplePeticiones) new CumplePeticiones(MainActivity.this,parametros,"loginc.php").execute();
			//Recogemos la respuesta
			try {
				this.jsonObject = new JSONObject(result.get());
				//Comprobamos si el identificador que teníamos guardado coincide con el de la BD
				
			} catch (JSONException | InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
					
					
					SharedPreferences.Editor editor = prefs.edit();
					editor.putString("codigo", usu.getText().toString());
					editor.commit();
				}
			});
		}
	}
}


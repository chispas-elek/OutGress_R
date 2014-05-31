package org.games.outgresresloaded;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.provider.MediaStore;


public class InsertarPortalNuevoActivity extends Activity {

	private static final int REQUEST_IMAGE_CAPTURE = 1;
	private ImageView imagen;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_insertar_portal_nuevo);
		//Recuperamos las coordenadas enviadas por el intent
		LatLng posicionJugador = null;
		Bundle extras = getIntent().getExtras();
		if (extras != null){
			posicionJugador = (LatLng) extras.get("posicion");
		}
		
		imagen = (ImageView) findViewById(R.id.addFoto);
		imagen.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Ejecutamos el intent para sacar la foto
				dispatchTakePictureIntent();
				
			}
		});
		
		final SharedPreferences prefs = getSharedPreferences("preferenciasOR", Context.MODE_PRIVATE);
		
		TextView addEditNombre = (TextView) findViewById(R.id.addEditNombre);
		final String nombre = (String) addEditNombre.getText();
		TextView addEditInfo = (TextView) findViewById(R.id.addEditInfo);
		final String info = (String) addEditInfo.getText();
		
		final String latitud = String.valueOf(posicionJugador.latitude);
		final String longitud = String.valueOf(posicionJugador.longitude);
		
		final String idusuario = String.valueOf(prefs.getInt("idusuario",0));
	
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		final String fecha = df.format(new Date());
		
		final String aceptado = String.valueOf(0);
		
		Button enviar = (Button) findViewById(R.id.addEnviar);
		enviar.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();
				parametros.add(new BasicNameValuePair("foto",fecha));
				parametros.add(new BasicNameValuePair("nombre",nombre));
				parametros.add(new BasicNameValuePair("info",info));
				parametros.add(new BasicNameValuePair("latitud",latitud));
				parametros.add(new BasicNameValuePair("longitud",longitud));
				parametros.add(new BasicNameValuePair("owner",idusuario));
				parametros.add(new BasicNameValuePair("fecha",fecha));
				parametros.add(new BasicNameValuePair("aceptado",aceptado));
				CumplePeticiones result = (CumplePeticiones) new CumplePeticiones(InsertarPortalNuevoActivity.this,parametros,"anadirportal.php").execute();
				
				
				
			}
		});
	}
	
	/**
	 * Éste método se encarga de inicializar la cámara y obtener una foto
	 */
	
	private void dispatchTakePictureIntent() {
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
	    }
	}
	
	/**
	 * Éste método tiene como objetivo crear un Thumbail de una foto sacada con la cámara
	 * para realizar una previsualización
	 */
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
	        Bundle extras = data.getExtras();
	        Bitmap imageBitmap = (Bitmap) extras.get("data");
	        imagen.setImageBitmap(imageBitmap);
	    }
	}
	
}

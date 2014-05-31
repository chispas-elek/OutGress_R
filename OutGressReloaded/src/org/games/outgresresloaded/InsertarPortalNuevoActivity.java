package org.games.outgresresloaded;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import com.google.android.gms.drive.internal.AddEventListenerRequest;
import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.MediaStore;


public class InsertarPortalNuevoActivity extends Activity {

	private static final int REQUEST_IMAGE_CAPTURE = 1;
	private ImageView imagen;
	private Timer mTimer;
	private TextView addEditNombre;
	private TextView addEditInfo;
	private Button enviar;
	
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
		
		//Gestionamos el ImageView y configuramos que al pulsar sobre el elemento se abra la cámara de fotos.
		imagen = (ImageView) findViewById(R.id.addFoto);
		imagen.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Ejecutamos el intent para sacar la foto
				dispatchTakePictureIntent();
				
			}
		});
		
		//Recogemos los parámetros y los enviamos a la BD
		//final SharedPreferences prefs = getSharedPreferences("preferenciasOR", Context.MODE_PRIVATE);
		
		addEditNombre = (TextView) findViewById(R.id.addEditNombre);
		addEditInfo = (TextView) findViewById(R.id.addEditInfo);
		final String latitud = Double.toString(posicionJugador.latitude);
		final String longitud = Double.toString(posicionJugador.longitude);
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		final String fecha = df.format(new Date());
		
		addEditNombre.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				activarBoton();
				
			}
		});
		
		addEditInfo.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				activarBoton();
			}
		});
		
		
		enviar = (Button) findViewById(R.id.addEnviar);
		enviar.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();
				parametros.add(new BasicNameValuePair("foto",fecha));
				parametros.add(new BasicNameValuePair("nombre",addEditNombre.getText().toString()));
				parametros.add(new BasicNameValuePair("info",addEditInfo.getText().toString()));
				parametros.add(new BasicNameValuePair("latitud",latitud));
				parametros.add(new BasicNameValuePair("longitud",longitud));
				parametros.add(new BasicNameValuePair("owner","0"));
				parametros.add(new BasicNameValuePair("fecha",fecha));
				parametros.add(new BasicNameValuePair("aceptado","0"));
				CumplePeticiones result = (CumplePeticiones) new CumplePeticiones(InsertarPortalNuevoActivity.this,parametros,"anadirportal.php").execute();
				try {
					cerrarActivity(result.get());
				} catch (InterruptedException e) {
					e.printStackTrace();
					Log.e("Error de Interrupción","Ha ocurrido un error de interrupción en InsertarPortalNuevo");
				} catch (ExecutionException e) {
					e.printStackTrace();
					Log.e("Error de ejecución", "Ha ocurrido un error de ejecución en InsertarPortalNuevo");
				}
			}
		});
		
	}
	
	/**
	 * Ã‰ste mÃ©todo se encarga de inicializar la cÃ¡mara y obtener una foto
	 */
	
	private void dispatchTakePictureIntent() {
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
	    }
	}
	
	/**
	 * Ã‰ste mÃ©todo tiene como objetivo crear un Thumbail de una foto sacada con la cÃ¡mara
	 * para realizar una previsualizacion
	 */
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
	        Bundle extras = data.getExtras();
	        Bitmap imageBitmap = (Bitmap) extras.get("data");
	        imagen.setImageBitmap(imageBitmap);
	    }
	}
	
	private void cerrarActivity(String resultado) {
		if(resultado.contains("0")) {
			//Inserción correcta
			this.finish();
		}else {
			Toast.makeText(getApplicationContext(), "Ha ocurrido un error a la hora de agregar el nuevo portal, por favor, intentalo de nuevo", Toast.LENGTH_LONG).show();
		}
	}
	
	/**
	 * Éste método sirve para activar el botón de enviar en caso de contener datos
	 */
	
	public void activarBoton() {
		boolean preparadoEdit = addEditNombre.getText().toString().length() > 3;
		boolean preparadoInfo = addEditInfo.getText().toString().length() > 3;
		Bitmap bitmap = ((BitmapDrawable)imagen.getDrawable()).getBitmap();
		//TODO Encontrar una solución mejor
		//La imagen preinsertada tiene un count fijo , al sacar una foto y cambiar el bitmap, el count cambia, El codigo detecta ese cambio y habilita el botón
		if(preparadoEdit && preparadoInfo && bitmap.getByteCount() != 5776) {
			//Los campos contienen datos
			enviar.setEnabled(true);
		}else {
			enviar.setEnabled(false);
		}
	}
}

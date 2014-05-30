package org.games.outgresresloaded;

import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import android.os.Build;
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
		
		imagen = (ImageView) findViewById(R.id.imageView1);
		imagen.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Ejecutamos el intent para sacar la foto
				dispatchTakePictureIntent();
				
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

package org.games.outgresresloaded;

import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class InfoPortal extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info_portal);
		
		int idportal;
		
		//Recogemos los datos del intent
		Bundle extras = getIntent().getExtras();
		if (extras != null){
			idportal = extras.getInt("idportal");
		}
		
				
		/* Código que funcionaba para sustituir la imagen recibida como URL por intent
		//Variables
		String urlFoto = null;
		Bitmap img;
		
		//Descargamos la foto
		ImageView iv = (ImageView) findViewById(R.id.infoFotoPortal);
		DescargaImagen di = (DescargaImagen) new DescargaImagen(urlFoto).execute();
		
		//Guardamos la foto en img
		try {
			img=di.get();
			iv.setImageBitmap(img);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Log.e("InterruptedException", "Interrupción durante el procesamiento de la imagen");
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("ExecutionException", "Error durante la ejecución del procesamiento de la imagen");
		}
		*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.info_portal, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}



}

package org.games.outgresresloaded;

import java.util.Timer;
import java.util.TimerTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;

/**
 * Ésta clase busca crear un Splash Screen para darle color y dinamismo a la aplicación y hacer creer al usuario
 * que la aplicación está actualizando cosas, regenerando archivos etc.
 * 
 * @author Rubén Mulero
 *
 */

public class SplashScreenActivity extends Activity {

  private long splashDelay = 6000; //6 segundos

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash_screen);

    TimerTask task = new TimerTask() {
      @Override
      public void run() {
        Intent mainIntent = new Intent().setClass(SplashScreenActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();//Destruimos esta activity para prevenit que el usuario retorne aqui presionando el boton Atras.
      }
    };

    Timer timer = new Timer();
    timer.schedule(task, splashDelay);//Pasado los 6 segundos dispara la tarea
  }

}

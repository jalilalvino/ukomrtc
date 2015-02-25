package com.jalil.rtc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends Activity {

	 /* kode untuk menampilkan splash screen salama 3 detik */
    private final int SPLASH_DISPLAY_LENGHT = 3000;

    /* Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		/* layout splashscreen dengan background gambar */
        setContentView(R.layout.activity_splash);
	/* handler untuk menjalankan splashscreen selama 3 detik lalu
	 * membuat HomeActivity
	 */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = null;

                mainIntent = new Intent(Splash.this,
                        SignIn.class);

                Splash.this.startActivity(mainIntent);
                Splash.this.finish();
            }
        }, SPLASH_DISPLAY_LENGHT);
    }
}
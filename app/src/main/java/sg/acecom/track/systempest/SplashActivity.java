package sg.acecom.track.systempest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by jmingl on 5/3/18.
 */

public class SplashActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initMainActivity();
    }

    private void initMainActivity(){
        Intent i = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(i);
        // close this activity
        finish();
    }
}

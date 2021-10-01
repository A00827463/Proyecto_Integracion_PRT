package mx.itesm.testbasicapi.controller;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import mx.itesm.testbasicapi.R;


public class SplashScreenActivity extends AppCompatActivity {

    // Constants
    private static int SPLASH_TIMER = 1200;
    // Variables
    ImageView backgroundImage;
    TextView poweredByLine;
    SharedPreferences onBoardingScreen;
    // Animations
    Animation sideAnim, bottomAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        // Hooks
        backgroundImage = findViewById(R.id.background_image);
//        poweredByLine = findViewById(R.id.powered_by_line);

        // Animations
        sideAnim = AnimationUtils.loadAnimation(this, R.anim.side_anim);
//        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_anim);

        // set Animations on elements
        backgroundImage.setAnimation(sideAnim);
//        poweredByLine.setAnimation(bottomAnim);

        new Handler().postDelayed(() -> {
            onBoardingScreen = getSharedPreferences("onBoardingScreen", MODE_PRIVATE);
            boolean isFirstTime = onBoardingScreen.getBoolean("firstTime", true);
//            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//            startActivity(intent);
//            finish();
            if(isFirstTime){
                SharedPreferences.Editor editor = onBoardingScreen.edit();
                editor.putBoolean("firstTime", false); // Change
                editor.commit();
                Intent intent = new Intent(getApplicationContext(), OnBoarding.class);
                startActivity(intent);
                finish();
            } else{
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }

        }, SPLASH_TIMER);

    }
}
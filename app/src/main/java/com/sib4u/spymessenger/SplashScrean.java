package com.sib4u.spymessenger;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScrean extends AppCompatActivity {
    TextView textView;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        user = FirebaseAuth.getInstance ( ).getCurrentUser ( );
        setContentView ( R.layout.activity_splash_screan );
        textView = findViewById ( R.id.Splash_Text );
        Animation anim = AnimationUtils.loadAnimation ( this, R.anim.splash_screan );
        textView.setAnimation ( anim );
        new Handler ( ).postDelayed ( new Runnable ( ) {
            @Override
            public void run() {
                if ( user != null && user.isEmailVerified ( ) ) {
                    startActivity ( new Intent ( SplashScrean.this, MainActivity.class ) );
                    finish ( );
                } else {
                    startActivity ( new Intent ( SplashScrean.this, LoginActivity.class ) );
                    finish ( );
                }
            }
        }, 400 );

    }
}
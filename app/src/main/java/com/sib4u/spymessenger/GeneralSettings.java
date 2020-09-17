package com.sib4u.spymessenger;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class GeneralSettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_general_settings );

        findViewById ( R.id.button ).setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance ( ).signOut ( );
                startActivity ( new Intent ( GeneralSettings.this, LoginActivity.class ).setFlags ( Intent.FLAG_ACTIVITY_CLEAR_TOP ) );
            }
        } );

    }
}
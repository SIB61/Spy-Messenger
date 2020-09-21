package com.sib4u.spymessenger;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;

public class SharedPref {
    private final String PublicKey = "PublicKey" + FirebaseAuth.getInstance ( ).getCurrentUser ( ).getUid ( );
    private final String PrivateKey = "PrivateKey" + FirebaseAuth.getInstance ( ).getCurrentUser ( ).getUid ( );
    public String MY_PREFS_NAME = "My_prefs";
    ;
    Context context;
    ;
    SharedPreferences sharedPreferences;

    public SharedPref(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences ( MY_PREFS_NAME, Context.MODE_PRIVATE );

    }

    public String getPublicKey() {
        if ( sharedPreferences.contains ( PublicKey ) ) {
            return sharedPreferences.getString ( PublicKey, null );
        } else return null;
    }

    public void setPublicKey(String publicKey) {
        if ( !sharedPreferences.contains ( PublicKey ) )
            sharedPreferences.edit ( ).putString ( PublicKey, publicKey ).apply ( );
    }

    public String getPrivateKey() {
        if ( sharedPreferences.contains ( PublicKey ) ) {
            return sharedPreferences.getString ( PrivateKey, null );
        } else return null;
    }

    public void setPrivateKey(String privateKey) {
        if ( !sharedPreferences.contains ( PrivateKey ) )
            sharedPreferences.edit ( ).putString ( PrivateKey, privateKey ).apply ( );
    }
}


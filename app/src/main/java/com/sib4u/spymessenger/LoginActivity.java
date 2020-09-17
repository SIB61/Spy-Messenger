package com.sib4u.spymessenger;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    EditText email, pass;
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_login );
        mAuth = FirebaseAuth.getInstance ( );
        email = findViewById ( R.id.LogInEmail );
        pass = findViewById ( R.id.LogInPass );

    }

    public void gotoRegister(View view) {
        startActivity ( new Intent ( LoginActivity.this, RegisterActivity.class ) );
    }

    public void logIn(View view) {
        String Email = email.getText ( ).toString ( ).trim ( );
        String Pass = pass.getText ( ).toString ( ).trim ( );
        if ( Email.isEmpty ( ) || (!Patterns.EMAIL_ADDRESS.matcher ( Email ).matches ( )) ) {
            email.setError ( "invalid email" );
        } else if ( Pass.isEmpty ( ) || Pass.length ( ) < 6 ) {
            pass.setError ( "must be at least 6 letters" );
        } else {
            login ( Email, Pass );
        }
    }

    private void login(String email, String password) {
        mAuth.signInWithEmailAndPassword ( email, password )
                .addOnCompleteListener ( this, new OnCompleteListener<AuthResult> ( ) {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if ( task.isSuccessful ( ) ) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d ( "TAG", "signInWithEmail:success" );
                            FirebaseUser user = mAuth.getCurrentUser ( );
                            updateUI ( user );
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w ( "TAG", "signInWithEmail:failure", task.getException ( ) );
                            Toast.makeText ( LoginActivity.this, "Email or Password incorrect",
                                    Toast.LENGTH_SHORT ).show ( );
                            updateUI ( null );
                        }
                    }
                } );
    }

    private void updateUI(FirebaseUser user) {
        if ( user != null && user.isEmailVerified ( ) ) {
            startActivity ( new Intent ( LoginActivity.this, MainActivity.class ) );
        }
    }

    public void forgot(View view) {
        String Email = email.getText ( ).toString ( ).trim ( );
        if ( Email.isEmpty ( ) || (!Patterns.EMAIL_ADDRESS.matcher ( Email ).matches ( )) ) {
            email.setError ( "invalid email" );
        } else {
            mAuth.sendPasswordResetEmail ( Email );
            Toast.makeText ( LoginActivity.this, "password reset email has been sent",
                    Toast.LENGTH_SHORT ).show ( );
        }
    }
}
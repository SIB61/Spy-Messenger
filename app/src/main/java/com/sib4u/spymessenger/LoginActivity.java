package com.sib4u.spymessenger;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

public class LoginActivity extends AppCompatActivity {
    EditText UsernameEditText, pass;
    FirebaseAuth mAuth;
    FirebaseUser user;
    CollectionReference UserNamesCollectionReference;
    DocumentReference UserNameDocumentReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_login );
        mAuth = FirebaseAuth.getInstance ( );
        UsernameEditText = findViewById ( R.id.LogInUsername );
        pass = findViewById ( R.id.LogInPass );

    }

    public void gotoRegister(View view) {
        startActivity ( new Intent ( LoginActivity.this, RegisterActivity.class ) );
    }

    public void showProgressDialog(String title, String message, int icon) {
        progressDialog = new ProgressDialog ( this );
        progressDialog.setTitle ( title );
        progressDialog.setMessage ( message );
        progressDialog.setIcon ( icon );
        progressDialog.setCanceledOnTouchOutside ( false );
        progressDialog.show ( );
    }

    public void logIn(View view) {
        showProgressDialog ( "loging in...", "Please wait", R.drawable.ic_baseline_person_24 );
        final String email = UsernameEditText.getText ( ).toString ( ).trim ( );
        final String Pass = pass.getText ( ).toString ( ).trim ( );
        if ( !Patterns.EMAIL_ADDRESS.matcher ( email ).matches ( ) ) {
            UsernameEditText.setError ( "invalid username" );
            progressDialog.dismiss ( );
        } else if ( Pass.isEmpty ( ) || Pass.length ( ) < 6 ) {
            pass.setError ( "must be at least 6 letters" );
            progressDialog.dismiss ( );
        } else {
            login ( email, Pass );
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
                            updateUI ( null );
                        }
                    }
                } );
    }

    private void updateUI(FirebaseUser user) {
        progressDialog.dismiss ( );
        if ( user != null && user.isEmailVerified ( ) ) {
            startActivity ( new Intent ( LoginActivity.this, MainActivity.class ).setFlags ( Intent.FLAG_ACTIVITY_CLEAR_TOP ) );
        } else if ( user == null ) {
            Toast.makeText ( getApplicationContext ( ), "invalid username or password", Toast.LENGTH_SHORT ).show ( );
        } else if ( !user.isEmailVerified ( ) ) {
            Toast.makeText ( getApplicationContext ( ), "verify your email and try again", Toast.LENGTH_SHORT ).show ( );
        }
    }

    public void forgot(View view) {
        final View v = LayoutInflater.from ( getApplicationContext ( ) ).inflate ( R.layout.edit_text, null );
        final AlertDialog.Builder builder = new AlertDialog.Builder ( this );
        builder.setIcon ( R.drawable.ic_baseline_email_24 );
        builder.setTitle ( "Enter your email" );
        builder.setView ( v );
        builder.setCancelable ( false );
        builder.setPositiveButton ( "done", new DialogInterface.OnClickListener ( ) {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showProgressDialog ( "sending password reset email", "wait", R.drawable.ic_baseline_swap_vertical_circle_24 );
                EditText e = v.findViewById ( R.id.editTextDialog );
                String email = e.getText ( ).toString ( ).trim ( );
                if ( email.isEmpty ( ) || (!Patterns.EMAIL_ADDRESS.matcher ( email ).matches ( )) ) {
                    e.setError ( "invalid email" );
                    progressDialog.dismiss ( );
                } else {

                    mAuth.sendPasswordResetEmail ( email ).addOnCompleteListener ( new OnCompleteListener<Void> ( ) {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss ( );
                            if ( task.isSuccessful ( ) ) {
                                Toast.makeText ( LoginActivity.this, "password reset email has been sent",
                                        Toast.LENGTH_SHORT ).show ( );
                            } else {
                                Toast.makeText ( LoginActivity.this, "sorry! something went wrong",
                                        Toast.LENGTH_SHORT ).show ( );
                            }
                        }
                    } );


                }
            }
        } ).setNegativeButton ( "cancel", new DialogInterface.OnClickListener ( ) {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss ( );
                progressDialog.dismiss ( );
            }
        } );
        AlertDialog alertDialog = builder.create ( );
        alertDialog.show ( );
    }
}
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {
    EditText name, email, pass;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    DocumentReference documentReference;
    CollectionReference collectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_register );
        firestore = FirebaseFirestore.getInstance ( );
        collectionReference = firestore.collection ( "Users" );
        mAuth = FirebaseAuth.getInstance ( );
        name = findViewById ( R.id.regName );
        email = findViewById ( R.id.regEmail );
        pass = findViewById ( R.id.regPass );


    }

    public void register(View view) {
        String Email = email.getText ( ).toString ( ).trim ( );
        String Name = name.getText ( ).toString ( ).trim ( );
        String Pass = pass.getText ( ).toString ( ).trim ( );

        if ( Name.length ( ) < 3 ) {
            name.setError ( "invalid name" );
        } else if ( !Patterns.EMAIL_ADDRESS.matcher ( Email ).matches ( ) ) {
            email.setError ( "invalid email" );
        } else if ( Pass.length ( ) < 6 ) {
            pass.setError ( "must be at least 6 letters" );
        } else {
            signUp ( Email, Pass, Name );
        }

    }

    private void signUp(String email, String password, final String name) {
        mAuth.createUserWithEmailAndPassword ( email, password )
                .addOnCompleteListener ( this, new OnCompleteListener<AuthResult> ( ) {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if ( task.isSuccessful ( ) ) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d ( "TAG", "createUserWithEmail:success" );
                            FirebaseUser user = mAuth.getCurrentUser ( );
                            user.sendEmailVerification ( );
                            UserModel userModel = new UserModel ( );
                            userModel.setName ( name );
                            userModel.setUserId ( user.getUid ( ) );
                            collectionReference.document ( user.getUid ( ) ).set ( userModel );
                            Toast.makeText ( RegisterActivity.this, "Verification mail has been sent. Login with email after verification",
                                    Toast.LENGTH_SHORT ).show ( );

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w ( "TAG", "createUserWithEmail:failure", task.getException ( ) );
                            Toast.makeText ( RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT ).show ( );
                        }

                        // ...
                    }
                } );

    }

    @Override
    protected void onStart() {
        super.onStart ( );
        if ( mAuth.getCurrentUser ( ) != null && mAuth.getCurrentUser ( ).isEmailVerified ( ) ) {
            startActivity ( new Intent ( RegisterActivity.this, MainActivity.class ).setFlags ( Intent.FLAG_ACTIVITY_CLEAR_TOP ) );
        }
    }

    public void back(View view) {
        onBackPressed ( );
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed ( );
        finish ( );
    }
}
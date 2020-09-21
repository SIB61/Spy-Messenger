package com.sib4u.spymessenger;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    EditText name, email, pass;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    DocumentReference documentReference;
    CollectionReference collectionReference;
    CollectionReference UserNamesRef;
    DocumentReference UserInfoReference;
    ProgressDialog progressDialog;
    boolean isAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_register );
        firestore = FirebaseFirestore.getInstance ( );
        collectionReference = firestore.collection ( "UserInfo" );
        //    UserNamesRef = firestore.collection ( "UserNames" );
        mAuth = FirebaseAuth.getInstance ( );
        name = findViewById ( R.id.regName );
        email = findViewById ( R.id.regEmail );
        pass = findViewById ( R.id.regPass );

        name.addTextChangedListener ( new TextWatcher ( ) {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(final CharSequence charSequence, int i, int i1, int i2) {

                if ( !new UserNameValidator ( ).validate ( charSequence.toString ( ).trim ( ) ) ) {
                    isAvailable = false;
                    name.setError ( "invalid username" );
                } else if ( charSequence.toString ( ).trim ( ).length ( ) > 0 ) {
                    collectionReference.whereEqualTo ( "name", charSequence.toString ( ).trim ( ) )
                            .get ( ).addOnCompleteListener (
                            new OnCompleteListener<QuerySnapshot> ( ) {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if ( task.isSuccessful ( ) ) {
                                        isAvailable = Objects.requireNonNull ( task.getResult ( ) ).isEmpty ( );
                                        if ( isAvailable ) {
                                            @SuppressLint("UseCompatLoadingForDrawables") Drawable icon = getResources ( ).getDrawable ( R.drawable.ic_baseline_check_24 );
                                            if ( icon != null ) {
                                                icon.setBounds ( 0, 0,
                                                        icon.getIntrinsicWidth ( ),
                                                        icon.getIntrinsicHeight ( ) );
                                                name.setError ( "available", icon );
                                            }
                                        } else {
                                            name.setError ( "not available" );
                                        }
                                    } else {
                                        Toast.makeText ( getApplicationContext ( ), "something went wrong", Toast.LENGTH_SHORT ).show ( );
                                        Log.d ( "UserNameSearch", "onComplete: " + Objects.requireNonNull ( task.getException ( ) ).getMessage ( ) );
                                    }
                                }
                            }
                    );


                   /*  UserNamesRef.document ( "UserNames1" ).get ().addOnCompleteListener ( new OnCompleteListener<DocumentSnapshot> ( ) {
                         @Override
                         public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                             if(task.isSuccessful ())
                             {
                                 isAvailable=!task.getResult ().contains ( charSequence.toString ().trim () );

                                 if(isAvailable) {
                                     Drawable icon =
                                             getResources ( ).getDrawable ( R.drawable.ic_baseline_check_24 );
                                     if ( icon != null ) {
                                         icon.setBounds ( 0, 0,
                                                 icon.getIntrinsicWidth ( ),
                                                 icon.getIntrinsicHeight ( ) );
                                         name.setError ( "available", icon );
                                     }
                                 }
                                 else{

                                         name.setError ( "not available" );
                                 }
                             }
                             else {
                                 Log.d ("TAG", "onComplete: "+task.getException ().getMessage () );
                             }
                         }
                     } );*/
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        } );

    }

    public void register(View view) {
        String Email = email.getText ( ).toString ( ).trim ( );
        String Name = name.getText ( ).toString ( ).trim ( );
        String Pass = pass.getText ( ).toString ( ).trim ( );
        if ( !isAvailable ) {
            name.findFocus ( );
        } else if ( !Patterns.EMAIL_ADDRESS.matcher ( Email ).matches ( ) ) {
            email.setError ( "invalid email" );
        } else if ( Pass.length ( ) < 6 ) {
            pass.setError ( "must be at least 6 letters" );
        } else {
            signUp ( Email, Pass, Name );
        }

    }

    public void showProgressDialog(String title, String message, int icon) {
        progressDialog = new ProgressDialog ( this );
        progressDialog.setTitle ( title );
        progressDialog.setMessage ( message );
        progressDialog.setIcon ( icon );
        progressDialog.setCanceledOnTouchOutside ( false );
        progressDialog.show ( );
    }

    private void signUp(final String emailText, String password, final String username) {
        email.setText ( null );
        pass.setText ( null );
        name.setText ( null );
        showProgressDialog ( "Creating your account", "please wait", R.drawable.ic_baseline_person_24 );
        mAuth.createUserWithEmailAndPassword ( emailText, password )
                .addOnCompleteListener ( this, new OnCompleteListener<AuthResult> ( ) {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if ( task.isSuccessful ( ) ) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d ( "TAG", "createUserWithEmail:success" );
                            final FirebaseUser user = mAuth.getCurrentUser ( );
                            final UserModel userModel = new UserModel ( );
                            userModel.setName ( username );
                            userModel.setUserId ( Objects.requireNonNull ( user ).getUid ( ) );
                            userModel.setVisibility ( true );
                            collectionReference.document ( user.getUid ( ) ).set ( userModel ).addOnCompleteListener (
                                    new OnCompleteListener<Void> ( ) {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if ( task.isSuccessful ( ) ) {
                                                user.sendEmailVerification ( ).addOnCompleteListener (
                                                        new OnCompleteListener<Void> ( ) {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if ( task.isSuccessful ( ) ) {
                                                                    progressDialog.dismiss ( );
                                                                    Toast.makeText ( RegisterActivity.this, "Verification mail has been sent. Login with email after verification",
                                                                            Toast.LENGTH_LONG ).show ( );
                                                                    onBackPressed ( );
                                                                }
                                                            }
                                                        }
                                                );
                                            } else {
                                                Log.w ( "TAG", "onComplete: " + task.getException ( ).getCause ( ) );

                                            }
                                        }
                                    }
                            );
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w ( "TAG", "createUserWithEmail:failure", task.getException ( ) );
                            Toast.makeText ( RegisterActivity.this, "Something went wrong",
                                    Toast.LENGTH_SHORT ).show ( );
                        }
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
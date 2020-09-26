package com.sib4u.spymessenger;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.annotation.Nullable;

import id.zelory.compressor.Compressor;

public class MyProfileActivity extends AppCompatActivity {
    final int PICK_PROFILE = 1, PICK_COVER = 2;
    ImageView cover, profile;
    TextView name, status, school, job, location;
    StorageReference storageReference, storageReference1;
    DocumentReference documentReference;
    FirebaseUser firebaseUser;
    UserModel userModel;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_my_profile );
      //  cover = findViewById ( R.id.MyProfileCover );
        userModel = new UserModel ( );
        profile = findViewById ( R.id.MyProfilePic );
        name = findViewById ( R.id.MyProfileName );
        status = findViewById ( R.id.MyProfileStatus );
        school = findViewById ( R.id.MyProfileSchool );
        job = findViewById ( R.id.MyProfileJob );
        location = findViewById ( R.id.MyProfileLoc );
        firebaseUser = FirebaseAuth.getInstance ( ).getCurrentUser ( );
        documentReference = FirebaseFirestore.getInstance ( ).document ( "UserInfo/" + firebaseUser.getUid ( ) );
        storageReference = FirebaseStorage.getInstance ( ).getReference ( ).child ( "ProfilePic/" + firebaseUser.getUid ( ) + ".jpg" );

    }

    private void addListener() {
        documentReference.addSnapshotListener ( this, new EventListener<DocumentSnapshot> ( ) {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String profileUrl = documentSnapshot.getString ( "profilePic" );
                if ( profileUrl != null )
                    Picasso.get ( ).load ( profileUrl ).networkPolicy ( NetworkPolicy.OFFLINE )
                            .placeholder ( R.drawable.ic_default_image )
                            .into ( profile, new Callback ( ) {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get ( ).load ( profileUrl )
                                            .placeholder ( R.drawable.ic_default_image )
                                            .error ( R.drawable.ic_default_image )
                                            .into ( profile );

                                }
                            } );
                if ( documentSnapshot.getString ( "name" ) != null )
                    name.setText ( documentSnapshot.getString ( "name" ) );
                if ( documentSnapshot.getString ( "status" ) != null )
                    status.setText ( documentSnapshot.getString ( "status" ) );
                if ( documentSnapshot.getString ( "location" ) != null )
                    location.setText ( documentSnapshot.getString ( "location" ) );
                if ( documentSnapshot.getString ( "job" ) != null )
                    job.setText ( documentSnapshot.getString ( "job" ) );
                if ( documentSnapshot.getString ( "education" ) != null )
                    school.setText ( documentSnapshot.getString ( "education" ) );
            }
        } );

    }


    public void showProgressDialog(String title, String message, int icon) {
        progressDialog = new ProgressDialog ( this );
        progressDialog.setTitle ( title );
        progressDialog.setMessage ( message );
        progressDialog.setIcon ( icon );
        progressDialog.setCanceledOnTouchOutside ( false );
        progressDialog.show ( );
    }


    @Override
    protected void onStart() {
        super.onStart ( );
        setOnline ( );
        addListener ( );
    }

    @Override
    protected void onStop() {
        super.onStop ( );
        setLastSeen ( );
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed ( );
    }

    public void editProfile(View view) {
    /*    progressDialog=new ProgressDialog ( this );
        progressDialog.setTitle ( "Wait!" );
        progressDialog.setMessage ( "updating your card" );
        progressDialog.setCanceledOnTouchOutside ( false );
        progressDialog.show ();*/
        //   OptionsView.setVisibility ( View.VISIBLE );

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog ( this );
        // bottomSheetDialog.setContentView ( LayoutInflater.from ( this ).inflate ( R.layout.edit_options,null ));
        bottomSheetDialog.setContentView ( R.layout.edit_options );
        bottomSheetDialog.create ( );
        bottomSheetDialog.setCanceledOnTouchOutside ( false );
        bottomSheetDialog.show ( );
    }

  /*  public void popup(View view) {
        popupMenu=new PopupMenu ( getApplicationContext (),view );
        popupMenu.inflate ( R.menu.pop );
        popupMenu.setOnMenuItemClickListener ( itemClickListener );
        popupMenu.show ();
    }

  PopupMenu.OnMenuItemClickListener itemClickListener=
            new PopupMenu.OnMenuItemClickListener ( ) {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    if ( menuItem.getItemId ( ) == R.id.mutual ) {
                       visibility.setText ( "mutual friends" );
                    } else {
                       visibility.setText ( "everyone" );
                    }
                    return  true;
                }
            }; */
  private void setLastSeen() {
      @SuppressLint("SimpleDateFormat") String time = new SimpleDateFormat ( "d MMM yyyy, h:mm a" ).format ( Timestamp.now ( ).toDate ( ) );
      FirebaseFirestore.getInstance ( ).document ( "UserInfo/" + FirebaseAuth.getInstance ( ).getCurrentUser ( ).getUid ( ) )
              .update ( "lastSeen", "last seen " + time );
  }

    private void setOnline() {
        FirebaseFirestore.getInstance ( ).document ( "UserInfo/" + FirebaseAuth.getInstance ( ).getCurrentUser ( ).getUid ( ) )
                .update ( "lastSeen", "online" );
    }


    public void setProfile(View v) {


        Intent intent = CropImage.activity ( )
                .setCropShape ( CropImageView.CropShape.OVAL )
                .setAllowFlipping ( true )
                .setAllowRotation ( true )
                .setActivityTitle ( "set profile" )
                .setGuidelines ( CropImageView.Guidelines.ON )
                .setAspectRatio ( 1, 1 )
                .setFixAspectRatio ( true )
                .getIntent ( this );
        startActivityForResult ( intent, PICK_PROFILE );

    }

   /* public void setCover(View view) {
        Intent intent = CropImage.activity ( )
                .setGuidelines ( CropImageView.Guidelines.ON )
                .setAspectRatio ( 16, 9 )
                .setFixAspectRatio ( true )
                .getIntent ( this );
        startActivityForResult ( intent, PICK_COVER );*/

    //}

    public void setStatus(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder ( this );
        final View view1 = LayoutInflater.from ( this ).inflate ( R.layout.edit_text, null );
        builder.setView ( view1 ).setTitle ( "set Your favourite quote" )
                .setIcon ( R.drawable.ic_baseline_title_24 )
                .setMessage ( "length should be at least 3 and at most 200" )
                .setCancelable ( false )
                .setPositiveButton ( "done", new DialogInterface.OnClickListener ( ) {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {
                        showProgressDialog ( "Updating status...", "Please wait while updating ", R.drawable.ic_baseline_cloud_upload_24 );
                        EditText editText = view1.findViewById ( R.id.editTextDialog );
                        String Status = editText.getText ( ).toString ( ).trim ( );
                        if ( Status.length ( ) < 3 ) {
                            editText.setError ( "length must be at least 3" );
                            progressDialog.dismiss ( );
                        } else {
                            documentReference.update ( "status", Status ).addOnCompleteListener ( new OnCompleteListener<Void> ( ) {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if ( task.isSuccessful ( ) ) {
                                        progressDialog.dismiss ( );
                                    } else {
                                        Toast.makeText ( getApplicationContext ( ), task.getException ( ).getCause ( ).toString ( ), Toast.LENGTH_SHORT ).show ( );
                                        progressDialog.dismiss ( );
                                    }
                                }
                            } );
                        }

                        dialogInterface.dismiss ( );

                    }
                } ).setNegativeButton ( "cancel", new DialogInterface.OnClickListener ( ) {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss ( );
            }
        } );
        AlertDialog alertDialog = builder.create ( );
        alertDialog.show ( );
    }

    public void setEdu(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder ( this );
        final View view1 = LayoutInflater.from ( this ).inflate ( R.layout.edit_text, null );
        builder.setView ( view1 ).setTitle ( "Enter your recent educational institution" )
                .setIcon ( R.drawable.ic_baseline_school_24 )
                .setCancelable ( false )
                .setPositiveButton ( "done", new DialogInterface.OnClickListener ( ) {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {
                        showProgressDialog ( "Updating educational institution...", "Please wait while updating ", R.drawable.ic_baseline_cloud_upload_24 );
                        EditText editText = view1.findViewById ( R.id.editTextDialog );
                        String edu = editText.getText ( ).toString ( ).trim ( );
                        if ( edu.length ( ) < 2 ) {
                            editText.setError ( "length must be at least 2" );
                            progressDialog.dismiss ( );
                        } else {
                            documentReference.update ( "education", edu ).addOnCompleteListener ( new OnCompleteListener<Void> ( ) {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if ( task.isSuccessful ( ) ) {
                                        progressDialog.dismiss ( );
                                    } else {
                                        Toast.makeText ( getApplicationContext ( ), task.getException ( ).getCause ( ).toString ( ), Toast.LENGTH_SHORT ).show ( );
                                        progressDialog.dismiss ( );
                                    }
                                }
                            } );
                        }

                        dialogInterface.dismiss ( );

                    }
                } ).setNegativeButton ( "cancel", new DialogInterface.OnClickListener ( ) {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss ( );
            }
        } );
        AlertDialog alertDialog = builder.create ( );
        alertDialog.show ( );
    }

    public void setLoc(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder ( this );
        final View view1 = LayoutInflater.from ( this ).inflate ( R.layout.edit_text, null );
        builder.setView ( view1 ).setTitle ( "Enter Your Current Location" )
                .setIcon ( R.drawable.ic_baseline_location_on_24 )
                .setMessage ( "length should be at least 3" )
                .setCancelable ( false )
                .setPositiveButton ( "done", new DialogInterface.OnClickListener ( ) {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {
                        showProgressDialog ( "Updating location...", "Please wait while updating ", R.drawable.ic_baseline_cloud_upload_24 );
                        EditText editText = view1.findViewById ( R.id.editTextDialog );
                        String location = editText.getText ( ).toString ( ).trim ( );
                        if ( location.length ( ) < 2 ) {
                            editText.setError ( "length must be at least 2" );
                            progressDialog.dismiss ( );
                        } else {
                            documentReference.update ( "location", location ).addOnCompleteListener ( new OnCompleteListener<Void> ( ) {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if ( task.isSuccessful ( ) ) {
                                        progressDialog.dismiss ( );
                                    } else {
                                        Toast.makeText ( getApplicationContext ( ), task.getException ( ).getCause ( ).toString ( ), Toast.LENGTH_SHORT ).show ( );
                                        progressDialog.dismiss ( );
                                    }
                                }
                            } );
                        }

                        dialogInterface.dismiss ( );

                    }
                } ).setNegativeButton ( "cancel", new DialogInterface.OnClickListener ( ) {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss ( );
            }
        } );
        AlertDialog alertDialog = builder.create ( );
        alertDialog.show ( );
    }

    public void setJob(View view) {
        {
            final AlertDialog.Builder builder = new AlertDialog.Builder ( this );
            final View view1 = LayoutInflater.from ( this ).inflate ( R.layout.edit_text, null );
            builder.setView ( view1 ).setTitle ( "Enter Your Current Location" )
                    .setIcon ( R.drawable.ic_baseline_location_on_24 )
                    .setMessage ( "length should be at least 3" )
                    .setCancelable ( false )
                    .setPositiveButton ( "done", new DialogInterface.OnClickListener ( ) {
                        @Override
                        public void onClick(final DialogInterface dialogInterface, int i) {
                            showProgressDialog ( "Updating location...", "Please wait while updating ", R.drawable.ic_baseline_cloud_upload_24 );
                            EditText editText = view1.findViewById ( R.id.editTextDialog );
                            String job = editText.getText ( ).toString ( ).trim ( );
                            if ( job.length ( ) < 3 ) {
                                editText.setError ( "length must be at least 3" );
                                progressDialog.dismiss ( );
                            } else {
                                documentReference.update ( "job", job ).addOnCompleteListener ( new OnCompleteListener<Void> ( ) {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if ( task.isSuccessful ( ) ) {
                                            progressDialog.dismiss ( );
                                        } else {
                                            Toast.makeText ( getApplicationContext ( ), task.getException ( ).getCause ( ).toString ( ), Toast.LENGTH_SHORT ).show ( );
                                            progressDialog.dismiss ( );
                                        }
                                    }
                                } );
                            }

                            dialogInterface.dismiss ( );

                        }
                    } ).setNegativeButton ( "cancel", new DialogInterface.OnClickListener ( ) {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss ( );
                }
            } );
            AlertDialog alertDialog = builder.create ( );
            alertDialog.show ( );
        }
    }

    /* public void setName(View view) {

        final AlertDialog.Builder builder=new AlertDialog.Builder ( this );
        final View view1= LayoutInflater.from ( this ).inflate ( R.layout.edit_text,null );
        builder.setView ( view1 ).setTitle ( "Enter Your Name" )
                .setIcon ( R.drawable.ic_baseline_title_24 )
                .setMessage ( "length should be at least 3" )
                .setCancelable ( false )
                .setPositiveButton ( "done", new DialogInterface.OnClickListener ( ) {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                showProgressDialog ( "Updating name...","Please wait while updating " ,R.drawable.ic_baseline_title_24);
                EditText editText=view1.findViewById ( R.id.editTextDialog );
                String Name=editText.getText ().toString ().trim ();
                if(Name.length ()<3){
                    editText.setError ( "length must be at least 3" );
                    progressDialog.dismiss ();
                }
                else {
                    documentReference.update ( "name" , Name).addOnCompleteListener ( new OnCompleteListener<Void> ( ) {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful ()){
                                progressDialog.dismiss ();
                            }
                            else{
                                Toast.makeText ( getApplicationContext (),task.getException ().getCause ().toString (),Toast.LENGTH_SHORT ).show ();
                                progressDialog.dismiss ();
                            }
                        }
                    } );
                }

                dialogInterface.dismiss ();

            }
        } ).setNegativeButton ( "cancel", new DialogInterface.OnClickListener ( ) {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss ();
            }
        } );
        AlertDialog alertDialog= builder.create ();
        alertDialog.show ();
    }*/


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult ( requestCode, resultCode, data );
        if ( requestCode == PICK_PROFILE && resultCode == RESULT_OK ) {
            showProgressDialog ( "Uploading Profile", "Please wait while uploading!", R.drawable.ic_baseline_cloud_upload_24 );
            CropImage.ActivityResult result = CropImage.getActivityResult ( data );
            if ( resultCode == RESULT_OK ) {
                File finalImageFile = null;
                Uri imageUri = result.getUri ( );
                File imageFile = new File ( imageUri.getPath ( ) );
                try {
                    finalImageFile = new Compressor ( this )
                            .setMaxHeight ( 200 )
                            .setMaxWidth ( 200 )
                            .setQuality ( 75 )
                            .compressToFile ( imageFile );
                } catch (IOException e) {
                    e.printStackTrace ( );
                }
                storageReference.putFile ( Uri.fromFile ( finalImageFile ) ).addOnCompleteListener ( new OnCompleteListener<UploadTask.TaskSnapshot> ( ) {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if ( task.isSuccessful ( ) ) {
                            storageReference.getDownloadUrl ( ).addOnCompleteListener ( new OnCompleteListener<Uri> ( ) {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if ( task.isSuccessful ( ) ) {
                                        String url = task.getResult ( ).toString ( );
                                        documentReference.update ( "profilePic", url ).addOnCompleteListener ( new OnCompleteListener<Void> ( ) {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if ( task.isSuccessful ( ) ) {
                                                    progressDialog.dismiss ( );
                                                } else {
                                                    progressDialog.dismiss ( );
                                                }
                                            }
                                        } );
                                    } else {
                                        progressDialog.dismiss ( );
                                    }
                                }
                            } );
                        } else {
                            progressDialog.dismiss ( );
                        }
                    }
                } );


            } else if ( resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE ) {
                Exception error = result.getError ( );
            }
        } else if ( requestCode == PICK_COVER && resultCode == RESULT_OK ) {
            showProgressDialog ( "Uploading Cover", "Please wait while uploading!", R.drawable.ic_baseline_cloud_upload_24 );
            CropImage.ActivityResult result = CropImage.getActivityResult ( data );
            if ( resultCode == RESULT_OK ) {

                Uri imageUri = result.getUri ( );
                storageReference1.putFile ( imageUri ).addOnCompleteListener ( new OnCompleteListener<UploadTask.TaskSnapshot> ( ) {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if ( task.isSuccessful ( ) ) {
                            storageReference1.getDownloadUrl ( ).addOnCompleteListener ( new OnCompleteListener<Uri> ( ) {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if ( task.isSuccessful ( ) ) {
                                        String url = task.getResult ( ).toString ( );
                                        documentReference.update ( "coverPic", url ).addOnCompleteListener ( new OnCompleteListener<Void> ( ) {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if ( task.isSuccessful ( ) ) {
                                                    progressDialog.dismiss ( );
                                                } else {
                                                    progressDialog.dismiss ( );
                                                }
                                            }
                                        } );
                                    } else {
                                        progressDialog.dismiss ( );
                                    }
                                }
                            } );
                        } else {
                            progressDialog.dismiss ( );
                        }
                    }
                } );


            } else if ( resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE ) {
                Exception error = result.getError ( );
            }
        }
    }

    public void Back(View view) {
        onBackPressed ( );
    }
}
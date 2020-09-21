package com.sib4u.spymessenger;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendProfileActivity extends AppCompatActivity {
    private static final String TAG = "TAG";
    String ID, NAME, PHOTO;
    String FriendPubKey;
    String Type;
    Button action;
    Button sendMsg;
    Timestamp timestamp;

    CircleImageView FPPic;
    TextView FPName, FPStatus, FPEducation, FPJob, FPLocation;
    FirebaseUser firebaseUser;
    Map<String, Object> map = new HashMap<> ( );


    DocumentReference UserConnectionRef, MyConnectionRef, UserInfoRef;
    public View.OnClickListener listener = new View.OnClickListener ( ) {
        @Override
        public void onClick(View view) {
            Map<String, Object> map1 = new HashMap<> ( );
            final Map<String, Object> map2 = new HashMap<> ( );
            if ( Type == null ) {
                map1.put ( "type", "toUser" );
                map2.put ( "type", "toMe" );
            } else if ( Type.equals ( "toMe" ) ) {
                map1.put ( "type", "friends" );
                map2.put ( "type", "friends" );
            } else if ( Type.equals ( "toUser" ) || Type.equals ( "friends" ) ) {
                map1.put ( "type", null );
                map2.put ( "type", null );
            }
            MyConnectionRef.set ( map1 ).addOnCompleteListener ( new OnCompleteListener<Void> ( ) {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if ( task.isSuccessful ( ) ) {
                        UserConnectionRef.set ( map2 );
                    }
                }
            } );
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_friend_profile );
        //noinspection unchecked
        map = (Map<String, Object>) getIntent ( ).getSerializableExtra ( "map" );
        if ( map != null ) {
            ID = (String) map.get ( "userId" );
        }
        action = findViewById ( R.id.send_req );
        action.setOnClickListener ( listener );
        sendMsg = findViewById ( R.id.send_msg );
        firebaseUser = FirebaseAuth.getInstance ( ).getCurrentUser ( );
        UserConnectionRef = FirebaseFirestore.getInstance ( ).document ( "Connections/" +
                ID + "/MyConnections/" + firebaseUser.getUid ( ) );
        MyConnectionRef = FirebaseFirestore.getInstance ( ).document ( "Connections/" + firebaseUser.getUid ( ) + "/MyConnections/" + ID );
        FPPic = findViewById ( R.id.FriendProfilePic );
        FPName = findViewById ( R.id.FriendProfileName );
        FPStatus = findViewById ( R.id.FriendProfileStatus );
        FPEducation = findViewById ( R.id.FriendProfileSchool );
        FPJob = findViewById ( R.id.FriendProfileJob );
        FPLocation = findViewById ( R.id.FriendProfileLoc );
        MyConnectionRef.addSnapshotListener ( new EventListener<DocumentSnapshot> ( ) {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if ( e == null ) {
                    Type = documentSnapshot.getString ( "type" );
                    setAction ( Type );
                }
            }
        } );


        setInfo ( );
    }

    private void setInfo() {
        if ( map.get ( "name" ) != null ) {
            FPName.setText ( (String) map.get ( "name" ) );
        }
        if ( map.get ( "status" ) != null ) {
            FPStatus.setText ( (String) map.get ( "status" ) );
        }
        if ( map.get ( "education" ) != null ) {
            FPEducation.setText ( (String) map.get ( "education" ) );
        }
        if ( map.get ( "job" ) != null ) {
            FPJob.setText ( (String) map.get ( "job" ) );
        }
        if ( map.get ( "location" ) != null ) {
            FPLocation.setText ( (String) map.get ( "location" ) );
        }
        PHOTO = (String) map.get ( "profilePic" );

        if ( PHOTO != null )
            Picasso.with ( getApplicationContext ( ) ).load ( PHOTO ).networkPolicy ( NetworkPolicy.OFFLINE )
                    .into ( FPPic, new Callback ( ) {
                        @Override
                        public void onSuccess() {
                            Picasso.with ( getApplicationContext ( ) ).load ( PHOTO )
                                    .into ( FPPic );
                        }

                        @Override
                        public void onError() {
                            Picasso.with ( getApplicationContext ( ) ).load ( PHOTO )
                                    .into ( FPPic );
                        }
                    } );


    }

    @Override
    protected void onStart() {
        super.onStart ( );


      /* UserInfoRef.addSnapshotListener ( new EventListener<DocumentSnapshot> ( ) {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e==null){
                    NAME=documentSnapshot.getString ( "name" );
                    PHOTO=documentSnapshot.getString ( "profilePic" );
                    FPName.setText ( documentSnapshot.getString ( "name" ) );
                    FriendPubKey=documentSnapshot.getString ( "publicKey" );
                    FPEducation.setText ( documentSnapshot.getString ( "education" ) );
                    FPJob.setText ( documentSnapshot.getString ( "job" ) );
                    FPStatus.setText ( documentSnapshot.getString ( "status" ) );
                    FPLocation.setText ( documentSnapshot.getString ( "location" ) );
                    Picasso.with ( getApplicationContext () ).load ( PHOTO ).networkPolicy ( NetworkPolicy.OFFLINE )
                            .into ( FPPic, new Callback ( ) {
                                @Override
                                public void onSuccess() {
                                    Picasso.with ( getApplicationContext () ).load ( PHOTO )
                                            .into ( FPPic);
                                }

                                @Override
                                public void onError() {
                                    Picasso.with ( getApplicationContext () ).load ( PHOTO )
                                            .into ( FPPic);
                                }
                            } );
                }
            }
        } );*/

    }

    @SuppressLint("SetTextI18n")
    private void setAction(String type) {
        if ( type == null ) {
            action.setText ( "send request" );
            sendMsg.setVisibility ( View.GONE );
        } else if ( type.equals ( "toUser" ) ) {
            action.setText ( "cancel request" );
            sendMsg.setVisibility ( View.GONE );
        } else if ( type.equals ( "friends" ) ) {
            action.setText ( "unfriend" );
            sendMsg.setVisibility ( View.VISIBLE );

        } else if ( type.equals ( "toMe" ) ) {
            action.setText ( "accept request" );
            sendMsg.setVisibility ( View.GONE );
        } else {
            Toast.makeText ( getApplicationContext ( ), "something went wrong", Toast.LENGTH_SHORT ).show ( );
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed ( );
        finish ( );
    }

    public void sendMsg(View view) {
        startActivity ( new Intent ( this, ChatActivity.class ).putExtra ( "map", (Serializable) map ) );
        finish ( );
    }
}
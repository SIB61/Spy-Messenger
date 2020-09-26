package com.sib4u.spymessenger;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendProfileActivity extends AppCompatActivity {
    private static final String TAG = "TAG";
    String ID, NAME, PHOTO;
    String FriendPubKey;
    String Type = null;
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
            ConnectionListModel model1 = new ConnectionListModel ( ID, null, Timestamp.now ( ) );
            ConnectionListModel model2 = new ConnectionListModel ( firebaseUser.getUid ( ), null, Timestamp.now ( ) );
            Map<String, Object> map1 = new HashMap<> ( );
            final Map<String, Object> map2 = new HashMap<> ( );
            if ( Type == null ) {
                model1.setType ( "toUser" );
                model2.setType ( "toMe" );
            } else if ( Type.equals ( "toMe" ) ) {
                model1.setType ( "friends" );
                model2.setType ( "friends" );
            } else if ( Type.equals ( "toUser" ) || Type.equals ( "friends" ) ) {
                model1.setType ( null );
                model2.setType ( null );
            }
            MyConnectionRef.set ( model1 ).addOnCompleteListener ( new OnCompleteListener<Void> ( ) {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if ( task.isSuccessful ( ) ) {
                        UserConnectionRef.set ( model2 );
                    }
                }
            } );
        }
    };
    Map<String, Object> MyMap = new HashMap<> ( );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_friend_profile );
        //noinspection unchecked
        ID = getIntent ( ).getStringExtra ( "ID" );
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
    }

    @Override
    protected void onStop() {
        super.onStop ( );
        setLastSeen ( );
    }

    private void addListener() {
        MyConnectionRef.addSnapshotListener ( this, new EventListener<DocumentSnapshot> ( ) {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if ( e == null ) {
                    ConnectionListModel model = null;
                    if ( documentSnapshot != null ) {
                        model = documentSnapshot.toObject ( ConnectionListModel.class );
                    }
                    if ( model != null ) {
                        Type = model.getType ( );
                        setAction ( Type );
                    }
                }
            }
        } );
    }

    private void setInfo() {
        FirebaseFirestore.getInstance ( ).document ( "UserInfo/" + ID )
                .addSnapshotListener ( this, new EventListener<DocumentSnapshot> ( ) {
                    @Override
                    public void onEvent(@androidx.annotation.Nullable DocumentSnapshot value, @androidx.annotation.Nullable FirebaseFirestoreException error) {
                        String profilePic = null;
                        if ( value != null ) {
                            map = value.getData ( );
                            Log.d ( "dog", "onEvent: " + map.toString ( ) );
                        }
                        if ( map != null ) {
                            FPName.setText ( (CharSequence) map.get ( "name" ) );
                            FPStatus.setText ( (CharSequence) map.get ( "status" ) );
                            FPEducation.setText ( (CharSequence) map.get ( "education" ) );
                            FPJob.setText ( (CharSequence) map.get ( "job" ) );
                            FPLocation.setText ( (CharSequence) map.get ( "location" ) );
                            profilePic = (String) map.get ( "profilePic" );
                        }
                        if ( profilePic != null ) {
                            String finalProfilePic1 = profilePic;
                            Picasso.get ( ).load ( finalProfilePic1 ).networkPolicy ( NetworkPolicy.OFFLINE )
                                    .placeholder ( R.drawable.ic_default_image )
                                    .into ( FPPic, new Callback ( ) {
                                        @Override
                                        public void onSuccess() {
                                            Picasso.get ( ).load ( finalProfilePic1 )
                                                    .placeholder ( R.drawable.ic_default_image )
                                                    .error ( R.drawable.ic_default_image )
                                                    .into ( FPPic );
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            Picasso.get ( ).load ( finalProfilePic1 )
                                                    .placeholder ( R.drawable.ic_default_image )
                                                    .error ( R.drawable.ic_default_image )
                                                    .into ( FPPic );
                                        }

                                    } );
                        }
                    }
                } );
    }

    @Override
    protected void onStart() {
        super.onStart ( );
        setOnline ( );
        setInfo ( );
        addListener ( );
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
        startActivity ( new Intent ( this, ChatActivity.class ).putExtra ( "ID", ID ) );
        finish ( );
    }

    private void setLastSeen() {
        @SuppressLint("SimpleDateFormat") String time = new SimpleDateFormat ( "d MMM yyyy, h:mm a" ).format ( Timestamp.now ( ).toDate ( ) );
        FirebaseFirestore.getInstance ( ).document ( "UserInfo/" + FirebaseAuth.getInstance ( ).getCurrentUser ( ).getUid ( ) )
                .update ( "lastSeen", "last seen " + time );
    }

    private void setOnline() {
        FirebaseFirestore.getInstance ( ).document ( "UserInfo/" + FirebaseAuth.getInstance ( ).getCurrentUser ( ).getUid ( ) )
                .update ( "lastSeen", "online" );
    }

    public void back(View view) {
        onBackPressed ( );
    }

}
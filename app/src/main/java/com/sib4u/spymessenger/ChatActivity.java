package com.sib4u.spymessenger;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    public int MY_MESSAGE = 1;
    public int FRIEND_MESSAGE = 2;
    private String FriendID;
    private String PHOTO, NAME;
    private String MyID;
    private CollectionReference MyMessages, FriendMessages;
    private String MyEncryptedMsg = null, FriendEncryptedMsg = null;
    private EditText editText;
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private TextView NameTextView;
    private CircleImageView imageView;
    private Map<String, Object> map = new HashMap<> ( );
    private String MyPubKey, FriendPubKey, MyPrivateKey;
    private List<Map<String, Object>> messageMaps;
    private SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_chat );

        //noinspection unchecked
        map = (Map<String, Object>) getIntent ( ).getSerializableExtra ( "map" );
        sharedPref = new SharedPref ( getApplicationContext ( ) );
        FriendID = (String) map.get ( "userId" );
        PHOTO = (String) map.get ( "profilePic" );
        NAME = (String) map.get ( "name" );
        MyID = FirebaseAuth.getInstance ( ).getCurrentUser ( ).getUid ( );
        MyMessages = FirebaseFirestore.getInstance ( ).collection ( "Chats/" + MyID + "/" + FriendID );
        FriendMessages = FirebaseFirestore.getInstance ( ).collection ( "Chats/" + FriendID + "/" + MyID );
        MyPubKey = sharedPref.getPublicKey ( );
        MyPrivateKey = sharedPref.getPrivateKey ( );
        FriendPubKey = (String) map.get ( "publicKey" );
        editText = findViewById ( R.id.CAEditText );
        imageView = findViewById ( R.id.CAProfilePic );
        linearLayoutManager = new LinearLayoutManager ( getApplicationContext ( ), RecyclerView.VERTICAL, false );
        linearLayoutManager.setStackFromEnd ( true );
        recyclerView = findViewById ( R.id.CARecycleView );
        recyclerView.setLayoutManager ( linearLayoutManager );
        messageMaps = new ArrayList<> ( );
        adapter = new MessageAdapter ( this, messageMaps );
        addListener ( );
        recyclerView.setAdapter ( adapter );
        NameTextView = findViewById ( R.id.CAName );
        NameTextView.setText ( NAME );
        if ( PHOTO != null )
            Picasso.with ( this ).load ( PHOTO ).networkPolicy ( NetworkPolicy.OFFLINE ).placeholder ( R.drawable.ic_baseline_account_circle_24 )
                    .into ( imageView, new Callback ( ) {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with ( getApplicationContext ( ) ).load ( PHOTO ).error ( R.drawable.ic_baseline_account_circle_24 ).placeholder ( R.drawable.ic_baseline_account_circle_24 )
                                    .into ( imageView );
                        }
                    } );
    }

    private void addListener() {
        MyMessages.orderBy ( "timestamp", Query.Direction.ASCENDING ).addSnapshotListener ( this, new EventListener<QuerySnapshot> ( ) {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for (DocumentChange DC : queryDocumentSnapshots.getDocumentChanges ( )) {
                    if ( DC.getType ( ) == DocumentChange.Type.ADDED ) {
                        Map<String, Object> messageMap = DC.getDocument ( ).getData ( );
                        messageMaps.add ( messageMap );
                        adapter.notifyDataSetChanged ( );
                        if ( !isVisible ( ) ) {
                            recyclerView.smoothScrollToPosition ( recyclerView.getAdapter ( ).getItemCount ( ) - 1 );
                        }
                    }
                }
            }
        } );

    }

    public void sendMessage(View view) {
        String message = editText.getText ( ).toString ( ).trim ( );
        editText.setText ( null );
        try {
            FriendEncryptedMsg = new RSAAlgo ( ).Encrypt ( message, FriendPubKey );
            MyEncryptedMsg = new RSAAlgo ( ).Encrypt ( message, MyPubKey );

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidKeySpecException e) {
            e.printStackTrace ( );
        }
        //   FieldValue timestamp=FieldValue.serverTimestamp ();
        Map<String, Object> map1 = new HashMap<> ( );
        map1.put ( "message", MyEncryptedMsg );
        map1.put ( "timestamp", Timestamp.now ( ) );
        map1.put ( "type", MY_MESSAGE );

        Map<String, Object> map2 = new HashMap<> ( );
        map2.put ( "message", FriendEncryptedMsg );
        map2.put ( "timestamp", Timestamp.now ( ) );
        map2.put ( "type", FRIEND_MESSAGE );

        MyMessages.document ( ).set ( map1 ).addOnCompleteListener ( new OnCompleteListener<Void> ( ) {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if ( task.isSuccessful ( ) ) {
                    FriendMessages.document ( ).set ( map2 ).addOnCompleteListener (
                            new OnCompleteListener<Void> ( ) {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if ( !task.isSuccessful ( ) ) {
                                        Toast.makeText ( getApplicationContext ( ), "something went wrong", Toast.LENGTH_SHORT ).show ( );
                                    }
                                }
                            }
                    );
                } else {
                    Toast.makeText ( getApplicationContext ( ), "something went wrong", Toast.LENGTH_SHORT ).show ( );
                }
            }
        } );


    }

    private boolean isVisible() {
        return linearLayoutManager.findLastCompletelyVisibleItemPosition ( ) > recyclerView.getAdapter ( ).getItemCount ( );
    }

    public void addFile(View view) {

    }
}
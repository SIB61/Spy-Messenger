package com.sib4u.spymessenger;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    public int MY_MESSAGE = 1;
    public int FRIEND_MESSAGE = 2;
    private String FriendID;
    private String MyID;
    private CollectionReference MyMessages, FriendMessages;
    private DocumentReference MyChatInfo, FriendChatInfo;
    private String MyEncryptedMsg = null, FriendEncryptedMsg = null;
    private EditText editText;
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private TextView NameTextView;
    private CircleImageView imageView;
    private List<MessageModel> messageModels;
    private String MyPubKey, FriendPubKey, MyPrivateKey;
    private SharedPref sharedPref;
    private UserModel userModel;
    private TextView LastSeenTextView;
    private FirestoreRecyclerAdapter<MessageModel, RecyclerView.ViewHolder> adapter1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_chat );

        //noinspection unchecked
        sharedPref = new SharedPref ( getApplicationContext ( ) );
        FriendID = getIntent ( ).getStringExtra ( "ID" );

        MyID = FirebaseAuth.getInstance ( ).getCurrentUser ( ).getUid ( );
        MyMessages = FirebaseFirestore.getInstance ( ).collection ( "Chats/" + MyID + "/" + FriendID );
        MyChatInfo = FirebaseFirestore.getInstance ( ).document ( "Chats/Info/" + MyID + "/" + FriendID );
        FriendMessages = FirebaseFirestore.getInstance ( ).collection ( "Chats/" + FriendID + "/" + MyID );
        FriendChatInfo = FirebaseFirestore.getInstance ( ).document ( "Chats/Info/" + FriendID + "/" + MyID );
        MyPubKey = sharedPref.getPublicKey ( );
        MyPrivateKey = sharedPref.getPrivateKey ( );
        editText = findViewById ( R.id.CAEditText );
        imageView = findViewById ( R.id.CAProfilePic );
        linearLayoutManager = new LinearLayoutManager ( getApplicationContext ( ), RecyclerView.VERTICAL, false );
        linearLayoutManager.setStackFromEnd ( true );
        recyclerView = findViewById ( R.id.CARecycleView );
        recyclerView.setLayoutManager ( linearLayoutManager );
        messageModels = new ArrayList<> ( );
        adapter = new MessageAdapter ( this, messageModels );

        NameTextView = findViewById ( R.id.CAName );
        LastSeenTextView = findViewById ( R.id.CALastSeen );
        setAdapter ( );
        recyclerView.setAdapter ( adapter1 );
        setInfo ( );

    }


    private void setInfo() {
        FirebaseFirestore.getInstance ( ).document ( "UserInfo/" + FriendID ).addSnapshotListener ( this,
                new EventListener<DocumentSnapshot> ( ) {
                    @Override
                    public void onEvent(@androidx.annotation.Nullable DocumentSnapshot value, @androidx.annotation.Nullable FirebaseFirestoreException error) {
                        if ( value != null ) {
                            userModel = value.toObject ( UserModel.class );
                            if ( userModel != null ) {
                                NameTextView.setText ( userModel.getName ( ) );
                                LastSeenTextView.setText ( userModel.getLastSeen ( ) );
                                FriendPubKey = userModel.getPublicKey ( );
                                if ( userModel.getProfilePic ( ) != null ) {
                                    Picasso.get ( ).load ( userModel.getProfilePic ( ) ).networkPolicy ( NetworkPolicy.OFFLINE )
                                            .placeholder ( R.drawable.ic_baseline_account_circle_24 )
                                            .into ( imageView, new Callback ( ) {
                                                @Override
                                                public void onSuccess() {

                                                }

                                                @Override
                                                public void onError(Exception e) {
                                                    Picasso.get ( ).load ( userModel.getProfilePic ( ) ).error ( R.drawable.ic_baseline_account_circle_24 ).placeholder ( R.drawable.ic_baseline_account_circle_24 )
                                                            .into ( imageView );
                                                }
                                            } );
                                }
                            }
                        }
                    }
                } );
    }

    @Override
    protected void onStart() {
        super.onStart ( );
        setOnline ( );
        adapter1.startListening ( );
    }

    @Override
    protected void onStop() {
        super.onStop ( );
        setLastSeen ( );
        adapter1.stopListening ( );
    }

    public void sendMessage(View view) {
        String message = editText.getText ( ).toString ( ).trim ( );
        if ( !message.isEmpty ( ) ) {
            editText.setText ( null );
            try {
                FriendEncryptedMsg = new RSAAlgo ( ).Encrypt ( message, FriendPubKey );
                MyEncryptedMsg = new RSAAlgo ( ).Encrypt ( message, MyPubKey );

            } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidKeySpecException e) {
                e.printStackTrace ( );
            }
            //   FieldValue timestamp=FieldValue.serverTimestamp ();
            MessageModel messageModel1 = new MessageModel ( MyID, MyEncryptedMsg, Timestamp.now ( ) );
            MessageModel messageModel2 = new MessageModel ( MyID, FriendEncryptedMsg, Timestamp.now ( ) );
            ChatListModel chatListModel1 = new ChatListModel ( MyID, FriendID, MyEncryptedMsg, Timestamp.now ( ) );
            ChatListModel chatListModel2 = new ChatListModel ( MyID, MyID, FriendEncryptedMsg, Timestamp.now ( ) );
            Task<Void> task1 = MyMessages.document ( ).set ( messageModel1 );
            Task<Void> task2 = FriendMessages.document ( ).set ( messageModel2 );
            Task<Void> task3 = MyChatInfo.set ( chatListModel1 );
            Task<Void> task4 = FriendChatInfo.set ( chatListModel2 );
            Tasks.whenAll ( task1, task2, task3, task4 )
                    .addOnCompleteListener ( new OnCompleteListener<Void> ( ) {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            Toast.makeText ( getApplicationContext ( ), "success", Toast.LENGTH_SHORT ).show ( );
                        }
                    } );
        }
    }

    private boolean isVisible() {
        return linearLayoutManager.findLastCompletelyVisibleItemPosition ( ) > recyclerView.getAdapter ( ).getItemCount ( );
    }

    public void addFile(View view) {

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


    private void setAdapter() {
        SharedPref sharedPref = new SharedPref ( getApplicationContext ( ) );
        Query query = MyMessages.orderBy ( "timestamp" );
        FirestoreRecyclerOptions<MessageModel> options = new FirestoreRecyclerOptions.Builder<MessageModel> ( )
                .setQuery ( query, MessageModel.class ).build ( );
        adapter1 = new FirestoreRecyclerAdapter<MessageModel, RecyclerView.ViewHolder> ( options ) {

            @Override
            public void onDataChanged() {
                super.onDataChanged ( );
                if ( !isVisible ( ) ) {
                    recyclerView.smoothScrollToPosition ( recyclerView.getAdapter ( ).getItemCount ( ) - 1 );
                }
            }

            @Override
            protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull MessageModel model) {
                String message = "decryption failed";
                @SuppressLint("SimpleDateFormat") String time = new SimpleDateFormat ( "h:mm a" ).format ( model.getTimestamp ( ).toDate ( ) );
                try {
                    message = new RSAAlgo ( ).Decrypt ( model.getMessage ( ), sharedPref.getPrivateKey ( ) );
                } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidKeySpecException e) {
                    e.printStackTrace ( );
                }


                if ( getItemViewType ( position ) == MY_MESSAGE ) {
                    MyMessageViewHolder holder1 = (MyMessageViewHolder) holder;
                    holder1.message.setText ( message );
                    holder1.time.setText ( time );
                } else {
                    FriendMessageViewHolder holder1 = (FriendMessageViewHolder) holder;
                    holder1.message.setText ( message );
                    holder1.time.setText ( time );
                }
            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                if ( viewType == MY_MESSAGE ) {
                    return new MyMessageViewHolder ( LayoutInflater.from ( getApplicationContext ( ) ).inflate ( R.layout.my_text, parent, false ) );
                } else if ( viewType == FRIEND_MESSAGE ) {
                    return new FriendMessageViewHolder ( LayoutInflater.from ( getApplicationContext ( ) ).inflate ( R.layout.friend_text, parent, false ) );
                }
                return null;
            }

            @Override
            public int getItemViewType(int position) {
                return getItem ( position ).getFrom ( ).equals ( MyID ) ? MY_MESSAGE : FRIEND_MESSAGE;
            }
        };

    }

    class MyMessageViewHolder extends RecyclerView.ViewHolder {
        TextView message, time;

        public MyMessageViewHolder(@NonNull View itemView) {
            super ( itemView );
            message = itemView.findViewById ( R.id.MyMessage );
            time = itemView.findViewById ( R.id.MyTime );
        }
    }

    class FriendMessageViewHolder extends RecyclerView.ViewHolder {
        TextView message, time;

        public FriendMessageViewHolder(@NonNull View itemView) {
            super ( itemView );
            message = itemView.findViewById ( R.id.FriendMessage );
            time = itemView.findViewById ( R.id.FriendTime );
        }
    }


}
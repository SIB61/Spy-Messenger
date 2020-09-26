package com.sib4u.spymessenger;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatFragment extends Fragment {
    public ChatFragment() {
        // Required empty public constructor
    }

    private RecyclerView recyclerView;
    //Setting Up Adapter
    // setting up recycler view adapter;
    private Query query = FirebaseFirestore.getInstance ( )
            .collection ( "Chats/Info/" + FirebaseAuth.getInstance ( ).getCurrentUser ( ).getUid ( ) )
            .orderBy ( "timestamp" );
    private FirestoreRecyclerOptions<ChatListModel> options = new FirestoreRecyclerOptions.Builder<ChatListModel> ( )
            .setQuery ( query, ChatListModel.class ).build ( );
    private FirestoreRecyclerAdapter<ChatListModel, MessageHolder> adapter = new FirestoreRecyclerAdapter<ChatListModel, MessageHolder> ( options ) {
        @Override
        protected void onBindViewHolder(@NonNull MessageHolder holder, int position, @NonNull ChatListModel model) {
            FirebaseFirestore.getInstance ( ).document ( "UserInfo/" + model.getUserId ( ) )
                    .get ( ).addOnCompleteListener ( new OnCompleteListener<DocumentSnapshot> ( ) {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if ( task.isSuccessful ( ) ) {
                        Map<String, Object> map = task.getResult ( ).getData ( );
                        if ( map != null ) {
                            holder.textView.setText ( (CharSequence) map.get ( "name" ) );
                            try {
                                holder.lastMsg.setText ( new RSAAlgo ( ).Decrypt ( model.getLastMessage ( ),
                                        new SharedPref ( getContext ( ) ).getPrivateKey ( ) ) );
                            } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidKeySpecException e) {
                                e.printStackTrace ( );
                            }
                            Picasso.get ( ).load ( (String) map.get ( "profilePic" ) ).networkPolicy ( NetworkPolicy.OFFLINE ).
                                    placeholder ( R.drawable.ic_default_image ).into ( holder.imageView,
                                    new Callback ( ) {
                                        @Override
                                        public void onSuccess() {
                                            // Picasso.with ( ctx ).load ( pp ).error ( R.drawable.ic_baseline_account_circle_24 ).into ( holder.imageView );
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            Picasso.get ( ).load ( (String) map.get ( "profilePic" ) ).
                                                    placeholder ( R.drawable.ic_default_image )
                                                    .error ( R.drawable.ic_default_image ).into ( holder.imageView );

                                        }

                                    } );
                        }
                    }
                }
            } );

        }

        @NonNull
        @Override
        public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from ( getContext ( ) ).inflate ( R.layout.chat_list, parent, false );
            return new MessageHolder ( v );
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate ( R.layout.fragment_chat, container, false );
        recyclerView = v.findViewById ( R.id.CFRecyclerView );
        recyclerView.setAdapter ( adapter );

        return v;
    }

    @Override
    public void onStart() {
        super.onStart ( );
        adapter.startListening ( );
    }

    @Override
    public void onStop() {
        super.onStop ( );
        adapter.stopListening ( );
    }

    private class MessageHolder extends RecyclerView.ViewHolder {
        CircleImageView imageView;
        TextView textView;
        TextView lastMsg;

        public MessageHolder(@NonNull View itemView) {
            super ( itemView );
            imageView = itemView.findViewById ( R.id.CFImage );
            textView = itemView.findViewById ( R.id.CFName );
            lastMsg = itemView.findViewById ( R.id.CFLastMessage );
            Log.d ( "jam", "ConnectionHolder: " );
            itemView.setOnClickListener ( new View.OnClickListener ( ) {
                @Override
                public void onClick(View view) {
                    startActivity ( new Intent ( getActivity ( ), ChatActivity.class )
                            .putExtra ( "ID", adapter.getItem ( getAdapterPosition ( ) ).getUserId ( ) ) );
                }
            } );
        }
    }


}
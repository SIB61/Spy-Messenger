package com.sib4u.spymessenger;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

@RequiresApi(api = Build.VERSION_CODES.O)
public class FriendsFragment extends Fragment {
    private static final String TAG = "OMG";
    RecyclerView recyclerView;

    public FriendsFragment() {
        // Required empty public constructor
    }

    // setting up recycler view adapter;
    private Query query = FirebaseFirestore.getInstance ( )
            .collection ( "Connections/" + FirebaseAuth.getInstance ( ).getCurrentUser ( ).getUid ( ) + "/MyConnections" )
            .whereEqualTo ( "type", "friends" )
            .orderBy ( "timestamp" );
    private FirestoreRecyclerOptions<ConnectionListModel> options = new FirestoreRecyclerOptions.Builder<ConnectionListModel> ( )
            .setQuery ( query, ConnectionListModel.class ).build ( );
    private FirestoreRecyclerAdapter<ConnectionListModel, ConnectionHolder> adapter = new FirestoreRecyclerAdapter<ConnectionListModel, ConnectionHolder> ( options ) {
        @Override
        protected void onBindViewHolder(@NonNull ConnectionHolder holder, int position, @NonNull ConnectionListModel model) {
            Log.d ( "jam", "onBindViewHolder: " );
            FirebaseFirestore.getInstance ( ).document ( "UserInfo/" + model.getID ( ) )
                    .get ( ).addOnCompleteListener ( new OnCompleteListener<DocumentSnapshot> ( ) {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if ( task.isSuccessful ( ) ) {
                        Map<String, Object> map = task.getResult ( ).getData ( );
                        if ( map != null ) {
                            holder.textView.setText ( (CharSequence) map.get ( "name" ) );
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

            String profilePic;
        }

        @NonNull
        @Override
        public ConnectionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from ( getContext ( ) ).inflate ( R.layout.ff_list_item, parent, false );
            return new ConnectionHolder ( v );
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate ( R.layout.fragment_friends, container, false );
        recyclerView = v.findViewById ( R.id.FFRecyclerView );
        recyclerView.setAdapter ( adapter );
        Log.d ( "jam", "onCreateView: " );
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

    private class ConnectionHolder extends RecyclerView.ViewHolder {
        CircleImageView imageView;
        TextView textView;

        public ConnectionHolder(@NonNull View itemView) {
            super ( itemView );
            imageView = itemView.findViewById ( R.id.FFImage );
            textView = itemView.findViewById ( R.id.FFName );
            Log.d ( "jam", "ConnectionHolder: " );
            itemView.setOnClickListener ( new View.OnClickListener ( ) {
                @Override
                public void onClick(View view) {
                    startActivity ( new Intent ( getActivity ( ), FriendProfileActivity.class )
                            .putExtra ( "ID", adapter.getItem ( getAdapterPosition ( ) ).getID ( ) ) );
                }
            } );
        }
    }
}
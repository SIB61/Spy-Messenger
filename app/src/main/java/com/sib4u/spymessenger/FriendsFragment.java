package com.sib4u.spymessenger;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FriendsFragment extends Fragment {

    List<UserModel> userModels;
    CollectionReference collectionReference;
    FFAdapter adapter;
    RecyclerView recyclerView;
    List<String> list;
    List<Map<String, Object>> mapList = new ArrayList<> ( );
    FFAdapter.OnClick onClick = new FFAdapter.OnClick ( ) {
        @Override
        public void listener(int position, View view) {
            startActivity ( new Intent ( getActivity ( ), FriendProfileActivity.class ).putExtra ( "map", (Serializable) mapList.get ( position ) ) );
        }
    };

    public FriendsFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    private ViewModel viewModel;

    public static FriendsFragment newInstance(List<Map<String, Object>> list) {
        Log.d ( "jhakanaka", "newInstance: " + list.toString ( ) );
        FriendsFragment fragment = new FriendsFragment ( );
        Bundle args = new Bundle ( );
        args.putSerializable ( "maps", (Serializable) list );
        fragment.setArguments ( args );
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        // viewModel= ViewModelProviders.of ( this ).get ( ViewModel.class );

        if ( getArguments ( ) != null ) {
            if ( getArguments ( ).containsKey ( "maps" ) ) {
                mapList = (List<Map<String, Object>>) getArguments ( ).getSerializable ( "maps" );
            }
            if ( mapList != null ) {
                Log.d ( "jhakanaka", "onCreateView: " + mapList.toString ( ) );
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate ( R.layout.fragment_friends, container, false );


        userModels = new ArrayList<> ( );
        list = new ArrayList<> ( );
        collectionReference = FirebaseFirestore.getInstance ( ).collection ( "Connections/" +
                FirebaseAuth.getInstance ( ).getCurrentUser ( ).getUid ( ) + "/MyConnections" );
        adapter = new FFAdapter ( getContext ( ), mapList );
        recyclerView = v.findViewById ( R.id.FFRecyclerView );
        recyclerView.setAdapter ( adapter );
        adapter.setOnItemClickListener ( onClick );
        Log.d ( "OnChanged", "onChanged: " + "hi there" );
        //  mapList=viewModel.getFriendsMaps ().getValue ();
      /*  viewModel.getFriendsMaps ().observe ( getViewLifecycleOwner ( ), new Observer<List<Map<String, Object>>> ( ) {
            @Override
            public void onChanged(List<Map<String, Object>> maps) {
                mapList=maps;
                adapter.notifyDataSetChanged ();
                Log.d ( "OnChanged", "onChanged: "+maps.toString () );
            }
        } );*/
        return v;
    }

    @Override
    public void onResume() {
        super.onResume ( );


    }
}
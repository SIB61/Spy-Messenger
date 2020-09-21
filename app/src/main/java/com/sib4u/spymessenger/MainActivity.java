package com.sib4u.spymessenger;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MainActivity extends AppCompatActivity {
    ViewPager viewPager;
    TabLayout tabLayout;
    SectionPagerAdapter adapter;
    Toolbar toolbar;
    FloatingActionButton edit;
    public String privateKeyString;
    public String publicKeyString;
    public String MY_PREFS_NAME = "My_prefs";
    UserModel userModel;
    String MyID = FirebaseAuth.getInstance ( ).getCurrentUser ( ).getUid ( );
    DocumentReference documentReference = FirebaseFirestore.getInstance ( ).document ( "UserInfo/" + MyID );
    KeyPairGenerator kpg;
    KeyPair kp;
    PublicKey publicKey;
    PrivateKey privateKey;
    SharedPref sharedPref;
    CollectionReference collectionReference;
    private List<Map<String, Object>> list = new ArrayList<> ( );
    private List<Map<String, Object>> list2 = new ArrayList<> ( );
    //  ViewModel viewModel;

    Toolbar.OnMenuItemClickListener menuItemClickListener = new Toolbar.OnMenuItemClickListener ( ) {
        @Override
        public boolean onMenuItemClick(final MenuItem menuItem) {
            switch (menuItem.getItemId ( )) {
                case R.id.MyProfile:
                    startActivity ( new Intent ( MainActivity.this, MyProfileActivity.class ) );
                    break;
                case R.id.GeneralSettings:
                    startActivity ( new Intent ( MainActivity.this, GeneralSettings.class ) );
                    break;
                case R.id.app_bar_search:
                    final SearchView searchView = (SearchView) MenuItemCompat.getActionView ( menuItem );
                    searchView.setOnQueryTextListener ( new SearchView.OnQueryTextListener ( ) {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            SearchUser ( query );
                            searchView.clearFocus ( );
                            menuItem.collapseActionView ( );
                            return true;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            return false;
                        }
                    } );
            }


            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_main );
        sharedPref = new SharedPref ( getApplicationContext ( ) );
        //viewModel= ViewModelProviders.of ( this ).get ( ViewModel.class );
        toolbar = findViewById ( R.id.toolbar );
        toolbar.setOnMenuItemClickListener ( menuItemClickListener );
        viewPager = findViewById ( R.id.viewPager );
      /*  viewModel.getFriendsMaps ().observe ( this, new Observer<List<Map<String, Object>>> ( ) {
            @Override
            public void onChanged(List<Map<String, Object>> maps) {
                list=maps;
            }
        } );*/
        tabLayout = findViewById ( R.id.tab );
        adapter = new SectionPagerAdapter ( getSupportFragmentManager ( ), list, list2 );
        viewPager.setAdapter ( adapter );
        collectionReference = FirebaseFirestore.getInstance ( ).collection ( "Connections/" +
                FirebaseAuth.getInstance ( ).getCurrentUser ( ).getUid ( ) + "/MyConnections" );
        collectionReference.whereEqualTo ( "type", "friends" ).addSnapshotListener ( this, new EventListener<QuerySnapshot> ( ) {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if ( queryDocumentSnapshots != null )
                    for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges ( )) {
                        if ( dc.getType ( ) == DocumentChange.Type.ADDED ) {
                            String INFO_ID = dc.getDocument ( ).getId ( );
                            DocumentReference DC = FirebaseFirestore.getInstance ( ).document ( "UserInfo/" + INFO_ID );
                            DC.get ( ).addOnCompleteListener ( new OnCompleteListener<DocumentSnapshot> ( ) {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if ( task.isSuccessful ( ) ) {
                                        Map<String, Object> map = task.getResult ( ).getData ( );
                                        if ( map != null ) {
                                            list.add ( map );
                                            // viewModel.setFriendsMaps ( map );
                                            Log.d ( "find", "onComplete: " + map.toString ( ) );
                                            adapter.notifyDataSetChanged ( );
                                            // adapter = new SectionPagerAdapter ( getSupportFragmentManager ( ),list );
                                            //  viewPager.setAdapter ( adapter );
                                        }
                                    }
                                }
                            } );
                        }
                    }
            }
        } );
        collectionReference.whereEqualTo ( "type", "toMe" ).addSnapshotListener ( this, new EventListener<QuerySnapshot> ( ) {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if ( queryDocumentSnapshots != null )
                    for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges ( )) {
                        if ( dc.getType ( ) == DocumentChange.Type.ADDED ) {
                            String INFO_ID = dc.getDocument ( ).getId ( );
                            DocumentReference DC = FirebaseFirestore.getInstance ( ).document ( "UserInfo/" + INFO_ID );
                            DC.get ( ).addOnCompleteListener ( new OnCompleteListener<DocumentSnapshot> ( ) {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if ( task.isSuccessful ( ) ) {
                                        Map<String, Object> map = task.getResult ( ).getData ( );
                                        if ( map != null ) {
                                            list2.add ( map );
                                            // viewModel.setFriendsMaps ( map );
                                            Log.d ( "find", "onComplete: " + map.toString ( ) );
                                            adapter.notifyDataSetChanged ( );
                                            // adapter = new SectionPagerAdapter ( getSupportFragmentManager ( ),list );
                                            //  viewPager.setAdapter ( adapter );
                                        }
                                    }
                                }
                            } );
                        }
                    }
            }
        } );

        tabLayout.setupWithViewPager ( viewPager );
        try {
            genKeys ( );
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace ( );
        }
    }

    private void SearchUser(String query) {
        final ProgressDialog progressDialog = new ProgressDialog ( this );
        progressDialog.setTitle ( "Searching..." );
        progressDialog.create ( );
        progressDialog.setCanceledOnTouchOutside ( false );
        progressDialog.show ( );

        CollectionReference collectionReference = FirebaseFirestore.getInstance ( ).collection ( "UserInfo" );
        collectionReference.whereEqualTo ( "name", query )
                .whereEqualTo ( "visibility", true )
                .get ( )
                .addOnSuccessListener ( new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Map<String, Object> map = new HashMap<> ( );
                        for (QueryDocumentSnapshot ds : queryDocumentSnapshots) {
                            map = ds.getData ( );
                            //  Toast.makeText ( getApplicationContext ( ), ID, Toast.LENGTH_SHORT ).show ( );
                        }
                        if ( map.containsKey ( "userId" ) ) {
                            Intent intent;
                            if ( Objects.requireNonNull ( map.get ( "userId" ) ).equals ( Objects.requireNonNull ( FirebaseAuth.getInstance ( ).getCurrentUser ( ) ).getUid ( ) ) ) {
                                intent = new Intent ( getApplicationContext ( ), MyProfileActivity.class );
                            } else {
                                intent = new Intent ( getApplicationContext ( ), FriendProfileActivity.class );
                                intent.putExtra ( "map", (Serializable) map );
                            }
                            startActivity ( intent );
                            progressDialog.dismiss ( );
                        } else {
                            Toast.makeText ( getApplicationContext ( ), "account doesn't exists", Toast.LENGTH_SHORT ).show ( );
                            progressDialog.dismiss ( );
                        }

                    }
                } ).addOnFailureListener ( new OnFailureListener ( ) {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Toast.makeText ( getApplicationContext (),"account doesn't exists",Toast.LENGTH_SHORT).show ();
                progressDialog.dismiss ( );
            }
        } );
    }

    public void genKeys() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        privateKeyString = sharedPref.getPrivateKey ( );
        publicKeyString = sharedPref.getPublicKey ( );

        if ( privateKeyString == null && publicKeyString == null ) {
            kpg = KeyPairGenerator.getInstance ( "RSA" );
            kpg.initialize ( 1024 );
            kp = kpg.genKeyPair ( );
            publicKey = kp.getPublic ( );
            privateKey = kp.getPrivate ( );

            if ( privateKey != null ) {
                privateKeyString = Base64.encodeToString ( privateKey.getEncoded ( ), Base64.DEFAULT );
            }
            if ( publicKey != null ) {
                publicKeyString = Base64.encodeToString ( publicKey.getEncoded ( ), Base64.DEFAULT );
            }

            documentReference.update ( "publicKey", publicKeyString ).addOnCompleteListener (
                    new OnCompleteListener<Void> ( ) {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if ( task.isSuccessful ( ) ) {
                                sharedPref.setPublicKey ( publicKeyString );
                                sharedPref.setPrivateKey ( privateKeyString );
                                Log.d ( "TAG", "private: " + privateKeyString + " \n public: " + publicKeyString );
                            }
                        }
                    }
            );
        }
//        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
//        String priKey = sharedPref.getString("private_key",null);
    }

    @Override
    protected void onStart() {
        super.onStart ( );
    }
}
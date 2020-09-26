package com.sib4u.spymessenger;

import android.annotation.SuppressLint;
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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MainActivity extends AppCompatActivity {


    private final String MY_PREFS_NAME = "My_prefs";
    private final String MyID = FirebaseAuth.getInstance ( ).getCurrentUser ( ).getUid ( );
    private final DocumentReference documentReference = FirebaseFirestore.getInstance ( ).document ( "UserInfo/" + MyID );
    ViewPager viewPager;
    TabLayout tabLayout;
    SectionPagerAdapter adapter;
    Toolbar toolbar;
    FloatingActionButton edit;
    UserModel userModel;
    KeyPairGenerator kpg;
    KeyPair kp;
    PublicKey publicKey;
    PrivateKey privateKey;
    SharedPref sharedPref;
    CollectionReference collectionReference = FirebaseFirestore.getInstance ( ).collection ( "Connections/" +
            FirebaseAuth.getInstance ( ).getCurrentUser ( ).getUid ( ) + "/MyConnections" );

    ListenerRegistration listenerRegistration;
    FFAdapter adapter2;
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
    private String privateKeyString;
    private String publicKeyString;
    private List<Map<String, Object>> list;
    //  ViewModel viewModel;
    private List<Map<String, Object>> list2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_main );
        list = new ArrayList<> ( );
        list2 = new ArrayList<> ( );
        sharedPref = new SharedPref ( getApplicationContext ( ) );
        //viewModel= ViewModelProviders.of ( this ).get ( ViewModel.class );
        toolbar = findViewById ( R.id.toolbar );
        toolbar.setOnMenuItemClickListener ( menuItemClickListener );
        viewPager = findViewById ( R.id.viewPager );
        adapter = new SectionPagerAdapter ( getSupportFragmentManager ( ) );
        viewPager.setAdapter ( adapter );
        viewPager.setCurrentItem ( 1, true );
        tabLayout = findViewById ( R.id.tab );
        tabLayout.setupWithViewPager ( viewPager );
        try {
            genKeys ( );
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace ( );
        }
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


    private void addListener() {
        listenerRegistration = collectionReference.addSnapshotListener ( new EventListener<QuerySnapshot> ( ) {
            @Override
            public void onEvent(@androidx.annotation.Nullable QuerySnapshot value, @androidx.annotation.Nullable FirebaseFirestoreException error) {
                if ( value != null ) {
                    for (DocumentChange DC : value.getDocumentChanges ( )) {
                        Map<String, Object> DCMap = DC.getDocument ( ).getData ( );
                        DocumentReference DR = FirebaseFirestore.getInstance ( ).document ( "UserInfo/" + DC.getDocument ( ).getId ( ) );
                        DR.get ( ).addOnCompleteListener ( new OnCompleteListener<DocumentSnapshot> ( ) {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if ( task.isSuccessful ( ) ) {
                                    // Log.d ( "fuck", "onComplete: " + task.getResult ( ).getData ( ).toString ( ) );
                                    if ( task.getResult ( ) != null )
                                        if ( task.getResult ( ).getData ( ) != null ) {
                                            Map<String, Object> DRMap = Objects.requireNonNull ( task.getResult ( ) ).getData ( );
                                            if ( DRMap != null ) {
                                                DRMap.put ( "type", DCMap.get ( "type" ) );
                                                if ( DC.getType ( ) == DocumentChange.Type.ADDED ) {
                                                    list.add ( DRMap );
                                                } else if ( DC.getType ( ) == DocumentChange.Type.MODIFIED ) {
                                                    if ( DC.getOldIndex ( ) == DC.getNewIndex ( ) ) {
                                                        list.set ( DC.getOldIndex ( ), DRMap );
                                                    } else {
                                                        list.remove ( DC.getOldIndex ( ) );
                                                        list.add ( DRMap );
                                                    }
                                                } else if ( DC.getType ( ) == DocumentChange.Type.REMOVED ) {
                                                    list.remove ( DC.getOldIndex ( ) );
                                                }

                                            }

                                        }
                                }
                            }
                        } );
                    }
                }
            }
        } );
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
                            if ( !map.get ( "userId" ).equals ( FirebaseAuth.getInstance ( ).getCurrentUser ( ).getUid ( ) ) )
                                startActivity ( new Intent ( getApplicationContext ( ), FriendProfileActivity.class ).putExtra ( "ID", (String) map.get ( "userId" ) ) );
                            else
                                startActivity ( new Intent ( getApplicationContext ( ), MyProfileActivity.class ) );
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
    protected void onResume() {
        super.onResume ( );
    }

    @Override
    protected void onStart() {
        super.onStart ( );
        setOnline ( );
        Log.d ( "fuck", "onStart: " + list.toString ( ) );
    }

    @Override
    protected void onPause() {
        super.onPause ( );
    }

    @Override
    protected void onStop() {
        super.onStop ( );
        setLastSeen ( );
    }


    @Override
    protected void onDestroy() {

        super.onDestroy ( );
        //  listenerRegistration.remove ( );
    }


}
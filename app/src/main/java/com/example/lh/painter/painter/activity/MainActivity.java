package com.example.lh.painter.painter.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lh.painter.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import com.example.lh.painter.painter.helper.DataBaseMessage;


public class MainActivity extends AppCompatActivity {
    public static final String NUMBER_OF_FILE_TO_RETRIEVE_ID = "219";
    public static final String FILENAME_ID = "828";
    private Button mOpenFileButton;
    private Button mOpenCameraButton;
    private EditText mEditText;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;

    private DatabaseReference mDatabase;
    private BroadcastReceiver networkStateReceiver;
    private static final int REQUEST_IMAGE_CAPTURE = 798;
    private static final int SEARCH_FILE_INTENT = 624;
    private static final int RC_SIGN_IN = 992;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReferenceFromUrl("gs://firstproject-b3334.appspot.com");
        mStorageRef = mStorageRef.child("images");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                } else {
                    // User is signed out
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(
                                            AuthUI.EMAIL_PROVIDER,
                                            AuthUI.GOOGLE_PROVIDER)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
        mOpenCameraButton = (Button) findViewById(R.id.camera);
        mOpenFileButton = (Button) findViewById(R.id.file);
        mEditText = (EditText) findViewById(R.id.editText);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Look for an image", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.wikiart.org/"));
                startActivity(browserIntent);
            }
        });

        mOpenCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }

            }
        });
        mOpenFileButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action with"), SEARCH_FILE_INTENT);
            }
        });


        networkStateReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(final Context context, final Intent intent) {
                if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                    NetworkInfo networkInfo = intent
                            .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                    if (networkInfo.isConnected()) {
                        // Wifi is connected
                        Snackbar.make(findViewById(R.id.main_view), "Wifi is connected", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        mOpenFileButton.setEnabled(true);
                        mOpenCameraButton.setEnabled(true);
                    }
                } else if (intent.getAction().equals(
                        ConnectivityManager.CONNECTIVITY_ACTION)) {
                    NetworkInfo networkInfo = intent
                            .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                    if (networkInfo.getDetailedState() == NetworkInfo.DetailedState.DISCONNECTED) {
                        // Wifi is disconnected
                        Snackbar.make(findViewById(R.id.main_view), "Wifi is disconnected", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        mOpenCameraButton.setEnabled(false);
                        mOpenFileButton.setEnabled(false);
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkStateReceiver, filter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(networkStateReceiver);

    }

    class MyOnSuccessListener implements OnSuccessListener<UploadTask.TaskSnapshot> {
        private String filename;

        public MyOnSuccessListener(String filename) {
            this.filename = filename;
        }

        @Override
        public void onSuccess(@NonNull UploadTask.TaskSnapshot v) {
            Integer nbFileToRetrieve = Integer.parseInt(mEditText.getText().toString());
            DataBaseMessage dataBaseMessage = new DataBaseMessage(filename, nbFileToRetrieve);
            mDatabase.child("messages").setValue(dataBaseMessage);
            Toast.makeText(getBaseContext(), "File send", Toast.LENGTH_LONG);
            Intent intent = new Intent(getBaseContext(), Main2Activity.class);
            intent.putExtra(NUMBER_OF_FILE_TO_RETRIEVE_ID, nbFileToRetrieve);
            intent.putExtra(FILENAME_ID, filename);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri imageURI = null;
        /*
            Search for a new file and send the file to the server
         */
        if (requestCode == SEARCH_FILE_INTENT && resultCode == RESULT_OK) {
            imageURI = data.getData();
            UploadTask uploadTask = mStorageRef.child(imageURI.getLastPathSegment()).putFile(imageURI);
            uploadTask.addOnSuccessListener(new MyOnSuccessListener(imageURI.getLastPathSegment()));

        }
        /*
            Take a picture and sent it to the server
         */

        else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageURI = getImageUri(getApplicationContext(), imageBitmap);
            UploadTask uploadTask = mStorageRef.child(imageURI.getLastPathSegment()).putFile(imageURI);
            uploadTask.addOnSuccessListener(new MyOnSuccessListener(imageURI.getLastPathSegment()));

        } else if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Sign-in succeeded, set up the UI
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // Sign in was canceled by the user, finish the com.example.lh.painter.activity
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            System.out.println("Error" + Integer.toString(resultCode));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent com.example.lh.painter.activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

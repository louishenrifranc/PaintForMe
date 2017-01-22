package com.example.lh.painter.painter.activity;

import android.app.FragmentTransaction;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.lh.painter.R;

import android.app.ProgressDialog;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.lh.painter.painter.adapter.GalleryAdapter;
import com.example.lh.painter.painter.app.AppController;
import com.example.lh.painter.painter.model.Image;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;


public class Main2Activity extends AppCompatActivity {

    private ArrayList<Image> images;
    private ProgressDialog pDialog;
    private GalleryAdapter mAdapter;
    private RecyclerView recyclerView;

    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;

    private DatabaseReference mDatabase;
    private Integer nbfile;
    private String filename;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        nbfile = (Integer) getIntent().getExtras().get(MainActivity.NUMBER_OF_FILE_TO_RETRIEVE_ID);
        filename = (String) getIntent().getExtras().get(MainActivity.FILENAME_ID);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        pDialog = new ProgressDialog(this);
        images = new ArrayList<>();
        mAdapter = new GalleryAdapter(getApplicationContext(), images, nbfile, filename);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getBaseContext(), 2);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getApplicationContext(), recyclerView, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("images", images);
                bundle.putInt("position", position);

                android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        Runnable r = new Runnable() {
            @Override
            public void run(){
                recyclerView.setAdapter(mAdapter);
            }
        };
        handler = new Handler();
        handler.postDelayed(r, 20000);
    }





    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("11111", "Get destroy");
        // for (int position = 0; position < nbfile; position++) {
        //     mStorageRef.child(filename + Integer.toString(position) + ".jpg").delete().addOnSuccessListener(new OnSuccessListener<Void>() {
        //         @Override
        //         public void onSuccess(Void aVoid) {
        //             Log.d("111", "Successfully delete file");
        //         }
//
        //     });
//
        // }
    }
}

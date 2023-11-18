package com.example.nuevorep.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.nuevorep.R;
import com.example.nuevorep.VideoModel;
import com.example.nuevorep.adapter.VideosAdapter;

import java.util.ArrayList;
import java.util.Locale;

public class VideoFolder extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static final String MY_SORT_PREF = "sortOrder";
    private RecyclerView recyclerView;
    private String name;
    private ArrayList<VideoModel> videoModelArrayList = new ArrayList<>();
    private VideosAdapter videosAdapter;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_folder);


        name = getIntent().getStringExtra("folderName");
        recyclerView = findViewById(R.id.video_recyclerview);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.go_back));
        int index = name.lastIndexOf("/");
        String onlyFolderName = name.substring(index+1);
        toolbar.setTitle(onlyFolderName);



        loadVideos();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        ImageView ivClose = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        ivClose.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white),
                PorterDuff.Mode.SRC_IN);
        searchView.setQueryHint("Buscando nombre de archivo");
        searchView.setOnQueryTextListener(this);



        return super.onCreateOptionsMenu(menu);

    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String input = newText.toLowerCase();
        ArrayList<VideoModel> searchList = new ArrayList<>();
        for (VideoModel model : videoModelArrayList){
            if (model.getTitle().toLowerCase().contains(input)){
                searchList.add(model);
            }
        }
        videosAdapter.updateSearchList(searchList);


        return false;
    }

    private void loadVideos() {
        videoModelArrayList = getallVideoFromFolder(this, name);
        if (name!=null && videoModelArrayList.size()>0){
            videosAdapter = new VideosAdapter(videoModelArrayList, this);
            //Se agrego esto para que la vista recyclerview esta retrasado
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemViewCacheSize(20);
            recyclerView.setDrawingCacheEnabled(true);
            recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setLayoutManager(new LinearLayoutManager(this,
                    RecyclerView.VERTICAL, false));


            recyclerView.setAdapter(videosAdapter);
            //Es crucial para que el RecyclerView muestre los elementos del
            // adaptador. Sin ella, los datos del adaptador no se conectarán correctamente
            // con la vista y no se mostrará ningún elemento


        }else{
            Toast.makeText(this, "No se pudo encontrar ningun video", Toast.LENGTH_SHORT).show();
        }
    }

    private ArrayList<VideoModel> getallVideoFromFolder(Context context, String name) {
        ArrayList<VideoModel> list = new ArrayList<>();
        SharedPreferences preferences = getSharedPreferences(MY_SORT_PREF, MODE_PRIVATE);
        String sort = preferences.getString("clasificación", "ordenar por fecha");
        String order = null;
        switch (sort){

            case "ordenar por fecha":
                order = MediaStore.MediaColumns.DATE_ADDED + " ASC";
                break;

            case "ordenar por nombre":
                order = MediaStore.MediaColumns.DISPLAY_NAME + " ASC";
                break;

            case "ordenar por tamaño":
                order = MediaStore.MediaColumns.DATE_ADDED + " DESC";
                break;



        }

        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.HEIGHT,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.RESOLUTION
        };

        String selection = MediaStore.Video.Media.DATA + " like?";
        String[] selectionArgs = new String[]{"%" + name + "%"};

        Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, order);

        if (cursor!=null){
            while (cursor.moveToNext()){

                String id = cursor.getString(0);
                String path = cursor.getString(1);
                String title = cursor.getString(2);
                int size = cursor.getInt(3);
                String resolution = cursor.getString(4);
                int duration = cursor.getInt(5);
                String disName = cursor.getString(6);
                String bucket_display_name = cursor.getString(7);
                String width_height = cursor.getString(8);

                //Este metodo convierte 1024KB en 1MB
                String human_can_read = null;
                if(size < 1024) {
                    human_can_read = String.format(context.getString(R.string.size_in_b), (double) size);
                } else if (size < Math.pow(1024, 2)) {
                    human_can_read = String.format(context.getString(R.string.size_in_kb), (double) (size / 1024));

                } else if (size < Math.pow(1024, 3)) {
                    human_can_read = String.format(context.getString(R.string.size_in_mb), (double) (size / Math.pow(1024, 2)));

                } else {
                    human_can_read = String.format(context.getString(R.string.size_in_gb), (double) (size / Math.pow(1024, 3)));
                }

                //Este metodo convierte cualquier duracion de video aleatorio como 1331533132 en 1:21:12
                String duration_formatted;
                int sec = (duration / 1000) % 60;
                int min = (duration / (1000 * 60)) % 60;
                int hrs = duration / (1000 * 60 *60);

                if (hrs == 0) {
                    duration_formatted = String.valueOf(min)
                            .concat(":".concat(String.format(Locale.UK, "%02d", sec)));
                } else {
                    duration_formatted = String.valueOf(hrs)
                            .concat(":".concat(String.format(Locale.UK, "%02d", min)
                                    .concat(":".concat(String.format(Locale.UK, "%02d", sec)))));
                }

                VideoModel files = new VideoModel(id, path, title,
                        human_can_read, resolution, duration_formatted,
                        disName, width_height);
                if (name.endsWith(bucket_display_name))
                    list.add(files);

            }cursor.close();
        }

        return list;
    }

    @SuppressLint("NonConstantResourceId")
    private static final int SORT_BY_DATE = 1;
    private static final int SORT_BY_NAME = 2;
    private static final int SORT_BY_SIZE = 3;
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SharedPreferences.Editor editor = getSharedPreferences(MY_SORT_PREF, MODE_PRIVATE).edit();
        int itemId = item.getItemId();
        switch (itemId){
            case SORT_BY_DATE:
                editor.putString("clasificación", "ordenar por fecha");
                editor.apply();
                this.recreate();
                break;
            case SORT_BY_NAME:
                editor.putString("clasificación", "ordenar por nombre");
                editor.apply();
                this.recreate();
                break;
            case SORT_BY_SIZE:
                editor.putString("clasificación", "ordenar por tamaño");
                editor.apply();
                this.recreate();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
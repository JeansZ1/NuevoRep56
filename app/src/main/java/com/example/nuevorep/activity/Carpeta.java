package com.example.nuevorep.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import com.example.nuevorep.R;
import com.example.nuevorep.VideoModel;
import com.example.nuevorep.adapter.FolderAdapter;

import java.util.ArrayList;

public class Carpeta extends AppCompatActivity {

    private ArrayList<String> folderList = new ArrayList<>();
    private ArrayList<VideoModel> videoList = new ArrayList<>();
    FolderAdapter folderAdapter;
    RecyclerView recyclerView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carpeta);
        recyclerView = findViewById(R.id.folder_recyclerview);

        videoList = fetchAllVideos(this);
        if (folderList!=null && folderList.size()> 0 && videoList!=null){
            folderAdapter = new FolderAdapter(folderList, videoList, this);
            recyclerView.setAdapter(folderAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager( this,
                    RecyclerView.VERTICAL, false));
        }else{
            Toast.makeText(this, "No se pudo encontrar ninguna carpeta de Video", Toast.LENGTH_SHORT).show();
        }
    }

    //Este es el método que recuperará todos los videos del almacenamiento interno o externo
    private ArrayList<VideoModel> fetchAllVideos(Context context){

        ArrayList<VideoModel> videoModels = new ArrayList<>();

        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String orderBy = MediaStore.Video.Media.DATE_ADDED + " DESC";

        String[] projection = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.HEIGHT,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.RESOLUTION
        };

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, orderBy);

        if (cursor != null){
            while (cursor.moveToNext()) {

                String id = cursor.getString(0);
                String path = cursor.getString(1);
                String title = cursor.getString(2);
                String size = cursor.getString(3);
                String resolution = cursor.getString(4);
                String duration = cursor.getString(5);
                String disName = cursor.getString(6);
                String width_height = cursor.getString(7);

                VideoModel videofiles = new VideoModel(id, path, title, size, resolution, duration, disName, width_height);

                int slashFirstIndex = path.lastIndexOf("/");
                String subString = path.substring(0, slashFirstIndex);


                if (!folderList.contains(subString)){
                    folderList.add(subString);
                }
                videoModels.add(videofiles);
            }
            cursor.close();
        }
        return videoModels;

    }
}
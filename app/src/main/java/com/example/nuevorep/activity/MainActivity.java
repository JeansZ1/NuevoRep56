package com.example.nuevorep.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import com.example.nuevorep.R;
import com.example.nuevorep.VideoModel;
import com.example.nuevorep.adapter.FolderAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        if (!isLoggedIn()) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }








    }
    private boolean isLoggedIn() {
        SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        return preferences.getBoolean("isLoggedIn", false);
    }



}
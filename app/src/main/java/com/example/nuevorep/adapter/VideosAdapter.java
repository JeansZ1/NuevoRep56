package com.example.nuevorep.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nuevorep.R;
import com.example.nuevorep.VideoModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.util.ArrayList;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.MyHolder> {

    private ArrayList<VideoModel> videoFolder = new ArrayList<>();
    private Context context;


    public VideosAdapter(ArrayList<VideoModel> videoFolder, Context context) {
        this.videoFolder = videoFolder;
        this.context = context;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.files_view, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        Glide.with(context).load(videoFolder.get(position).getPath()).into(holder.thumbnail);
        holder.title.setText(videoFolder.get(position).getTitle());
        holder.duration.setText(videoFolder.get(position).getDuration());
        holder.size.setText(videoFolder.get(position).getSize());
        holder.resolution.setText(videoFolder.get(position).getResolution());
        holder.menu.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
            View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.file_menu, null);

            bottomSheetView.findViewById(R.id.menu_down).setOnClickListener(v1 -> {
                bottomSheetDialog.dismiss();
            });
            bottomSheetView.findViewById(R.id.menu_share).setOnClickListener(v2 ->{
                Toast.makeText(context, "Compartir", Toast.LENGTH_SHORT).show();
            });
            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();
        });

    }

    @Override
    public int getItemCount() {

        return videoFolder.size();
    }


    public class MyHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail, menu;
        TextView title, size, duration, resolution;
        public MyHolder(@NonNull View itemView) {
            super(itemView);

            thumbnail = itemView.findViewById(R.id.thumbnail);
            title = itemView.findViewById(R.id.video_title);
            size = itemView.findViewById(R.id.video_size);
            duration = itemView.findViewById(R.id.video_duration);
            resolution = itemView.findViewById(R.id.video_quality);
            menu = itemView.findViewById(R.id.video_menu);
        }
    }
}

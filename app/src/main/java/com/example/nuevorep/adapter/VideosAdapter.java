package com.example.nuevorep.adapter;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nuevorep.R;
import com.example.nuevorep.VideoModel;
import com.example.nuevorep.activity.VideoPlayer;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.MyHolder> {


    public static ArrayList<VideoModel> videoFolder = new ArrayList<>();
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
    public void onBindViewHolder(@NonNull MyHolder holder, @SuppressLint("RecyclerView") int position) {

        Glide.with(context)
                .load(Uri.fromFile(new File(videoFolder.get(position).getPath())))
                .into(holder.thumbnail);
        holder.title.setText(videoFolder.get(position).getTitle());
        holder.duration.setText(videoFolder.get(position).getDuration());
        holder.size.setText(videoFolder.get(position).getSize());
        holder.resolution.setText(videoFolder.get(position).getResolution());
        holder.menu.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
            View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.file_menu, null);


            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();

            bottomSheetView.findViewById(R.id.menu_down).setOnClickListener(v1 -> {
                bottomSheetDialog.dismiss();
            });
            bottomSheetView.findViewById(R.id.menu_share).setOnClickListener(v2 ->{
                bottomSheetDialog.dismiss();
                shareFile(position);
            });
            bottomSheetView.findViewById(R.id.menu_rename).setOnClickListener(v3 ->{
                bottomSheetDialog.dismiss();
                renameFiles(position, v);

            });
            bottomSheetView.findViewById(R.id.menu_delete).setOnClickListener(v4 ->{
                bottomSheetDialog.dismiss();
                deleteFiles(position, v);

            });
            bottomSheetView.findViewById(R.id.menu_properties).setOnClickListener(v5 ->{
                bottomSheetDialog.dismiss();
                showProperties(position);

            });
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VideoPlayer.class);
                intent.putExtra("p", position);
                context.startActivity(intent);
            }
        });





    }


    private void shareFile(int p){
        Uri uri = Uri.parse(videoFolder.get(p).getPath());
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("video/*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(Intent.createChooser(intent,"Compartir"));
        Toast.makeText(context, "Cargando..", Toast.LENGTH_SHORT).show();
    }

    private void deleteFiles(int position, View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Eliminar")
                .setMessage(videoFolder.get(position).getTitle())
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Ok", (dialog, which) -> {
                    Uri contentUri = ContentUris.withAppendedId(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            Long.parseLong(videoFolder.get(position).getId())
                    );
                    try {
                        context.getContentResolver().delete(contentUri, null, null);
                        videoFolder.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, videoFolder.size());
                        Snackbar.make(view, "Archivo eliminado exitosamente", Snackbar.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Snackbar.make(view, "Fallo al eliminar el archivo", Snackbar.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void renameFiles(int position, View view) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.rename_layout);
        EditText editText = dialog.findViewById(R.id.rename_edit_text);
        Button cancel = dialog.findViewById(R.id.cancel_rename_button);
        Button renameBtn = dialog.findViewById(R.id.rename_button);

        final File renameFile = new File(videoFolder.get(position).getPath());
        String nameText = renameFile.getName();
        nameText = nameText.substring(0, nameText.lastIndexOf("."));
        editText.setText(nameText);

        cancel.setOnClickListener(v -> dialog.dismiss());

        renameBtn.setOnClickListener(v -> {
            String newName = editText.getText().toString();
            String parentPath = renameFile.getParent();
            String extension = renameFile.getAbsolutePath().substring(renameFile.getAbsolutePath().lastIndexOf("."));
            String newPath = parentPath + "/" + newName + extension;
            File newFile = new File(newPath);

            try {
                if (renameFile.exists() && renameFile.renameTo(newFile)) {
                    videoFolder.get(position).setTitle(newName);
                    notifyItemChanged(position);
                    Snackbar.make(view, "Nombre cambiado correctamente", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(view, "Fallo al cambiar el nombre", Snackbar.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Snackbar.make(view, "Fallo al cambiar el nombre", Snackbar.LENGTH_SHORT).show();
            }

            dialog.dismiss();
        });

        dialog.show();
    }



    private void showProperties(int p){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.file_properties);

        String name = videoFolder.get(p).getTitle();
        String path = videoFolder.get(p).getPath();
        String size = videoFolder.get(p).getSize();
        String duration = videoFolder.get(p).getDuration();
        String resolution = videoFolder.get(p).getResolution();

        TextView tit = dialog.findViewById(R.id.pro_title);
        TextView st = dialog.findViewById(R.id.pro_storage);
        TextView siz = dialog.findViewById(R.id.pro_size);
        TextView dur = dialog.findViewById(R.id.pro_duration);
        TextView res = dialog.findViewById(R.id.pro_resolution);

        tit.setText(name);
        st.setText(path);
        siz.setText(size);
        dur.setText(duration);
        res.setText(resolution);
        dialog.show();
    }


    @Override
    public int getItemCount() {

        return videoFolder.size();
    }

    public void updateSearchList(ArrayList<VideoModel> searchList) {
        videoFolder = new ArrayList<>();
        videoFolder.addAll(searchList);
        notifyDataSetChanged();

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

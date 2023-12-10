package com.example.nuevorep.activity;

import static com.example.nuevorep.R.id.videoView_go_back;
import static com.example.nuevorep.adapter.VideosAdapter.videoFolder;

import androidx.annotation.NonNull;

import android.annotation.SuppressLint;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.nuevorep.R;

public class VideoPlayer extends AppCompatActivity implements View.OnTouchListener,
        ScaleGestureDetector.OnScaleGestureListener, View.OnClickListener {






    RelativeLayout zoomLayout;
    ScaleGestureDetector scaleDetector;
    GestureDetectorCompat gestureDetector;
    private static final float MIN_ZOOM = 1.0f;
    private static final float MAX_ZOOM = 5.0f;
    boolean intLeft, intRight;
    private Display display;
    private Point size;
    private Mode mode = Mode.NONE;

    @Override
    public void onClick(View v) {
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        rewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.seekTo(videoView.getCurrentPosition() - 10000);
            }
        });

        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    videoView.pause();
                    playPause.setImageResource(R.drawable.ic_play);
                } else {
                    videoView.start();
                    playPause.setImageResource(R.drawable.netflix_pause_button);
                }
            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.seekTo(videoView.getCurrentPosition() + 10000);
            }
        });


    }


    private enum Mode {
        NONE,
        DRAG,
        ZOOM
    }
    int device_width;
    private int sWidth;
    private boolean isEnable = true;
    private float scale = 1.0f;
    private float lastScaleFactor = 0f;
    // Where the finger first  touches the screen
    private float startX = 0f;
    private float startY = 0f;
    // How much to translate the canvas
    private float dx = 0f;
    private float dy = 0f;
    private float prevDx = 0f;
    private float prevDy = 0f;


    int position = -1;
    private VideoView videoView;
    LinearLayout one, two , three , four;
    ImageButton goBack, rewind, forward, playPause;
    TextView title, endTimes;
    SeekBar videoSeekBar;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                hideDefaultControls();
                if (scale > MIN_ZOOM) {
                    mode = Mode.DRAG;
                    startX = event.getX() - prevDx;
                    startY = event.getY() - prevDy;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                hideDefaultControls();
                isEnable = false;
                if (mode == Mode.DRAG) {
                    dx = event.getX() - startX;
                    dy = event.getY() - startY;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mode = Mode.ZOOM;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode = Mode.DRAG;
                break;
            case MotionEvent.ACTION_UP:
                mode = Mode.NONE;
                prevDx = dx;
                prevDy = dy;
                break;
        }
        scaleDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        if ((mode == Mode.DRAG && scale >= MIN_ZOOM) || mode == Mode.ZOOM) {
            zoomLayout.requestDisallowInterceptTouchEvent(true);
            float maxDx = (child().getWidth() - (child().getWidth() / scale)) / 2 * scale;
            float maxDy = (child().getHeight() - (child().getHeight() / scale)) / 2 * scale;
            dx = Math.min(Math.max(dx, -maxDx), maxDx);
            dy = Math.min(Math.max(dy, -maxDy), maxDy);
            applyScaleAndTranslation();
        }
        return true;
    }

    private void applyScaleAndTranslation() {
        child().setScaleX(scale);
        child().setScaleY(scale);
        child().setTranslationX(dx);
        child().setTranslationY(dy);
    }

    private View child() {
        return zoomLayout(0);
    }

    private View zoomLayout(int i) {
        return videoView;
    }



    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scaleFactor = scaleDetector.getScaleFactor();
        if (lastScaleFactor == 0 || (Math.signum(scaleFactor) == Math.signum(lastScaleFactor))) {
            scale *= scaleFactor;
            scale = Math.max(MIN_ZOOM, Math.min(scale, MAX_ZOOM));
            lastScaleFactor = scaleFactor;
        } else {
            lastScaleFactor = 0;
        }
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) { }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        videoView = findViewById(R.id.video_view);
        one = findViewById(R.id.videoView_one_layout);
        two = findViewById(R.id.videoView_two_layout);
        three = findViewById(R.id.videoView_three_layout);
        four = findViewById(R.id.videoView_four_layout);
        goBack = findViewById(videoView_go_back);
        title = findViewById(R.id.videoView_title);
        rewind = findViewById(R.id.videoView_rewind);
        playPause = findViewById(R.id.videoView_play_pause_btn);
        forward = findViewById(R.id.videoView_forward);
        endTimes = findViewById(R.id.videoView_endtime);
        videoSeekBar = findViewById(R.id.videoView_seekbar);




        // Configura los listeners para los botones






        position = getIntent().getIntExtra("p", -1);
        title.setText(videoFolder.get(position).getTitle());
        String path = videoFolder.get(position).getPath();
        if (path!=null){
            videoView.setVideoPath(path);
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);

            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    videoSeekBar.setMax(videoView.getDuration());
                    videoView.start();
                }
            });
        }else {
            Toast.makeText(this, "La ruta no existe", Toast.LENGTH_SHORT).show();
        }
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                        Toast.makeText(VideoPlayer.this, "Error desconocido", Toast.LENGTH_SHORT).show();
                        break;
                    case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                        Toast.makeText(VideoPlayer.this, "Servidor muerto", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false; // Devuelve false para indicar que no se ha gestionado el error completamente
            }
        });

        zoomLayout = findViewById(R.id.zoom_layout);
        display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        sWidth = size.x;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        device_width = displayMetrics.widthPixels;
        zoomLayout.setOnTouchListener(this);
        scaleDetector = new ScaleGestureDetector(getApplicationContext(), this);
        gestureDetector = new GestureDetectorCompat(getApplicationContext(), new GestureDetector());

        initalizeSeekBars();
        setHandle();


    }

    private void initalizeSeekBars(){
        videoSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    videoView.seekTo(progress);
                    videoView.start();
                    int currentPosition = videoView.getCurrentPosition();
                    endTimes.setText(""+convertIntoTime(videoView.getDuration()-currentPosition));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private String convertIntoTime(int ms){
        String time;
        int x, seconds, minutes, hours;
        x = ms / 1000;
        seconds = x % 60;
        x /= 60;
        minutes = x % 60;
        x /= 60;
        hours = x % 24;
        if (hours != 0)
            time = String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
        else time = String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
        return time;
    }
    private void setHandle(){
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (videoView.getDuration()>0){
                    int currentPosition = videoView.getCurrentPosition();
                    videoSeekBar.setProgress(currentPosition);
                    endTimes.setText(""+convertIntoTime(videoView.getDuration()-currentPosition));
                }
                handler.postDelayed(this,0);
            }
        };
        handler.postDelayed(runnable,500);
    }
    @SuppressLint("NonConstantResourceId")


    private class GestureDetector extends android.view.GestureDetector.SimpleOnGestureListener{

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (isEnable){
                hideDefaultControls();
                isEnable = false;
            }else {
                showDefaultControls();
                isEnable = true;
            }
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent event) {
            if (event.getX() < (sWidth / 2)) {
                intLeft = true;
                intRight = false;
                videoView.seekTo(videoView.getCurrentPosition() - 20000);
                Toast.makeText(VideoPlayer.this, "-20sec", Toast.LENGTH_SHORT).show();
            } else if (event.getX() > (sWidth / 2)) {
                intLeft = false;
                intRight = true;
                videoView.seekTo(videoView.getCurrentPosition() + 20000);
                Toast.makeText(VideoPlayer.this, "+20sec", Toast.LENGTH_SHORT).show();
            }
            return super.onDoubleTap(event);
        }


    }

    private void hideDefaultControls(){
        one.setVisibility(View.GONE);
        two.setVisibility(View.GONE);
        three.setVisibility(View.GONE);
        four.setVisibility(View.GONE);
        // Todo esta función ocultará el estado y la navegación cuando hagamos clic en la pantalla
        final Window window = this.getWindow();
        if (window == null){
            return;
        }
        window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final View decorView = window.getDecorView();
        if (decorView!=null){
            int uiOption = decorView.getSystemUiVisibility();
            if (Build.VERSION.SDK_INT >= 14) {
                uiOption |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
            }
            if (Build.VERSION.SDK_INT >= 16) {
                uiOption |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            }
            if (Build.VERSION.SDK_INT >= 19) {
                uiOption |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            }
            decorView.setSystemUiVisibility(uiOption);
        }
    }

    private void showDefaultControls(){
        one.setVisibility(View.VISIBLE);
        two.setVisibility(View.VISIBLE);
        three.setVisibility(View.VISIBLE);
        four.setVisibility(View.VISIBLE);
        //Todo esta función mostrará el estado y la navegación cuando hagamos clic en la pantalla.
        final Window window = this.getWindow();
        if (window == null){
            return;
        }
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        final View decorView = window.getDecorView();
        if (decorView!=null){
            int uiOption = decorView.getSystemUiVisibility();
            if (Build.VERSION.SDK_INT >= 14) {
                uiOption &= ~View.SYSTEM_UI_FLAG_LOW_PROFILE;
            }
            if (Build.VERSION.SDK_INT >= 16) {
                uiOption &= ~View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            }
            if (Build.VERSION.SDK_INT >= 19) {
                uiOption &= ~View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            }
            decorView.setSystemUiVisibility(uiOption);
        }
    }
}
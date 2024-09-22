package com.devappsys.videouploadsample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import com.devappsys.videouploadsample.utils.VideoProcessor;

public class MainActivity extends AppCompatActivity {

    private static final int VIDEO_PICK_REQUEST_CODE = 1;
    private final VideoProcessor videoProcessor = VideoProcessor.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        Button btnSelectVideo = findViewById(R.id.btn_select_video);
        btnSelectVideo.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            intent.setType("video/*");
            startActivityForResult(intent, VIDEO_PICK_REQUEST_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VIDEO_PICK_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedVideoUri = data.getData();
            if (selectedVideoUri != null) {
                Toast.makeText(this, "Video Selected: " + selectedVideoUri.toString(), Toast.LENGTH_SHORT).show();
                File video = videoProcessor.saveVideoToLocalStorage(selectedVideoUri, this);
                if (video != null) {
                    videoProcessor.processVideo(video, this);
                } else {
                    Toast.makeText(this, "Failed to save video", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No video selected", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

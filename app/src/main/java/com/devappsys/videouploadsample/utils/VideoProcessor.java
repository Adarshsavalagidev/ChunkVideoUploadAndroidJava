package com.devappsys.videouploadsample.utils;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;
import com.arthenica.mobileffmpeg.FFmpeg;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VideoProcessor {

    private VideoUploader videoUploader;
    private VideoProcessor() {
        videoUploader = VideoUploader.getInstance();
    }

    private static VideoProcessor instance;

    private File outputDir;  // Directory for storing video chunks

    public static VideoProcessor getInstance() {
        if (instance == null) {
            instance = new VideoProcessor();
        }
        return instance;
    }

    // Method to save the video to local storage
    public File saveVideoToLocalStorage(Uri videoUri, Context context) {
        try {
            File dir = new File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), "MyAppVideos");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName = getFileName(videoUri, context);
            File videoFile = new File(dir, fileName);
            InputStream inputStream = context.getContentResolver().openInputStream(videoUri);
            FileOutputStream outputStream = new FileOutputStream(videoFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            inputStream.close();
            outputStream.close();
            Toast.makeText(context, "Video saved to: " + videoFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            return videoFile;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to save video", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    // Helper method to get the file name from the Uri
    private String getFileName(Uri uri, Context context) {
        String result = null;

        if (Objects.equals(uri.getScheme(), "content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex);
                    } else {
                        result = uri.getLastPathSegment();
                    }
                }
            }
        }

        if (result == null) {
            result = uri.getLastPathSegment();
        }

        return result;
    }

    // Method to process the video by compressing and splitting it into chunks
    public void processVideo(File videoFile, Context context) {
        // Create the parent directory for video chunks
        File parentDir = new File(videoFile.getParent(), "VideoChunks");
        if (!parentDir.exists()) {
            parentDir.mkdirs(); // Ensure the parent directory exists
        }

        // Create a subdirectory for the specific video
        String videoName = videoFile.getName().substring(0, videoFile.getName().lastIndexOf('.'));
        File videoDir = new File(parentDir, videoName);
        if (!videoDir.exists()) {
            videoDir.mkdirs(); // Create directory for this video
        }

        // Define the command for FFmpeg to split the video
        String[] cmd = {
                "-i", videoFile.getAbsolutePath(),
                "-vcodec", "mpeg4",
                "-b:v", "1000k",
                "-b:a", "128k",
                "-f", "segment",
                "-segment_time", "10", // This sets the duration of each segment to 10 seconds
                new File(videoDir, "chunk%03d.mp4").getAbsolutePath() // Output files in the video-specific directory
        };

        // Execute the FFmpeg command asynchronously
        long executionId = FFmpeg.executeAsync(cmd, (executionId1, returnCode) -> {
            if (returnCode == RETURN_CODE_SUCCESS) {
                Toast.makeText(context, "Compression and chunking completed successfully", Toast.LENGTH_SHORT).show();
                videoUploader.initiateUpload("surveyId",videoDir.getAbsolutePath(), context); // Initiate the upload process
            } else {
                Toast.makeText(context, "Processing failed", Toast.LENGTH_SHORT).show();
            }
        });

        Toast.makeText(context, "Processing video...", Toast.LENGTH_SHORT).show(); // Inform the user that processing has started
    }


    // Method to retrieve the video chunks after processing
    public List<File> getVideoChunks() {
        List<File> chunks = new ArrayList<>();
        File[] chunkFiles = outputDir.listFiles();

        if (chunkFiles != null) {
            for (File chunk : chunkFiles) {
                if (chunk.getName().endsWith(".mp4")) {
                    chunks.add(chunk);
                }
            }
        }

        return chunks;
    }
}

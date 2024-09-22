package com.devappsys.videouploadsample.utils;

import android.content.Context;
import android.widget.Toast;

import com.devappsys.videouploadsample.DTO.Requests.UploadInitRequest;
import com.devappsys.videouploadsample.DTO.Requests.VerifyChunkRequests;
import com.devappsys.videouploadsample.DTO.Response.ChunkResponse;
import com.devappsys.videouploadsample.DTO.Response.UploadInitResponse;
import com.devappsys.videouploadsample.DTO.Response.VerifyChunkResponse;
import com.devappsys.videouploadsample.api.ApiClient;
import com.devappsys.videouploadsample.api.ApiService;

import java.io.File;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoUploader {

    private static VideoUploader instance;
    private final ApiService apiService;

    private VideoUploader() {
        apiService = ApiClient.getApiService();
    }

    public static VideoUploader getInstance() {
        if (instance == null) {
            instance = new VideoUploader();
        }
        return instance;
    }

    // Initiate upload by getting videoId and chunkCount
    public void initiateUpload(String surveyId, String chunkPath, Context context) {
        File chunkDir = new File(chunkPath);  // Get the directory containing the chunks

        System.out.println("initiateUpload: " + chunkDir.getAbsolutePath());

        // Count the number of chunks (assuming all chunks are .mp4 files)
        File[] chunkFiles = chunkDir.listFiles((dir, name) -> name.endsWith(".mp4"));
        int chunkCount = chunkFiles != null ? chunkFiles.length : 0;
        System.out.println("Chunk count: " + chunkCount);
        UploadInitRequest request = new UploadInitRequest(surveyId, chunkCount);
        apiService.uploadInit(request).enqueue(new Callback<UploadInitResponse>() {
            @Override
            public void onResponse(Call<UploadInitResponse> call, Response<UploadInitResponse> response) {
                assert response.body() != null;
                System.out.println("Response: " + response.body().getVideoId());
                if (response.isSuccessful() && response.body() != null) {
                    String videoId = response.body().getVideoId();
                    Toast.makeText(context, "Upload Initialized: " + videoId, Toast.LENGTH_SHORT).show();
                    // After initiation, start uploading chunks
                    assert chunkFiles != null;
                    uploadVideoChunks(videoId, chunkFiles, context);  // Pass the chunk files for uploading
                } else {
                    Toast.makeText(context, "Failed to initiate upload", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UploadInitResponse> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Updated uploadVideoChunks method to accept chunk files
    public void uploadVideoChunks(String videoId, File[] chunkFiles, Context context) {
        for (int i = 0; i < chunkFiles.length; i++) {
            System.out.println("Uploading chunk: " + i);
            uploadVideoChunk(videoId, i , chunkFiles[i], chunkFiles.length,context);  // Upload each chunk
        }
    }



    // Helper method to upload a single chunk
    public void uploadVideoChunk(String videoId, int sequenceNumber, File chunkFile, int totalChunks, Context context) {
        // Create a RequestBody with the correct media type for mp4 files
        okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(
                okhttp3.MediaType.parse("video/mp4"),
                chunkFile
        );

        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", chunkFile.getName(), requestBody);
        System.out.println("Uploading chunk: " + videoId);
        apiService.uploadChunk(videoId, sequenceNumber, filePart).enqueue(new Callback<ChunkResponse>() {
            @Override
            public void onResponse(Call<ChunkResponse> call, Response<ChunkResponse> response) {

                if (response.isSuccessful()) {
                    Toast.makeText(context, "Chunk " + sequenceNumber + " uploaded successfully", Toast.LENGTH_SHORT).show();
                    // Check if this was the last chunk and verify
                    if (sequenceNumber + 1 == totalChunks) {
                        verifyChunk(videoId, context);
                    }
                } else {
                    Toast.makeText(context, "Failed to upload chunk " + sequenceNumber, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ChunkResponse> call, Throwable t) {
                Toast.makeText(context, "Error uploading chunk: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Verify if the chunks were uploaded correctly
    public void verifyChunk(String videoId, Context context) {
        VerifyChunkRequests request = new VerifyChunkRequests(videoId);

        apiService.verifyChunk(request).enqueue(new Callback<VerifyChunkResponse>() {
            @Override
            public void onResponse(Call<VerifyChunkResponse> call, Response<VerifyChunkResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    VerifyChunkResponse verifyChunkResponse = response.body();

                    if ("success".equals(verifyChunkResponse.getStatus())) {
                        Toast.makeText(context, "Upload Verified Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle pending status
                        Map<String, Object> additionalInfo = verifyChunkResponse.getAdditionalInfo();
                        if (additionalInfo != null && additionalInfo.containsKey("pending")) {
                            int pendingChunks = (int) additionalInfo.get("pending");
                            Toast.makeText(context, "Upload Pending: " + pendingChunks + " chunks remaining", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "No pending chunks information available", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(context, "Failed to verify upload", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<VerifyChunkResponse> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



}

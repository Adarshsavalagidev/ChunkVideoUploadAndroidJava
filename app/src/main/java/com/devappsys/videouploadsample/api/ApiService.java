package com.devappsys.videouploadsample.api;

import com.devappsys.videouploadsample.DTO.Requests.UploadInitRequest;
import com.devappsys.videouploadsample.DTO.Requests.VerifyChunkRequests;
import com.devappsys.videouploadsample.DTO.Response.ChunkResponse;
import com.devappsys.videouploadsample.DTO.Response.UploadInitResponse;
import com.devappsys.videouploadsample.DTO.Response.VerifyChunkResponse;


import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {

    // Endpoint for initializing video upload
    @POST("/api/v1/upload/init")
    Call<UploadInitResponse> uploadInit(@Body UploadInitRequest initializeDTO);

    // Endpoint for uploading a video chunk
    @Multipart
    @POST("/api/v1/upload/chunk")
    Call<ChunkResponse> uploadChunk(
            @Part("videoId") String videoId,
            @Part("sequenceNumber") int sequenceNumber,
            @Part MultipartBody.Part file
    );

    // Endpoint for verifying video upload
    @POST("/api/v1/upload/verify")
    Call<VerifyChunkResponse> verifyChunk(@Body VerifyChunkRequests verifyChunk);
}

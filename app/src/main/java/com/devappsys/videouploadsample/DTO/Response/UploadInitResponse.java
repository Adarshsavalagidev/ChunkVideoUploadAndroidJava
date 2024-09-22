package com.devappsys.videouploadsample.DTO.Response;

import com.devappsys.videouploadsample.DTO.models.Survey;

public class UploadInitResponse {
    private String id;
    private String videoId;
    private int chunkCount;

    public UploadInitResponse(String id, int chunkCount, String videoId) {
        this.id = id;
        this.chunkCount = chunkCount;
        this.videoId = videoId;
    }

    public int getChunkCount() {
        return chunkCount;
    }

    public void setChunkCount(int chunkCount) {
        this.chunkCount = chunkCount;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

package com.devappsys.videouploadsample.DTO.Requests;

public class VerifyChunkRequests {
    private String videoId;

    public VerifyChunkRequests(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoId() {
        return videoId;
    }
}

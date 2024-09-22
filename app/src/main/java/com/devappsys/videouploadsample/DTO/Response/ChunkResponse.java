package com.devappsys.videouploadsample.DTO.Response;

public class ChunkResponse {
    private String status;
    private String message;

    public ChunkResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}

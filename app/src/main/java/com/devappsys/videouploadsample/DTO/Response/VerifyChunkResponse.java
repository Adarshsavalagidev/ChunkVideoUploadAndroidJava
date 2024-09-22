package com.devappsys.videouploadsample.DTO.Response;

import java.util.Map;

public class VerifyChunkResponse {
    private String status;              // Indicates the status of the verification ("success", "pending", "error")
    private int chunkCount;             // Total number of chunks expected for the video
    private Map<String, Object> additionalInfo; // Additional information (e.g., pending chunks, messages)

    // Constructor for successful verification
    public VerifyChunkResponse(String status, int chunkCount, Map<String, Object> additionalInfo) {
        this.status = status;
        this.chunkCount = chunkCount;
        this.additionalInfo = additionalInfo;
    }

    // Constructor for error responses
    public VerifyChunkResponse(String status, String errorMessage) {
        this.status = status;
        this.chunkCount = 0; // Set to 0 for error responses
        this.additionalInfo = Map.of("error", errorMessage); // Add error message to additionalInfo
    }

    // Getters
    public String getStatus() {
        return status;
    }

    public int getChunkCount() {
        return chunkCount;
    }

    public Map<String, Object> getAdditionalInfo() {
        return additionalInfo;
    }
}

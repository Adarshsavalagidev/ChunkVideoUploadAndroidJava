package com.devappsys.videouploadsample.DTO.Requests;

public class UploadInitRequest {
    private String surveyId;
    private int chunkCount;

    public UploadInitRequest(String surveyId, int chunkCount) {
        this.surveyId = surveyId;
        this.chunkCount = chunkCount;
    }

    public String getSurveyId() {
        return surveyId;
    }

    public int getChunkCount() {
        return chunkCount;
    }
}

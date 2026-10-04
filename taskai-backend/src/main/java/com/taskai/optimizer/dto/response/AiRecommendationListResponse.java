package com.taskai.optimizer.dto.response;

import java.util.ArrayList;
import java.util.List;

public class AiRecommendationListResponse {

    private String userEmail;
    private int totalCandidates;
    private List<AiRecommendationResponse> recommendations = new ArrayList<>();

    public AiRecommendationListResponse() {
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public int getTotalCandidates() {
        return totalCandidates;
    }

    public void setTotalCandidates(int totalCandidates) {
        this.totalCandidates = totalCandidates;
    }

    public List<AiRecommendationResponse> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<AiRecommendationResponse> recommendations) {
        this.recommendations = recommendations;
    }
}
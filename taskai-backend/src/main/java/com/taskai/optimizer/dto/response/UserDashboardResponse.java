package com.taskai.optimizer.dto.response;

public class UserDashboardResponse {

    private String userEmail;
    private AnalyticsResponse analytics;
    private AiRecommendationResponse recommendation;
    private String summary;

    public UserDashboardResponse() {
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public AnalyticsResponse getAnalytics() {
        return analytics;
    }

    public void setAnalytics(AnalyticsResponse analytics) {
        this.analytics = analytics;
    }

    public AiRecommendationResponse getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(AiRecommendationResponse recommendation) {
        this.recommendation = recommendation;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
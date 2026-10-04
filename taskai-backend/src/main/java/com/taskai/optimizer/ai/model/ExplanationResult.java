package com.taskai.optimizer.ai.model;

import java.util.ArrayList;
import java.util.List;

public class ExplanationResult {

    private String summary;
    private List<String> reasons = new ArrayList<>();

    public ExplanationResult() {
    }

    public ExplanationResult(String summary, List<String> reasons) {
        this.summary = summary;
        this.reasons = reasons;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getReasons() {
        return reasons;
    }

    public void setReasons(List<String> reasons) {
        this.reasons = reasons;
    }
}
package com.glitterlabs.videoqnaapp.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class FeedBack {

    public String feedback;

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public FeedBack() {
    }

    public FeedBack(String feedback) {
        this.feedback = feedback;
    }

}

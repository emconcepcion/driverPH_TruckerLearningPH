package com.cav.quizinstructions;

public class UserProgressModules {

    public int userId;
    public String module;
    public String lessonId;
    public int status;
    public String duration;
    public String date_taken;

    public UserProgressModules() {
    }

    public UserProgressModules(int userId, String module, String lessonId, int status, String duration, String date_taken) {
        this.userId = userId;
        this.module = module;
        this.lessonId = lessonId;
        this.status = status;
        this.duration = duration;
        this.date_taken = date_taken;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getLessonId() {
        return lessonId;
    }

    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDate_taken() {
        return date_taken;
    }

    public void setDate_taken(String date_taken) {
        this.date_taken = date_taken;
    }
}

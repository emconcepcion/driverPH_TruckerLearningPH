package com.cav.quizinstructions;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Score implements Serializable {

    public int user_id;
    public String email;
    public int score;
    public int num_of_items;
    public String chapter;
    public int num_of_attempt;
    public String duration;
    public String date_taken;
    public int isLocked;
    public int isCompleted;
    public int sync_status;

    public Score() {
    }

    public Score(int user_id, String email, int score, int num_of_items, String chapter,
                 int num_of_attempt, String duration, String date_taken, int isLocked,
                 int isCompleted, int sync_status) {
        this.user_id = user_id;
        this.email = email;
        this.score = score;
        this.num_of_items = num_of_items;
        this.chapter = chapter;
        this.num_of_attempt = num_of_attempt;
        this.duration = duration;
        this.date_taken = date_taken;
        this.isLocked = isLocked;
        this.isCompleted = isCompleted;
        this.sync_status = sync_status;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getNum_of_items() {
        return num_of_items;
    }

    public void setNum_of_items(int num_of_items) {
        this.num_of_items = num_of_items;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public int getNum_of_attempt() {
        return num_of_attempt;
    }

    public void setNum_of_attempt(int num_of_attempt) {
        this.num_of_attempt = num_of_attempt;
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

    public int getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(int isLocked) {
        this.isLocked = isLocked;
    }

    public int getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(int isCompleted) {
        this.isCompleted = isCompleted;
    }

    public int getSync_status() {
        return sync_status;
    }

    public void setSync_status(int sync_status) {
        this.sync_status = sync_status;
    }
}

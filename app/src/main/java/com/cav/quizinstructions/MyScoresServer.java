package com.cav.quizinstructions;

public class MyScoresServer {

    public int user_id;
    public String email;
    public int score;
    public int num_of_items;
    public String chapter;
    public int num_of_attempt;
    public String date_taken;

    public MyScoresServer(int user_id, String email, int score, int num_of_items, String chapter, int num_of_attempt, String date_taken) {
        this.user_id = user_id;
        this.email = email;
        this.score = score;
        this.num_of_items = num_of_items;
        this.chapter = chapter;
        this.num_of_attempt = num_of_attempt;
        this.date_taken = date_taken;
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

    public String getDate_taken() {
        return date_taken;
    }

    public void setDate_taken(String date_taken) {
        this.date_taken = date_taken;
    }
}

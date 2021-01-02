package com.cav.quizinstructions;

import android.provider.BaseColumns;

import java.text.SimpleDateFormat;

public class DbContract {

    public static final int SYNC_STATUS_SAVED = 0;
    public static final int SYNC_STATUS_FAILED = 1;

    public static class ScoresTable implements BaseColumns {

        //android access to localhost
//        public static final String SERVER_URL = "https://phportal.net/driverph/scores.php";
        public static final String SERVER_URL = "https://phportal.net/driverph/xample_score.php";
        public static final String SERVER_ALL_ATTEMPTS_URL = "https://phportal.net/driverph/post_all_attempts.php";
        public static final String SERVER_UPDATE_PROGRESS = "https://phportal.net/driverph/update_progress.php";
        public static final String UI_UPDATE_BROADCAST = "com.cav.quizinstructions.uiupdatebroadcast";

        public static final String DATABASE_NAME = "phdriver";
        public static final String TABLE_NAME_SCORES = "tbl_scores";
        public static final String COLUMN_NAME_USER_ID = "user_id";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_SCORE = "score";
        public static final String COLUMN_NAME_NUM_ITEMS = "num_of_items";
        public static final String COLUMN_NAME_CHAPTER = "chapter";
        public static final String COLUMN_NAME_NUM_ATTEMPT = "num_of_attempt";
        public static final String COLUMN_NAME_DURATION = "duration";
        public static final String COLUMN_NAME_DATE_TAKEN = "date_taken";
        public static final String COLUMN_NAME_IS_LOCKED = "isLocked";
        public static final String COLUMN_NAME_IS_COMPLETED = "isCompleted";
        public static final String SYNC_STATUS = "syncStatus";

    }

    public static class ScoresMySQLTable implements BaseColumns {

        //android access to server
        public static final String SERVER_URL = "https://phportal.net/driverph/scores.php";

        public static final String TABLE_NAME_SCORES_MYSQL = "tbl_scores_server";
        public static final String COLUMN_NAME_USER_ID_MYSQL = "user_id";
        public static final String COLUMN_NAME_EMAIL_MYSQL = "email";
        public static final String COLUMN_NAME_SCORE_MYSQL = "score";
        public static final String COLUMN_NAME_NUM_ITEMS_MYSQL = "num_of_items";
        public static final String COLUMN_NAME_CHAPTER_MYSQL = "chapter";
        public static final String COLUMN_NAME_NUM_ATTEMPT_MYSQL = "num_of_attempt";
        public static final String COLUMN_NAME_DURATION_MYSQL = "duration";
        public static final String COLUMN_NAME_DATE_TAKEN_MYSQL = "date_taken";
        public static final String COLUMN_NAME_IS_COMPLETED_MYSQL = "isCompleted";
        public static final String COLUMN_NAME_IS_LOCKED_MYSQL = "isLocked";
//        public static final String SYNC_STATUS_MYSQL = "syncStatus";
    }

    public static class AllAttemptsMySQLTable implements BaseColumns {
        public static final String TABLE_NAME_SCORES_ALL = "tbl_scores_all_attempts";
        public static final String COLUMN_NAME_USER_ID_ALL = "user_id";
        public static final String COLUMN_NAME_EMAIL_ALL = "email";
        public static final String COLUMN_NAME_SCORE_ALL= "score";
        public static final String COLUMN_NAME_NUM_ITEMS_ALL = "num_of_items";
        public static final String COLUMN_NAME_CHAPTER_ALL = "chapter";
        public static final String COLUMN_NAME_NUM_ATTEMPT_ALL = "num_of_attempt";
        public static final String COLUMN_NAME_DURATION_ALL = "duration";
        public static final String COLUMN_NAME_DATE_TAKEN_ALL = "date_taken";
        public static final String COLUMN_NAME_IS_COMPLETED_ALL = "isCompleted";
        public static final String COLUMN_NAME_IS_LOCKED_ALL = "isLocked";
    }


}

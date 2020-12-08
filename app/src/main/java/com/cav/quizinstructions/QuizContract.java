package com.cav.quizinstructions;

import android.provider.BaseColumns;

//container for different constants
public final class QuizContract {

    private QuizContract(){}

    public static class QuestionsTable implements BaseColumns {
        public static final String TABLE_NAME = "tbl_questions";
        public static final String COLUMN_QUESTION = "question_text";
        public static final String COLUMN_OPTION1 = "option1";
        public static final String COLUMN_OPTION2 = "option2";
        public static final String COLUMN_OPTION3 = "option3";
        public static final String COLUMN_OPTION4 = "option4";
        public static final String COLUMN_ANSWER_NR = "answer_nr";
        public static final String COLUMN_CHAPTER = "chapter";
    }

}

package com.cav.quizinstructions;

public class Constant {

    public static final String SP_LESSONID = "LessonIdSharedPrefs";

    public static final String _1 = "Basic Competencies";
    public static final String _2 = "Common Competencies";
    public static final String _3 = "Core Competencies";

    public static final String MODULE_ID_1 = "1";
    public static final String MODULE_ID_2 = "2";
    public static final String MODULE_ID_3 = "3";

    public void moduleCode(){
        String module = "";
        switch(module){
            case "1":
                module = _1;
                break;
            case "2":
                module = _2;
                break;
            case "3":
                module = _3;
                break;
        }
    }
}

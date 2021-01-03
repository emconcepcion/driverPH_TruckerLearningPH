package com.cav.quizinstructions;

public class Constant {

    public static final String _1 = "Basic Competencies";
    public static final String _2 = "Common Competencies";
    public static final String _3 = "Core Competencies";

    public static final String MY_LEVEL_PREFS = "Level_Prefs";
    public static final String MOD_1 = "Basic Competencies";
    public static final String MOD_2 = "Common Competencies";
    public static final String MOD_3 = "Core Competencies";

    public int moduleCode(){
        String module = "";
        int modId = 1;
        switch(modId){
            case 1:
                module = "Basic Competencies";
                break;
            case 2:
                module = "Common Competencies";
                break;
            case 3:
                module = "Core Competencies";
                break;
        }
        return modId;
    }
}

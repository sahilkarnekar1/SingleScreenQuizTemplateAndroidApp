package com.example.myapplication;

import java.util.List;

public class Question {

    private String questionText;
    private String questionImage;
    private List<String> options;
    private int correctAnswerIndex;

    public Question(String questionText, String questionImage, List<String> options, int correctAnswerIndex) {
        this.questionText = questionText;
        this.questionImage = questionImage;
        this.options = options;
        this.correctAnswerIndex = correctAnswerIndex;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getQuestionImage() {
        return questionImage;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }
}


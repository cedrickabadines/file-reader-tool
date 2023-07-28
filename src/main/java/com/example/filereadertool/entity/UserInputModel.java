package com.example.filereadertool.entity;

public class UserInputModel {
    private String userInput;

    public UserInputModel() {
    }

    public UserInputModel(String userInput) {
        this.userInput = userInput;
    }

    public String getUserInput() {
        return userInput;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }

    @Override
    public String toString() {
        return "UserInputModel{" +
                "userInput='" + userInput + '\'' +
                '}';
    }
}

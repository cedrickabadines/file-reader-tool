package com.example.filereadertool;

public class FileNotFound  extends RuntimeException{
    public FileNotFound(String message) {
        super(message);
    }
}

package com.example;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class OngoingDirectory implements Serializable {
    private String currentDirectory;
    private ArrayList<String> directories;
    private HashMap<String, Integer> files;

    public String getCurrentDirectory() {
        return currentDirectory;
    }

    public void setCurrentDirectory(String currentDirectory) {
        this.currentDirectory = currentDirectory;
    }

    public ArrayList<String> getDirectories() {
        return directories;
    }

    public void setDirectories(ArrayList<String> directories) {
        this.directories = directories;
    }

    public HashMap<String, Integer> getFiles() {
        return files;
    }

    public void setFiles(HashMap<String, Integer> files) {
        this.files = files;
    }
}

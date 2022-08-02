package com.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

public class FileExplorer {
    private File currentDirectory;

    public boolean goToParent() {
        if (currentDirectory == null) return false;
        currentDirectory = currentDirectory.getParentFile();
        return currentDirectory != null;
    }

    public boolean goToDirectory(String absolutePath) {
        File file = new File(absolutePath);
        boolean directoryExist;
        try {
            directoryExist = file.exists() && file.isDirectory();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        if (directoryExist) currentDirectory = file;
        return directoryExist;
    }

    public OngoingDirectory getOngoingDirectory() {
        OngoingDirectory ongoingDirectory = new OngoingDirectory();
        if (currentDirectory == null) {
            ongoingDirectory.setCurrentDirectory("");
            ArrayList<String> arrayList = new ArrayList<>();
            File[] roots = File.listRoots();
            if (roots == null || roots.length == 0) return null;
            for (File root : roots) {
                arrayList.add(root.getAbsolutePath());
            }
            ongoingDirectory.setDirectories(arrayList);
            ongoingDirectory.setFiles(new HashMap<>());
        } else {
            try {
                ongoingDirectory.setCurrentDirectory(currentDirectory.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            ArrayList<String> directoriesList = new ArrayList<>();
            HashMap<String, Integer> filesCollection = new HashMap<>();
            File[] files;
            try {
                files = currentDirectory.listFiles();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            if (files == null) return null;
            for (File file : files) {
                try {
                    if (file.isDirectory()) directoriesList.add(file.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (file.isFile()) {
                    try {
                        filesCollection.put(file.getName(), (int) Files.size(file.toPath()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        filesCollection.put(file.getName(), 0);
                    }
                }
            }
            ongoingDirectory.setDirectories(directoriesList);
            ongoingDirectory.setFiles(filesCollection);
        }
        return ongoingDirectory;
    }

    public boolean deleteFile(String absolutePath) {
        File file = new File(absolutePath);
        try {
            if (file.exists()) return file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean createDirectory(String absolutePath) {
        File file = new File(absolutePath);
        try {
            return file.mkdir();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean copyFile(String sourceAbsolutePath, String targetAbsolutePath) {
        File source = new File(sourceAbsolutePath);
        File target = new File(targetAbsolutePath);
        Path path = null;
        try {
            if (source.exists() && !target.exists()) {
                path = Files.copy(source.toPath(), target.toPath());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return target.toPath().equals(path);
    }

    public boolean rename(String oldName, String newName) {
        File oldNameFile = new File(oldName);
        File newNameFile= new File(newName);
        try {
            if (oldNameFile.exists() && !newNameFile.exists() && oldNameFile.getParent().equals(newNameFile.getParent())) {
                return oldNameFile.renameTo(newNameFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getOngoingDirectoryJSON() {
        ObjectMapper objectMapper = new ObjectMapper();
        OngoingDirectory ongoingDirectory = getOngoingDirectory();
        String json = "";
        try {
            json = objectMapper.writeValueAsString(ongoingDirectory);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }
}

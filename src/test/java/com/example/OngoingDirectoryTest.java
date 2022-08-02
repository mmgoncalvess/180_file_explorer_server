package com.example;

import com.example.OngoingDirectory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OngoingDirectoryTest {
    OngoingDirectory ongoingDirectory;

    @BeforeEach
    void setUp() {
        ongoingDirectory = new OngoingDirectory();
    }

    @AfterEach
    void tearDown() {
        ongoingDirectory = null;
    }

    @Test
    void getCurrentDirectory() {
        String expectedValue = "abc";
        ongoingDirectory.setCurrentDirectory("abc");
        String actualValue = ongoingDirectory.getCurrentDirectory();
        assertEquals(expectedValue, actualValue);
    }

    @Test
    void setCurrentDirectory() {
        String expectedValue = "abc";
        ongoingDirectory.setCurrentDirectory("abc");
        String actualValue = ongoingDirectory.getCurrentDirectory();
        assertEquals(expectedValue, actualValue);
    }

    @Test
    void getDirectories() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("abc");
        ongoingDirectory.setDirectories(arrayList);
        assertEquals(ongoingDirectory.getDirectories().get(0), "abc");
    }

    @Test
    void setDirectories() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("abc");
        ongoingDirectory.setDirectories(arrayList);
        assertEquals(ongoingDirectory.getDirectories().get(0), "abc");
    }

    @Test
    void getFiles() {
        HashMap<String, Integer> hashMap = new HashMap<>();
        hashMap.put("abc", 123);
        ongoingDirectory.setFiles(hashMap);
        assertEquals(ongoingDirectory.getFiles().get("abc"), 123);
    }

    @Test
    void setFiles() {
        HashMap<String, Integer> hashMap = new HashMap<>();
        hashMap.put("abc", 123);
        ongoingDirectory.setFiles(hashMap);
        assertEquals(ongoingDirectory.getFiles().get("abc"), 123);
    }
}
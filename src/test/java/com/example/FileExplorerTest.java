package com.example;

import com.example.FileExplorer;
import com.example.OngoingDirectory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class FileExplorerTest {
    FileExplorer fileExplorer;

    @BeforeEach
    void setUp() {
        fileExplorer = new FileExplorer();
        fileExplorer.goToDirectory(System.getProperty("user.dir"));
    }

    @AfterEach
    void tearDown() {
        fileExplorer = null;
    }

    @Test
    void goToParent() {
        File current = new File(System.getProperty("user.dir"));
        File parent = current.getParentFile();
        String expectedValue = parent.getAbsolutePath();
        fileExplorer.goToParent();
        String actualValue = fileExplorer.getOngoingDirectory().getCurrentDirectory();
        assertEquals(expectedValue, actualValue);
    }

    @Test
    void goToDirectory() {
        File current = new File(System.getProperty("user.dir"));
        File parent = current.getParentFile();
        File[] list = parent.listFiles();
        File directory = null;
        assert list != null;
        for (File item : list) {
            if (item.isDirectory()) {
                directory = item;
                break;
            }
        }
        if (directory == null) return;
        String expectedValue = directory.getAbsolutePath();
        fileExplorer.goToDirectory(expectedValue);
        String actualValue = fileExplorer.getOngoingDirectory().getCurrentDirectory();
        assertEquals(expectedValue, actualValue);
    }

    @Test
    void getOngoingDirectory() {
        String expectedCurrentDirectory = System.getProperty("user.dir");
        int expectedNumberOfFiles = Objects.requireNonNull((new File(expectedCurrentDirectory)).listFiles()).length;
        OngoingDirectory ongoingDirectory = fileExplorer.getOngoingDirectory();
        String actualCurrentDirectory = ongoingDirectory.getCurrentDirectory();
        int actualNumberOfFiles = ongoingDirectory.getDirectories().size() + ongoingDirectory.getFiles().size();
        assertEquals(expectedCurrentDirectory, actualCurrentDirectory);
        assertEquals(expectedNumberOfFiles, actualNumberOfFiles);

        String rootDirectory = File.listRoots()[0].getAbsolutePath();
        fileExplorer.goToDirectory(rootDirectory);
        fileExplorer.goToParent();
        expectedCurrentDirectory = "";
        ongoingDirectory = fileExplorer.getOngoingDirectory();
        actualCurrentDirectory = ongoingDirectory.getCurrentDirectory();
        assertEquals(expectedCurrentDirectory, actualCurrentDirectory);
        String actualRootDirectory = ongoingDirectory.getDirectories().get(0);
        assertEquals(rootDirectory, actualRootDirectory);
    }


    @Test
    void deleteFile() {
        String currentDirectory = System.getProperty("user.dir");
        File file = new File(currentDirectory + "\\newFileNewFileNewFile.txt");
        assertFalse(file.exists());
        try {
            assertTrue(file.createNewFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(file.exists());
        fileExplorer.deleteFile(file.getAbsolutePath());
        assertFalse(file.exists());
    }

    @Test
    void createDirectory() {
        String filePath = System.getProperty("user.dir") + "\\newDirectoryNewDirectory";
        File directory = new File(filePath);
        assertFalse(directory.exists());
        assertTrue(fileExplorer.createDirectory(filePath));
        assertTrue(directory.exists());
        assertTrue(fileExplorer.deleteFile(directory.getAbsolutePath()));
        assertFalse(directory.exists());
    }

    @Test
    void copyFile() {
        String source = System.getProperty("user.dir") + "\\checkFileABCXYZ.txt";
        String target = System.getProperty("user.dir") + "\\src\\main\\resources\\checkFileABCXYZ.txt";
        File sourceFile = new File(source);
        File targetFile = new File(target);
        assertFalse(sourceFile.exists());
        Path path = null;
        try {
            path = Files.createFile(sourceFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(path);
        assertFalse(targetFile.exists());
        boolean result = fileExplorer.copyFile(source, target);
        assertTrue(result);
        assertTrue(targetFile.exists());
        fileExplorer.deleteFile(target);
        fileExplorer.deleteFile(source);
    }

    @Test
    void rename() {
        String source = System.getProperty("user.dir") + "\\checkFileABCXYZ.txt";
        File sourceFile = new File(source);
        try {
            Files.createFile(sourceFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(sourceFile.exists());
        String newName = System.getProperty("user.dir") + "\\Manuel.txt";
        File newNameFile = new File(newName);
        assertFalse(newNameFile.exists());
        fileExplorer.rename(source, newName);
        assertTrue(newNameFile.exists());
        fileExplorer.deleteFile(newName);
    }

    @Test
    void getOngoingDirectoryJSON() {
        String expectedValue = System.getProperty("user.dir");
        fileExplorer.goToDirectory(expectedValue);
        String json = fileExplorer.getOngoingDirectoryJSON();
        ObjectMapper objectMapper = new ObjectMapper();
        OngoingDirectory ongoingDirectory = null;
        try {
            ongoingDirectory = objectMapper.readValue(json, OngoingDirectory.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String actualValue = ongoingDirectory.getCurrentDirectory();
        assertEquals(expectedValue, actualValue);
    }
}
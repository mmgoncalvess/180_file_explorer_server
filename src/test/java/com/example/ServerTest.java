package com.example;

import com.example.OngoingDirectory;
import com.example.Request;
import com.example.mock.MockClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ServerTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void startServer() {
    }

    @Test
    void caseOngoingDirectory() {
        MockClient mockClient = new MockClient();
        mockClient.setInstruction(Request.ONGOING_DIRECTORY);
        boolean confirmation = mockClient.connect();
        String json = mockClient.getJson();
        assertTrue(confirmation);
        assertTrue(json.length() > 10);
    }

    @Test
    void caseParent() {
        MockClient mockClient = new MockClient();
        mockClient.setPathOne("C:" + File.separator + "Windows");
        mockClient.setInstruction(Request.DIRECTORY);
        boolean confirmation = mockClient.connect();
        String json = mockClient.getJson();
        assertTrue(confirmation);
        ObjectMapper mapper = new ObjectMapper();
        String currentDirectory = "";
        try {
            OngoingDirectory ongoingDirectory = mapper.readValue(json, OngoingDirectory.class);
            currentDirectory = ongoingDirectory.getCurrentDirectory();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File currentDirectoryFile = new File(currentDirectory);
        String expectedParentDirectory = currentDirectoryFile.getParent();
        mockClient = new MockClient();
        mockClient.setInstruction(Request.PARENT);
        confirmation = mockClient.connect();
        json = mockClient.getJson();
        assertTrue(confirmation);
        assertTrue(json.length() > 10);
        try {
            OngoingDirectory ongoingDirectory = mapper.readValue(json, OngoingDirectory.class);
            String actualParentDirectory = ongoingDirectory.getCurrentDirectory();
            assertEquals(expectedParentDirectory, actualParentDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void caseDirectory() {
        MockClient mockClient = new MockClient();
        mockClient.setPathOne("C:" + File.separator + "Windows");
        mockClient.setInstruction(Request.DIRECTORY);
        String expectedDirectoryString = "C:" + File.separator + "Windows";
        mockClient.setPathOne(expectedDirectoryString);
        boolean confirmation = mockClient.connect();
        String json = mockClient.getJson();
        assertTrue(confirmation);
        assertTrue(json.length() > 10);
        ObjectMapper mapper = new ObjectMapper();
        try {
            OngoingDirectory ongoingDirectory = mapper.readValue(json, OngoingDirectory.class);
            String actualDirectoryString = ongoingDirectory.getCurrentDirectory();
            assertEquals(expectedDirectoryString, actualDirectoryString);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    void caseNewDirectory() {
        MockClient mockClient = new MockClient();
        mockClient.setPathOne("C:" + File.separator);
        mockClient.setInstruction(Request.DIRECTORY);
        boolean confirmation = mockClient.connect();
        String json = mockClient.getJson();
        assertTrue(confirmation);
        assertTrue(json.length() > 10);
        ObjectMapper mapper = new ObjectMapper();
        String currentDirectory = "";
        try {
            OngoingDirectory ongoingDirectory = mapper.readValue(json, OngoingDirectory.class);
            currentDirectory = ongoingDirectory.getCurrentDirectory();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mockClient = new MockClient();
        mockClient.setInstruction(Request.NEW_DIRECTORY);
        String path = currentDirectory + File.separator + "ThisIsATestDirectory";
        mockClient.setPathOne(path);
        confirmation = mockClient.connect();
        json = mockClient.getJson();
        assertTrue(confirmation);
        assertTrue(json.length() > 10);
        try {
            OngoingDirectory ongoingDirectory = mapper.readValue(json, OngoingDirectory.class);
            ArrayList<String> directories = ongoingDirectory.getDirectories();
            assertTrue(directories.contains("ThisIsATestDirectory"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mockClient = new MockClient();
        mockClient.setInstruction(Request.DELETE);
        mockClient.setPathOne(path);
        confirmation = mockClient.connect();
        assertTrue(confirmation);
    }

    @Test
    void caseDelete() {
        MockClient mockClient = new MockClient();
        mockClient.setPathOne("C:" + File.separator);
        mockClient.setInstruction(Request.DIRECTORY);
        boolean confirmation = mockClient.connect();
        String json = mockClient.getJson();
        assertTrue(confirmation);
        assertTrue(json.length() > 10);
        ObjectMapper mapper = new ObjectMapper();
        String currentDirectory = "";
        try {
            OngoingDirectory ongoingDirectory = mapper.readValue(json, OngoingDirectory.class);
            currentDirectory = ongoingDirectory.getCurrentDirectory();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mockClient = new MockClient();
        mockClient.setInstruction(Request.NEW_DIRECTORY);
        String path = currentDirectory + File.separator + "ThisIsATestDirectory";
        mockClient.setPathOne(path);
        confirmation = mockClient.connect();
        json = mockClient.getJson();
        assertTrue(confirmation);
        assertTrue(json.length() > 10);
        try {
            OngoingDirectory ongoingDirectory = mapper.readValue(json, OngoingDirectory.class);
            ArrayList<String> directories = ongoingDirectory.getDirectories();
            assertTrue(directories.contains("ThisIsATestDirectory"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mockClient = new MockClient();
        mockClient.setInstruction(Request.DELETE);
        mockClient.setPathOne(path);
        confirmation = mockClient.connect();
        json = mockClient.getJson();
        assertTrue(confirmation);
        assertTrue(json.length() > 10);
    }

    @Test
    void caseRename() {
        MockClient mockClient = new MockClient();
        mockClient.setInstruction(Request.DIRECTORY);
        String pathOne = "C:\\";
        mockClient.setPathOne(pathOne);
        boolean confirmation = mockClient.connect();
        assertTrue(confirmation);

        mockClient = new MockClient();
        mockClient.setInstruction(Request.NEW_DIRECTORY);
        mockClient.setPathOne("C:" + File.separator + "TestDirectory");
        confirmation = mockClient.connect();
        assertTrue(confirmation);
        String json = mockClient.getJson();
        assertTrue(json.length() > 10);

        mockClient = new MockClient();
        mockClient.setInstruction(Request.RENAME);
        pathOne = "C:" + File.separator + "TestDirectory";
        String pathTwo = "C:" + File.separator + "NewTestDirectory";
        mockClient.setPathOne(pathOne);
        mockClient.setPathTwo(pathTwo);
        confirmation = mockClient.connect();
        json = mockClient.getJson();
        assertTrue(confirmation);
        assertTrue(json.length() > 10);
        ObjectMapper mapper = new ObjectMapper();
        try {
            OngoingDirectory ongoingDirectory = mapper.readValue(json, OngoingDirectory.class);
            ArrayList<String> directories = ongoingDirectory.getDirectories();
            assertFalse(directories.contains("TestDirectory"));
            assertTrue(directories.contains("NewTestDirectory"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mockClient = new MockClient();
        mockClient.setInstruction(Request.DELETE);
        mockClient.setPathOne(pathTwo);
        confirmation = mockClient.connect();
        json = mockClient.getJson();
        assertTrue(confirmation);
        assertTrue(json.length() > 10);
    }

    @Test
    void caseCopy() {
        MockClient mockClient = new MockClient();
        mockClient.setInstruction(Request.DIRECTORY);
        String pathOne = "C:\\";
        mockClient.setPathOne(pathOne);
        boolean confirmation = mockClient.connect();
        assertTrue(confirmation);

        mockClient = new MockClient();
        mockClient.setInstruction(Request.NEW_DIRECTORY);
        mockClient.setPathOne("C:" + File.separator + "TestDirectory");
        confirmation = mockClient.connect();
        assertTrue(confirmation);
        String json = mockClient.getJson();
        assertTrue(json.length() > 10);

        mockClient = new MockClient();
        mockClient.setInstruction(Request.COPY);
        pathOne = "C:" + File.separator + "TestDirectory";
        String pathTwo = "C:" + File.separator + "TestDirectoryRenamed";
        mockClient.setPathOne(pathOne);
        mockClient.setPathTwo(pathTwo);
        confirmation = mockClient.connect();
        json = mockClient.getJson();
        assertTrue(confirmation);
        assertTrue(json.length() > 10);
        ObjectMapper mapper = new ObjectMapper();
        try {
            OngoingDirectory ongoingDirectory = mapper.readValue(json, OngoingDirectory.class);
            ArrayList<String> directories = ongoingDirectory.getDirectories();
            assertTrue(directories.contains("TestDirectory"));
            assertTrue(directories.contains("TestDirectoryRenamed"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mockClient = new MockClient();
        mockClient.setInstruction(Request.DELETE);
        mockClient.setPathOne(pathOne);
        confirmation = mockClient.connect();
        json = mockClient.getJson();
        assertTrue(confirmation);
        assertTrue(json.length() > 10);

        mockClient = new MockClient();
        mockClient.setInstruction(Request.DELETE);
        mockClient.setPathOne(pathTwo);
        confirmation = mockClient.connect();
        json = mockClient.getJson();
        assertTrue(confirmation);
        assertTrue(json.length() > 10);
    }

    @Test
    void caseExport() {
        MockClient mockClient = new MockClient();
        mockClient.setInstruction(Request.DIRECTORY);
        String pathOne = "C:\\";
        mockClient.setPathOne(pathOne);
        boolean confirmation = mockClient.connect();
        assertTrue(confirmation);

        mockClient = new MockClient();
        mockClient.setInstruction(Request.NEW_DIRECTORY);
        mockClient.setPathOne("C:" + File.separator + "TestDirectory");
        confirmation = mockClient.connect();
        assertTrue(confirmation);
        String json = mockClient.getJson();
        assertTrue(json.length() > 10);
        File testFile = new File("C:\\TestDirectory\\abcxyz.txt");
        try {
            FileWriter fileWriter = new FileWriter(testFile);
            fileWriter.write("Manuel Sequeira");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(testFile.exists());

        mockClient = new MockClient();
        mockClient.setInstruction(Request.EXPORT);
        pathOne = testFile.getAbsolutePath();
        String pathTwo = "C:\\TestDirectory\\abcxyzEXPORTED.txt";
        mockClient.setPathOne(pathOne);
        mockClient.setPathTwo(pathTwo);
        int size = 0;
        try {
            size = (int) Files.size(testFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mockClient.setSizeFileToSend(size);
        confirmation = mockClient.connect();
        json = mockClient.getJson();
        assertTrue(confirmation);
        assertTrue(json.length() > 10);
        File exportedFile = new File(pathTwo);
        assertTrue(exportedFile.exists());

        mockClient = new MockClient();
        mockClient.setInstruction(Request.DELETE);
        mockClient.setPathOne(pathOne);
        confirmation = mockClient.connect();
        assertTrue(confirmation);

        mockClient = new MockClient();
        mockClient.setInstruction(Request.DELETE);
        mockClient.setPathOne(pathTwo);
        confirmation = mockClient.connect();
        assertTrue(confirmation);

        mockClient = new MockClient();
        mockClient.setInstruction(Request.DELETE);
        mockClient.setPathOne("C:" + File.separator + "TestDirectory");
        confirmation = mockClient.connect();
        assertTrue(confirmation);
        assertFalse(testFile.exists());
        assertFalse(exportedFile.exists());
    }

    @Test
    void caseImport() {
        MockClient mockClient = new MockClient();
        mockClient.setInstruction(Request.DIRECTORY);
        String pathOne = "C:\\";
        mockClient.setPathOne(pathOne);
        boolean confirmation = mockClient.connect();
        assertTrue(confirmation);

        mockClient = new MockClient();
        mockClient.setInstruction(Request.NEW_DIRECTORY);
        mockClient.setPathOne("C:" + File.separator + "TestDirectory");
        confirmation = mockClient.connect();
        assertTrue(confirmation);
        String json = mockClient.getJson();
        assertTrue(json.length() > 10);
        File testFile = new File("C:\\TestDirectory\\abcxyz.txt");
        try {
            FileWriter fileWriter = new FileWriter(testFile);
            fileWriter.write("Manuel Sequeira");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(testFile.exists());

        mockClient = new MockClient();
        mockClient.setInstruction(Request.IMPORT);
        pathOne = testFile.getAbsolutePath();
        String pathTwo = "C:\\TestDirectory\\abcxyzEXPORTED.txt";
        mockClient.setPathOne(pathOne);
        mockClient.setPathTwo(pathTwo);
        int size = 0;
        try {
            size = (int) Files.size(testFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mockClient.setSizeFileToSend(size);
        confirmation = mockClient.connect();
        json = mockClient.getJson();
        assertTrue(confirmation);
        assertTrue(json.length() > 10);
        File exportedFile = new File(pathTwo);
        assertTrue(exportedFile.exists());

        mockClient = new MockClient();
        mockClient.setInstruction(Request.DELETE);
        mockClient.setPathOne(pathOne);
        confirmation = mockClient.connect();
        assertTrue(confirmation);

        mockClient = new MockClient();
        mockClient.setInstruction(Request.DELETE);
        mockClient.setPathOne(pathTwo);
        confirmation = mockClient.connect();
        assertTrue(confirmation);

        mockClient = new MockClient();
        mockClient.setInstruction(Request.DELETE);
        mockClient.setPathOne("C:" + File.separator + "TestDirectory");
        confirmation = mockClient.connect();
        assertTrue(confirmation);
        assertFalse(testFile.exists());
        assertFalse(exportedFile.exists());
    }
}

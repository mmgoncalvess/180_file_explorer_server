package com.example;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;

public class Dispatcher {
    private final Socket clientSocket;
    private final FileExplorer fileExplorer;
    private String instruction = "";
    private String pathOne = "";
    private String pathTwo = "";
    private int size = 0;
    private InputStream inputStream;
    private OutputStream outputStream;
    private boolean confirmation;
    private String json;

    public Dispatcher(Socket connection, FileExplorer fileExplorer) {
        this.clientSocket = connection;
        this.fileExplorer = fileExplorer;
    }

    public void run() {
        receive();
        process();
        send();
        close();
    }

    private void receive() {
        try {
            inputStream = new BufferedInputStream(clientSocket.getInputStream());
            byte[] arrayInstruction = new byte[20];
            byte[] arrayPathOne = new byte[260];
            byte[] arrayPathTwo = new byte[260];
            byte[] arraySize = new byte[10];
            int count;
            count = inputStream.read(arrayInstruction);
            count = count + inputStream.read(arrayPathOne);
            count = count + inputStream.read(arrayPathTwo);
            count = count + inputStream.read(arraySize);
            instruction = new String(arrayInstruction);
            pathOne = new String(arrayPathOne);
            pathTwo = new String(arrayPathTwo);
            ByteBuffer buffer = ByteBuffer.allocate(10);
            buffer.put(arraySize);
            buffer.rewind();
            size = buffer.getInt();
            instruction = instruction.trim();
            pathOne = pathOne.trim();
            pathTwo = pathTwo.trim();

            System.out.println("\n===========================================================");
            System.out.println("Bytes read: " + count);
            System.out.println("Instruction: " + instruction);
            System.out.println("PathOne: " + pathOne);
            System.out.println("PathTwo: " + pathTwo);
            System.out.println("Array size: " + size);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void process() {
        switch (instruction) {
            case "ONGOING_DIRECTORY":
                json = fileExplorer.getOngoingDirectoryJSON();
                confirmation = json.length() != 0;
                break;

            case "PARENT":
                boolean result_parent = fileExplorer.goToParent();
                json = fileExplorer.getOngoingDirectoryJSON();
                confirmation = json.length() != 0 && result_parent;
                break;

            case "DIRECTORY":
                boolean result_directory = fileExplorer.goToDirectory(pathOne);
                json = fileExplorer.getOngoingDirectoryJSON();
                confirmation = json.length() != 0 && result_directory;
                break;

            case "DELETE":
                boolean result_delete = fileExplorer.deleteFile(pathOne);
                json = fileExplorer.getOngoingDirectoryJSON();
                confirmation = json.length() != 0 && result_delete;
                break;

            case "NEW_DIRECTORY":
                boolean result_new = fileExplorer.createDirectory(pathOne);
                json = fileExplorer.getOngoingDirectoryJSON();
                confirmation = json.length() != 0 && result_new;
                break;

            case "RENAME":
                boolean result_rename = fileExplorer.rename(pathOne, pathTwo);
                json = fileExplorer.getOngoingDirectoryJSON();
                confirmation = json.length() != 0 && result_rename;
                break;

            case "COPY":
                boolean result_copy = fileExplorer.copyFile(pathOne, pathTwo);
                System.out.println("result_copy: " + result_copy);
                json = fileExplorer.getOngoingDirectoryJSON();
                confirmation = json.length() != 0 && result_copy;
                System.out.println("Confirmation: " + confirmation);
                break;

            case "EXPORT":
                boolean result_receive = copyReceivedFileFromClient();
                json = fileExplorer.getOngoingDirectoryJSON();
                confirmation = result_receive;
                break;

            case "IMPORT":
                boolean result_send = sendFileToClient();
                json = fileExplorer.getOngoingDirectoryJSON();
                confirmation = result_send;
                break;

            default:
                json = fileExplorer.getOngoingDirectoryJSON();
                confirmation = false;
        }
    }

    private void send() {
        try {
            outputStream = clientSocket.getOutputStream();
            byte byteConfirmation = (byte) (confirmation? 1 : 0);
            byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);

            ByteBuffer byteBuffer = ByteBuffer.allocate(10);
            byteBuffer.putInt(0);
            byteBuffer.rewind();
            byte[] sizeFileToSendNet = byteBuffer.array();
            if (!Objects.equals(instruction, "IMPORT")) outputStream.write(sizeFileToSendNet);

            outputStream.write(byteConfirmation);
            outputStream.write(jsonBytes);
            outputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void close() {
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean copyReceivedFileFromClient() {
        File target = new File(pathTwo);
        boolean result_receive = false;
        if (!target.exists()) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(target);
                int count = 0;
                while (count < size) {
                    fileOutputStream.write(inputStream.read());
                    count++;
                }
                fileOutputStream.close();
                if (count == size) result_receive = true;
                System.out.println(count + " bytes copied");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result_receive;
    }

    private boolean sendFileToClient() {
        try {
            outputStream = clientSocket.getOutputStream();
            File source = new File(pathOne);
            boolean fileExists = source.exists();
            int outputFileSize = (int) Files.size(source.toPath());

            if (fileExists && outputFileSize > 1) {
                ByteBuffer byteBuffer = ByteBuffer.allocate(10);
                byteBuffer.putInt(outputFileSize);
                byteBuffer.rewind();
                byte[] sizeByteArray = byteBuffer.array();
                outputStream.write(sizeByteArray);
                FileInputStream fileInputStream = new FileInputStream(source);
                int data;
                int count = 0;
                while ((data = fileInputStream.read()) != -1) {
                    outputStream.write(data);
                    count++;
                }
                fileInputStream.close();
                System.out.println(count + " bytes sent from Server... !!!");
                if (outputFileSize == count) return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}

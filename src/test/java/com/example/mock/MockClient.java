package com.example.mock;

import com.example.Request;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class MockClient {
    private Request instruction;
    private String pathOne;
    private String pathTwo;
    private int sizeFileToSend;
    private int sizeFileToReceive;
    private boolean confirmation;
    private String json;
    private OutputStream outputStream;
    private InputStream inputStream;
    private boolean once = false;

    public boolean connect() {
        if (once) {
            System.out.println("This connection can be used only once!!!");
            return false;
        }
        once = true;
        Socket connection = NetworkConnection.getConnection();
        if (connection == null) return false;
        try {
            outputStream = connection.getOutputStream();
            inputStream = connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        if (!sendRequest()) return false;
        if (!exportFile()) return false;
        if (!importFile()) return false;
        return receiveResponse();
    }

    private boolean sendRequest() {
        byte[] instructionNet = String.format("%-20s", instruction).getBytes(StandardCharsets.UTF_8);
        byte[] pathOneNet = String.format("%-260s", pathOne).getBytes(StandardCharsets.UTF_8);
        byte[] pathTwoNet = String.format("%-260s", pathTwo).getBytes(StandardCharsets.UTF_8);
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        byteBuffer.putInt(sizeFileToSend);
        byteBuffer.rewind();
        byte[] sizeFileToSendNet = byteBuffer.array();
        try {
            outputStream.write(instructionNet);
            outputStream.write(pathOneNet);
            outputStream.write(pathTwoNet);
            outputStream.write(sizeFileToSendNet);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean exportFile() {
        if (sizeFileToSend == 0) return true;
        File file = new File(pathOne);
        int count = 0;
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            int data;
            while ((data = fileInputStream.read()) != -1) {
                outputStream.write(data);
                count++;
            }
            fileInputStream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return sizeFileToSend == count;
    }

    private boolean importFile() {
        int count = 0;
        try {
            byte[] size = new byte[10];
            int bytesRead = inputStream.read(size);
            if (bytesRead != 10) return false;
            ByteBuffer buffer = ByteBuffer.allocate(10);
            buffer.put(size);
            buffer.rewind();
            sizeFileToReceive = buffer.getInt();
            if (sizeFileToReceive == 0) return true;
            File file = new File(pathTwo);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            while (count < sizeFileToReceive) {
                fileOutputStream.write(inputStream.read());
                count++;
            }
            fileOutputStream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return sizeFileToReceive == count;
    }

    private boolean receiveResponse() {
        try {
            int input = inputStream.read();
            if (input == 0) return false;
            if (input == 1) confirmation = true;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int data;
            while ((data = inputStream.read()) != -1) {
                byteArrayOutputStream.write(data);
            }
            json = byteArrayOutputStream.toString();
            byteArrayOutputStream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return confirmation;
    }

    public void setInstruction(Request instruction) {
        this.instruction = instruction;
    }

    public void setPathOne(String pathOne) {
        this.pathOne = pathOne;
    }

    public void setPathTwo(String pathTwo) {
        this.pathTwo = pathTwo;
    }

    public void setSizeFileToSend(int sizeFileToSend) {
        this.sizeFileToSend = sizeFileToSend;
    }

    public int getSizeFileToReceive() {
        return sizeFileToReceive;
    }

    public void setSizeFileToReceive(int sizeFileToReceive) {
        this.sizeFileToReceive = sizeFileToReceive;
    }

    public String getJson() {
        return json;
    }
}

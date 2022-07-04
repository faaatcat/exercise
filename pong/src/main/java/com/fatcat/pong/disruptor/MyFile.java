package com.fatcat.pong.disruptor;

public class MyFile {
    String fileName;
    String fileContent;

    public MyFile() {
    }

    public MyFile(String fileName, String fileContent) {
        this.fileName = fileName;
        this.fileContent = fileContent;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }

    @Override
    public String toString() {
        return "MyFile{" +
                "fileName='" + fileName + '\'' +
                ", fileContent='" + fileContent + '\'' +
                '}';
    }
}

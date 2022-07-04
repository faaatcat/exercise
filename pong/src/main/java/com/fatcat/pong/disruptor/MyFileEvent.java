package com.fatcat.pong.disruptor;

import javafx.event.Event;

/*
    通过disruptor进行交换的数据类型
 */
public class MyFileEvent{
    private MyFile myFile;

    public MyFile getMyFile() {
        return myFile;
    }

    public void setMyFile(MyFile myFile) {
        this.myFile = myFile;
    }

    @Override
    public String toString() {
        return "MyFileEvent{" +
                "myFile=" + myFile +
                '}';
    }
}

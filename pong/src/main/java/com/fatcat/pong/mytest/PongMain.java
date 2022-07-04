package com.fatcat.pong.mytest;

import com.fatcat.pong.disruptor.MyFileHelper;

public class PongMain {
    public static void main(String[] args) {
        MyFileHelper myFileHelper = new MyFileHelper();
        myFileHelper.init();
        myFileHelper.initWatch();
    }
}

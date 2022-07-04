package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class FileTest {
    private static final SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    private static final SimpleDateFormat sfname = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    @Test
    public void testCreateFile() {
        System.out.println(sf.format(new Date()));
        String path = "src/main/resources/" + sfname.format(new Date()) + ".txt";
        File file = new File(path);
        FileWriter fw = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            fw = new FileWriter(path);
            fw.write("hello");
            fw.close();
            System.out.println("finish");
            String resule = createStr();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("普通文件创建");

        System.out.println(sf.format(new Date()));

    }

    private String createFile() {
        String path = "src/main/resources/" + sfname.format(new Date()) + ".txt";
//        File file = new File(path);
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(path);
            fileWriter.write("hello");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileWriter != null) {
                    fileWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "finish";
    }

    // 一个线程阻塞测试类
    private String createStr() {
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "some thing";
    }

    @Test
    public void testCreateFileByMono(){
        System.out.println(sf.format(new Date()));
        Mono.fromSupplier(()->createFile()).subscribe();
        System.out.println(sf.format(new Date()));
    }


    // flux测试方式
    @Test
    public void testCreateFileByStream() {
        System.out.println(sf.format(new Date()));
        String path = "src/main/resources/" + sfname.format(new Date()) + ".txt";

        try {
            FileWriter fileWriter = new FileWriter(new File(path).getPath());
            System.out.println(sf.format(new Date()));
            Flux.just("hello")
//                    .concatWith(Flux.error(new Exception()))
                    .subscribe((t) -> {
                        try {
                            fileWriter.write(t);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (fileWriter != null) {
                                    fileWriter.close();
                                }
                                System.out.println("finish");
//                        Thread.sleep(5000);
                                Mono<String> stringMono = Mono.fromSupplier(() -> createStr());
//                        String resule=createStr();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(sf.format(new Date()));

    }
}
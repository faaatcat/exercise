package com.fatcat.pong.disruptor;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

@Slf4j
public class MyFileHelper {

    private static final int RING_BUFFER_SIZE = 1024;

    private String localPath = "/Users/fatcat/code/test_resource/";

    private Disruptor<MyFileEvent> disruptor;

    private MyFileEventProducer producer;

    /**
     * 统计消息总数
     */
    private final AtomicLong eventCount = new AtomicLong();

    //    @PostConstruct
    public void init() {
        // 创建缓冲池
        // ExecutorService executorService = Executors.newCachedThreadPool();
        // 创建工厂
        MyFileEventFactory factory = new MyFileEventFactory();

        // new Disruptor<>(new MyFileEventFactory(),ringBufferSize,executorService, ProducerType.SINGLE,new YieldingWaitStrategy());
        disruptor = new Disruptor<>(new MyFileEventFactory(), RING_BUFFER_SIZE, new CustomizableThreadFactory("event-handler-"), ProducerType.SINGLE, new YieldingWaitStrategy());

        // 这样每次处理事件时，都会将已经处理事件的总数打印出来
        Consumer<?> eventCountPrinter = new Consumer<Object>() {
            @Override
            public void accept(Object o) {
                long count = eventCount.incrementAndGet();
                log.info("receive [{}] event", count);
            }
        };
        // 连接消费事件方法
        disruptor.handleEventsWith(new MyFileEventHandler(eventCountPrinter));

        // 启动
        disruptor.start();
        // 发布事件
        RingBuffer<MyFileEvent> ringBuffer = disruptor.getRingBuffer();
        producer = new MyFileEventProducer(ringBuffer);

//        producer.onData(new MyFile(String.valueOf(i), String.valueOf(i)));

//        disruptor.shutdown();
    }


    public void publish(MyFile myFile) {
        producer.onData(myFile);
    }

    public long eventCount() {
        return eventCount.get();
    }

    /**
     * 监听文件
     */
    public void initWatch() {
        try {

            //创建一个监听服务
            WatchService service = FileSystems.getDefault().newWatchService();
            //设置路径
            Path path = Paths.get(localPath);
            //注册监听器
            path.register(service, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);

            WatchKey watchKey;

            //使用dowhile
            do {
                //获取一个watch key
                watchKey = service.take();
                for (WatchEvent<?> event : watchKey.pollEvents()) {
                    //如果时间列表不为空，打印事件内容
                    WatchEvent.Kind<?> kind = event.kind();
                    Path eventPath = (Path) event.context();
                    MyFile myFile = readFile(eventPath.toString());
                    // System.out.println(eventPath+":"+kind+":"+eventPath);
                    this.publish(myFile);
                }
//                System.out.println("目录内容发生改变");

            } while (watchKey.reset());
        } catch (Exception e) {
            e.printStackTrace();

        }


        // 1、通过FileSystems.getDefault().newWatchService()创建一个监听服务；
        // 2、设置路径；
        // 3、对目录注册一个监听器；
        // 4、之后进入循环，等待watch key；
        // 5、此时如果有事件发生可通过watchkey的pollevent()方法获取；
        // 6、之后可以对event处理；
    }

    public MyFile readFile(String fileName) {
        MyFile myFile = new MyFile();
        myFile.setFileName(fileName);
        List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get(localPath + fileName),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        myFile.setFileContent(lines.toString());
        return myFile;
    }
}

package com.fatcat.pong.service.impl;

import com.fatcat.pong.disruptor.MyFile;
import com.fatcat.pong.service.BasicEventService;
import com.fatcat.pong.service.WatchFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

@Service
@Slf4j
public class WatchFileServiceImpl implements WatchFileService {
    @Value("${pong.path}")
    private String localPath;

    @Autowired
    private BasicEventService basicEventService;

    @Override
    public void initWatch() {
        try{

            //创建一个监听服务
            WatchService service= FileSystems.getDefault().newWatchService();
            //设置路径
            Path path= Paths.get(localPath);
            //注册监听器
            path.register(service, StandardWatchEventKinds.ENTRY_CREATE,StandardWatchEventKinds.ENTRY_DELETE,StandardWatchEventKinds.ENTRY_MODIFY);

            WatchKey watchKey;

            //使用dowhile
            do{
                //获取一个watch key
                watchKey=service.take();
                for(WatchEvent<?> event:watchKey.pollEvents()){
                    //如果时间列表不为空，打印事件内容
                    WatchEvent.Kind<?> kind=event.kind();
                    Path eventPath=(Path)event.context();
                    System.out.println(eventPath.toString());
                    MyFile myFile = readFile(eventPath.toString());
//                    System.out.println(eventPath+":"+kind+":"+eventPath);
                    basicEventService.publish(myFile);
                }
                System.out.println("目录内容发生改变");

            }while(watchKey.reset());
        }catch(Exception e){
            e.printStackTrace();

        }

//        readFile("2022-07-04-16-09-45-0102008.txt");

        // 1、通过FileSystems.getDefault().newWatchService()创建一个监听服务；
        // 2、设置路径；
        // 3、对目录注册一个监听器；
        // 4、之后进入循环，等待watch key；
        // 5、此时如果有事件发生可通过watchkey的pollevent()方法获取；
        // 6、之后可以对event处理；
    }

    @Override
    public MyFile readFile(String fileName) {
        MyFile myFile=new MyFile();
        myFile.setFileName(fileName);
//        File file=new File(path);
        List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get(localPath+fileName),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        myFile.setFileContent(lines.toString());

//        System.out.println(myFile);
        return myFile;
    }
}

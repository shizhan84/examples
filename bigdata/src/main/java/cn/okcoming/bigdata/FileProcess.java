package cn.okcoming.bigdata;

import org.apache.commons.lang.math.NumberUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

/**
 * 文件内容批量处理 支持多线程并发
 * 一个线程可以处理多个文件，但一个文件只被一个线程处理，避免了资源文件的读写冲突，这样不容易出错
 */
public class FileProcess {

    public static void main(String[] args){
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("请输入需要处理的文件路径（模板 c://tmp//filename-block-{}.data ):");
            String fileTemplate = br.readLine();
            System.out.print("请输入开启线程总数量(直接回车则等于cpu个数):");
            int nThreads = NumberUtils.toInt(br.readLine(),Runtime.getRuntime().availableProcessors());
            System.out.print("请输入文件总数量:");
            int count = Integer.parseInt(br.readLine());
            Queue<String> filenames = new LinkedBlockingQueue(count);
            for(int i=0;i<count;i++){
                String name = fileTemplate.replace("{}",String.valueOf(i));
                System.out.println(name);
                filenames.add(name);
            }
            System.out.println("确认当前需要处理的文件列表");
            br.readLine();
            //ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            ExecutorService executorService = new ThreadPoolExecutor(nThreads,nThreads, 10000L, TimeUnit.SECONDS,
                    new LinkedBlockingQueue(count),
                    r -> {
                        Thread t = new Thread(r);
                        return t;
                    });
            String node;
            while((node = filenames.poll())!=null){
                final String finalNode = node;
                executorService.execute(() -> {
                    try {
                        processChunk(finalNode);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
            executorService.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static AtomicLong monitor_1 = new AtomicLong(0);
    private static AtomicLong monitor_2 = new AtomicLong(0);
    private static AtomicLong monitor_3 = new AtomicLong(0);
    private static AtomicLong monitor_All = new AtomicLong(0);
    public static void processChunk(String filename) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(filename);
        Pattern pattern = Pattern.compile("(\\d{11})");
        while(fileInputStream.available() > 0 ){
            int block = 5 * 1024 * 1024;
            byte[] read = new byte[block];
            byte[] dst;
            int length = fileInputStream.read(read);
            if(length == block){ //还没有读到文件末尾的数据块需要补齐到换行符才能结束
                byte[] second = new byte[600];//稍微大一点 避免读不到换行符引发bug 这里假定以回车结束的一个完整语句不超过数组指定长度
                int data;
                int i = 0;
                while( (data = fileInputStream.read()) !=  10){ //回车符
                    if(data == -1){//表示到文件末尾了
                        break;
                    }
                    second[i++] = (byte)data;
                }
                dst = new byte[length+i];
                System.arraycopy(read,0,dst,0,length);
                System.arraycopy(second,0,dst,length,i);
            }else{ //截取前面非\u0000的字符串
                dst = new byte[length];
                System.arraycopy(read,0,dst,0,length);
            }
            String temp = new String(dst,"utf8");
            String[] records = temp.split("\n");
            for(String record : records){
                if(record.indexOf("车贷")>-1){
                    monitor_1.incrementAndGet();
                    //System.out.println(monitor.incrementAndGet() + " file: "+ filename + " : "+ record);
                }
                monitor_All.incrementAndGet();
            }
            //提取策略模拟
            /*Matcher matcher = pattern.matcher(temp);
            while(matcher.find()){
                String mobile = matcher.group(1);
            }*/
        }
        System.out.println(monitor_1.get());
        System.out.println(monitor_All.get());
        fileInputStream.close();
    }

    /*public static void processChannelByLine(String filename) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(filename,"r");
        FileChannel fileChannel = raf.getChannel();
        long total = fileChannel.size();

        Pattern pattern = Pattern.compile("(\\d{11})");
        long index = 0;
        while(index < total ){
            long end = indexOf(fileChannel,index,new byte[]{10}) + 1;
            ByteBuffer buffer = ByteBuffer.allocate((int)(end - index));
            fileChannel.read(buffer);
            String temp = new String(buffer.array(),"utf8").trim();
            //提取策略
            *//*Matcher matcher = pattern.matcher(temp);
            if(matcher.find()){
                System.out.println(matcher.group(1) + "--"+filename+"-"+monitor.getAndIncrement());
            }*//*
            System.out.println(filename+"-"+monitor.getAndIncrement());
            index = end ;
        }
    }*/

}

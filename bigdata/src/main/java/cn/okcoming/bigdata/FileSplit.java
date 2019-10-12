package cn.okcoming.bigdata;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;

/**
 * 超大文件切割为小文件
 */
public class FileSplit {
    public static void main(String[] args){
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.print("请输入需要处理的文件路径:");
            String filename = br.readLine();
            System.out.print("请输入分割后文件大小(MB):");
            String size = br.readLine();
            splitFile(filename,Integer.parseInt(size) * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //3,841,142,065
    //3,841,142,065
    public static void splitFile(String filename,long blockSize) throws IOException {
        long sss = System.currentTimeMillis();
        RandomAccessFile raf = new RandomAccessFile(filename,"r");
        FileChannel fileChannel = raf.getChannel();
        long total = fileChannel.size();
        System.out.println(total);
        long block = total / blockSize;
        long begin = 0;
        for(int i= 0;i<= block;i++){
            WritableByteChannel write = Channels.newChannel(new FileOutputStream(filename+"-block-"+i+".data"));
            long end = (i + 1) * blockSize  > total ? total : (i + 1) * blockSize;
            //需要读完整 读到下一个回车符为止
            long index = indexOf(fileChannel,end - 1,new byte[]{10});
            end = index == total ? total : index + 1;
            System.out.println("begin:"+ begin+" -- end:"+end);
            fileChannel.transferTo(begin,end - begin ,write);
            begin = end;
            write.close();
            if(total == end){
                break;
            }
        }
        System.out.println(System.currentTimeMillis()-sss);
    }
    /**
     * 返回fileChannel中从指定位置开始查找匹配字节数组的第一个位置，
     * 未找到则返回 总的数组大小
     * @param fileChannel
     * @param position 指定开始的位置
     * @param search 匹配的字节数组
     * @return
     */
    public static long indexOf(FileChannel fileChannel,long position, byte[] search) throws IOException {
        long total = fileChannel.size();
        ByteBuffer buffer = ByteBuffer.allocate(search.length);
        long index = position;
        while (index < total){
            buffer.position(0);
            fileChannel.read(buffer,index);
            if(Arrays.equals(search,buffer.array())){
                return index;
            }
            index++;
        }
        return total;
    }


}

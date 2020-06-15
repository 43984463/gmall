package com.sherlock.gmall.product;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * @auther Sherlock
 * @date 2020/6/13 17:59
 * @Description:
 */
public class readFileName {
    public static void main(String[] args) throws IOException {
        //PrintAllFileName();
        File file = new File("C:\\Users\\Administrator\\Desktop\\title.txt");
        BufferedReader reader = null;
        for (int i = 156; i < 250; i++) {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                if (Integer.parseInt(tempString.substring(0,3)) == i-1) {
                    System.out.println(tempString);
                    new File("F:\\新建文件夹\\格式工厂混流 "+i+".mp4").renameTo(new File("F:\\新建文件夹\\"+tempString+".mp4"));
                }
                line++;

            }
        //new File("H:\\tem\\157.mp4").renameTo(new File("H:\\tem\\157.3gp"));
        //copyFileUsingFileChannels(new File("H:\\学习视频\\968476100\\"+i+"\\64\\video.m4s"), new File("H:\\tem\\"+i+".mp4"));
            //copyFileUsingFileChannels(new File("H:\\学习视频\\968476100\\"+i+"\\64\\audio.m4s"), new File("H:\\tem\\"+i+".mp3"));
            //copyFileUsingFileChannels(new File("H:\\tem\\157.mp4"), new File("H:\\tem\\157.3gp"));
           // new File("H:\\tem\\157.mp4").renameTo(new File("H:\\tem\\157.3gp"));
            //System.out.println(i);
            //System.out.println(new File("F:\\FFOutput\\格式工厂混流 video~"+i+".mp4").getName());
        }
        System.out.println("打印完成");
    }

    private static void PrintAllFileName() throws IOException {
        for (int i = 102; i < 409; i++) {
            File file = new File("H:\\学习视频\\968476100\\"+i+"\\entry.json");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                System.out.println(tempString.substring(tempString.indexOf("download_subtitle")+51,tempString.indexOf("\"}}")));
                line++;

            }
            reader.close();
        }
    }

    private static void copyFileUsingFileChannels(File source, File dest) throws IOException {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            inputChannel.close();
            outputChannel.close();
        }
    }
}

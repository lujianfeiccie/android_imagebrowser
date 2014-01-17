package com.example.imagebrowser;

import java.io.File;

import android.content.Context;

/*
版权所有：版权所有(C)2014，固派软件
文件名称：com.goopai.shenma.whfUtils.FileCache.java
系统编号：
系统名称：Shenma
模块编号：
模块名称：
设计文档：
创建日期：2014-1-3 下午3:38:54
作 者：万海峰
内容摘要：
类中的代码包括三个区段：类变量区、类属性区、类方法区。
文件调用:
 */
public class FileCache {  
	  
    private File cacheDir;  
  
    public FileCache(Context context) {  
        // 如果有SD卡则在SD卡中建一个LazyList的目录存放缓存的图片  
        // 没有SD卡就放在系统的缓存目录中  
        if (android.os.Environment.getExternalStorageState().equals(  
                android.os.Environment.MEDIA_MOUNTED))  
            cacheDir = new File(  
                    android.os.Environment.getExternalStorageDirectory(),  
                    "LazyList");  
        else  
            cacheDir = context.getCacheDir();  
        if (!cacheDir.exists())  
            cacheDir.mkdirs();  
    }  
  
    public File getFile(String url) {  
        // 将url的hashCode作为缓存的文件名  
        String filename = String.valueOf(url.hashCode());  
        // Another possible solution  
        // String filename = URLEncoder.encode(url);  
        File f = new File(cacheDir, filename);  
        return f;  
  
    }  
  
    public void clear() {  
        File[] files = cacheDir.listFiles();  
        if (files == null)  
            return;  
        for (File f : files)  
            f.delete();  
    }  
  
} 


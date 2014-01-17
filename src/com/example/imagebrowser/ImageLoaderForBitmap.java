package com.example.imagebrowser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

/*
版权所有：版权所有(C)2014，固派软件
文件名称：com.goopai.shenma.whfUtils.ImageLoader.java
系统编号：
系统名称：Shenma
模块编号：
模块名称：
设计文档：
创建日期：2014-1-3 下午3:37:54
作 者：万海峰
内容摘要：
类中的代码包括三个区段：类变量区、类属性区、类方法区。
文件调用:
 */
public class ImageLoaderForBitmap {  
	  
    //MemoryCache memoryCache = new MemoryCache();  
	Map<MyUrl,Bitmap> memoryCache = new HashMap<ImageLoaderForBitmap.MyUrl, Bitmap>();
    FileCache fileCache;  
    // 线程池  
    ExecutorService executorService;  
    boolean isBackground=false;
  
    Context context = null;
    
    OnBitmapReceiveListener listener;
    public ImageLoaderForBitmap(Context context) {  
        fileCache = new FileCache(context);  
        executorService = Executors.newFixedThreadPool(5);  
        this.context = context;
    }  
  
    // 当进入listview时默认的图片，可换成你自己的默认图片  
    final int stub_id = R.drawable.ic_launcher;  
    // 最主要的方法  
    public void DisplayImage(MyUrl url,OnBitmapReceiveListener listener) {  
    	this.listener  = listener;
        // 先从内存缓存中查找  
        Bitmap bitmap = memoryCache.get(url);  
        if (bitmap != null){  
        		listener.onBitmapReceive(bitmap,url.index);
        }else {  
            // 若没有的话则开启新线程加载图片  
        	queuePhoto(url);
        }  
    }  
    public static class MyUrl{
    	public String url;
    	public int index;
    }
    private void queuePhoto(MyUrl url) {  
        executorService.submit(new PhotosLoader(url));  
    }  
  
    private Bitmap getBitmap(String url) {  
        File f = fileCache.getFile(url);  
  
        // 先从文件缓存中查找是否有  
        Bitmap b = decodeFile(f);  
        if (b != null)  
            return b;  
  
        // 最后从指定的url中下载图片  
        try {  
            Bitmap bitmap = null;  
            URL imageUrl = new URL(url);  
            HttpURLConnection conn = (HttpURLConnection) imageUrl  
                    .openConnection();  
            conn.setConnectTimeout(30000);  
            conn.setReadTimeout(30000);  
            conn.setInstanceFollowRedirects(true);  
            InputStream is = conn.getInputStream();  
            OutputStream os = new FileOutputStream(f);  
            CopyStream(is, os);  
            os.close();  
            bitmap = decodeFile(f);  
            return bitmap;  
        } catch (Exception ex) {  
            ex.printStackTrace();  
            return null;  
        }  
    }  
  
    // decode这个图片并且按比例缩放以减少内存消耗，虚拟机对每张图片的缓存大小也是有限制的  
    private Bitmap decodeFile(File f) {  
        try {  
            // decode image size  
            BitmapFactory.Options o = new BitmapFactory.Options();  
            o.inJustDecodeBounds = true;  
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);  
  
            // Find the correct scale value. It should be the power of 2.  
            final int REQUIRED_SIZE = 70;  
            int width_tmp = o.outWidth, height_tmp = o.outHeight;  
            int scale = 1;  
            while (true) {  
                if (width_tmp / 2 < REQUIRED_SIZE  
                        || height_tmp / 2 < REQUIRED_SIZE)  
                    break;  
                width_tmp /= 2;  
                height_tmp /= 2;  
                scale *= 2;  
            }  
  
            // decode with inSampleSize  
            BitmapFactory.Options o2 = new BitmapFactory.Options();  
            o2.inSampleSize = scale;  
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);  
        } catch (FileNotFoundException e) {  
        }  
        return null;  
    }  
  
  
    class PhotosLoader implements Runnable {  
    	MyUrl url;
        PhotosLoader(MyUrl url) {  
            this.url = url;  
        }  
  
        @Override  
        public void run() {  
            Bitmap bmp = getBitmap(url.url);  
            memoryCache.put(url, bmp);  
            listener.onBitmapReceive(bmp,url.index);
        }  
    }  
  
  
    public void clearCache() {  
        memoryCache.clear();  
        fileCache.clear();  
    }  
  
    public static void CopyStream(InputStream is, OutputStream os) {  
        final int buffer_size = 1024;  
        try {  
            byte[] bytes = new byte[buffer_size];  
            for (;;) {  
                int count = is.read(bytes, 0, buffer_size);  
                if (count == -1)  
                    break;  
                os.write(bytes, 0, count);  
            }  
        } catch (Exception ex) {  
        }  
    }  
    public interface OnBitmapReceiveListener{
    	   void onBitmapReceive(Bitmap bitmap,int index);
    }
}


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
��Ȩ���У���Ȩ����(C)2014���������
�ļ����ƣ�com.goopai.shenma.whfUtils.ImageLoader.java
ϵͳ��ţ�
ϵͳ���ƣ�Shenma
ģ���ţ�
ģ�����ƣ�
����ĵ���
�������ڣ�2014-1-3 ����3:37:54
�� �ߣ��򺣷�
����ժҪ��
���еĴ�������������Σ���������������������෽������
�ļ�����:
 */
public class ImageLoaderForBitmap {  
	  
    //MemoryCache memoryCache = new MemoryCache();  
	Map<MyUrl,Bitmap> memoryCache = new HashMap<ImageLoaderForBitmap.MyUrl, Bitmap>();
    FileCache fileCache;  
    // �̳߳�  
    ExecutorService executorService;  
    boolean isBackground=false;
  
    Context context = null;
    
    OnBitmapReceiveListener listener;
    public ImageLoaderForBitmap(Context context) {  
        fileCache = new FileCache(context);  
        executorService = Executors.newFixedThreadPool(5);  
        this.context = context;
    }  
  
    // ������listviewʱĬ�ϵ�ͼƬ���ɻ������Լ���Ĭ��ͼƬ  
    final int stub_id = R.drawable.ic_launcher;  
    // ����Ҫ�ķ���  
    public void DisplayImage(MyUrl url,OnBitmapReceiveListener listener) {  
    	this.listener  = listener;
        // �ȴ��ڴ滺���в���  
        Bitmap bitmap = memoryCache.get(url);  
        if (bitmap != null){  
        		listener.onBitmapReceive(bitmap,url.index);
        }else {  
            // ��û�еĻ��������̼߳���ͼƬ  
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
  
        // �ȴ��ļ������в����Ƿ���  
        Bitmap b = decodeFile(f);  
        if (b != null)  
            return b;  
  
        // ����ָ����url������ͼƬ  
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
  
    // decode���ͼƬ���Ұ����������Լ����ڴ����ģ��������ÿ��ͼƬ�Ļ����СҲ�������Ƶ�  
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


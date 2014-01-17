package com.example.imagebrowser;


import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;

public class ImageHelper {
	
	
	/**
	 * 放大缩小图片
	 * @param bitmap
	 * @param w
	 * @param h
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap,int w,int h){
	if(bitmap==null) return null;
	int width = bitmap.getWidth(); 
	int height = bitmap.getHeight(); 
	Matrix matrix = new Matrix(); 
	float scaleWidht = ((float)w / width); 
	float scaleHeight = ((float)h / height); 
	matrix.postScale(scaleWidht, scaleHeight); 
	Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true); 
	return newbmp; 
	} 
	
	
	/**
	 * 将Drawable转化为Bitmap
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable){ 
	int width = drawable.getIntrinsicWidth(); 
	int height = drawable.getIntrinsicHeight(); 
	Bitmap bitmap = Bitmap.createBitmap(width, height, 
	drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 
	: Bitmap.Config.RGB_565); 
	Canvas canvas = new Canvas(bitmap); 
	drawable.setBounds(0,0,width,height); 
	drawable.draw(canvas); 
	return bitmap;

	}

	
	/**
	 * 获得圆角图片的方法
	 * @param bitmap  输入的源bitmap
	 * @param roundPx  圆角的角度大小
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap,float roundPx){

	Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap 
	.getHeight(), Config.ARGB_8888); 
	Canvas canvas = new Canvas(output);

	final int color = 0xff424242; 
	final Paint paint = new Paint(); 
	final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()); 
	final RectF rectF = new RectF(rect);

	paint.setAntiAlias(true); 
	canvas.drawARGB(0, 0, 0, 0); 
	paint.setColor(color); 
	canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

	paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN)); 
	canvas.drawBitmap(bitmap, rect, rect, paint);

	return output; 
	} 
	

	/**获得带倒影的图片方法
	 * @param bitmap
	 * @return
	 */
	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap){ 
	final int reflectionGap = 4; 
	int width = bitmap.getWidth(); 
	int height = bitmap.getHeight();

	Matrix matrix = new Matrix(); 
	matrix.preScale(1, -1);

	Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 
	0, height/2, width, height/2, matrix, false);

	Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height/2), Config.ARGB_8888);

	Canvas canvas = new Canvas(bitmapWithReflection); 
	canvas.drawBitmap(bitmap, 0, 0, null); 
	Paint deafalutPaint = new Paint(); 
	canvas.drawRect(0, height,width,height + reflectionGap, 
	deafalutPaint);

	canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

	Paint paint = new Paint(); 
	LinearGradient shader = new LinearGradient(0, 
	bitmap.getHeight(), 0, bitmapWithReflection.getHeight() 
	+ reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP); 
	paint.setShader(shader); 
	// Set the Transfer mode to be porter duff and destination in 
	paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN)); 
	// Draw a rectangle using the paint with our linear gradient 
	canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() 
	+ reflectionGap, paint);

	return bitmapWithReflection; 
	}
	
	/** 
	* 图片透明度处理 
	* 
	* @param sourceImg 
	*            原始图片 
	* @param number 
	*            透明度 
	* @return 
	*/  
	public static Bitmap setAlpha(Bitmap sourceImg, int number) {  
	int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];  
	sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0,sourceImg.getWidth(), sourceImg.getHeight());// 获得图片的ARGB值  
	number = number * 255 / 100;  
	for (int i = 0; i < argb.length; i++) {  
	argb[i]= (number << 24) | (argb[i] & 0x00FFFFFF);// 修改最高2位的值  
	}  
	sourceImg = Bitmap.createBitmap(argb, sourceImg.getWidth(),
			sourceImg.getHeight(), Config.ARGB_8888);  
	return sourceImg;  
	} 
	
	
	/** 
     * 将彩色图转换为灰度图 
     * @param img 位图 
     * @return  返回转换好的位图 
     */  
    public static Bitmap convertGreyImg(Bitmap img) {  
        int width = img.getWidth();         //获取位图的宽  
        int height = img.getHeight();       //获取位图的高  
          
        int []pixels = new int[width * height]; //通过位图的大小创建像素点数组  
          
        img.getPixels(pixels, 0, width, 0, 0, width, height);  
        int alpha = 0xFF << 24;   
        for(int i = 0; i < height; i++)  {  
            for(int j = 0; j < width; j++) {  
                int grey = pixels[width * i + j];  
                  
                int red = ((grey  & 0x00FF0000 ) >> 16);  
                int green = ((grey & 0x0000FF00) >> 8);  
                int blue = (grey & 0x000000FF);  
                  
                grey = (int)((float) red * 0.3 + (float)green * 0.59 + (float)blue * 0.11);  
                grey = alpha | (grey << 16) | (grey << 8) | grey;  
                pixels[width * i + j] = grey;  
            }  
        }  
        Bitmap result = Bitmap.createBitmap(width, height, Config.RGB_565);  
        result.setPixels(pixels, 0, width, 0, 0, width, height);  
        return result;  
    }
    
    /**
     * 将base64字符串编码成图片
     * @return
     */
    public static Bitmap StringToBitmap(String decoded){
       Bitmap bitmap=null;
       try {
    	    byte[]bitmapArray;
    	    bitmapArray=Base64.decode(decoded, Base64.DEFAULT);
    	    bitmap=BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
    	} catch (Exception e) {
    	    e.printStackTrace();
    	}
       return bitmap;
    }
    
    /**
     * 将图片转换成base64编码后的字符串
     * @return
     */
    public static String BitmapToString(Bitmap bitmap){
    	String encode=null;
    	ByteArrayOutputStream bStream=new ByteArrayOutputStream();
    	bitmap.compress(CompressFormat.PNG,10,bStream);
        byte[]bytes=bStream.toByteArray();
        encode=Base64.encodeToString(bytes,Base64.DEFAULT);
    	return encode;
    }
    /**
     * 将Bitmap转化为字节数组
     * @param bm
     * @return
     */
	public static byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);       
		return baos.toByteArray();   
	}
	/**
	 * 将Byte转换成Bitmap
	 * @param b
	 * @return
	 */
	public static Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
	        return BitmapFactory.decodeByteArray(b, 0, b.length);
	     } else {
	        return null;
	  }
	}
	//通过url取得bitmap
		public static Bitmap getImage(String Url) throws Exception {
			URL url;
			byte[] b=null;
			try {
				url = new URL(Url);   //设置URL
				HttpURLConnection con;
			    con = (HttpURLConnection)url.openConnection();  //打开连接
				con.setRequestMethod("GET"); //设置请求方法
				//设置连接超时时间为5s
				con.setConnectTimeout(5000);
				InputStream in=con.getInputStream();  //取得字节输入流
			    b=readInputStream(in);
			  //二进制数据生成位图
		        Bitmap bit=BitmapFactory.decodeByteArray(b, 0, b.length);
				return bit;  //返回byte数组
			} catch (Exception e) {
				
				e.printStackTrace();
			}
			return null;  //返回byte数组
		}
		private static byte[] readInputStream(InputStream in) throws Exception{
			int len=0;
			byte buf[]=new byte[1024];
			ByteArrayOutputStream out=new ByteArrayOutputStream();
			while((len=in.read(buf))!=-1){
				out.write(buf,0,len);  //把数据写入内存
			}
			out.close();  //关闭内存输出流
			return out.toByteArray(); //把内存输出流转换成byte数组
		}
		/** 
		* 转换图片成圆形 
		* @param bitmap 传入Bitmap对象 
		* @return 
		*/ 
		public static Bitmap getRoundBitmap(Bitmap bitmap,int width) { 
			return toRoundBitmap(bitmap,width,2);
		}
		/**
		  * 转换图片成圆形
		  * @param bitmap
		  * @param width
		  * @param ring_width 外边框宽度
		  * @return
		  */
		 private static Bitmap toRoundBitmap(Bitmap bitmap,int width,int ring_width) {
		  float radius; //半径
		  float left,top; //左上角
		  float right,bottom; //右下角
		  left = ring_width;
		  top = ring_width;
		  bottom = width-ring_width;
		  right = width-ring_width;
		  radius = width / 2 -ring_width;//半径等于原半径去除圆环的宽度
		 
		  Bitmap output = Bitmap.createBitmap(width, width, Config.ARGB_8888);
		  Canvas canvas = new Canvas(output);
		  final Paint paint = new Paint();
		  final RectF rectF = new RectF(left, //矩形绘制区域
		    top,
		    right,
		    bottom);
		  canvas.drawRoundRect(rectF, //通过矩形区域绘制内接圆
		    radius,
		    radius,
		    paint);
		  paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN)); //显示bitmap在圆形区域中的交集部分
		  canvas.drawBitmap(bitmap, 0, 0, paint);
		 return withWhiteCircle(output,radius+ring_width);
		 }
		 
		 /**
		  * 加入白边
		  * @param bitmap
		  * @param radius
		  * @return
		  */
		 private static Bitmap withWhiteCircle(Bitmap bitmap,float radius){
		  return withCircle(bitmap,radius,Color.WHITE);
		 }
		 
		 private static Bitmap withCircle(Bitmap bitmap,float radius,int color){
		  float width = radius * 2;
		  float height = radius * 2;
		 
		  float left = 0;
		  float top = 0;
		  float right = width;
		  float bottom = height;
		 
		  Bitmap output = Bitmap.createBitmap((int)width,
		    (int)height,
		    Config.ARGB_8888);
		  Canvas canvas = new Canvas(output);
		  final Paint paint = new Paint();
		  paint.setAntiAlias(true);
		  paint.setColor(color);
		  final RectF rectF = new RectF(left, top, right,bottom);
		  canvas.drawRoundRect(rectF, radius, radius, paint);
		  paint.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));//显示bitmap在output中的补集区域
		  canvas.drawBitmap(bitmap, 0, 0, paint);
		  return output;
		 }
		 
		 public static Drawable BitmapToDrawable(Bitmap bitmap){
				Drawable drawable = null;
				BitmapDrawable bd= new BitmapDrawable(bitmap);
				drawable = bd;
				return drawable;
		}
		 
		 
		 public static Bitmap getBitmapFromPath(String path, int w, int h) {
			     BitmapFactory.Options opts = new BitmapFactory.Options();
			     // 设置为ture只获取图片大小
			     opts.inJustDecodeBounds = true;
			     opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
			     // 返回为空
			     BitmapFactory.decodeFile(path, opts);
			     int width = opts.outWidth;
			     int height = opts.outHeight;
			     float scaleWidth = 0.f, scaleHeight = 0.f;
			     if (width > w || height > h) {
			        // 缩放
			        scaleWidth = ((float) width) / w;
			        scaleHeight = ((float) height) / h;
			     }
			      opts.inJustDecodeBounds = false;
			      float scale = Math.max(scaleWidth, scaleHeight);
			      opts.inSampleSize = (int)scale;
			      //WeakReference<Bitmap> weak = new WeakReference<Bitmap>(BitmapFactory.decodeFile(path, opts));
			      Bitmap result = BitmapFactory.decodeFile(path, opts);
			      return result;
//			    return Bitmap.createScaledBitmap(weak.get(), w, h, true);
		}
}

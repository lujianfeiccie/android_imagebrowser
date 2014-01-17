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
	 * �Ŵ���СͼƬ
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
	 * ��Drawableת��ΪBitmap
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
	 * ���Բ��ͼƬ�ķ���
	 * @param bitmap  �����Դbitmap
	 * @param roundPx  Բ�ǵĽǶȴ�С
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
	

	/**��ô���Ӱ��ͼƬ����
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
	* ͼƬ͸���ȴ��� 
	* 
	* @param sourceImg 
	*            ԭʼͼƬ 
	* @param number 
	*            ͸���� 
	* @return 
	*/  
	public static Bitmap setAlpha(Bitmap sourceImg, int number) {  
	int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];  
	sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0,sourceImg.getWidth(), sourceImg.getHeight());// ���ͼƬ��ARGBֵ  
	number = number * 255 / 100;  
	for (int i = 0; i < argb.length; i++) {  
	argb[i]= (number << 24) | (argb[i] & 0x00FFFFFF);// �޸����2λ��ֵ  
	}  
	sourceImg = Bitmap.createBitmap(argb, sourceImg.getWidth(),
			sourceImg.getHeight(), Config.ARGB_8888);  
	return sourceImg;  
	} 
	
	
	/** 
     * ����ɫͼת��Ϊ�Ҷ�ͼ 
     * @param img λͼ 
     * @return  ����ת���õ�λͼ 
     */  
    public static Bitmap convertGreyImg(Bitmap img) {  
        int width = img.getWidth();         //��ȡλͼ�Ŀ�  
        int height = img.getHeight();       //��ȡλͼ�ĸ�  
          
        int []pixels = new int[width * height]; //ͨ��λͼ�Ĵ�С�������ص�����  
          
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
     * ��base64�ַ��������ͼƬ
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
     * ��ͼƬת����base64�������ַ���
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
     * ��Bitmapת��Ϊ�ֽ�����
     * @param bm
     * @return
     */
	public static byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);       
		return baos.toByteArray();   
	}
	/**
	 * ��Byteת����Bitmap
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
	//ͨ��urlȡ��bitmap
		public static Bitmap getImage(String Url) throws Exception {
			URL url;
			byte[] b=null;
			try {
				url = new URL(Url);   //����URL
				HttpURLConnection con;
			    con = (HttpURLConnection)url.openConnection();  //������
				con.setRequestMethod("GET"); //�������󷽷�
				//�������ӳ�ʱʱ��Ϊ5s
				con.setConnectTimeout(5000);
				InputStream in=con.getInputStream();  //ȡ���ֽ�������
			    b=readInputStream(in);
			  //��������������λͼ
		        Bitmap bit=BitmapFactory.decodeByteArray(b, 0, b.length);
				return bit;  //����byte����
			} catch (Exception e) {
				
				e.printStackTrace();
			}
			return null;  //����byte����
		}
		private static byte[] readInputStream(InputStream in) throws Exception{
			int len=0;
			byte buf[]=new byte[1024];
			ByteArrayOutputStream out=new ByteArrayOutputStream();
			while((len=in.read(buf))!=-1){
				out.write(buf,0,len);  //������д���ڴ�
			}
			out.close();  //�ر��ڴ������
			return out.toByteArray(); //���ڴ������ת����byte����
		}
		/** 
		* ת��ͼƬ��Բ�� 
		* @param bitmap ����Bitmap���� 
		* @return 
		*/ 
		public static Bitmap getRoundBitmap(Bitmap bitmap,int width) { 
			return toRoundBitmap(bitmap,width,2);
		}
		/**
		  * ת��ͼƬ��Բ��
		  * @param bitmap
		  * @param width
		  * @param ring_width ��߿���
		  * @return
		  */
		 private static Bitmap toRoundBitmap(Bitmap bitmap,int width,int ring_width) {
		  float radius; //�뾶
		  float left,top; //���Ͻ�
		  float right,bottom; //���½�
		  left = ring_width;
		  top = ring_width;
		  bottom = width-ring_width;
		  right = width-ring_width;
		  radius = width / 2 -ring_width;//�뾶����ԭ�뾶ȥ��Բ���Ŀ��
		 
		  Bitmap output = Bitmap.createBitmap(width, width, Config.ARGB_8888);
		  Canvas canvas = new Canvas(output);
		  final Paint paint = new Paint();
		  final RectF rectF = new RectF(left, //���λ�������
		    top,
		    right,
		    bottom);
		  canvas.drawRoundRect(rectF, //ͨ��������������ڽ�Բ
		    radius,
		    radius,
		    paint);
		  paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN)); //��ʾbitmap��Բ�������еĽ�������
		  canvas.drawBitmap(bitmap, 0, 0, paint);
		 return withWhiteCircle(output,radius+ring_width);
		 }
		 
		 /**
		  * ����ױ�
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
		  paint.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));//��ʾbitmap��output�еĲ�������
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
			     // ����Ϊtureֻ��ȡͼƬ��С
			     opts.inJustDecodeBounds = true;
			     opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
			     // ����Ϊ��
			     BitmapFactory.decodeFile(path, opts);
			     int width = opts.outWidth;
			     int height = opts.outHeight;
			     float scaleWidth = 0.f, scaleHeight = 0.f;
			     if (width > w || height > h) {
			        // ����
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

package com.android.picture.slide;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.example.imagebrowser.ImageLoaderForBitmap;
import com.example.imagebrowser.ImageLoaderForBitmap.MyUrl;
import com.example.imagebrowser.ImageLoaderForBitmap.OnBitmapReceiveListener;

public class ViewPagerActivity extends Activity implements OnPageChangeListener {

	private ViewPager mViewPager;
	private ImageView[] tips;
	private static ArrayList<String> imageList;
	private static Bitmap[] bitmaps;
	private static int location;
	ImageLoaderForBitmap image_loader = null;
	private String tag = "ViewPagerActivity";
	int j=0;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mViewPager = new HackyViewPager(this);
		setContentView(mViewPager);
        
		Intent intent = getIntent();
		imageList = intent.getStringArrayListExtra("imagePath");
		location = intent.getIntExtra("location", 0);
		image_loader = new ImageLoaderForBitmap(this);
		
		if (imageList != null) {

			bitmaps = new Bitmap[imageList.size()];
			
			for (int i = 0; i < imageList.size(); i++) {
				MyUrl url = new MyUrl();
				url.url = imageList.get(i);
				url.index = i;
				image_loader.DisplayImage(url,new OnBitmapReceiveListener() {
					@Override
					public void onBitmapReceive(Bitmap bitmap,int index) {
						bitmaps[index]=bitmap;
						++j;
						if(j==imageList.size()){
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									mViewPager.setAdapter(new SamplePagerAdapter());
									mViewPager.setCurrentItem(location);
								}
							});
						}
					}
				});
			}
		}
		
	}

	static class SamplePagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return bitmaps.length;
		}

		@Override
		public View instantiateItem(ViewGroup container, int position) {
			PhotoView photoView = new PhotoView(container.getContext());
			// photoView.setImageResource(sDrawables[position]);
			photoView.setImageBitmap(bitmaps[position]);
			container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			return photoView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int arg0) {
	}

	void log(String msg){
		Log.d(tag , msg);
	}
}

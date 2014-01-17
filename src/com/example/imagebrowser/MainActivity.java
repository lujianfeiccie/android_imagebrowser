package com.example.imagebrowser;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.android.picture.slide.ViewPagerActivity;

public class MainActivity extends Activity implements OnClickListener{

	Button bt_test;
	ArrayList<String> imageList = new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		bt_test = (Button)findViewById(R.id.bt_test);
		bt_test.setOnClickListener(this);
		imageList.add("http://pic1.sc.chinaz.com/files/pic/pic9/201401/apic3167.jpg");
		imageList.add("http://pic1.sc.chinaz.com/files/pic/pic9/201401/apic3168.jpg");//手机
		imageList.add("http://pic1.sc.chinaz.com/files/pic/pic9/201401/apic3169.jpg");//旋转摸吗
		imageList.add("http://pic1.sc.chinaz.com/files/pic/pic9/201401/apic3170.jpg");//海
		imageList.add("http://pic1.sc.chinaz.com/files/pic/pic9/201401/apic3171.jpg");//家具
		imageList.add("http://pic1.sc.chinaz.com/files/pic/pic9/201401/apic3172.jpg");//接吻
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_test:
			Intent intent = new Intent(this, ViewPagerActivity.class);
			intent.putStringArrayListExtra("imagePath",imageList);
			intent.putExtra("location", 1);
			startActivity(intent);
			break;

		default:
			break;
		}
	}
	
/*
 * Intent intent = new Intent(ChatActivity.this, ViewPagerActivity.class);
				intent.putStringArrayListExtra("imagePath", info.getLocal());
				intent.putExtra("location", arg2);
				startActivity(intent);
 */
}

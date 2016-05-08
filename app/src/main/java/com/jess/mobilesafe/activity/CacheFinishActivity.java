package com.jess.mobilesafe.activity;

import com.jess.mobilesafe.R;

import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.TextView;

public class CacheFinishActivity extends BaseTouch {
	private TextView tv_cache_finishsize;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cachefinish);
		tv_cache_finishsize = (TextView) findViewById(R.id.tv_cache_fin);
		
		long cacheTotal = getIntent().getLongExtra("cacheTotal", 0);
		
		tv_cache_finishsize.setText(Formatter.formatFileSize(this, cacheTotal));
	}
	
	
	public void finish(View v ){
		finish();
		overridePendingTransition(R.anim.splansh_in, R.anim.splansh_out);
	}
	@Override
	public void showNextPage() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showPreviousPage() {
		// TODO Auto-generated method stub

	}

}

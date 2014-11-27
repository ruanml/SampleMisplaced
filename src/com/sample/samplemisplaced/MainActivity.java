package com.sample.samplemisplaced;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class MainActivity extends ActionBarActivity {

	@Nullable
	private Context mContext = null;
	/**
	 * view to show if no images
	 */
	private View mNoImageView = null;
	/**
	 * grid view to show images
	 */
	private GridView mGridView = null;
	/**
	 * adapter of grid view
	 */
	private ImageAdapter mAdapter = null;
	/**
	 * broadcast receiver to reload
	 */
	private BroadcastReceiver mReceiver = null;
	/**
	 * tag
	 */
	private static final String TAG = "print.PhotoActivity";
	/**
	 * list of image info
	 */
	private List<ImageInfo> mImageInfoList = new ArrayList<ImageInfo>();
	/**
	 * id of done button
	 */
	public static final int ID_DONE = 0;
	/**
	 * flag of broadcast receiver
	 */
	private boolean isRegisteredOrNot = false;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mGridView = (GridView) findViewById(R.id.photo_gridview);
		mNoImageView = findViewById(R.id.photo_noimages);
		try {
			/**
			 * find all of images in this device
			 */
			addImageUriCall();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/**
		 * create broadcast receiver
		 */
		mReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				String action = intent.getAction();
				if (isNeedReload(action)) {
					if (mAdapter != null) {
						mNoImageView.setVisibility(View.GONE);
						try {
							addImageUriCall();
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				}
			}

			/**
			 * if need reload
			 * 
			 * @param action
			 * @return
			 */
			private boolean isNeedReload(String action) {
				if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
					return true;
				}
				if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
					return true;
				}
				if (action.equals(Intent.ACTION_MEDIA_SCANNER_FINISHED)) {
					return true;
				}
				if (action.equals(Intent.ACTION_MEDIA_SCANNER_STARTED)) {
					return true;
				}
				if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
					return true;
				}
				return false;
			}
		};
		/**
		 * create adapter
		 */
		mAdapter = new ImageAdapter(this, mImageInfoList);

		mNoImageView.setVisibility(View.GONE);
		mGridView.setAdapter(mAdapter);
		IntentFilter intentFilter = new IntentFilter(
				Intent.ACTION_MEDIA_MOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
		intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
		intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
		intentFilter.addDataScheme("file");
		registerReceiver(mReceiver, intentFilter);
		isRegisteredOrNot = true;
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				mAdapter.onClickGridView(position);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	/**
	 * back button pressed
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (this.isFinishing()) {
			mAdapter = null;
			mGridView.setAdapter(null);
			System.gc();
		}
	}

	/**
	 * unregister receiver if not
	 */
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		if (isRegisteredOrNot) {
			unregisterReceiver(mReceiver);
			isRegisteredOrNot = false;
		}
		super.onStop();
	}

	/**
	 * call the method to search images and add uri to list then sort the list
	 * 
	 * @throws InterruptedException
	 */
	private void addImageUriCall() throws InterruptedException {
		mImageInfoList.clear();
		addImageUri(mImageInfoList,
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		addImageUri(mImageInfoList,
				MediaStore.Images.Media.getContentUri("phoneStorage"));
		addImageUri(mImageInfoList,
				MediaStore.Images.Media.getContentUri("internaldata"));
		try {
			Collections.sort(mImageInfoList, new ImageInfo.DateComparator());
		} catch (IllegalArgumentException e) {
			// TODO: handle exception
			Log.d(TAG, "collections sort failed!");
		}
	}

	/**
	 * search images and add uri to list
	 * 
	 * @param imageList
	 * @param baseUri
	 * @throws InterruptedException
	 */
	private void addImageUri(List<ImageInfo> imageList, Uri baseUri)
			throws InterruptedException {
		String selection = MediaStore.Images.Media.MIME_TYPE
				+ " = 'image/jpeg'";
		String orderBy = MediaStore.Images.Media.DATE_MODIFIED + " ASC";
		Cursor cursor = null;
		try {
			ContentResolver mContentResolver = this.getContentResolver();
			cursor = mContentResolver.query(baseUri, null, selection, null,
					orderBy);
			if (cursor == null) {
				return;
			}
			if (!cursor.moveToFirst()) {
				return;
			}
			int cursor_max = cursor.getCount();
			int idxTime = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN);
			int idxPath = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
			int idxSize = cursor.getColumnIndexOrThrow(MediaColumns.SIZE);
			int idxId = cursor.getColumnIndex(MediaStore.Images.Media._ID);

			for (int i = 0; i < cursor_max; i++) {
				long id = cursor.getLong(idxId);
				int size = cursor.getInt(idxSize);
				String path = cursor.getString(idxPath);
				long ts = cursor.getLong(idxTime);

				if ((path != null) && (new File(path).exists())) {
					imageList.add(new ImageInfo(baseUri, id, ts, size, path));
				}
				cursor.moveToNext();

			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Log.d(TAG, "addImageList is failed!");
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

}

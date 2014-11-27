/**
 * Copyright 2013 Square, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.sample.samplemisplaced;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.bumptech.glide.Glide;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;


public class ImageAdapter extends BaseAdapter {

	int mGallaryItemBackground;
	
	private final Context mContext;
	
	private final Activity mActivity;
	
	private GridView mGridView;
	
	private final ArrayList<Map<String, Object>> mItemsList = new ArrayList<Map<String,Object>>();
	
	private static final String TAG = "sample.ImageAdapter";
	
	private final LayoutInflater mLayoutInflater;
	
	private List<ImageInfo> mImageInfoList = new ArrayList<ImageInfo>();
	
	private ArrayList<String> mSelectedUriList = new ArrayList<String>();
	
	private List<Integer> mSelectedFlagList = new ArrayList<Integer>();
	
	
	public ImageAdapter(Activity activity,  List<ImageInfo> list) {
		// TODO Auto-generated constructor stub
		mImageInfoList = list;
		mSelectedFlagList.clear();
		mSelectedUriList.clear();
		mActivity = activity;
		mContext = activity;
		mGridView = (GridView)mActivity.findViewById(R.id.photo_gridview);
		mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}
	
	public ArrayList<Map<String, Object>> getmItemsList() {
		return mItemsList;
	}

	public List<ImageInfo> getmImageInfoList() {
		return mImageInfoList;
	}

	public ArrayList<String> getmSelectedUriList() {
		return mSelectedUriList;
	}

	public List<Integer> getmSelectedFlagList() {
		return mSelectedFlagList;
	}

	/**
	 * add check icon or remove when click image
	 * @param position
	 */
	public void onClickGridView(int position){
		if (mSelectedFlagList == null) {
			mSelectedFlagList = new ArrayList<Integer>();
		}
		if (mSelectedFlagList.contains(position)) {
			mSelectedFlagList.remove(Integer.valueOf(position));
		} else {
			mSelectedFlagList.add(position);
		}
		int firstVisible = mGridView.getFirstVisiblePosition();
		View targetView = mGridView.getChildAt(position - firstVisible);
		mGridView.getAdapter().getView(position, targetView, mGridView);
	}
	
	/**
	 * view holder
	 * @author gen
	 *
	 */
	public class ViewHolder {
		ImageView thumb_imageView;
		ImageView check_imageView;
}
	
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mImageInfoList.get(position);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mImageInfoList.size();
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View _convertView;
		ViewHolder _viewHolder;
		if (convertView == null) {
			_convertView = mLayoutInflater.inflate(R.layout.activity_main_thumbnail, parent, false);
			_viewHolder = new ViewHolder();
			_convertView.setTag(_viewHolder);
		} else {
			_convertView = convertView;
			_viewHolder = (ViewHolder)_convertView.getTag();
		}
		try {
			_viewHolder.thumb_imageView = (ImageView)_convertView.findViewById(R.id.photo_thumb_image);
			_viewHolder.check_imageView = (ImageView)_convertView.findViewById(R.id.photo_check_image);
			final ImageInfo _imageInfo = mImageInfoList.get(position);
			/**
			 * show image with glide
			 */
			Glide.with(mActivity).load(_imageInfo.getmPath()).thumbnail(0.4f).centerCrop().into(_viewHolder.thumb_imageView);
			
			/**
			 * add check icon is current image in list of selected images 
			 * remove check icon if not
			 */
			if (mSelectedFlagList.contains(position)) {
				_viewHolder.check_imageView.setImageResource(R.drawable.status_check_icon);
			} else {
				_viewHolder.check_imageView.setImageResource(R.drawable.status_uncheck_icon);
			}
		} catch (IndexOutOfBoundsException e) {
			// TODO: handle exception
			e.printStackTrace();
			Log.d(TAG, "getView index out of bounds exception");
			_viewHolder.thumb_imageView.setImageResource(R.drawable.photo_nowloading);
			_viewHolder.check_imageView.setImageResource(R.drawable.status_uncheck_icon);
		}
		return _convertView;
	}
	
	
	
	

}

package com.sample.samplemisplaced;

import java.util.Comparator;

import android.content.ContentUris;
import android.net.Uri;

public class ImageInfo {

	/**
	 * uri of image
	 */
	private Uri mUri;
	/**
	 * id of image
	 */
	private long mId;
	/**
	 * taken date of image
	 */
	private final long mDate;
	/**
	 * size of image
	 */
	private int mSize;
	/**
	 * path of image
	 */
	private String mPath;

	public ImageInfo(Uri uri, long id, long date, int size, String path) {
		mUri = uri;
		mId = id;
		mDate = date;
		mSize = size;
		mPath = path;
	}

	public Uri getmUri() {
		Uri fullUri = ContentUris.withAppendedId(mUri, mId);
		return fullUri;
	}

	public long getmId() {
		return mId;
	}

	public long getmDate() {
		return mDate;
	}

	public int getmSize() {
		return mSize;
	}

	public String getmPath() {
		return mPath;
	}

	public static class DateComparator implements Comparator<ImageInfo> {

		@Override
		public int compare(ImageInfo lhs, ImageInfo rhs) {
			// TODO Auto-generated method stub
			if (lhs == null || rhs == null) {
				return 0;
			}
			if (lhs == rhs) {
				return 0;
			}
			return lhs.mDate < rhs.mDate ? 1 : -1;
		}

	}
}

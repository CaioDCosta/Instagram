package com.example.instagram.utils;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.instagram.BitmapScaler;
import com.parse.ParseFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class Photos {
	public static final int PICK_PHOTO_CODE = 1046;
	public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;

	public static Bitmap onActivityResult(int requestCode, int resultCode, Intent data, Activity activity, File photoFile, View v) {
		if (requestCode == PICK_PHOTO_CODE) {
			if(resultCode == RESULT_OK && data != null) {
				Uri photoUri = data.getData();
				return BitmapScaler.scaleToFill(rotateGallery(photoUri, activity), v.getWidth(), v.getHeight());
			}
			else {
				Toast.makeText(activity, "Image wasn't selected", Toast.LENGTH_SHORT).show();
			}
		}
		else if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK && photoFile != null) {
				String path = photoFile.getAbsolutePath();
				return BitmapScaler.scaleToFill(rotateCamera(path), v.getWidth(), v.getHeight());
			} else {
				Toast.makeText(activity, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
			}
		}
		return null;
	}

	public static Bitmap rotateGallery(Uri uri, Activity activity) {
		try {
			Bitmap bm = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
			String[] columns = {MediaStore.Images.Media.ORIENTATION};
			int rotationAngle = 0;
			Cursor cursor = null;
			try {
				cursor = activity.getContentResolver().query(uri, columns, null, null, null);
				if (cursor != null && cursor.moveToFirst()) {
					int orientationColumnIndex = cursor.getColumnIndex(columns[0]);
					rotationAngle = cursor.getInt(orientationColumnIndex);
				}
			} catch (Exception e) {
				//Do nothing
			} finally {
				if (cursor != null) {
					cursor.close();
				}
			}//End of try-catch block
			return rotate(bm, rotationAngle);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Bitmap rotateCamera(String photoFilePath) {
		// Create and configure BitmapFactory
		BitmapFactory.Options opts = new BitmapFactory.Options();
		Bitmap bm = BitmapFactory.decodeFile(photoFilePath, opts);
		// Read EXIF Data
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(photoFilePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
		int rotationAngle = 0;
		switch(orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				rotationAngle = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				rotationAngle = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				rotationAngle = 270;
				break;
		}
		// Rotate and return Bitmap
		return rotate(bm, rotationAngle);
	}

	private static Bitmap rotate(Bitmap bm, int rotationAngle) {
		Matrix matrix = new Matrix();
		matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
		return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
	}

	// Returns the File for a photo stored on disk given the fileName
	public static File getPhotoFileUri(String fileName, Activity activity) {
		// Get safe storage directory for photos
		// Use `getExternalFilesDir` on Context to access package-specific directories.
		// This way, we don't need to request external read/write runtime permissions.
		File mediaStorageDir = new File(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "LoginActivity");

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
			Log.d("LoginActivity", "Failed to create directory");
		}

		// Return the file target for the photo based on filename
		File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

		return file;
	}

	public static ParseFile getParseFile(Bitmap bitmap) {
		// Convert it to byte
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		// Compress image to lower quality scale 1 - 100
		bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
		byte[] image = stream.toByteArray();

		// Create and return the ParseFile
		return new ParseFile(image);
	}

}

package pt.ulisboa.tecnico.cmov.foodist;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddPictureActivity extends AppCompatActivity {

	static final int REQUEST_TAKE_PHOTO = 1;
	static final int REQUEST_GET_PHOTO = 2;
	ImageView imageView;
	String currentPhotoPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picture);
		imageView = findViewById(R.id.image_view);
	}
	//event handlers
	public void takePictureClick(View view) {
		//TODO: check if device has camera
		dispatchTakePictureIntent();
	}
	public void importPictureClick(View view) {
		//TODO: check if device has camera
		dispatchImportPictureIntent();
	}

	private void dispatchImportPictureIntent() {
		//Create an Intent with action as ACTION_PICK
		Intent intent=new Intent(Intent.ACTION_PICK);
		// Sets the type as image/*. This ensures only components of type image are selected
		intent.setType("image/*");
		//We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
		String[] mimeTypes = {"image/jpeg", "image/png"};
		intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
		// Launching the Intent
		startActivityForResult(intent,REQUEST_GET_PHOTO);

	}

	private void dispatchTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			// Create the File where the photo should go
			File photoFile = null;
				try {
					photoFile = createImageFile("JPEG_");
				} catch (IOException ex) {
					// Error occurred while creating the File
				Log.w("AddPictureActivity", "Error creating " + ex);
			}
			// Continue only if the File was successfully created
			if (photoFile != null) {
				Uri photoURI = FileProvider.getUriForFile(this,
						"pt.ulisboa.tecnico.cmov.foodist.fileprovider",
						photoFile);
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
				startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
			}
		}
	}

	//Aux functions
	private File createImageFile(String format) throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = format + timeStamp + "_";
		File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(
				imageFileName,  /* prefix */
				".jpg",         /* suffix */
				storageDir      /* directory */
		);

		// Save a file: path for use with ACTION_VIEW intents
		currentPhotoPath = image.getAbsolutePath();
		return image;
	}

	public void savePicture(ImageView iv,Uri uri) {
		//TODO: transform this into an ASync Task, because it does not need to run in the main thread

		//create the file
		File photoFile = null;
		try {
			photoFile = createImageFile("JPEG_");
		} catch (IOException ex) {
			// Error occurred while creating the File
			Log.w("AddPictureActivity", "Error creating " + ex);
		}

		BitmapDrawable drawable = (BitmapDrawable) iv.getDrawable();
		Bitmap bitmap = drawable.getBitmap();

		try {
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(photoFile));
		} catch (FileNotFoundException e) {
			Log.w("AddPictureActivity", "File not found ..." + e);
			e.printStackTrace();
		}
	}



	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if(resultCode == Activity.RESULT_OK)
			switch (requestCode){
				case REQUEST_GET_PHOTO:

					imageView.setImageURI(data.getData());
					savePicture(imageView,data.getData());

					//TODO: Choose how to deal with the image here
					break;
				case REQUEST_TAKE_PHOTO:
					imageView.setImageURI(Uri.parse(currentPhotoPath));
					break;

				default:
					Log.d("onActivityResult","not a valid request code");
			}
	}
	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
		//TODO ?
	}

}

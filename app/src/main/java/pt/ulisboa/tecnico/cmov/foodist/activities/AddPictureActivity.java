package pt.ulisboa.tecnico.cmov.foodist.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import pt.ulisboa.tecnico.cmov.foodist.R;

public class AddPictureActivity extends AppCompatActivity {
	//TODO: fix rotation, (save the image views)

	static final int REQUEST_TAKE_PHOTO = 1;
	static final int REQUEST_GET_PHOTO = 2;
	private ImageView imageView;
	private String currentPhotoPath;
	private Button postButton;
	private String dishName, category, price;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picture);
		imageView = findViewById(R.id.image_view);
		postButton = findViewById(R.id.postButton);
		postButton.setEnabled(false);

		Intent intent = getIntent();
		dishName = intent.getStringExtra("name");
		category = intent.getStringExtra("category");
		price = intent.getStringExtra("price");

		BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
		bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem item) {
				switch (item.getItemId()) {
					case R.id.action_explore:
						Intent intent =  new Intent(AddPictureActivity.this, ListFoodServicesActivity.class);
						startActivity(intent);
						break;
					case R.id.action_profile:
						Intent profileIntent =  new Intent(AddPictureActivity.this, ProfileActivity.class);
						startActivity(profileIntent);
						break;
				}
				return true;
			}
		});

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

	public void addPictureToMenu(View v) {
		// MISSING: SEND PICTURE TO SERVER
		Intent intent = new Intent(AddPictureActivity.this, DishActivity.class);
		intent.putExtra("name", dishName);
		intent.putExtra("category", category);
		intent.putExtra("price", price);
		startActivity(intent);
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

	/*
	 *	Returns an abstract representation of the file just created, as well as
	 * 	pathnames (?)
	 *
	 * 	@param format 	in what format should the file be (e.g. jpeg, png, ..)
	 * 	@return 		File
	 *
	 *
	 * */
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

	/*
	*	Responsible for caching the images being imported to the app.
	*	Since the image size can be too big, do the actual saving in the background.
	* 	@param iv place where the image is to be displayed
	* 	@param uri where the image is
	*
	*
	* */
	private void savePicture(ImageView iv,Uri uri) {
		//create the file
		File photoFile = null;
		BitmapDrawable drawable = (BitmapDrawable) iv.getDrawable();
		Bitmap bitmap = drawable.getBitmap();

		try {
			photoFile = createImageFile("JPEG_");
		} catch (IOException ex) {
			// Error occurred while creating the File
			Log.w("AddPictureActivity", "Error creating " + ex);
		}
		Thread thread = new Thread(new SaveImageRunnable(bitmap,photoFile));
		thread.start();
	}



	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if(resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
				case REQUEST_GET_PHOTO:
					Uri uri = data.getData();
					imageView.setImageURI(uri);
					savePicture(imageView, uri);

					//TODO: save the images in server (use some kind of AsyncTask)
					break;
				case REQUEST_TAKE_PHOTO:
					imageView.setImageURI(Uri.parse(currentPhotoPath));
					//TODO: save the images in server (use some kind of AsyncTask)
					break;
				default:
					Log.d("onActivityResult", "not a valid request code");
			}

			postButton.setEnabled(true);
			postButton.setTextColor(Color.WHITE);
		}
	}
	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
		//TODO ?
	}

	public class SaveImageRunnable implements Runnable {
		private Bitmap bitmap;
		private File photoFile;

		public SaveImageRunnable(Bitmap bt, File pf){
				bitmap = bt;
				photoFile = pf;
		}
		@Override
		public void run(){
			try {
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(photoFile));
			} catch (FileNotFoundException e) {
				Log.w("AddPictureActivity", "File not found ..." + e);
				e.printStackTrace();
			}

		}
	}
}

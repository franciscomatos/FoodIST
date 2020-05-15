package pt.ulisboa.tecnico.cmov.foodist.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import pt.ulisboa.tecnico.cmov.foodist.domain.AppImage;
import pt.ulisboa.tecnico.cmov.foodist.fetch.uploadImageProfile;
import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;

public class AddPictureProfileActivity extends AppCompatActivity {
	//TODO: fix rotation, (save the image views)

	static final int REQUEST_TAKE_PHOTO = 1;
	static final int REQUEST_GET_PHOTO = 2;
	private ImageView imageView;
	private String currentPhotoPath;
	private Button postButton;
	private String dishName, category, price, foodService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picture);


		imageView = findViewById(R.id.image_view);

		Intent intent = getIntent();
		dishName = intent.getStringExtra("name");
		category = intent.getStringExtra("category");
		price = intent.getStringExtra("price");
		foodService = intent.getStringExtra("foodService");


		BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
		bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem item) {
				switch (item.getItemId()) {
					case R.id.action_explore:
						Intent intent =  new Intent(AddPictureProfileActivity.this, ListFoodServicesActivity.class);
						startActivity(intent);
						break;
					case R.id.action_profile:
						Intent profileIntent =  new Intent(AddPictureProfileActivity.this, ProfileActivity.class);
						startActivity(profileIntent);
						break;
				}
				return true;
			}
		});

	}

	@Override
	public void onResume() {
		super.onResume();

		GlobalClass global = (GlobalClass) getApplicationContext();
		ImageButton doneButton = findViewById(R.id.doneButton);

		if(global.getUser().getImage() == null) doneButton.setEnabled(false);
		else doneButton.setEnabled(true);

		doneButton.setOnClickListener(new View.OnClickListener() {
			// Start new list activity
			public void onClick(View v) {
				Intent intent = new Intent(AddPictureProfileActivity.this, ProfileActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
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
		Intent intent = new Intent(AddPictureProfileActivity.this, DishActivity.class);
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
		String[] mimeTypes = {"image/jpeg"};
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

	public void sendImage(GlobalClass global,Bitmap imageBitmap){
		AppImage img, tbn;
		Uri uri;
		uploadImageProfile process;
		Date current = new Date();

		global.getUser().setImage(Bitmap.createScaledBitmap(imageBitmap, 500, 500, false));//should be enough

		process = new uploadImageProfile(global);
		process.execute();

	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if(resultCode == Activity.RESULT_OK) {
            GlobalClass global = (GlobalClass) getApplicationContext();
			Bitmap imageBitmap = null;
			switch (requestCode) {
				case REQUEST_TAKE_PHOTO:
					//we need to create a temp file to get the image full size, which is in currentPhotoPath
					imageView.setImageURI(Uri.parse(currentPhotoPath));
					//Get bitmap from camera by putting into an imageView and then retrieving it
					BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
					imageBitmap = drawable.getBitmap();

					if(imageBitmap != null) {
						sendImage(global, imageBitmap);
					}
					break;

				case REQUEST_GET_PHOTO :

					try{
						imageBitmap = MediaStore.Images.Media.getBitmap(
								this.getContentResolver(), data.getData());
					}catch(FileNotFoundException fnf){
						Log.e("ERROR", ": Couldnt load image");
						fnf.printStackTrace();
					}catch(IOException io){
						Log.e("ERROR", ": Something to due with the IO?");
						io.printStackTrace();
					}

					if(imageBitmap != null) {
						imageView.setImageBitmap(imageBitmap);
						sendImage(global, imageBitmap);
					}
					break;

				default:
					Log.d("onActivityResult", "not a valid request code");
			}

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
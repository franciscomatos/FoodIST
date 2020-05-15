package pt.ulisboa.tecnico.cmov.foodist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.synnapps.carouselview.CarouselViewPager;

import java.util.concurrent.ExecutionException;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.domain.AppImage;
import pt.ulisboa.tecnico.cmov.foodist.fetch.fetchSingleImage;
import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;

public class FullscreenImageActivity extends AppCompatActivity {

    private String fullImageName;
    private String foodServiceName;
    private String dishName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscreen_image);

        GlobalClass global = (GlobalClass) getApplicationContext();

        Intent intent = getIntent();

        foodServiceName = intent.getStringExtra("foodServiceName");
        dishName = intent.getStringExtra("dishName");
        fullImageName = intent.getStringExtra("imageName");

        // check if full image is in cache
        AppImage fullImage = global.getImageFromCache(fullImageName);
        ImageView currentImage = (ImageView) findViewById(R.id.fullScreenImage);

        if(fullImage == null) {
            // if not fetch full image from server
            fetchSingleImage process = new fetchSingleImage(global,
                    foodServiceName, dishName, fullImageName, currentImage, true);
            try {
                process.execute().get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            currentImage.setImageBitmap(fullImage.getImage());
        }
    }
}

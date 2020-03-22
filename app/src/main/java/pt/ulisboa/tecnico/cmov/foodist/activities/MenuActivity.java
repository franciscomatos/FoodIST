package pt.ulisboa.tecnico.cmov.foodist.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.domain.Dish;
import pt.ulisboa.tecnico.cmov.foodist.states.MenuState;

public class MenuActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private MenuState menuState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        this.menuState = (MenuState) getApplicationContext();

        Log.i("MenuActivity", "got Context");

        for(int i = 0; i < 6; i++)
            menuState.addDish(new Dish("Dish " + i, 5.5, "Category " + i));

        Log.i("MenuActivity", "added Dishes");

        this.tableLayout = findViewById(R.id.menuTable);

        Log.i("MenuActivity", "found Table");

        for(int i = 0; i < menuState.getCounter(); i += 2) {

            TableRow tr = (TableRow) getLayoutInflater().inflate(R.layout.activity_menu_dish, null);

            ImageView imageViewLeft = tr.findViewById(R.id.menuDishPhotoLeft);
            //roundImageCorners(imageViewLeft, true, true, false, false);

            ImageView imageViewRight = tr.findViewById(R.id.menuDishPhotoRight);
            //§roundImageCorners(imageViewRight, true, true, false, false);


            TextView dishNameLeftView = tr.findViewById(R.id.menuDishNameLeft);
            dishNameLeftView.setText(menuState.getDish(i).getName());
            TextView dishNameRightView = tr.findViewById(R.id.menuDishNameRight);
            dishNameRightView.setText(menuState.getDish(i+1).getName());

            TextView dishCategoryLeftView = tr.findViewById(R.id.menuDishCategoryLeft);
            dishCategoryLeftView.setText(menuState.getDish(i).getCategory());
            TextView dishCategoryRightView = tr.findViewById(R.id.menuDishCategoryRight);
            dishCategoryRightView.setText(menuState.getDish(i+1).getCategory());

            TextView dishPriceLeftView = tr.findViewById(R.id.menuDishPriceLeft);
            dishPriceLeftView.setText(String.format(menuState.getDish(i).getPrice().toString()));
            TextView dishPriceRightView = tr.findViewById(R.id.menuDishPriceRight);
            dishPriceRightView.setText(String.format(menuState.getDish(i+1).getPrice().toString()));

            LinearLayout leftLayout = tr.findViewById(R.id.menuLeftDish);
            LinearLayout rightLayout = tr.findViewById(R.id.menuRightDish);

            leftLayout.setTag(menuState.getDish(i));
            rightLayout.setTag(menuState.getDish(i+1));

            final int index = i;
            leftLayout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Dish dish = menuState.getDish(index);
                    Intent intent = new Intent(getBaseContext(), DishActivity.class);
                    intent.putExtra("name", dish.getName());
                    intent.putExtra("category", dish.getCategory());
                    intent.putExtra("price", dish.getPrice().toString());
                    startActivity(intent);
                }
            });

            rightLayout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Dish dish = menuState.getDish(index+1);
                    Intent intent = new Intent(getBaseContext(), DishActivity.class);
                    intent.putExtra("name", dish.getName());
                    intent.putExtra("category", dish.getCategory());
                    intent.putExtra("price", dish.getPrice().toString());
                    startActivity(intent);
                }
            });

            this.tableLayout.addView(tr);
            Log.i("MenuActivity", "added row to table");
        }
    }

    public void roundImageCorners(ImageView image, boolean topLeft, boolean topRight,
                                    boolean bottomLeft, boolean bottomRight) {
        Bitmap mbitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        Bitmap imageRounded = Bitmap.createBitmap(175*14, 250*14, mbitmap.getConfig());
        /*Bitmap imageRounded = Bitmap.createBitmap(mbitmap.getWidth(), mbitmap.getHeight(), mbitmap.getConfig());*/
        Canvas canvas = new Canvas(imageRounded);
        Paint mpaint = new Paint();
        mpaint.setAntiAlias(true);
        mpaint.setShader(new BitmapShader(mbitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

        Path path = RoundedRect(0, 0,  175 *14f, 250*14f, 100, 100,
                topLeft, topRight, bottomRight, bottomLeft);
        /*Path path = RoundedRect(0, 0,  mbitmap.getWidth() , mbitmap.getHeight(), 100, 100,
                topLeft, topRight, bottomRight, bottomLeft);*/

        canvas.drawPath(path,mpaint);

        image.setImageBitmap(imageRounded);

    }

    public static Path RoundedRect(
            float left, float top, float right, float bottom, float rx, float ry,
            boolean tl, boolean tr, boolean br, boolean bl
    ){
        Path path = new Path();
        if (rx < 0) rx = 0;
        if (ry < 0) ry = 0;
        float width = right - left;
        float height = bottom - top;
        if (rx > width / 2) rx = width / 2;
        if (ry > height / 2) ry = height / 2;
        float widthMinusCorners = (width - (2 * rx));
        float heightMinusCorners = (height - (2 * ry));

        path.moveTo(right, top + ry);
        if (tr)
            path.rQuadTo(0, -ry, -rx, -ry);//top-right corner
        else{
            path.rLineTo(0, -ry);
            path.rLineTo(-rx,0);
        }
        path.rLineTo(-widthMinusCorners, 0);
        if (tl)
            path.rQuadTo(-rx, 0, -rx, ry); //top-left corner
        else{
            path.rLineTo(-rx, 0);
            path.rLineTo(0,ry);
        }
        path.rLineTo(0, heightMinusCorners);

        if (bl)
            path.rQuadTo(0, ry, rx, ry);//bottom-left corner
        else{
            path.rLineTo(0, ry);
            path.rLineTo(rx,0);
        }

        path.rLineTo(widthMinusCorners, 0);
        if (br)
            path.rQuadTo(rx, 0, rx, -ry); //bottom-right corner
        else{
            path.rLineTo(rx,0);
            path.rLineTo(0, -ry);
        }

        path.rLineTo(0, -heightMinusCorners);

        path.close();//Given close, last lineto can be removed.

        return path;
    }
}

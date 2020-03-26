package pt.ulisboa.tecnico.cmov.foodist.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import pt.ulisboa.tecnico.cmov.foodist.PopUpClass;
import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.domain.Dish;
import pt.ulisboa.tecnico.cmov.foodist.domain.Menu;
import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;

public class MenuActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private Menu menuState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        FrameLayout background = findViewById(R.id.background);
        background.getForeground().setAlpha(0); // restore
        GlobalClass global = (GlobalClass) getApplicationContext();
        String foodServiceName = getIntent().getStringExtra("foodService");
        Log.i("MYLOGS", foodServiceName);
        this.menuState = global.getFoodService(foodServiceName).getMenu();

        Log.i("MenuActivity", "got Context");
/*
        for(int i = 0; i < 6; i++)
            menuState.addDish(new Dish("Dish " + i, 5.5, "Category " + i));
*/
        Log.i("MenuActivity", "added Dishes");

        this.tableLayout = findViewById(R.id.menuTable);

        Log.i("MenuActivity", "found Table");

        updateDishes();

    }

    public void updateDishes() {

        // NOT THE MOST EFFICIENT
        // NEED TO FIX THIS

        // first we clear all the current Views
        this.tableLayout.removeAllViews();

        // then we add all views again
        for(int i = 0; i < menuState.getCounter(); i += 2) {

            TableRow tr = (TableRow) getLayoutInflater().inflate(R.layout.activity_menu_dish, null);

            // left dish
            TextView dishNameLeftView = tr.findViewById(R.id.menuDishNameLeft);
            dishNameLeftView.setText(menuState.getDish(i).getName());

            TextView dishCategoryLeftView = tr.findViewById(R.id.menuDishCategoryLeft);
            dishCategoryLeftView.setText(menuState.getDish(i).getCategory());

            TextView dishPriceLeftView = tr.findViewById(R.id.menuDishPriceLeft);
            dishPriceLeftView.setText(String.format(menuState.getDish(i).getPrice().toString()));

            LinearLayout leftLayout = tr.findViewById(R.id.menuLeftDish);

            leftLayout.setTag(menuState.getDish(i));

            final int index = i;
            // right dish
            if(index+1 < menuState.getCounter()) {
                TextView dishNameRightView = tr.findViewById(R.id.menuDishNameRight);
                dishNameRightView.setText(menuState.getDish(i + 1).getName());
                TextView dishCategoryRightView = tr.findViewById(R.id.menuDishCategoryRight);
                dishCategoryRightView.setText(menuState.getDish(i + 1).getCategory());

                TextView dishPriceRightView = tr.findViewById(R.id.menuDishPriceRight);
                dishPriceRightView.setText(String.format(menuState.getDish(i + 1).getPrice().toString()));

                LinearLayout rightLayout = tr.findViewById(R.id.menuRightDish);

                rightLayout.setTag(menuState.getDish(i + 1));

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
            } else {
                LinearLayout rightLayout = tr.findViewById(R.id.menuRightDish);
                rightLayout.removeAllViews();
                rightLayout.setBackgroundColor(Color.parseColor("#EEEEEE"));
            }


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


            this.tableLayout.addView(tr);
            Log.i("MenuActivity", "added row to table");
        }
    }

    public void showPopUp(View v) {
        // Create a button handler and call the dialog box display method in it
        final PopUpClass popUpClass = new PopUpClass();
        final View popupView = popUpClass.showPopupWindow(v);
        Button okButton = popupView.findViewById(R.id.okButton);

        final FrameLayout background = findViewById(R.id.background);
        background.getForeground().setAlpha(220); // dim

        final RadioGroup dishCategoryGroup = popupView.findViewById(R.id.categoryGroup);
        final EditText dishNameView = popupView.findViewById(R.id.dishName);
        final EditText dishPriceView = popupView.findViewById(R.id.dishPrice);

        //Handler for clicking on the inactive zone of the window

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                background.getForeground().setAlpha(0);
                popUpClass.onTouch();
                return true;
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int checkedCategoryId = dishCategoryGroup.getCheckedRadioButtonId();
                RadioButton dishCategoryButton = popupView.findViewById(checkedCategoryId);

                String dishName = dishNameView.getText().toString();
                Double dishPrice = Double.parseDouble(dishPriceView.getText().toString());
                String dishCategory = dishCategoryButton.getText().toString();

                MenuActivity.this.menuState.addDish(new Dish(dishName, dishPrice, dishCategory));
                MenuActivity.this.updateDishes();

                background.getForeground().setAlpha(0);
                popUpClass.onTouch();
            }
        });

    }


}

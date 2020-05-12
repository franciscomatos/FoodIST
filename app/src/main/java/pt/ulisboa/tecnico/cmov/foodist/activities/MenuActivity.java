package pt.ulisboa.tecnico.cmov.foodist.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Arrays;
import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.popups.FilterPopUpClass;
import pt.ulisboa.tecnico.cmov.foodist.popups.PopUpClass;
import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.domain.Dish;
import pt.ulisboa.tecnico.cmov.foodist.domain.Menu;
import pt.ulisboa.tecnico.cmov.foodist.fetch.fetchMenu;
import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;
import pt.ulisboa.tecnico.cmov.foodist.fetch.uploadDish;

public class MenuActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private Menu menuState;
    private String foodServiceName;
    private List<Boolean> checkedBoxes = Arrays.asList(false, false, false, false);
    private GlobalClass global;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        FrameLayout background = findViewById(R.id.background);
        background.getForeground().setAlpha(0); // restore
        global = (GlobalClass) getApplicationContext();
        foodServiceName = getIntent().getStringExtra("foodService");
        Log.i("MYLOGS", foodServiceName);
        menuState = global.getFoodService(foodServiceName).getMenu();

        if(menuState.containsConstraint(Dish.DishCategory.FISH)) checkedBoxes.set(0, true);
        if(menuState.containsConstraint(Dish.DishCategory.MEAT)) checkedBoxes.set(1, true);
        if(menuState.containsConstraint(Dish.DishCategory.VEGETARIAN)) checkedBoxes.set(2, true);
        if(menuState.containsConstraint(Dish.DishCategory.VEGAN)) checkedBoxes.set(3, true);

        Log.i("MenuActivity", "got Context");
/*
        for(int i = 0; i < 6; i++)
            menuState.addDish(new Dish("Dish " + i, 5.5, "Category " + i));

        menuState.addDish(new Dish("Dish Fish", 5.5, Dish.DishCategory.FISH));
        menuState.addDish(new Dish("Dish Meat", 5.5, Dish.DishCategory.MEAT));
        menuState.addDish(new Dish("Dish Vegetarian", 5.5, Dish.DishCategory.VEGETARIAN));
        menuState.addDish(new Dish("Dish Vegan", 5.5, Dish.DishCategory.VEGAN));
*/
        Log.i("MenuActivity", "added Dishes");

        this.tableLayout = findViewById(R.id.menuTable);

        Log.i("MenuActivity", "found Table");
        //menuState.clear();
        //fetchMenu process = new fetchMenu(this, menuState, foodServiceName,global);
        //process.execute();
        MenuActivity.this.updateDishes();

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_explore:
                        Intent intent =  new Intent(MenuActivity.this, ListFoodServicesActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.action_profile:
                        Intent profileIntent =  new Intent(MenuActivity.this, ProfileActivity.class);
                        startActivity(profileIntent);
                        break;
                }
                return true;
            }
        });

    }

    public void updateDishes() {

        // NOT THE MOST EFFICIENT
        // NEED TO FIX THIS

        // first we clear all the current Views
        this.tableLayout.removeAllViews();

        // then we add all views again
        for(int i = 0; i < menuState.getConstrainedCounter(); i += 2) {

            TableRow tr = (TableRow) getLayoutInflater().inflate(R.layout.activity_menu_dish, null);

            // left dish
            TextView dishNameLeftView = tr.findViewById(R.id.menuDishNameLeft);
            dishNameLeftView.setText(menuState.getConstraintDish(i).getName());

            TextView dishCategoryLeftView = tr.findViewById(R.id.menuDishCategoryLeft);
            dishCategoryLeftView.setText(menuState.getConstraintDish(i).getCategory().getCategory());

            TextView dishPriceLeftView = tr.findViewById(R.id.menuDishPriceLeft);
            dishPriceLeftView.setText(String.format(menuState.getConstraintDish(i).getPrice().toString()));

            LinearLayout leftLayout = tr.findViewById(R.id.menuLeftDish);

            leftLayout.setTag(menuState.getConstraintDish(i));

            final int index = i;
            final String foodService = this.foodServiceName;
            // right dish
            if(index+1 < menuState.getConstrainedCounter()) {
                TextView dishNameRightView = tr.findViewById(R.id.menuDishNameRight);
                dishNameRightView.setText(menuState.getConstraintDish(i + 1).getName());
                TextView dishCategoryRightView = tr.findViewById(R.id.menuDishCategoryRight);
                dishCategoryRightView.setText(menuState.getConstraintDish(i + 1).getCategory().getCategory());

                TextView dishPriceRightView = tr.findViewById(R.id.menuDishPriceRight);
                dishPriceRightView.setText(String.format(menuState.getConstraintDish(i + 1).getPrice().toString()));

                LinearLayout rightLayout = tr.findViewById(R.id.menuRightDish);

                rightLayout.setTag(menuState.getConstraintDish(i + 1));
                rightLayout.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Dish dish = menuState.getConstraintDish(index+1);
                        Intent intent = new Intent(getBaseContext(), DishActivity.class);
                        intent.putExtra("name", dish.getName());
                        intent.putExtra("category", dish.getCategory().getCategory());
                        intent.putExtra("price", dish.getPrice().toString());
                        intent.putExtra("foodService", foodService);
                        intent.putExtra("dishIndex", index+1);
                        startActivity(intent);
                    }
                });
            } else {
                LinearLayout rightLayout = tr.findViewById(R.id.menuRightDish);
                rightLayout.removeAllViews();
                rightLayout.setBackgroundResource(0);
            }


            leftLayout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Dish dish = menuState.getDish(index);
                    Intent intent = new Intent(getBaseContext(), DishActivity.class);
                    intent.putExtra("name", dish.getName());
                    intent.putExtra("category", dish.getCategory().getCategory());
                    intent.putExtra("price", dish.getPrice().toString());
                    intent.putExtra("foodService", foodService);
                    intent.putExtra("dishIndex", index);

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
        final View popupView = popUpClass.showPopupWindow(v, R.layout.pop_up_window_create_dish);
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
                Dish.DishCategory category = null;

                //better than the switch case
                category = Dish.DishCategory.valueOf(dishCategory.toUpperCase());

                //add the async task here
                uploadDish process = new uploadDish(dishPrice,dishName, dishCategory, foodServiceName, (GlobalClass) getApplicationContext());
                process.execute();

                MenuActivity.this.global.addDish(MenuActivity.this.foodServiceName, new Dish(dishName, dishPrice, category));
                MenuActivity.this.menuState.addDish(new Dish(dishName, dishPrice, category));
                MenuActivity.this.updateDishes();

                background.getForeground().setAlpha(0);
                popUpClass.onTouch();
            }
        });

    }

    public void showFilterPopUp(View v) {
        // Create a button handler and call the dialog box display method in it
        final FilterPopUpClass popUpClass = new FilterPopUpClass();
        final View popupView = popUpClass.showPopupWindow(v);

        CheckBox fishBox = popupView.findViewById(R.id.fishCheckbox);
        fishBox.setChecked(this.checkedBoxes.get(0));
        CheckBox meatBox = popupView.findViewById(R.id.meatCheckbox);
        meatBox.setChecked(this.checkedBoxes.get(1));
        CheckBox vegetarianBox = popupView.findViewById(R.id.vegetarianCheckbox);
        vegetarianBox.setChecked(this.checkedBoxes.get(2));
        CheckBox veganBox = popupView.findViewById(R.id.veganCheckbox);
        veganBox.setChecked(this.checkedBoxes.get(3));

        Button okButton = popupView.findViewById(R.id.okButton);

        final FrameLayout background = findViewById(R.id.background);
        background.getForeground().setAlpha(220); // dim

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
                MenuActivity.this.menuState.updateConstraintDishes();
                MenuActivity.this.updateDishes();
                background.getForeground().setAlpha(0);
                popUpClass.onTouch();
            }
        });

    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        CheckBox box = (CheckBox)view;

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.fishCheckbox:
                if (checked && !this.menuState.containsConstraint(Dish.DishCategory.FISH)) {
                    MenuActivity.this.menuState.addConstraint(Dish.DishCategory.FISH);
                    MenuActivity.this.checkedBoxes.set(0, true);
                }
                else {
                    MenuActivity.this.menuState.removeConstraint(Dish.DishCategory.FISH);
                    MenuActivity.this.checkedBoxes.set(0, false);
                }
                break;
            case R.id.meatCheckbox:
                if (checked && !this.menuState.containsConstraint(Dish.DishCategory.MEAT)) {
                    MenuActivity.this.menuState.addConstraint(Dish.DishCategory.MEAT);
                    MenuActivity.this.checkedBoxes.set(1, true);
                }
                else {
                    MenuActivity.this.menuState.removeConstraint(Dish.DishCategory.MEAT);
                    MenuActivity.this.checkedBoxes.set(1, false);
                }
                break;
            case R.id.vegetarianCheckbox:
                if (checked && !this.menuState.containsConstraint(Dish.DishCategory.VEGETARIAN)) {
                    MenuActivity.this.menuState.addConstraint(Dish.DishCategory.VEGETARIAN);
                    MenuActivity.this.checkedBoxes.set(2, true);
                }
                else {
                    MenuActivity.this.menuState.removeConstraint(Dish.DishCategory.VEGETARIAN);
                    MenuActivity.this.checkedBoxes.set(2, false);
                }
                break;
            case R.id.veganCheckbox:
                if (checked && !this.menuState.containsConstraint(Dish.DishCategory.VEGAN)) {
                    MenuActivity.this.menuState.addConstraint(Dish.DishCategory.VEGAN);
                    MenuActivity.this.checkedBoxes.set(3, true);
                }
                else {
                    MenuActivity.this.menuState.removeConstraint(Dish.DishCategory.VEGAN);
                    MenuActivity.this.checkedBoxes.set(3, false);
                }
                break;
        }
    }


}

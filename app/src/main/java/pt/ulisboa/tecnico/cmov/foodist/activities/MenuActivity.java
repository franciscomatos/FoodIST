package pt.ulisboa.tecnico.cmov.foodist.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.domain.Dish;
import pt.ulisboa.tecnico.cmov.foodist.states.MenuState;

public class MenuActivity extends AppCompatActivity {

    private TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        MenuState menuState = (MenuState) getApplicationContext();

        Log.i("MenuActivity", "got Context");

        for(int i = 0; i < 6; i++)
            menuState.addDish(new Dish("Dish " + i, 5.5, "Category " + i));

        Log.i("MenuActivity", "added Dishes");

        this.tableLayout = findViewById(R.id.menuTable);

        Log.i("MenuActivity", "found Table");

        for(int i = 0; i < menuState.getCounter(); i += 2) {
            TableRow tr = (TableRow) getLayoutInflater().inflate(R.layout.activity_menu_dish, null);

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

            this.tableLayout.addView(tr);
            Log.i("MenuActivity", "added row to table");
        }
    }
}

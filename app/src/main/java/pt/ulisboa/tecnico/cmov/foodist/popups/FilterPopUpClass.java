package pt.ulisboa.tecnico.cmov.foodist.popups;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import pt.ulisboa.tecnico.cmov.foodist.R;

public class FilterPopUpClass {

    private PopupWindow popupWindow;

    //PopupWindow display method

    public View showPopupWindow(final View view) {


        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.pop_up_window_filter_dishes, null);

        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        this.popupWindow = new PopupWindow(popupView, width, height, focusable);

        //Set the location of the window on the screen
        this.popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);


        return popupView;

    }

    public void onTouch() {
        //Close the window when clicked
        this.popupWindow.dismiss();
    }
}

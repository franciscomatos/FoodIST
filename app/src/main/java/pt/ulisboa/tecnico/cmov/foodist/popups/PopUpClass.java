package pt.ulisboa.tecnico.cmov.foodist.popups;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import pt.ulisboa.tecnico.cmov.foodist.R;

public class PopUpClass {

    private PopupWindow popupWindow;

    //PopupWindow display method

    public View showPopupWindow(final View view, int layoutResource) {


        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(layoutResource, null);

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

package pt.ulisboa.tecnico.cmov.foodist.activities;

import androidx.appcompat.app.AppCompatActivity;

import pt.ulisboa.tecnico.cmov.foodist.InputValidation;
import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.domain.Dish;
import pt.ulisboa.tecnico.cmov.foodist.domain.User;
import pt.ulisboa.tecnico.cmov.foodist.fetch.registerUser;
import pt.ulisboa.tecnico.cmov.foodist.states.AnnotationStatus;
import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.anychart.enums.AnnotationTypes;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button createAccountButton = findViewById(R.id.createAccount);
        final EditText usernameText = findViewById(R.id.name);
        final EditText emailText = findViewById(R.id.username);
        final EditText istNumberText = findViewById(R.id.istNumber);
        final EditText passwordText = findViewById(R.id.password);
        final RadioGroup userGroup = findViewById(R.id.userGroup);
        GlobalClass global = (GlobalClass) getApplicationContext();

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                InputValidation inputValidatorHelper = new InputValidation();
                StringBuilder errMsg = new StringBuilder("Unable to save. Please fix the following errors and try again.\n");
                boolean allowSave = true;


                String username = usernameText.getText().toString();
                String email = emailText.getText().toString();
                String istNumber = istNumberText.getText().toString();
                String password = passwordText.getText().toString();
                int checkedCategoryId = userGroup.getCheckedRadioButtonId();
                RadioButton userTypeButton = findViewById(checkedCategoryId);

                if(userTypeButton == null) {
                    errMsg.append("- Invalid user status.\n");
                    allowSave = false;
                }
                if(inputValidatorHelper.isNullOrEmpty(username)) {
                    errMsg.append("- Username name cannot be empty.\n");
                    allowSave = false;
                }
                if(inputValidatorHelper.isNullOrEmpty(email) || !inputValidatorHelper.isValidEmail(email)) {
                    errMsg.append("- Invalid email.\n");
                    allowSave = false;
                }
                if(inputValidatorHelper.isNullOrEmpty(istNumber) || !inputValidatorHelper.isValidIstNumber(istNumber)) {
                    errMsg.append("- Invalid ist number.\n");
                    allowSave = false;
                }
                if(inputValidatorHelper.isNullOrEmpty(password) || !inputValidatorHelper.isValidPassword(password, true)) {
                    errMsg.append("- Invalid password. Must be between 5 and 19 characters.\n");
                    allowSave = false;
                }

                if(!allowSave) {
                    Toast.makeText(getApplicationContext(), errMsg.toString(), Toast.LENGTH_LONG).show();
                    return;
                }

                String userType = userTypeButton.getText().toString();
                AnnotationStatus type = new AnnotationStatus(userType);

                // TO DO: create account in server
                registerUser register = new registerUser (global,username,password,email,istNumber,userType);
                //register register = new login (global,email,password, FIXME: add here userType and dietary stuff);
                register.execute();

                //global.setUser(new User(username, email, istNumber, password, type));

                Intent listFoodServicesIntent =  new Intent(RegisterActivity.this, ListFoodServicesActivity.class);
                startActivity(listFoodServicesIntent);
            }
        });
    }
}

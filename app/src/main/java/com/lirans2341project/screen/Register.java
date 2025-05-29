package com.lirans2341project.screen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.lirans2341project.R;
import com.lirans2341project.model.User;
import com.lirans2341project.services.AuthenticationService;
import com.lirans2341project.services.DatabaseService;
import com.lirans2341project.utils.SharedPreferencesUtil;
import com.lirans2341project.utils.Validator;

public class Register extends AppCompatActivity implements View.OnClickListener {

    TextView tvReg;
    EditText etFName, etLName, etPhone, etEmail, etPass, etAddress;
    Button btnReg, btnBack;
    String fName, lName, phone, email, pass;

    AuthenticationService authenticationService;
    DatabaseService databaseService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        authenticationService = AuthenticationService.getInstance();
        databaseService = DatabaseService.getInstance();

        init_views();
    }

    private void init_views() {
        btnReg = findViewById(R.id.btnReg);
        etFName = findViewById(R.id.etFName);
        etLName = findViewById(R.id.etLName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etPass = findViewById(R.id.etPass);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);
        btnReg.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        fName = etFName.getText().toString();
        lName = etLName.getText().toString();
        phone = etPhone.getText().toString();
        email = etEmail.getText().toString();
        pass = etPass.getText().toString();


        //check if registration is valid

        if (!isValid()) {
            return;
        }

        authenticationService.signUp(email, pass, new AuthenticationService.AuthCallback<String>() {
            @Override
            public void onCompleted(String uid) {
                // Sign in success, update UI with the signed-in user's information
                Log.d("TAG", "createUserWithEmail:success");
                User newUser = new User(uid, fName, lName, phone, email, pass, false, false);
                databaseService.createNewUser(newUser, new DatabaseService.DatabaseCallback<Void>() {
                    @Override
                    public void onCompleted(Void object) {
                        SharedPreferencesUtil.saveUser(getApplicationContext(), newUser);
                        Intent goLog = new Intent(getApplicationContext(), MainActivity.class);
                        goLog.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(goLog);
                    }

                    @Override
                    public void onFailed(Exception e) {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG", "createUserWithEmail:failure", e);
                        Toast.makeText(Register.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        authenticationService.signOut();
                    }
                });
            }

            @Override
            public void onFailed(Exception e) {
                // If sign in fails, display a message to the user.
                Log.w("TAG", "createUserWithEmail:failure", e);
                Toast.makeText(Register.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }


    private boolean isValid() {
        fName = etFName.getText().toString();
        lName = etLName.getText().toString();
        phone = etPhone.getText().toString();
        email = etEmail.getText().toString();
        pass = etPass.getText().toString();

        if (!Validator.isNameValid(fName)) {
            etFName.setError("שם פרטי קצר מדי");
            etFName.requestFocus();
            return false;
        }
        if (!Validator.isNameValid(lName)) {
            etLName.setError("שם משפחה קצר מדי");
            etLName.requestFocus();
            return false;
        }
        if (!Validator.isPhoneValid(phone)) {
            Toast.makeText(Register.this, "מספר הטלפון לא תקין", Toast.LENGTH_LONG).show();
            etPhone.requestFocus();
            return false;
        }

        if (!Validator.isEmailValid(email)) {
            etEmail.setError("כתובת האימייל לא תקינה");
            etEmail.requestFocus();
            return false;
        }
        if (!Validator.isPasswordValid(pass)) {
            etPass.setError("הסיסמה לא תקין");
            etPass.requestFocus();
            return false;
        }
        return true;
    }
}
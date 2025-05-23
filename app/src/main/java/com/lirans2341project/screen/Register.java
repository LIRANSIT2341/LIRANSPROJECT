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

public class Register extends AppCompatActivity implements View.OnClickListener {

    TextView tvReg;
    EditText etFName, etLName, etPhone, etEmail, etPass,  etAddress;
    Button btnReg,btnBack;
    String fName,lName, phone, email, pass;

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

    private void init_views(){
        btnReg=findViewById(R.id.btnReg);
        etFName=findViewById(R.id.etFName);
        etLName=findViewById(R.id.etLName);
        etPhone=findViewById(R.id.etPhone);
        etEmail=findViewById(R.id.etEmail);
        etPass=findViewById(R.id.etPass);

        btnBack=(Button)findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);
        btnReg.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        fName=etFName.getText().toString();
        lName=etLName.getText().toString();
        phone=etPhone.getText().toString();
        email=etEmail.getText().toString();
        pass=etPass.getText().toString();





        //check if registration is valid
        Boolean isValid=true;
        if(view == btnBack)
        {
            Intent intent=new Intent(getApplicationContext(),LandingActivity.class);
            startActivity(intent);
        }
        if (fName.length()<2){
            Toast.makeText(Register.this,"שם פרטי קצר מדי", Toast.LENGTH_LONG).show();
            isValid = false;
        }

        if (lName.length()<2){
            Toast.makeText(Register.this,"שם משפחה קצר מדי", Toast.LENGTH_LONG).show();
            isValid = false;
        }
        if (phone.length()<9||phone.length()>10){
            Toast.makeText(Register.this,"מספר הטלפון לא תקין", Toast.LENGTH_LONG).show();
            isValid = false;
        }

        if (!email.contains("@")){
            Toast.makeText(Register.this,"כתובת האימייל לא תקינה", Toast.LENGTH_LONG).show();
            isValid = false;
        }
        if(pass.length()<6){
            Toast.makeText(Register.this,"הסיסמה קצרה מדי", Toast.LENGTH_LONG).show();
            isValid = false;
        }
        if(pass.length()>20){
            Toast.makeText(Register.this,"הסיסמה ארוכה מדי", Toast.LENGTH_LONG).show();
            isValid = false;
        }

        if (isValid){

            authenticationService.signUp(email, pass, new AuthenticationService.AuthCallback<String>() {
                @Override
                public void onCompleted(String uid) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "createUserWithEmail:success");
                    User newUser=new User(uid, fName, lName, phone, email, pass, false);
                    databaseService.createNewUser(newUser, new DatabaseService.DatabaseCallback<Void>() {
                        @Override
                        public void onCompleted(Void object) {
                            SharedPreferencesUtil.saveUser(getApplicationContext(), newUser);
                            Intent goLog=new Intent(getApplicationContext(), Login.class);
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

    }
}
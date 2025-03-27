package com.lirans2341project.screen;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;

import com.lirans2341project.R;
import com.lirans2341project.model.User;
import com.lirans2341project.services.AuthenticationService;
import com.lirans2341project.services.DatabaseService;
import com.lirans2341project.utils.SharedPreferencesUtil;


import androidx.annotation.NonNull;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends AppCompatActivity implements View.OnClickListener {
    Button btnlog,btnBack;
    EditText etemail, etpass;
    String email, pass;
    TextView tvError;
    AuthenticationService authenticationService;
    DatabaseService databaseService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;




        });

        /// get the instance of the authentication service
        authenticationService = AuthenticationService.getInstance();
        /// get the instance of the database service
        databaseService = DatabaseService.getInstance();
        etemail = findViewById(R.id.etEmailLogin);
        etpass = findViewById(R.id.etPassLogin);
        tvError=findViewById(R.id.tvErrorLogIn);
        btnlog=(Button)findViewById(R.id.btnSignIn);
        btnlog.setOnClickListener(this);
        btnBack=(Button)findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        email = etemail.getText().toString()+"";
        pass = etpass.getText().toString()+"";
        Log.d("TAG", "onClick:btnSignIn");

        boolean isValid=true;
        if(view == btnBack)
        {
            Intent intent=new Intent(getApplicationContext(),LandingActivity.class);
            startActivity(intent);
        }
        if (!email.contains("@")){
            etpass.setError("כתובת אימייל אינה תקינה");
            isValid = false;
        }
        if(pass.length()<6){
          etpass.setError("סיסמא צריכה להיות בעלת שש תווים לפחות");
            isValid = false;
        }
        if(pass.length()>20){
            etpass.setError("סיסמא צריכה להיות מקסימום בעלת 20 תווים");
            isValid = false;
        }

        if(isValid) {
            authenticationService.signIn(email, pass, new AuthenticationService.AuthCallback<String>() {
                @Override
                public void onCompleted(String uid) {
                    Log.d("TAG", "signInWithEmail:success");
                    databaseService.getUser(uid, new DatabaseService.DatabaseCallback<User>() {
                        @Override
                        public void onCompleted(User user) {
                            SharedPreferencesUtil.saveUser(getApplicationContext(), user);
                            Intent go = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(go);
                        }

                        @Override
                        public void onFailed(Exception e) {
                            Log.w("etroor sign in", "signInWithEmail:failure", e);
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            tvError.setText("משתמש אינו קיים");
                        }
                    });
                }

                @Override
                public void onFailed(Exception e) {
                    Log.w("etroor sign in", "signInWithEmail:failure", e);
                    Toast.makeText(getApplicationContext(), "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    tvError.setText("משתמש אינו קיים");
                }
            });

        }
    }

}
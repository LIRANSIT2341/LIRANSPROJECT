package com.lirans2341project.screen;

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
import com.lirans2341project.services.DatabaseService;
import com.lirans2341project.utils.SharedPreferencesUtil;
import com.lirans2341project.utils.Validator;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "UserProfileActivity";

    private EditText etUserFirstName, etUserLastName, etUserPhone;
    private TextView etUserEmail, etUserPassword;
    private Button btnUpdateProfile, btnDeleteProfile;
    private DatabaseService databaseService;
    String selectedUid;
    User selectedUser;
    boolean isCurrentUser = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseService = DatabaseService.getInstance();

        selectedUid = getIntent().getStringExtra("USER_UID");
        User currentUser = SharedPreferencesUtil.getUser(this);
        if (selectedUid == null) {
            selectedUid = currentUser.getId();
        }
        isCurrentUser = selectedUid.equals(currentUser.getId());
        if (!isCurrentUser && !currentUser.isAdmin()) {
            // If the user is not an admin and the selected user is not the current user
            // then finish the activity
            Toast.makeText(this, "אין לך הרשאה לצפות בפרופיל זה", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d(TAG, "Selected user: " + selectedUid);

        // Initialize the EditText fields
        etUserFirstName = findViewById(R.id.et_user_first_name);
        etUserLastName = findViewById(R.id.et_user_last_name);
        etUserEmail = findViewById(R.id.et_user_email);
        etUserPhone = findViewById(R.id.et_user_phone);
        etUserPassword = findViewById(R.id.et_user_password);
        btnUpdateProfile = findViewById(R.id.btn_edit_profile);
        btnDeleteProfile = findViewById(R.id.btn_delete_profile);

        btnDeleteProfile.setOnClickListener(this);
        btnUpdateProfile.setOnClickListener(this);

        showUserProfile();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_edit_profile) {
            updateUserProfile();
            return;
        }
        if(v.getId() == R.id.btn_delete_profile) {
            setUserAsDeleted();
            return;
        }
    }

    private void setUserAsDeleted() {
        if (selectedUser == null) {
            return;
        }

        selectedUser.setDeleted(true);
        databaseService.createNewUser(selectedUser, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                Toast.makeText(UserProfileActivity.this, "המשתמש נמחק בהצלחה", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(UserProfileActivity.this, "שגיאה במחיקת המשתמש", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showUserProfile() {
        // Get the user data from shared preferences
        databaseService.getUser(selectedUid, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User user) {
                selectedUser = user;
                // Set the user data to the EditText fields
                etUserFirstName.setText(user.getFname());
                etUserLastName.setText(user.getLname());
                etUserEmail.setText(user.getEmail());
                etUserPhone.setText(user.getPhone());
                etUserPassword.setText(user.getPassword()); // מציג את הסיסמה האמיתית
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Error getting user profile", e);
                Toast.makeText(UserProfileActivity.this, "שגיאה בטעינת הפרופיל", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserProfile() {
        if (selectedUser == null) {
            Log.e(TAG, "User not found");
            Toast.makeText(this, "המשתמש לא נמצא", Toast.LENGTH_SHORT).show();
            return;
        }
        // Get the updated user data from the EditText fields
        String firstName = etUserFirstName.getText().toString();
        String lastName = etUserLastName.getText().toString();
        String phone = etUserPhone.getText().toString();

        if (!isValid(firstName, lastName, phone)) {
            Log.e(TAG, "Invalid input");
            return;
        }

        // Update the user object
        selectedUser.setFname(firstName);
        selectedUser.setLname(lastName);
        selectedUser.setPhone(phone);
        // לא מעדכנים אימייל וסיסמה

        databaseService.createNewUser(selectedUser, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                if (isCurrentUser)
                    SharedPreferencesUtil.saveUser(getApplicationContext(), selectedUser);
                Toast.makeText(UserProfileActivity.this, "הפרופיל עודכן בהצלחה", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Error updating profile", e);
                Toast.makeText(UserProfileActivity.this, "שגיאה בעדכון הפרופיל", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValid(String firstName, String lastName, String phone) {
        if (!Validator.isNameValid(firstName)) {
            etUserFirstName.setError("נא להזין שם פרטי");
            etUserFirstName.requestFocus();
            return false;
        }
        if (!Validator.isNameValid(lastName)) {
            etUserLastName.setError("נא להזין שם משפחה");
            etUserLastName.requestFocus();
            return false;
        }
        if (!Validator.isPhoneValid(phone)) {
            etUserPhone.setError("נא להזין מספר טלפון תקין");
            etUserPhone.requestFocus();
            return false;
        }
        return true;
    }
}
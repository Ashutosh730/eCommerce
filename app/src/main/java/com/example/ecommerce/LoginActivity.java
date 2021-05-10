package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ecommerce.Model.Users;
import com.example.ecommerce.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private EditText inputPhone, inputPassword;
    private Button loginBtn;
    private ProgressDialog loadingBar;
    private String parentDbName = "Users";
    private com.rey.material.widget.CheckBox rememberMeCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputPassword = findViewById(R.id.login_password_input);
        inputPhone = findViewById(R.id.login_phone_number_input);
        loginBtn = findViewById(R.id.login_btn);
        rememberMeCheckBox = findViewById(R.id.remember_me_chk);
        loadingBar = new ProgressDialog(this);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        rememberMeCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void loginUser() {
        String number = inputPhone.getText().toString();
        String password = inputPassword.getText().toString();

        if (TextUtils.isEmpty(number)) {
            Toast.makeText(this, "Enter Ur Phone Number...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter Ur Password...", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait, while we are login ur credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            allowAccessToAccount(number, password);
        }
    }

    public void allowAccessToAccount(String number, String password) {

        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(parentDbName).child(number).exists()) {

                    Users usersData = snapshot.child(parentDbName).child(number).getValue(Users.class);


                    if (usersData.getPhone().equals(number) && usersData.getPassword().equals(password)) {

                        if(rememberMeCheckBox.isChecked()){
                            Paper.book().write(Prevalent.userPhoneKey,number);
                            Paper.book().write(Prevalent.userPasswordKey,password);
                        }

                        Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    } else
                        Toast.makeText(LoginActivity.this, "Invalid phone number or password", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(LoginActivity.this, "This account does not exist", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(LoginActivity.this, "Please create account", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
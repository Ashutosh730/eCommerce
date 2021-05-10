package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ecommerce.Model.Users;
import com.example.ecommerce.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button loginButton, joinButton;
    private String parentDbName = "Users";
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        joinButton = findViewById(R.id.joinButton);
        loginButton = findViewById(R.id.loginButton);
        Paper.init(this);
        loadingBar = new ProgressDialog(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });

        String userPhoneKey = Paper.book().read(Prevalent.userPhoneKey);
        String userPasswordKey = Paper.book().read(Prevalent.userPasswordKey);

        if(userPasswordKey != null && userPhoneKey != null){
            if(TextUtils.isEmpty(userPasswordKey) && TextUtils.isEmpty(userPhoneKey))
                loadingBar.setTitle("Login Account");
                loadingBar.setMessage("Please wait, while we are login ur credentials");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
                allowAccess(userPhoneKey,userPasswordKey);
        }
    }

    private void allowAccess(final String number, final String password) {

        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(parentDbName).child(number).exists()) {

                    Users usersData = snapshot.child(parentDbName).child(number).getValue(Users.class);

                    if (usersData.getPhone().equals(number) && usersData.getPassword().equals(password)) {
                        loadingBar.dismiss();
                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
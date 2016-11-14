package com.example.izaya.smartoffice;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void onLoginClick(View v) {

        if (v.getId() == R.id.userLogin) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }

    }
}
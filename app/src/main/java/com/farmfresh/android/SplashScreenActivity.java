package com.farmfresh.android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.farmfresh.android.userAuth.LoginActivity;
import com.farmfresh.android.userAuth.LoginResultActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreenActivity extends AppCompatActivity {
    private static final String TAG = "SplashScreenActivity";

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, LoginActivity.class);
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();


        if (user == null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    LoginActivity.start(SplashScreenActivity.this);
                    finish();
                }

            }, 5000);
        } else {
            Log.d(TAG, "onCreate: user"+user);
            Log.d(TAG, "onCreate: user"+user.getUid());
            Log.d(TAG, "onCreate: user"+user.getIdToken(true));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    LoginResultActivity.start(SplashScreenActivity.this);
                    finish();
                }

            }, 5000);
        }
    }
}

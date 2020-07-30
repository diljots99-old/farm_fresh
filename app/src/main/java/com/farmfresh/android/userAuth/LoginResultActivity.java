package com.farmfresh.android.userAuth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.farmfresh.android.R;
import com.farmfresh.android.dashboard.DashboardActivity;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginResultActivity extends AppCompatActivity {

    private static final String TAG ="LoginResultActivity" ;
    private static FirebaseUser user;
    private FirebaseAuth mAuth;
    ImageView ivProfilePhoto;
    TextView tvInfo;

    public static void start(Context context ) {

        Intent intent = new Intent(context, LoginResultActivity.class);
        context.startActivity(intent);

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {

        String UID =  user.getUid();
        String DisplayName= user.getDisplayName();

        Uri PhotoUrl=user.getPhotoUrl();
        String PhoneNUmber = user.getPhoneNumber();
        String Email = user.getEmail();

        String DisplayData = "UID = "+UID+"\n Display Name = "+DisplayName+"\nPhoneNumber = "+PhoneNUmber+"\nEmail = "+Email;
        Log.d(TAG, "updateUI: "+DisplayData);

        Glide.with(this)
                .load(PhotoUrl)
                .centerCrop()
                .into(ivProfilePhoto);
        tvInfo.setText(DisplayData);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_result);

        ivProfilePhoto = findViewById(R.id.ivProfilePhoto);
        tvInfo = findViewById(R.id.tvInfo);


        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);


    }
}

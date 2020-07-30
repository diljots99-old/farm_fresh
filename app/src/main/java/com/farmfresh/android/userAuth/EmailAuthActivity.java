package com.farmfresh.android.userAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.farmfresh.android.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.auth.UserProfileChangeRequest;

public class EmailAuthActivity extends AppCompatActivity {
    private static final String TAG = "EmailAuthActivity";
    private FirebaseAuth mAuth;

    private String DisplayName ="",Confirm_password="";
    TextInputLayout tilEmail,tilPassword;
    Button next_button;
    public static void start(Context context ) {
        Intent intent = new Intent(context, EmailAuthActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_auth);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        next_button =findViewById(R.id.next_button);

        //Bottom Sheet Code Start
        // The View with the BottomSheetBehavior




        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String email = tilEmail.getEditText().getText().toString();
                final String password = tilPassword.getEditText().getText().toString();
                Log.d(TAG, "onClick: Email: "+email);


                mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        Log.d(TAG, "onComplete: "+task.getResult().getSignInMethods());

                        boolean isNewUser = task.getResult().getSignInMethods().isEmpty();
                        if (isNewUser){
                            Log.d(TAG, "onComplete: Create New User");
                            showConfirmNewSignUp(email,password);


                        }else{
                            Log.d(TAG, "onComplete: User Exits");
                            signIn(email,password);
                        }
                    }
                });

            }
        });

    }

    private  void signIn(String Email,String Password){
        mAuth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                }else {
                    Log.w(TAG, "signInWithEmailAndPassword: ", task.getException());
                }

            }
        });
    }

    private void createUser(final String Email, String Password, final String DisplayName){
        mAuth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                            .setDisplayName(DisplayName).build();
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                String msg= "A Verification mail has been sent to "+Email+". Please Verify your email";
                                showMessageDialog(msg,0,LoginResultActivity.class);
                            }else{
                                Log.w(TAG, "onComplete: ",task.getException() );
                                String msg= task.getException().toString();
                                showMessageDialog(msg,1,null);
                            }
                        }
                    });
                    user.updateProfile(userProfileChangeRequest);
                }else {
                    Log.w(TAG, "createUserWithEmailAndPassword: ", task.getException());
                    String msg= task.getException().toString();
                    showMessageDialog(msg,1,null);
                }
            }
        });
    }

    public void showConfirmNewSignUp(final String email, final String password){

        final boolean[] isConfirmPassValid = {false};
        final boolean[] isConfirmPassEntered = { false };
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(EmailAuthActivity.this);
        View sheetView = EmailAuthActivity.this.getLayoutInflater().inflate(R.layout.new_user_bottom_sheet, null);
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();

        Button btnContinue = mBottomSheetDialog.findViewById(R.id.btnContinue);
        final TextInputLayout tilConfirmPassword = mBottomSheetDialog.findViewById(R.id.tilConfirmPassword);
        final TextInputLayout tilDisplayName = mBottomSheetDialog.findViewById(R.id.tilDisplayName);


        tilConfirmPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                String cnfPassword = tilConfirmPassword.getEditText().getText().toString();
                if((!TextUtils.isEmpty(s)) && (password.equals(cnfPassword))){
                    isConfirmPassValid[0] = true;
                    isConfirmPassEntered[0] = true;
                    tilConfirmPassword.setErrorEnabled(false);

                }else{
                    isConfirmPassValid[0] = false;
                    isConfirmPassEntered[0] = false;
                    tilConfirmPassword.setErrorEnabled(true);
                    tilConfirmPassword.setError("Invalid Password");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cnfPassword = tilConfirmPassword.getEditText().getText().toString();
                String displayName = tilDisplayName.getEditText().getText().toString();



                if((isConfirmPassEntered[0]) && (isConfirmPassValid[0])) {
                    createUser(email, password,displayName);

                }else {
                    tilConfirmPassword.setErrorEnabled(true);
                    tilConfirmPassword.setError("Invalid Password");

                }

            }
        });
    }

    private void showMessageDialog(String msg, int iconCode, final Class<?> destnation ){
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(EmailAuthActivity.this);
        View sheetView = EmailAuthActivity.this.getLayoutInflater().inflate(R.layout.message_bottom_sheet, null);
        mBottomSheetDialog.setContentView(sheetView);

        LottieAnimationView lottieAnimationView = mBottomSheetDialog.findViewById(R.id.animationView);
        TextView textView = mBottomSheetDialog.findViewById(R.id.tvMessage);

        if (iconCode == 0) {
            lottieAnimationView.setAnimation(getResources().getIdentifier("json_success", "raw", EmailAuthActivity.this.getPackageName()));
            textView.setText(msg);

        }     if (iconCode == 1) {
            lottieAnimationView.setAnimation(getResources().getIdentifier("json_error", "raw", EmailAuthActivity.this.getPackageName()));
            textView.setText(msg);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                textView.setTextColor(getColor(R.color.red));
            }
        }


        Button btnContinue = mBottomSheetDialog.findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(destnation != null) {
                    Intent intent = new Intent(EmailAuthActivity.this, destnation);
                    startActivity(intent);
                }
            }
        });
        mBottomSheetDialog.show();
    }


}

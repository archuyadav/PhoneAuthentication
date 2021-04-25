package com.example.talkingking.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.talkingking.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SendOtpActivity extends AppCompatActivity {
    private Button btnSub;
    private EditText editcc;
    private EditText textPhone;
    private FirebaseAuth auth;
    private Dialog loadingDialog;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_otp);


        textPhone = findViewById(R.id.textPhone);
        editcc = findViewById(R.id.editcc);
        btnSub = findViewById(R.id.btnSubmit);

        auth = FirebaseAuth.getInstance();

        loadingDialog=new Dialog(SendOtpActivity.this);
        loadingDialog.setContentView(R.layout.loading_progressbar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        //if (auth.getCurrentUser()!=null){
            // Intent i=new Intent(MainActivity.this,HomeActivity.class);
            // startActivity(i);
            //finish();


        btnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String country_code = editcc.getText().toString();
                String phone = textPhone.getText().toString();
                String phoneNumber = "+" + country_code + " " + phone;


                if (!country_code.isEmpty() || !phone.isEmpty()) {
                    PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                            .setPhoneNumber(phoneNumber)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(SendOtpActivity.this)
                            .setCallbacks(callbacks)
                            .build();

                    PhoneAuthProvider.verifyPhoneNumber(options);

                } else {
                    Toast.makeText(SendOtpActivity.this, "Please Enter Country Code and Phone Number",
                            Toast.LENGTH_SHORT).show();

                }
            }
        });


        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                signIn(phoneAuthCredential);
                loadingDialog.show();


            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                loadingDialog.dismiss();
                Toast.makeText(SendOtpActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);


                Toast.makeText(SendOtpActivity.this, "OTP has been Sent", Toast.LENGTH_SHORT).show();


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Intent otpIntent=new Intent(SendOtpActivity.this, GetOtpActivity.class);
                        otpIntent.putExtra("ccp" + "mobile",textPhone.getText().toString());
                        otpIntent.putExtra("auth", s);
                        startActivity(otpIntent);


                    }
                },5000);

            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user=auth.getCurrentUser();
        if (user!=null){
            sentToMain();
        }
    }

    private void sentToMain() {
        loadingDialog.dismiss();
        Intent i=new Intent(SendOtpActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private void signIn(PhoneAuthCredential credential){

        loadingDialog.dismiss();
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                loadingDialog.show();

                if (task.isSuccessful()){
                    sentToMain();

                }else{
                    loadingDialog.dismiss();

                    Toast.makeText(SendOtpActivity.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}




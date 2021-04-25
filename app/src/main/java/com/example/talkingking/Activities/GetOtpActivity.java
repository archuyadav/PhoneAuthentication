package com.example.talkingking.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.talkingking.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class GetOtpActivity extends AppCompatActivity {

    private Button btnVerify;
    private EditText editOtp;
    TextView phoneLabel;

    private FirebaseAuth mAuth;
   private String Otp;
    private Dialog loadingDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_otp);

        mAuth = FirebaseAuth.getInstance();
        btnVerify = findViewById(R.id.btnVerify);
        editOtp = findViewById(R.id.getOtp);

        phoneLabel = findViewById(R.id.phoneLabel);
        phoneLabel.setText(String.format(getIntent().getStringExtra("ccp" + "mobile")));

        loadingDialog=new Dialog(GetOtpActivity.this);
        loadingDialog.setContentView(R.layout.loading_progressbar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();


        Otp = getIntent().getStringExtra("auth");
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  loadingDialog.show();
                String verification_code = editOtp.getText().toString();
                if (!verification_code.isEmpty()) {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(Otp, verification_code);
                    signIn(credential);

                } else {
                    loadingDialog.dismiss();
                    Toast.makeText(GetOtpActivity.this, "Please Enter the OTP ", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void signIn(PhoneAuthCredential credential){
        loadingDialog.dismiss();
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                loadingDialog.dismiss();

                if (task.isSuccessful()){
                    sentToMain();

                }else{

                    Toast.makeText(GetOtpActivity.this,"Verification Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user=mAuth.getCurrentUser();
        if (user!=null){
            sentToMain();
        }
    }

    private void sentToMain() {
        loadingDialog.dismiss();


        startActivity(new Intent(GetOtpActivity.this,MainActivity.class));
        finishAffinity();
    }

   
}
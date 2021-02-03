package com.myapp.myaccounts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class verifyPhone extends AppCompatActivity {
    EditText phoneNumberEditText;
    Button generateButton;
    String phoneNumber,verificationCodeBySystem;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);
        phoneNumberEditText=findViewById(R.id.verifyOtpEditTxtId);
        generateButton=findViewById(R.id.generateButtonIdverifyBtnId);
        progressBar=findViewById(R.id.progressBarOtpID);
        progressBar.setVisibility(View.GONE);
        SharedPreferences preferences=getSharedPreferences("checkbox",MODE_PRIVATE);
        String checkbox=preferences.getString("remember","");
        String mobile=preferences.getString("mobile","");
        if(checkbox.equals("true"))
        {
            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            IPAddress.PhoneNumber=mobile;
            startActivity(intent);
        }
        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(phoneNumberEditText.getText().toString().trim().isEmpty()){
                    Toast.makeText(verifyPhone.this,"Enter Mobile",Toast.LENGTH_LONG).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                generateButton.setVisibility(View.GONE);
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+91" + phoneNumberEditText.getText().toString(),
                60,TimeUnit.SECONDS,verifyPhone.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                progressBar.setVisibility(View.GONE);
                                generateButton.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                progressBar.setVisibility(View.GONE);
                                generateButton.setVisibility(View.VISIBLE);
                                Toast.makeText(verifyPhone.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                progressBar.setVisibility(View.GONE);
                                generateButton.setVisibility(View.VISIBLE);
                                Intent intent=new Intent(getApplicationContext(),VerifyOTPActivity.class);
                                intent.putExtra("mobile",phoneNumberEditText.getText().toString());
                                intent.putExtra("verificationID",s);
                                startActivity(intent);
                            }
                        });
            }
        });

    }
}

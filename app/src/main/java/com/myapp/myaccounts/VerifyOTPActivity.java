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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class VerifyOTPActivity extends AppCompatActivity {
    EditText otp;
    Button verifyButton;
    String verificationID;
    TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_o_t_p);
        otp=findViewById(R.id.verifyOtpEditTxtId);
        verifyButton=findViewById(R.id.verifyBtnId);
        title=findViewById(R.id.titleOTPId);
        final ProgressBar progressBar=findViewById(R.id.progressBarOtpID);
        progressBar.setVisibility(View.GONE);
        title.setText("Enter OTP sent to +91 "+getIntent().getStringExtra("mobile"));
        verificationID=getIntent().getStringExtra("verificationID");
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(otp.getText().toString().trim().isEmpty())
                {
                    Toast.makeText(VerifyOTPActivity.this,"Please enter valid code",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(verificationID!=null){
                    progressBar.setVisibility(View.VISIBLE);
                    verifyButton.setVisibility(View.VISIBLE);
                    PhoneAuthCredential phoneAuthCredential= PhoneAuthProvider.getCredential(
                            verificationID,
                            otp.getText().toString().trim()
                    );
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);
                                    verifyButton.setVisibility(View.VISIBLE);
                                    if(task.isSuccessful()){
                                        SharedPreferences preferences=getSharedPreferences("checkbox",MODE_PRIVATE);
                                        SharedPreferences.Editor editor=preferences.edit();
                                        editor.putString("remember","true");
                                        editor.putString("mobile",getIntent().getStringExtra("mobile"));
                                        editor.apply();
                                        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        IPAddress.PhoneNumber=getIntent().getStringExtra("mobile");
                                        startActivity(intent);
                                       // finish();
                                    }
                                    else
                                    {
                                        Toast.makeText(VerifyOTPActivity.this,"The verification code was invalid",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}
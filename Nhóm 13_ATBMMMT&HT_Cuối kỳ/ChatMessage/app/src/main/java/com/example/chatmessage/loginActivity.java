package com.example.chatmessage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firestore.v1.TargetOrBuilder;
import com.hbb20.CountryCodePicker;

import java.util.List;
import java.util.concurrent.TimeUnit;


public class loginActivity extends AppCompatActivity {



    EditText mgetphonenumber;
    EditText pass;


    android.widget.Button msendotp;
    CountryCodePicker mcountrycodepicker;
    String countrycode;
    String phonenumber;

    private Button btnregis;
    boolean isPhoneNumberExists = false;


    FirebaseAuth firebaseAuth;
    ProgressBar mprogressbarofmain;



    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String codesent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mcountrycodepicker=findViewById(R.id.countrycodepicker);
        msendotp=findViewById(R.id.sendotpbutton);
        mgetphonenumber=findViewById(R.id.getphonenumber);
        pass = findViewById(R.id.pass);


        mprogressbarofmain=findViewById(R.id.progressbarofmain);
        btnregis=findViewById(R.id.btnregis);
        firebaseAuth=FirebaseAuth.getInstance();

        countrycode=mcountrycodepicker.getSelectedCountryCodeWithPlus();

        mcountrycodepicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countrycode=mcountrycodepicker.getSelectedCountryCodeWithPlus();
            }
        });

        msendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number;
                number=mgetphonenumber.getText().toString();

                if(number.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"Please Enter Your number",Toast.LENGTH_SHORT).show();
                }
                else if(number.length()<10)
                {
                    Toast.makeText(getApplicationContext(),"Please Enter correct number",Toast.LENGTH_SHORT).show();
                }
                else if(pass.getText().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"Please Enter Password",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mprogressbarofmain.setVisibility(View.VISIBLE);
                    phonenumber=countrycode+number;

                    PhoneAuthOptions options=PhoneAuthOptions.newBuilder(firebaseAuth)
                            .setPhoneNumber(phonenumber)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(loginActivity.this)
                            .setCallbacks(mCallbacks)
                            .build();

                    PhoneAuthProvider.verifyPhoneNumber(options);
                }
            }
        });






        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                //how to automatically fetch code here
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

            }


            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Toast.makeText(getApplicationContext(),"OTP is Sent",Toast.LENGTH_SHORT).show();
                mprogressbarofmain.setVisibility(View.INVISIBLE);
                codesent=s;
                Intent intent=new Intent(loginActivity.this,otpLoginActivity.class);
                intent.putExtra("otp",codesent);
                //intent.putExtra("password", pass.getText().toString());



                startActivity(intent);
            }
        };
        btnregis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });


    }
    private void register() {
        Intent i=new Intent(loginActivity.this, MainActivity.class);
        startActivity(i);
    }



    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            Intent intent=new Intent(loginActivity.this,chatActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }






    }
}
package com.example.dichvudatxeandroid;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private EditText emailedit,passedit;
    private Button  btnregis;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth=FirebaseAuth.getInstance();

        emailedit=findViewById(R.id.email);
        passedit=findViewById(R.id.password);
        btnregis=findViewById(R.id.btnregis);

        btnregis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        // Check if user's email is verified
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && !user.isEmailVerified()) {
            Toast.makeText(getApplicationContext(), "Please verify your email address to continue.", Toast.LENGTH_LONG).show();
        }
    }

    private void register() {
        String email,pass;
        email=emailedit.getText().toString();
        pass=passedit.getText().toString();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Vui lòng nhập email!",Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(pass)){
            Toast.makeText(this,"Vui lòng nhập password!",Toast.LENGTH_LONG).show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Verification email sent to " + user.getEmail(), Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Failed to send verification email.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                    Toast.makeText(getApplicationContext(),"Tạo tài khoản thành công! Vui lòng kiểm tra email để xác thực tài khoản",Toast.LENGTH_LONG).show();

                }else{
                    Toast.makeText(getApplicationContext(),"Tạo tài khoản không thành công!",Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}


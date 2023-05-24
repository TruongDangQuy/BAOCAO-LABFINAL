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

public class LoginActivity extends AppCompatActivity {
    private EditText emailedit,passedit;
    private Button btnlogin, btnregis;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth=FirebaseAuth.getInstance();

        emailedit=findViewById(R.id.email);
        passedit=findViewById(R.id.password);
        btnlogin=findViewById(R.id.btnlogin);
        btnregis=findViewById(R.id.btnregis);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        btnregis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
    }

    private void register() {
        Intent i=new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(i);
    }

    private void login() {
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
        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null && user.isEmailVerified()) {
                        Toast.makeText(getApplicationContext(),"Đăng nhập thành công",Toast.LENGTH_LONG).show();
                        Intent intent=new Intent(LoginActivity.this,AESActivity.class);
                        startActivity(intent);
                    } else {
                        mAuth.signOut();
                        Toast.makeText(getApplicationContext(),"Email chưa được xác thực. Vui lòng kiểm tra email và thực hiện xác thực trước khi đăng nhập",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Đăng nhập không thành công",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}

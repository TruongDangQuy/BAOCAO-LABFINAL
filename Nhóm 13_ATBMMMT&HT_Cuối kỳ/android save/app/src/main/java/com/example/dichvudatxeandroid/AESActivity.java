package com.example.dichvudatxeandroid;

import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESActivity extends AppCompatActivity {
    EditText inputText, inputPassword;
    TextView outputText;
    Button encBtn, decBtn;
    String outputString;
    String AES = "AES";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aes);

        inputText = (EditText) findViewById(R.id.inputText);
        inputPassword = (EditText) findViewById(R.id.password);

        outputText = (TextView) findViewById(R.id.outputText);
        encBtn = (Button) findViewById(R.id.encBtn);
        decBtn = (Button) findViewById(R.id.decBtn);

        encBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    outputString = encrypt(inputText.getText().toString(), inputPassword.getText().toString());
                    outputText.setText(outputString);

                    // Initialize Firebase Realtime Database
                    FirebaseDatabase database = FirebaseDatabase.getInstance();

                    // Create a reference to the location where you want to store the encrypted text
                    DatabaseReference myRef = database.getReference().child("EncryptMessage");

                    // Save the encrypted text to the database
                    myRef.setValue(outputString);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        decBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    outputString = decrypt(outputString, inputPassword.getText().toString());
                } catch (Exception e) {
                    Toast.makeText(AESActivity.this, "Sai mật khẩu", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                outputText.setText(outputString);
            }
        });


    }
    private String decrypt(String outputString, String password) throws Exception{ // Phương thức decrypt() thực hiện
        // giải mã một chuỗi đã được mã hóa trước đó, với tham số đầu vào là chuỗi đã mã hóa outputString và password
        // là mật khẩu để giải mã.
        SecretKey key = generateKey(password); // Tạo một đối tượng SecretKey từ mật khẩu password
        // bằng cách gọi phương thức generateKey().
        Cipher c = Cipher.getInstance(AES); // Khởi tạo đối tượng Cipher sử dụng thuật toán AES.
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedValue = Base64.decode(outputString, Base64.DEFAULT); // Sử dụng Cipher để giải mã chuỗi đã mã hóa
        // outputString bằng cách giải mã dữ liệu đã được mã hóa trước đó bằng phương thức doFinal()
        byte[] decValue = c.doFinal(decodedValue); // decode chuỗi đã được giải mã thành dữ liệu thô.
        String decryptedValue = new String(decValue); // Chuyển đổi dữ liệu thô đã giải mã thành một chuỗi sử dụng lớp String.
        return decryptedValue; // Phương thức trả về chuỗi đã được giải mã decryptedValue.
    }

    private String encrypt (String Data, String password) throws Exception{ // Phương thức encrypt() thực hiện mã hóa
        // một chuỗi sử dụng thuật toán AES, với tham số đầu vào là chuỗi cần mã hóa Data và mật khẩu password để mã hóa
        SecretKey key = generateKey(password); // Tạo một đối tượng SecretKey từ mật khẩu password
        // bằng cách gọi phương thức generateKey().
        Cipher c = Cipher.getInstance(AES); // Khởi tạo đối tượng Cipher sử dụng thuật toán AES.
        c.init(Cipher.ENCRYPT_MODE,key);
        byte[]  encVal = c.doFinal(Data.getBytes()); // Sử dụng Cipher để mã hóa chuỗi Data bằng cách mã hóa dữ liệu
        // sử dụng phương thức doFinal()
        String encryptedValue = Base64.encodeToString(encVal, Base64.DEFAULT ); // encode chuỗi đã được mã hóa
        // thành một chuỗi Base64 sử dụng lớp Base64.
        return encryptedValue; // Phương thức trả về chuỗi đã được mã hóa encryptedValue.

    }

    private SecretKeySpec generateKey(String password) throws Exception  { // Phương thức generateKey() thực hiện tạo một
        // đối tượng SecretKeySpec từ mật khẩu password, để sử dụng cho việc mã hóa và giải mã dữ liệu sử dụng thuật toán AES
        final MessageDigest digest = MessageDigest.getInstance("SHA-256"); // Tạo một đối tượng MessageDigest
        // với thuật toán SHA-256 để mã hóa mật khẩu password thành một chuỗi byte có độ dài cố định.
        byte[] bytes = password.getBytes("UTF-8"); // Chuyển đổi chuỗi password thành chuỗi byte.
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest(); // Sử dụng MessageDigest để mã hóa chuỗi byte password và
        // lưu trữ kết quả mã hóa trong một mảng byte key.
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES"); // Tạo một đối tượng SecretKeySpec từ
        // mảng byte key đã được mã hóa, để sử dụng cho việc mã hóa và giải mã dữ liệu sử dụng thuật toán AES
        return  secretKeySpec; // Phương thức trả về đối tượng SecretKeySpec.
    }
}

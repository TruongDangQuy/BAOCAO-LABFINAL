package com.example.dichvudatxeandroid;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class ChatMacActivity extends AppCompatActivity {
    private EditText messageEditText;
    private EditText encryptEditText;
    private EditText showKeyEditText;
    private EditText enterKeyEditText;
    private EditText decryptEditText;

    private DatabaseReference databaseReference;
    private String messageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatmac);

        messageEditText = findViewById(R.id.message);
        encryptEditText = findViewById(R.id.encrypt);
        showKeyEditText = findViewById(R.id.showKey);
        enterKeyEditText = findViewById(R.id.enterKey);
        decryptEditText = findViewById(R.id.decrypt);

        Button postButton = findViewById(R.id.btnPost);
        Button confirmButton = findViewById(R.id.btnConfirm);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("messages");

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = messageEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(message)) {
                    String key = generateKey();
                    String encryptedMessage = encryptMessage(message, key);
                    storeEncryptedMessage(encryptedMessage);
                    showEncryptedMessage(encryptedMessage, key);
                }
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String enteredKey = enterKeyEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(enteredKey)) {
                    retrieveEncryptedMessage(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String encryptedMessage = dataSnapshot.getValue(String.class);
                            if (!TextUtils.isEmpty(encryptedMessage)) {
                                String decryptedMessage = decryptMessage(encryptedMessage, enteredKey);
                                if (decryptedMessage != null) {
                                    showDecryptedMessage(decryptedMessage);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(ChatMacActivity.this, "Failed to retrieve message: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private String generateKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            SecureRandom random = new SecureRandom();
            keyGen.init(random);
            SecretKey secretKey = keyGen.generateKey();
            byte[] encodedKey = secretKey.getEncoded();
            return Base64.encodeToString(encodedKey, Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String encryptMessage(String message, String key) {
        try {
            byte[] keyBytes = Base64.decode(key, Base64.DEFAULT);
            SecretKey secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKey);
            byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
            byte[] encryptedMessageBytes = mac.doFinal(messageBytes);
            return Base64.encodeToString(encryptedMessageBytes, Base64.DEFAULT);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
    }
    private void storeEncryptedMessage(String encryptedMessage) {
        messageId = databaseReference.push().getKey();
        if (messageId != null) {
            databaseReference.child(messageId).setValue(encryptedMessage);
        }
    }

    private void showEncryptedMessage(String encryptedMessage, String key) {
        encryptEditText.setText(encryptedMessage);
        showKeyEditText.setText(key);
        messageEditText.setText("");
    }

    private void retrieveEncryptedMessage(ValueEventListener valueEventListener) {
        if (messageId != null) {
            databaseReference.child(messageId).addListenerForSingleValueEvent(valueEventListener);
        }
    }

    private String decryptMessage(String encryptedMessage, String key) {
        try {
            byte[] keyBytes = Base64.decode(key, Base64.DEFAULT);
            SecretKey secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKey);
            byte[] encryptedMessageBytes = Base64.decode(encryptedMessage, Base64.DEFAULT);
            byte[] decryptedMessageBytes = mac.doFinal(encryptedMessageBytes);
            return new String(decryptedMessageBytes, StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showDecryptedMessage(String decryptedMessage) {
        decryptEditText.setText(decryptedMessage);
    }
}

package com.example.dichvudatxeandroid;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigInteger;
import java.security.MessageDigest;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatActivity extends AppCompatActivity {
    EditText editName;
    EditText editText;
    Button btnPost;

    // Reference to Firebase database
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        editName = findViewById(R.id.name);
        editText = findViewById(R.id.text);
        btnPost = findViewById(R.id.btnpost);

        // Initialize database reference
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Message");

        // Add click listener to the button
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the input values
                String name = editName.getText().toString();
                String text = editText.getText().toString();
                // Encrypt the text using MD5 hash
                String md5Text = null;
                try {
                    MessageDigest md = MessageDigest.getInstance("MD5");
                    byte[] hash = md.digest(text.getBytes("UTF-8"));
                    BigInteger bigInt = new BigInteger(1, hash);
                    md5Text = bigInt.toString(16);
                    while (md5Text.length() < 32) {
                        md5Text = "0" + md5Text;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // Create a new message object
                ChatMessage message = new ChatMessage(name, md5Text);

                // Insert the message into the database
                databaseReference.push().setValue(message);

                // Show a success message
                Toast.makeText(ChatActivity.this, "Message sent!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

package com.example.chatmessage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class specificchat extends AppCompatActivity {

    EditText mgetmessage;
    ImageButton msendmessagebutton;

    CardView msendmessagecardview;
    androidx.appcompat.widget.Toolbar mtoolbarofspecificchat;
    ImageView mimageviewofspecificuser;
    TextView mnameofspecificuser;

    private String enteredmessage;
    Intent intent;
    String mrecievername,sendername,mrecieveruid,msenderuid;
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    String senderroom,recieverroom;

    ImageButton mbackbuttonofspecificchat;

    RecyclerView mmessagerecyclerview;

    String currenttime;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;

    MessagesAdapter messagesAdapter;
    ArrayList<Messages> messagesArrayList;
    private SecretKey secretKey=null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specificchat);

        mgetmessage=findViewById(R.id.getmessage);
        msendmessagecardview=findViewById(R.id.carviewofsendmessage);
        msendmessagebutton=findViewById(R.id.imageviewsendmessage);
        mtoolbarofspecificchat=findViewById(R.id.toolbarofspecificchat);
        mnameofspecificuser=findViewById(R.id.Nameofspecificuser);
        mimageviewofspecificuser=findViewById(R.id.specificuserimageinimageview);
        mbackbuttonofspecificchat=findViewById(R.id.backbuttonofspecificchat);

        messagesArrayList=new ArrayList<>();
        mmessagerecyclerview=findViewById(R.id.recyclerviewofspecific);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mmessagerecyclerview.setLayoutManager(linearLayoutManager);
        messagesAdapter=new MessagesAdapter(specificchat.this,messagesArrayList);
        mmessagerecyclerview.setAdapter(messagesAdapter);




        intent=getIntent();

        setSupportActionBar(mtoolbarofspecificchat);
        mtoolbarofspecificchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Toolbar is Clicked",Toast.LENGTH_SHORT).show();


            }
        });

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        calendar=Calendar.getInstance();
        simpleDateFormat=new SimpleDateFormat("hh:mm a");


        msenderuid=firebaseAuth.getUid();
        mrecieveruid=getIntent().getStringExtra("receiveruid");
        mrecievername=getIntent().getStringExtra("name");



        senderroom=msenderuid+mrecieveruid;
        recieverroom=mrecieveruid+msenderuid;






        if (secretKey == null) {
            secretKey = loadSecretKey();
            if (secretKey == null) {
                secretKey = generateSecretKey();
                saveSecretKey(secretKey);
            }
        }



        DatabaseReference databaseReference = firebaseDatabase.getReference().child("chats").child(senderroom).child("messages");
        messagesAdapter = new MessagesAdapter(specificchat.this, messagesArrayList);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Messages messages = snapshot1.getValue(Messages.class);
                    String encryptedMessageBase64 = messages.getMessage();
                    byte[] encryptedMessage = Base64.decode(encryptedMessageBase64, Base64.DEFAULT);
                    String decryptedMessage = decryptMessage(encryptedMessage, secretKey);

                    messages.setMessage(decryptedMessage);
                    messagesArrayList.add(messages);
                }
                messagesAdapter.notifyDataSetChanged();
                mmessagerecyclerview.smoothScrollToPosition(messagesAdapter.getItemCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });




        mbackbuttonofspecificchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        mnameofspecificuser.setText(mrecievername);
        String uri=intent.getStringExtra("imageuri");
        if(uri.isEmpty())
        {
            Toast.makeText(getApplicationContext(),"null is recieved",Toast.LENGTH_SHORT).show();
        }
        else
        {
            Picasso.get().load(uri).into(mimageviewofspecificuser);
        }


        msendmessagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enteredmessage = mgetmessage.getText().toString();
                if (enteredmessage.isEmpty()) {
                    Toast.makeText(getApplicationContext(),"Enter message first",Toast.LENGTH_SHORT).show();
                } else {
                    try {

                        // Encrypt message
                        byte[] encryptedMessage = encryptMessage(enteredmessage, secretKey);
                        // Convert byte array to Base64 string for storage in Firebase
                        String encryptedMessageBase64 = Base64.encodeToString(encryptedMessage, Base64.DEFAULT);
                        // Get current time
                        Date date = new Date();
                        currenttime = simpleDateFormat.format(calendar.getTime());
                        // Create Messages object
                        Messages messages = new Messages(encryptedMessageBase64, firebaseAuth.getUid(), date.getTime(), currenttime);
                        firebaseDatabase=FirebaseDatabase.getInstance();
                        firebaseDatabase.getReference().child("chats")
                                .child(senderroom)
                                .child("messages")
                                .push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        firebaseDatabase.getReference()
                                                .child("chats")
                                                .child(recieverroom)
                                                .child("messages")
                                                .push()
                                                .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                    }
                                                });
                                    }
                                });
                        mgetmessage.setText(null);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });





    }

    private SecretKey generateSecretKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveSecretKey(SecretKey secretKey) {
        SharedPreferences preferences = getSharedPreferences("SECRET_KEY", MODE_PRIVATE);
        preferences.edit().putString("key", Base64.encodeToString(secretKey.getEncoded(), Base64.DEFAULT)).apply();
    }

    private SecretKey loadSecretKey() {
        SharedPreferences preferences = getSharedPreferences("SECRET_KEY", MODE_PRIVATE);
        String encodedKey = preferences.getString("key", null);
        if (encodedKey != null) {
            byte[] keyBytes = Base64.decode(encodedKey, Base64.DEFAULT);
            return new SecretKeySpec(keyBytes, "AES");
        } else {
            return null;
        }
    }


    private byte[] encryptMessage(String message, SecretKey secretKey) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] iv = new byte[cipher.getBlockSize()];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
            byte[] encryptedMessageBytes = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
            byte[] encryptedMessageBytesWithIV = new byte[iv.length + encryptedMessageBytes.length];
            System.arraycopy(iv, 0, encryptedMessageBytesWithIV, 0, iv.length);
            System.arraycopy(encryptedMessageBytes, 0, encryptedMessageBytesWithIV, iv.length, encryptedMessageBytes.length);
            return encryptedMessageBytesWithIV;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String decryptMessage(byte[] encryptedMessage, SecretKey secretKey) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] iv = new byte[cipher.getBlockSize()];
            System.arraycopy(encryptedMessage, 0, iv, 0, iv.length);
            byte[] encryptedMessageBytes = new byte[encryptedMessage.length - iv.length];
            System.arraycopy(encryptedMessage, iv.length, encryptedMessageBytes, 0, encryptedMessageBytes.length);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
            byte[] decryptedMessageBytes = cipher.doFinal(encryptedMessageBytes);
            return new String(decryptedMessageBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }




    @Override
    public void onStart() {
        super.onStart();
        messagesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(messagesAdapter!=null)
        {
            messagesAdapter.notifyDataSetChanged();
        }
    }



}
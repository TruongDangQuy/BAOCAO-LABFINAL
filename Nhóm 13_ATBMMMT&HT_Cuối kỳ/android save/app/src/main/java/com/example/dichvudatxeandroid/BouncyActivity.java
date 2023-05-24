package com.example.dichvudatxeandroid;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.security.Security;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
public class BouncyActivity extends AppCompatActivity {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS7Padding";
    private static final String KEY = "SecretKey12345"; // Khóa cố định cho ví dụ

    private EditText editText;
    private Button encryptButton;
    private Button decryptButton;
    private TextView resultTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bouncy);

        Security.addProvider(new BouncyCastleProvider());

        editText = findViewById(R.id.editText);
        encryptButton = findViewById(R.id.encryptButton);
        decryptButton = findViewById(R.id.decryptButton);
        resultTextView = findViewById(R.id.resultTextView);

        encryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String inputText = editText.getText().toString();
                    byte[] inputBytes = inputText.getBytes("UTF-8");

                    SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes("UTF-8"), ALGORITHM);
                    Cipher cipher = Cipher.getInstance(TRANSFORMATION);
                    cipher.init(Cipher.ENCRYPT_MODE, keySpec);

                    byte[] outputBytes = cipher.doFinal(inputBytes);
                    String outputText = new String(outputBytes, "UTF-8");

                    resultTextView.setText(outputText);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        decryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String inputText = resultTextView.getText().toString();
                    byte[] inputBytes = inputText.getBytes("UTF-8");

                    SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes("UTF-8"), ALGORITHM);
                    Cipher cipher = Cipher.getInstance(TRANSFORMATION);
                    cipher.init(Cipher.DECRYPT_MODE, keySpec);

                    byte[] outputBytes = cipher.doFinal(inputBytes);
                    String outputText = new String(outputBytes, "UTF-8");

                    editText.setText(outputText);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

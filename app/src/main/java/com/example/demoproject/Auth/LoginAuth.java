package com.example.demoproject.Auth;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.demoproject.ProductsListActivity;
import com.example.demoproject.R;
import com.google.firebase.auth.FirebaseAuth;

public class LoginAuth extends AppCompatActivity {

    TextView textViewNotMember,forgotTextLink,textViewLogin;
    EditText txt_email_login,txt_password_login;
    CheckBox checkBox ;
    Button buttonLogin, buttonSingUp;
    ProgressBar progressBar;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_auth);

        textViewLogin = findViewById(R.id.txtViewLogin);
        textViewNotMember = findViewById(R.id.textViewBecomeMember);
        forgotTextLink = findViewById(R.id.txtViewForgotPassword);
        txt_email_login = findViewById(R.id.txt_email_login);
        txt_password_login = findViewById(R.id.txt_password_login);
        checkBox = findViewById(R.id.checkBoxForPassword);
        buttonLogin = findViewById(R.id.btn_login);
        buttonSingUp = findViewById(R.id.btn_singUp);
        progressBar = findViewById(R.id.progressBarLogin);
        firebaseAuth = FirebaseAuth.getInstance();

        buttonLogin.setOnClickListener(v -> {

            String email = txt_email_login.getText().toString().trim();
            String password = txt_password_login.getText().toString().trim();

            if(TextUtils.isEmpty(email)){
                txt_email_login.setError("Email is required");
                return;
            }

            if(TextUtils.isEmpty(password)){
                txt_password_login.setError("Password is required");
                return;
            }
            progressBar.setVisibility(View.VISIBLE);


            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Toast.makeText(LoginAuth.this, "Logged is Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), ProductsListActivity.class));

                    finish();
                }else {
                    Toast.makeText(LoginAuth.this, "Error!!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        buttonSingUp.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), SingAuth.class));
            finish();
        });

        forgotTextLink.setOnClickListener(v -> {
            final EditText resetMail = new EditText(v.getContext());
            final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
            passwordResetDialog.setTitle("Do you want to reset your password?");
            passwordResetDialog.setMessage("Enter your e-mail to recieved reset link.");
            passwordResetDialog.setView(resetMail);

            passwordResetDialog.setPositiveButton("Yes", (dialog, which) -> {

                String mail = resetMail.getText().toString();
                firebaseAuth.sendPasswordResetEmail(mail).addOnSuccessListener(aVoid -> Toast.makeText(LoginAuth.this, "Password reset link has just sent your e-mail ",
                        Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(LoginAuth.this, "Error! Link did not send", Toast.LENGTH_SHORT).show());
            });

            passwordResetDialog.setNegativeButton("No", (dialog, which) -> {
            });
            passwordResetDialog.create().show();
        });


    }
 }

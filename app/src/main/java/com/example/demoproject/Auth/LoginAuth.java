package com.example.demoproject.Auth;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.demoproject.Activity.ProductsListActivity;
import com.example.demoproject.R;
import com.google.firebase.auth.FirebaseAuth;

public class LoginAuth extends AppCompatActivity {

    TextView txtView_info,txtView_forgot_password;
    EditText edtText_userEmail_login,edtText_password_login;
    Button btn_signIn, btn_join_us,btn_learn_more;
    ProgressBar progressBar;
    //animation
    LinearLayout l1,l2;
    Animation uptoDown,downtoUp;
    //firebase
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_auth);
        l1 =(LinearLayout) findViewById(R.id.l1);
        l2 =(LinearLayout) findViewById(R.id.l2);
        txtView_info = findViewById(R.id.txtView_info);
        txtView_forgot_password = findViewById(R.id.txtView_forgot_password);
        edtText_userEmail_login = findViewById(R.id.edtText_userEmail_login);
        edtText_password_login = findViewById(R.id.edtText_password_login);
        btn_signIn = findViewById(R.id.btn_signIn);
        btn_join_us = findViewById(R.id.btn_join_us);
        btn_learn_more = findViewById(R.id.btn_learn_more);
        progressBar = findViewById(R.id.progressBar);
        //animation
        uptoDown = AnimationUtils.loadAnimation(this,R.anim.up_to_down);
        downtoUp = AnimationUtils.loadAnimation(this,R.anim.down_to_up);
        l1.setAnimation(uptoDown);
        l2.setAnimation(downtoUp);
        //firebase
        firebaseAuth = FirebaseAuth.getInstance();

        btn_signIn.setOnClickListener(v -> {
            String email = edtText_userEmail_login.getText().toString().trim();
            String password = edtText_password_login.getText().toString().trim();
            if(TextUtils.isEmpty(email)){
                edtText_userEmail_login.setError("Email is required");
                return;
            }
            if(TextUtils.isEmpty(password)){
                edtText_password_login.setError("Password is required");
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
        btn_join_us.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), SingAuth.class));
            finish();
        });
        btn_learn_more.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://sdg-tracker.org/quality-education"));
            startActivity(browserIntent);
        });
        txtView_forgot_password.setOnClickListener(v -> {
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

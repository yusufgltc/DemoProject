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
import com.example.demoproject.databinding.ActivityLoginAuthBinding;
import com.google.firebase.auth.FirebaseAuth;

public class LoginAuth extends AppCompatActivity {
    //binding
    private ActivityLoginAuthBinding binding;
    //animation
    Animation uptoDown,downtoUp;
    //firebase
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //binding
        binding = ActivityLoginAuthBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        //animation
        uptoDown = AnimationUtils.loadAnimation(this,R.anim.up_to_down);
        downtoUp = AnimationUtils.loadAnimation(this,R.anim.down_to_up);
        binding.l1.setAnimation(uptoDown);
        binding.l2.setAnimation(downtoUp);
        //firebase
        firebaseAuth = FirebaseAuth.getInstance();
        binding.btnSignIn.setOnClickListener(v -> {
            String email = binding.edtTextUserEmailLogin.getText().toString().trim();
            String password = binding.edtTextPasswordLogin.getText().toString().trim();
            if(TextUtils.isEmpty(email)){
                binding.edtTextUserEmailLogin.setError("Email is required");
                return;
            }
            if(TextUtils.isEmpty(password)){
                binding.edtTextPasswordLogin.setError("Password is required");
                return;
            }
            binding.progressBar.setVisibility(View.VISIBLE);
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
        binding.btnJoinUs.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), SingAuth.class));
            finish();
        });
        binding.btnLearnMore.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://sdg-tracker.org/quality-education"));
            startActivity(browserIntent);
        });
        binding.txtViewForgotPassword.setOnClickListener(v -> {
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

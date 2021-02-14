package com.example.demoproject.Auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.demoproject.ProductsListActivity;
import com.example.demoproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SingAuth extends AppCompatActivity {

    public static final String TAG = "TAG";

    EditText txt_name_singUp,txt_surname_singUp,txt_username_singUp,txt_email_singUp,txt_password_singUp,txt_user_university_singUp,txt_user_departmant_singUp,txt_user_bio_singUp;
    ImageView imgView_user_singUp;
    Button singUpBtnCreateAccount;
    ProgressBar progressBar;

    FirebaseAuth firebaseAuth ;
    FirebaseFirestore firebaseFirestore;
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup_auth);

        txt_name_singUp = findViewById(R.id.txt_name_singUp);
        txt_surname_singUp = findViewById(R.id.txt_surname_singUp);
        txt_username_singUp = findViewById(R.id.txt_username_singUp);
        txt_email_singUp = findViewById(R.id.txt_email_singUp);
        txt_password_singUp = findViewById(R.id.txt_password_singUp);
        txt_user_university_singUp = findViewById(R.id.txt_user_university_singUp);
        txt_user_departmant_singUp = findViewById(R.id.txt_user_departmant_singUp);
        txt_user_bio_singUp= findViewById(R.id.txt_user_bio_singUp);
        imgView_user_singUp = findViewById(R.id.imgView_user_singUp);
        singUpBtnCreateAccount = findViewById(R.id.singUpBtnCreateAccount);
        progressBar = findViewById(R.id.progressBar);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        if (firebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), ProductsListActivity.class));
            finish();
        }


        singUpBtnCreateAccount.setOnClickListener(v -> {

            final String name = txt_name_singUp.getText().toString();
            final String surname = txt_surname_singUp.getText().toString();
            final String username = txt_username_singUp.getText().toString();
            final String email = txt_email_singUp.getText().toString().trim();
            String password = txt_password_singUp.getText().toString().trim();
            final String university = txt_user_university_singUp.getText().toString();
            final String departmant = txt_user_departmant_singUp.getText().toString();
            final String bio = txt_user_bio_singUp.getText().toString();


            if(TextUtils.isEmpty(username)){
                txt_username_singUp.setError("Username is required");
                return;
            }

            if(TextUtils.isEmpty(email)){
                txt_email_singUp.setError("Email is required");
                return;
            }

            if(TextUtils.isEmpty(password)){
                txt_password_singUp.setError("Password is required");
                return;
            }
            progressBar.setVisibility(View.VISIBLE);

            //Register the user in firebase

            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Toast.makeText(SingAuth.this, "User Created", Toast.LENGTH_SHORT).show();
                    userID = firebaseAuth.getUid();
                    DocumentReference documentReference = firebaseFirestore.collection("Users").document(userID);
                    Map<String, Object> user = new HashMap<>();
                    user.put("First Name",name);
                    user.put("Last Name",surname);
                    user.put("Username",username);
                    user.put("E-mail",email);
                    user.put("University",university);
                    user.put("Departmant",departmant);
                    user.put("Bio",bio);

                    documentReference.set(user).addOnSuccessListener(aVoid -> Log.d(TAG, "onSuccess: user profile is created for " + userID));
                    startActivity(new Intent(getApplicationContext(), ProductsListActivity.class));
                    finish();
                }else {
                    Toast.makeText(SingAuth.this, "Error!!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        });


    }
}
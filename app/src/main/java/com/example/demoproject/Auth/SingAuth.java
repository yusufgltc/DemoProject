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

import com.example.demoproject.Activity.ProductsListActivity;
import com.example.demoproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SingAuth extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText edtText_user_names_sign,edtText_user_email_sign,
            edtText_user_university_sign,edtText_user_departmant_sign,
            edtText_user_bio_sign,edtText_user_password_sign;
    ImageView imgView_backTo_sign,imgView_user_img_sign;
    Button btn_signUp;
    ProgressBar progressBar;
    //Firebase
    FirebaseAuth firebaseAuth ;
    FirebaseFirestore firebaseFirestore;
    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup_auth);

        edtText_user_names_sign = findViewById(R.id.edtText_user_names_sign);
        edtText_user_email_sign = findViewById(R.id.edtText_user_email_sign);
        edtText_user_university_sign = findViewById(R.id.edtText_user_university_sign);
        edtText_user_departmant_sign = findViewById(R.id.edtText_user_departmant_sign);
        edtText_user_bio_sign = findViewById(R.id.edtText_user_bio_sign);
        edtText_user_password_sign = findViewById(R.id.edtText_user_password_sign);
        imgView_backTo_sign = findViewById(R.id.imgView_backTo_sign);
        imgView_user_img_sign = findViewById(R.id.imgView_user_img_sign);
        btn_signUp= findViewById(R.id.btn_signUp);
        progressBar = findViewById(R.id.progressBar);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        if (firebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), ProductsListActivity.class));
            finish();
        }
        imgView_backTo_sign.setOnClickListener(v -> {
            Intent backTo = new Intent(SingAuth.this,LoginAuth.class);
            startActivity(backTo);
            finish();
        });
        btn_signUp.setOnClickListener(v -> {
            final String nameandsurname = edtText_user_names_sign.getText().toString();
            final String email = edtText_user_email_sign.getText().toString().trim();
            String password = edtText_user_password_sign.getText().toString().trim();
            final String university = edtText_user_university_sign.getText().toString();
            final String departmant = edtText_user_departmant_sign.getText().toString();
            final String bio = edtText_user_bio_sign.getText().toString();

            if(TextUtils.isEmpty(nameandsurname)){
                edtText_user_names_sign.setError("Username is required");
                return;
            }if(TextUtils.isEmpty(email)){
                edtText_user_email_sign.setError("Email is required");
                return;
            }if(TextUtils.isEmpty(password)){
                edtText_user_password_sign.setError("Password is required");
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
                    user.put("First and Name",nameandsurname);
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
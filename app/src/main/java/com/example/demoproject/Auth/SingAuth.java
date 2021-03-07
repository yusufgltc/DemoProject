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
import com.example.demoproject.databinding.ActivitySingupAuthBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SingAuth extends AppCompatActivity {
    //binding
    private ActivitySingupAuthBinding binding;
    public static final String TAG = "TAG";
    //Firebase
    FirebaseAuth firebaseAuth ;
    FirebaseFirestore firebaseFirestore;
    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //bar
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);
        setTitle("Sign Up");
        //binding
        binding = ActivitySingupAuthBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        //firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        if (firebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), ProductsListActivity.class));
            finish();
        }
        binding.btnSignUp.setOnClickListener(v -> {
            final String nameandsurname = binding.edtTextUserNamesSign.getText().toString();
            final String email = binding.edtTextUserEmailSign.getText().toString().trim();
            String password = binding.edtTextUserPasswordSign.getText().toString().trim();
            final String university = binding.edtTextUserUniversitySign.getText().toString();
            final String departmant = binding.edtTextUserDepartmantSign.getText().toString();
            final String bio = binding.edtTextUserBioSign.getText().toString();
            if(TextUtils.isEmpty(nameandsurname)){
                binding.edtTextUserNamesSign.setError("User names are required");
                return;
            }if(TextUtils.isEmpty(email)){
                binding.edtTextUserEmailSign.setError("Email is required");
                return;
            }if(TextUtils.isEmpty(password)){
                binding.edtTextUserPasswordSign.setError("Password is required");
                return;
            }
            binding.progressBarSign.setVisibility(View.VISIBLE);
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
                    binding.progressBarSign.setVisibility(View.GONE);
                }
            });
        });
    }
}
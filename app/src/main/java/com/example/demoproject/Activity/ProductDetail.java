package com.example.demoproject.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.demoproject.Auth.LoginAuth;
import com.example.demoproject.Auth.SingAuth;
import com.example.demoproject.R;
import com.example.demoproject.databinding.ActivityProductDetailBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProductDetail extends AppCompatActivity {
    //binding
    private ActivityProductDetailBinding binding;
    //firebase
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //bar
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24);
        setTitle("Product Detail");
        dialog = new Dialog(this);
        //binding
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        getIncomingIntent();
        binding.btnShareProductDetail.setOnClickListener(v -> {
            if (firebaseUser != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProductDetail.this);
                builder.setTitle("EduShare");
                builder.setMessage("You are transferred to Gmail. Do you confirm that?");
                builder.setNegativeButton("No",null);
                builder.setPositiveButton("Yes", (dialog, which) -> {
                     Intent intent = new Intent(Intent.ACTION_SENDTO);
                     intent.setData(Uri.parse("mailto:"));
                     intent.setType("text/plain");
                     intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getIntent().getStringExtra("email")});
                     intent.putExtra(Intent.EXTRA_SUBJECT,"EduShare");
                     intent.putExtra(Intent.EXTRA_TEXT,"Hi there!! I want to get more information about" + " " + getIntent().getStringExtra("title"));
                     startActivity(Intent.createChooser(intent,"Send mail.."));
                });
                builder.show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProductDetail.this);
                builder.setTitle("EduShare");
                builder.setMessage("Ohhh boże!! You have to be member for sharing stuff. Do you want to sign in/up ?");
                builder.setNegativeButton("No",null);
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    startActivity(new Intent(ProductDetail.this, LoginAuth.class));
                });
                builder.show();
            }
        });

        binding.txtViewUserEmail.setOnClickListener(v -> {
            ShowPopup(v);
        });
    }
    private void getIncomingIntent(){
        Glide.with(this)
                .asBitmap()
                .load(getIntent().getStringExtra("imageUrl"))
                .into(binding.imageViewProductDetail);
        binding.txtProductTitleDetail.setText("Title : " + getIntent().getStringExtra("title"));
        binding.txtProductCategoryDetail.setText("Category : " + getIntent().getStringExtra("category"));
        binding.txtProductQualityDetail.setText("Product Quality : " + "5/"+ getIntent().getStringExtra("rating"));
        binding.txtProductDescriptionDetail.setText("Description : "+ getIntent().getStringExtra("description"));
        binding.txtProductDateDetail.setText("Upload Date : "+getIntent().getStringExtra("date"));
        binding.txtViewUserEmail.setText("shared by : "+getIntent().getStringExtra("email"));
    }

    public void ShowPopup(View v) {
        dialog.setContentView(R.layout.user_info_popup);
        dialog.dismiss();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}
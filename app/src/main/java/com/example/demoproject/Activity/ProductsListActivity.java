package com.example.demoproject.Activity;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.demoproject.Adapter.ProductRecyclerAdapter;
import com.example.demoproject.Auth.LoginAuth;
import com.example.demoproject.Model.Products;
import com.example.demoproject.R;
import com.example.demoproject.databinding.ActivityProductsListBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;

public class ProductsListActivity extends AppCompatActivity implements ProductRecyclerAdapter.OnItemClickListener{
    //binding
    private ActivityProductsListBinding binding;
    //firebase
    FirebaseFirestore firebaseFirestore;
    CollectionReference collectionReference;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    RecyclerView recyclerView;
    Dialog dialog;
    ProductRecyclerAdapter productRecyclerAdapter;
    //ArrayList
    ArrayList<String> productImageUrl;
    ArrayList<String> productTitle;
    ArrayList<String> productCategory;
    ArrayList<String> productUploadDate;
    ArrayList<Float> productQuality;
    ArrayList<String> productDescription;
    ArrayList<String> productEmail;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductsListBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        //first time using app
        SharedPreferences sharedPreferences = getSharedPreferences("pref",MODE_PRIVATE);
        boolean firstStart = sharedPreferences.getBoolean("firstStart",true);
        if (firstStart){
            checkFirstTime();
        }
        recyclerView = findViewById(R.id.recyclerView);
        dialog = new Dialog(this);
        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("Posts");
        //list
        productImageUrl = new ArrayList<>();
        productTitle =new ArrayList<>();
        productCategory = new ArrayList<>();
        productQuality = new ArrayList<>();
        productDescription = new ArrayList<>();
        productUploadDate = new ArrayList<>();
        productEmail= new ArrayList<>();

        binding.btnAddProduct.setOnClickListener(v -> {
            if (firebaseUser != null) {
                startActivity(new Intent(ProductsListActivity.this,UploadProductActivity.class));
            } else {
                // No user is signed in
                AlertDialog.Builder builder = new AlertDialog.Builder(ProductsListActivity.this);
                builder.setTitle("EduShare");
                builder.setMessage("You have to be member for sharing stuff. Do you want to sign in/up ?");
                builder.setNegativeButton("No",null);
                builder.setPositiveButton("Yes", (dialog, which) ->
                        startActivity(new Intent(ProductsListActivity.this, LoginAuth.class)));
                builder.show();
            }
        });
        setUpRecyclerView();
    }

    private void checkFirstTime() {
        SharedPreferences sharedPreferences = getSharedPreferences("pref",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("firstStart",false);
        editor.apply();
        Intent intent = new Intent(ProductsListActivity.this,AppIntroActivity.class);
        startActivity(intent);
    }

    private void setUpRecyclerView() {
        Query query = collectionReference.orderBy("date", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Products> options = new FirestoreRecyclerOptions.Builder<Products>()
                .setQuery(query, Products.class)
                .build();
        productRecyclerAdapter = new ProductRecyclerAdapter(options);
        //initializeViews
        recyclerView.setAdapter(productRecyclerAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        //IntentToProductDetail
        productRecyclerAdapter.setOnItemClickListener((documentSnapshot, position) -> {
            Products products = documentSnapshot.toObject(Products.class);
            //String id = documentSnapshot.getId();
            //String path = documentSnapshot.getReference().getPath();
            //Toast.makeText(this, "Position" + position, Toast.LENGTH_SHORT).show();/

                Intent intent = new Intent(ProductsListActivity.this,ProductDetail.class);
                intent.putExtra("imageUrl",products.getImageUrl());
                intent.putExtra("title",products.getTitle());
                intent.putExtra("category",products.getCategory());
                intent.putExtra("rating",products.getRating().toString());
                intent.putExtra("description",products.getDescription());
                intent.putExtra("email",products.getEmail());
                intent.putExtra("date",products.getDate());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        productRecyclerAdapter.startListening();
    }
    @Override
    protected void onStop() {
        super.onStop();
        productRecyclerAdapter.stopListening();
    }

    @Override
    public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
        //
    }
}
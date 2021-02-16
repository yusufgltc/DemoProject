package com.example.demoproject.Activity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.demoproject.Adapter.ProductRecyclerAdapter;
import com.example.demoproject.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Map;

public class ProductsListActivity extends AppCompatActivity {
    FirebaseFirestore firebaseFirestore;
    RecyclerView recyclerView;
    Dialog dialog;
    ProductRecyclerAdapter productRecyclerAdapter;
    //ArrayList
    ArrayList<String> productTitle;
    ArrayList<String> productCategory;
    ArrayList<String> productImageUrl;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // dialog.findViewById... try it
        recyclerView = findViewById(R.id.recyclerView);
        dialog = new Dialog(this);
        firebaseFirestore = FirebaseFirestore.getInstance();
        //list
        productTitle =new ArrayList<>();
        productCategory = new ArrayList<>();
        productImageUrl = new ArrayList<>();

        getDataFromFirebase();
        initializeViews();
    }
    public void showPopup (View view){
        TextView txtclose;

        dialog.setContentView(R.layout.user_info_popup);
        txtclose = dialog.findViewById(R.id.txtclose);
        txtclose.setOnClickListener(v -> dialog.dismiss());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
    private void initializeViews() {
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        productRecyclerAdapter = new ProductRecyclerAdapter(productTitle,productCategory,productImageUrl);
        recyclerView.setAdapter(productRecyclerAdapter);
    }
    private void getDataFromFirebase() {
        CollectionReference collectionReference = firebaseFirestore.collection("Posts");
        collectionReference.orderBy("date", Query.Direction.DESCENDING).addSnapshotListener((value, error) -> {
            if(error!=null){
                Toast.makeText(ProductsListActivity.this, error.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
            if (value != null){
                for (DocumentSnapshot snapshot : value.getDocuments()){
                    Map<String,Object> data = snapshot.getData();
                    String productTitleFb = (String) data.get("titlePruduct");
                    String productCategoryFb = (String) data.get("spinnerProduct");
                    String productImageUrlFb = (String) data.get("imageUrl");

                    productTitle.add(productTitleFb);
                    productCategory.add(productCategoryFb);
                    productImageUrl.add(productImageUrlFb);
                    productRecyclerAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}
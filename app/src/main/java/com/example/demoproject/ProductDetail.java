package com.example.demoproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ProductDetail extends AppCompatActivity {

    TextView txt_product_title_detail,txt_product_category_detail,txt_product_date_detail,
            txt_product_quality_detail,txt_product_description_detail;
    ImageView imageView_product_detail;
    Button btn_share_product_detail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);


        txt_product_title_detail = findViewById(R.id.txt_product_title_detail);
        txt_product_category_detail = findViewById(R.id.txt_product_category_detail);
        txt_product_date_detail = findViewById(R.id.txt_product_date_detail);
        txt_product_quality_detail = findViewById(R.id.txt_product_quality_detail);
        txt_product_description_detail = findViewById(R.id.txt_product_description_detail);
        imageView_product_detail = findViewById(R.id.imageView_product_detail);
        btn_share_product_detail = findViewById(R.id.btn_share_product_detail);


    }


}
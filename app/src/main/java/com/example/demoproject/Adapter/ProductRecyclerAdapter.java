package com.example.demoproject.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demoproject.ProductDetail;
import com.example.demoproject.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductRecyclerAdapter extends RecyclerView.Adapter<ProductRecyclerAdapter.PostHolder> {
     ArrayList<String> productTitleList;
     ArrayList<String> productCategoryList;
     ArrayList<String> productImageUrlList;
    public ProductRecyclerAdapter(ArrayList<String> productTitle,
                                  ArrayList<String> productCategory,
                                  ArrayList<String> productImageUrl) {
        this.productTitleList = productTitle;
        this.productCategoryList = productCategory;
        this.productImageUrlList = productImageUrl;
    }
    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_product,parent,false);
        return new PostHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        holder.productTitle.setText(productTitleList.get(position));
        holder.productCategory.setText(productCategoryList.get(position));
        Picasso.get().load(productImageUrlList.get(position)).into(holder.productImageView);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProductDetail.class);
           // intent.putExtra();
            v.getContext().startActivity(intent);
        });
    }
    @Override
    public int getItemCount() {
        return productTitleList.size();
    }
    class PostHolder extends RecyclerView.ViewHolder{
        TextView productTitle,productCategory;
        ImageView productImageView;
        public PostHolder(@NonNull View itemView) {
            super(itemView);
            productTitle = itemView.findViewById(R.id.textView_product_title_row);
            productCategory = itemView.findViewById(R.id.textView_product_categoryName_row);
            productImageView = itemView.findViewById(R.id.imageView_product_row);
        }
    }
}

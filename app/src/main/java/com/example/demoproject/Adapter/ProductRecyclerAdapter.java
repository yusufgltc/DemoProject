package com.example.demoproject.Adapter;
import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.demoproject.Model.Products;
import com.example.demoproject.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class ProductRecyclerAdapter extends FirestoreRecyclerAdapter<Products,ProductRecyclerAdapter.ProductHolder> {
    private OnItemClickListener listener;
    
    public ProductRecyclerAdapter(@NonNull FirestoreRecyclerOptions<Products> options) {
        super(options);
    }
    @Override
    protected void onBindViewHolder(@NonNull ProductHolder holder, int position, @NonNull Products model) {
          holder.txtTitleProduct.setText(model.getTitle());
          holder.txtCategoryProduct.setText(model.getCategory());
          Picasso.get().load(model.getImageUrl()).into(holder.imgViewProduct);
    }
    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product,parent,false);
        return new ProductHolder(v);
    }
    class ProductHolder extends RecyclerView.ViewHolder{
        ImageView imgViewProduct;
        TextView  txtTitleProduct;
        TextView  txtCategoryProduct;

        @SuppressLint("ResourceType")
        public ProductHolder(@NonNull View itemView) {
            super(itemView);
            imgViewProduct = itemView.findViewById(R.id.imageView_product_row);
            txtTitleProduct = itemView.findViewById(R.id.textView_product_title_row);
            txtCategoryProduct = itemView.findViewById(R.id.textView_product_categoryName_row);
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION &&listener != null){
                    listener.onItemClick(getSnapshots().getSnapshot(position),position);
                }
            });
        }
    }
    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot,int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){ this.listener = listener; }
}

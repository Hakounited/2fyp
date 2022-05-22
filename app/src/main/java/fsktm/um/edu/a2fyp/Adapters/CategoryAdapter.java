package fsktm.um.edu.a2fyp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fsktm.um.edu.a2fyp.Activities.ChatActivity;
import fsktm.um.edu.a2fyp.Models.Product;
import fsktm.um.edu.a2fyp.databinding.CategoryCardBinding;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final List<Product> categoryProducts;
    Context context;

    public CategoryAdapter(List<Product> categoryProducts, Context context) {
        this.categoryProducts = categoryProducts;
        this.context = context;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CategoryCardBinding categoryCardBinding = CategoryCardBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        return new CategoryViewHolder(categoryCardBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.setCategoryDetails(categoryProducts.get(position));
    }

    @Override
    public int getItemCount() {
        return categoryProducts.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {

        CategoryCardBinding binding;

        CategoryViewHolder(CategoryCardBinding categoryCardBinding) {
            super(categoryCardBinding.getRoot());
            binding = categoryCardBinding;
        }

        void setCategoryDetails(Product product) {
            binding.categoryProductName.setText(product.getProductName());
            binding.categoryDescription.setText(product.getProductDescription());
            binding.chosenPrice.setText(product.getProductPrice());
            binding.categoryProductImg.setImageBitmap(getProductImg(product.getProductImg()));
            binding.categoryPostedBy.setText(product.getPostedBy());



        }

    }


    private Bitmap getProductImg(String encodedImg) {
        byte[] bytes = Base64.decode(encodedImg, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
}



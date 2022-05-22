package fsktm.um.edu.a2fyp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fsktm.um.edu.a2fyp.Activities.ChatActivity;
import fsktm.um.edu.a2fyp.Activities.ViewProductActivity;
import fsktm.um.edu.a2fyp.Models.Product;
import fsktm.um.edu.a2fyp.databinding.ProductCardBinding;
import fsktm.um.edu.a2fyp.utilities.Constants;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>  {

    private final List<Product> products;
    Context context;

    public ProductsAdapter(List<Product> products, Context context) {
        this.products = products;
        this.context = context;
    }


    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       ProductCardBinding productCardBinding = ProductCardBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ProductViewHolder(productCardBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {

        holder.setProductDetail(products.get(position));
//        holder.binding.productImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(context, ViewProductActivity.class);
//                intent.putExtra("product_name",holder.binding.productTitle.toString());
//                intent.putExtra("product_descriptioin",holder.binding.productDescription.toString());
//                intent.putExtra("product_price", holder.binding.chosenPrice.toString());
//                intent.putExtra("product_img",holder.binding.productImg.toString());
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intent);
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        ProductCardBinding binding;
        String img;

        ProductViewHolder(ProductCardBinding productCardBinding) {
            super(productCardBinding.getRoot());
            binding = productCardBinding;
        }

        void setProductDetail(Product product) {
            binding.productTitle.setText(product.getProductName());
            binding.productDescription.setText(product.getProductDescription());
            binding.chosenPrice.setText(product.getProductPrice());
            binding.productImg.setImageBitmap(getProductImg(product.getProductImg()));
            binding.postedBy.setText(product.getPostedBy());

            binding.productImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ViewProductActivity.class);
                    intent.putExtra("product_name",binding.productTitle.getText().toString());
                    intent.putExtra("product_description",binding.productDescription.getText().toString());
                    intent.putExtra("product_price", binding.chosenPrice.getText().toString());
//                    intent.putExtra("product_img",binding.productImg.); pass the img
                    intent.putExtra("posted_by", product.getPostedBy());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
            binding.contactSeller.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("posted_by",product.getPostedBy().toString());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }
    }

    private Bitmap getProductImg(String encodedImg) {
        byte[] bytes = Base64.decode(encodedImg, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
}

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
import fsktm.um.edu.a2fyp.databinding.CartCardBinding;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder>{

    private final List<Product> cartProducts;
    Context context;

    public CartAdapter(List<Product> cartProducts, Context context) {
        this.cartProducts = cartProducts;
        this.context = context;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CartCardBinding cartCardBinding = CartCardBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,false
        );

        return new CartViewHolder(cartCardBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        holder.setCartDetails(cartProducts.get(position));
    }

    @Override
    public int getItemCount() {
        return cartProducts.size();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {

        CartCardBinding binding;

        CartViewHolder(CartCardBinding cartCardBinding) {
            super(cartCardBinding.getRoot());
            binding = cartCardBinding;

        }

        void setCartDetails(Product product) {
            binding.cartProductName.setText(product.getProductName());
            binding.cartDescription.setText(product.getProductDescription());
            binding.cartChosenPrice.setText(product.getProductPrice());
//            binding.cartProductImg.setImageBitmap(getProductImg(product.getProductImg()));
            binding.cartPostedBy.setText(product.getPostedBy());

            binding.cartContactSeller.setOnClickListener(new View.OnClickListener() {
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

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
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import fsktm.um.edu.a2fyp.Activities.ChatActivity;
import fsktm.um.edu.a2fyp.Activities.PlaceOrder;
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


            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            binding.cartProductName.setText(product.getProductName());
            binding.cartDescription.setText(product.getProductDescription());
            binding.cartChosenPrice.setText(product.getProductPrice());
            binding.cartProductId.setText(product.getProductId());
//            binding.cartProductImg.setImageBitmap(getImg(product.getProductImg()));
////            binding.cartProductImg.setImageBitmap(getProductImg(product.productImg));
//            showToast(product.productImg);
            binding.cartPostedBy.setText(product.getPostedBy());

            FirebaseFirestore fstore = FirebaseFirestore.getInstance();
            DocumentReference ref = fstore.collection("products").document(product.getProductId());
            ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {

                        DocumentSnapshot ds = task.getResult();
                        if (ds.exists()) {
                            String productImg = ds.getString("product image");

                            binding.cartProductImg.setImageBitmap(getImg(productImg));
                            binding.cartProductImg.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    Intent intent = new Intent(context, PlaceOrder.class);
                                    intent.putExtra("name", product.getProductName());
                                    intent.putExtra("posted_by",product.getPostedBy());
                                    intent.putExtra("price", product.getProductPrice());
                                    intent.putExtra("image", productImg);
                                    intent.putExtra("description", product.getProductDescription());
                                    intent.putExtra("product_id", product.getProductId());
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                }
                            });
                        }
                    }
                }
            });

            String loggedInUser = mAuth.getCurrentUser().getUid();
            DocumentReference docRef = firestore.collection("users").document(loggedInUser);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            String loggedInUserName = documentSnapshot.getString("name");

                        }
                    }
                }
            });



            binding.cartContactSeller.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("posted_by",product.getPostedBy());
                    intent.putExtra("from", "cartAdapter");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });


        }

    }

    private void showToast(String message){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }


    private Bitmap getImg(String encodedImg) {
        byte[] bytes = Base64.decode(encodedImg, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
}

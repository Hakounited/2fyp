package fsktm.um.edu.a2fyp.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import fsktm.um.edu.a2fyp.Models.Product;
import fsktm.um.edu.a2fyp.databinding.MyProductsCardBinding;
import fsktm.um.edu.a2fyp.utilities.Constants;

public class MyProductsAdapter extends RecyclerView.Adapter<MyProductsAdapter.MYProductViewHolder> {

    List<Product> myProductlist;
    Context context;

    public MyProductsAdapter(List<Product> products, Context context) {
        this.myProductlist = products;
        this.context = context;
    }

    @NonNull
    @Override
    public MYProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MyProductsCardBinding productsCardBinding = MyProductsCardBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new MYProductViewHolder(productsCardBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MYProductViewHolder holder, int position) {

        holder.setMyProductDetails(myProductlist.get(position));
    }


    @Override
    public int getItemCount() {
        return myProductlist.size();
    }

    class MYProductViewHolder extends RecyclerView.ViewHolder {

        MyProductsCardBinding binding;

        MYProductViewHolder(MyProductsCardBinding myProductsCardBinding){
            super(myProductsCardBinding.getRoot());
            binding = myProductsCardBinding;
        }

        void setMyProductDetails(Product product) {
            binding.productTitle.setText(product.getProductName());
            binding.productDescription.setText(product.getProductDescription());
            binding.chosenPrice.setText(product.getProductPrice());
            binding.productImg.setImageBitmap(getProductImg(product.getProductImg()));

            binding.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deketeProduct(binding.productTitle.getText().toString());
                }
            });
        }
    }

    private void deketeProduct(String productName) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firestore.collection(Constants.KEY_COLLECTION_PRODUCTS).document(productName);
                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        showToast("deleted");
                    }
                });

    }

    private void showToast(String message) {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show();
    }

    private Bitmap getProductImg(String encodedImg) {
        byte[] bytes = Base64.decode(encodedImg, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
}

package fsktm.um.edu.a2fyp.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import fsktm.um.edu.a2fyp.Activities.EditProduct;
import fsktm.um.edu.a2fyp.Activities.MyProductsActivity;
import fsktm.um.edu.a2fyp.Activities.PlaceOrder;
import fsktm.um.edu.a2fyp.Models.Product;
import fsktm.um.edu.a2fyp.R;
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

            String product_id= product.getProductId();
            showToast(product_id);
            binding.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getRootView().getContext());


                    builder.setTitle("Warning");
                    builder.setMessage("Are you sure you want to delete your product?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();

                            deleteProduct(product_id);
                            myProductlist.remove(getAdapterPosition());
                            notifyItemRemoved(getAdapterPosition());

                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();

                        }
                    });
                    builder.show();



                }
            });

            binding.editMyProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, EditProduct.class);
                    intent.putExtra("product_id", product.getProductId());
                    intent.putExtra("product_img", product.getProductImg());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }
    }

    private void deleteProduct(String productId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firestore.collection(Constants.KEY_COLLECTION_PRODUCTS).document(productId);
                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        showToast("deleted");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast("failed");
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

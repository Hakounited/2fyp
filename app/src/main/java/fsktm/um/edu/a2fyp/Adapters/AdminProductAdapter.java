package fsktm.um.edu.a2fyp.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import fsktm.um.edu.a2fyp.Models.Product;
import fsktm.um.edu.a2fyp.databinding.AdminProductCardBinding;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.AdminProductViewHolder> {

    private final List<Product> productList;
    Context context;

    public AdminProductAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public AdminProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdminProductCardBinding adminProductCardBinding = AdminProductCardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new AdminProductViewHolder(adminProductCardBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminProductViewHolder holder, int position) {
        holder.setProductData(productList.get(position));

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class AdminProductViewHolder extends RecyclerView.ViewHolder {
        AdminProductCardBinding binding;

        AdminProductViewHolder(AdminProductCardBinding adminProductCardBinding) {
            super(adminProductCardBinding.getRoot());
            binding = adminProductCardBinding;
        }

        void setProductData(Product product) {
//            binding.productTitle.setText(product.getProductName());
//            binding.chosenPrice.setText(product.getProductPrice());
//            binding.productDescription.setText(product.getProductDescription());
//            binding.postedBy.setText(product.getPostedBy());
//            binding.productImg.setImageBitmap(getProductImg(product.getProductImg()));
            binding.contactSeller.setVisibility(View.GONE);
            binding.productTitle.setText(product.productName);
            binding.productDescription.setText(product.productDescription);
            binding.productImg.setImageBitmap(getProductImg(product.productImg));
            binding.postedBy.setText(product.postedBy);
            binding.chosenPrice.setText(product.productPrice);

            binding.productId.setText(product.productId);
//            showToast(binding.productId.getText().toString());

            binding.deleteProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getRootView().getContext());
                    builder.setTitle("Warning");
                    builder.setMessage("Are you sure you want to delete this product?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            deleteProduct(binding.productId.getText().toString());
                            productList.remove(getAdapterPosition());
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
//            showToast(product.getProductId());
        }
    }


    private void deleteProduct(String productId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference ref = firestore.collection("products").document(productId);
        ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                showToast("deleted");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showToast("Failed to delete product");
                showToast(e.getMessage());
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

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
            binding.productIdTv.setText(product.getProductId());

            FirebaseFirestore fstore = FirebaseFirestore.getInstance();

            fstore.collection("ratings").whereEqualTo("product_id",binding.productIdTv.getText().toString())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                String currentDocId = binding.productIdTv.getText().toString();
                                double numberOfRatings = 0;
                                String stringRating="";
                                double rating = 0;
                                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                    numberOfRatings++;
                                    stringRating = queryDocumentSnapshot.getString("rating");
                                    rating = rating + Double.parseDouble(stringRating);

                                }
                                if (numberOfRatings != 0){
                                    float f = (float) numberOfRatings;
//                                    showToast(String.valueOf(f));
                                    float average = (float) rating / f;
//                                    showToast(String.valueOf(average));
                                    binding.productRatingbar.setRating(average);
                                    DocumentReference ref = fstore.collection("products").document(binding.productIdTv.getText().toString());
                                    ref.update("rating", String.valueOf(average)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
//                                                showToast("product updated");
                                            }else{
//                                                showToast("failed to update product");
                                            }
                                        }
                                    });
                                }

                            }
                        }
                    });
//            fstore.collection("ratings").get()
//                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if (task.isSuccessful() && task.getResult()!=null) {
//                                double numOfRating = 0;
//
//                                for (QueryDocumentSnapshot qs : task.getResult()) {
//                                        numOfRating++;
//                                }
//
//                            }
//                        }
//                    });


            binding.productImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ViewProductActivity.class);
                    intent.putExtra("product_name",binding.productTitle.getText().toString());
                    intent.putExtra("product_description",binding.productDescription.getText().toString());
                    intent.putExtra("product_price", binding.chosenPrice.getText().toString());
                    intent.putExtra("product_img",product.getProductImg());
                    intent.putExtra("posted_by", binding.postedBy.getText().toString());

                    intent.putExtra("product_id", product.getProductId());

                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            String loggedInUserId = mAuth.getCurrentUser().getUid();
            DocumentReference docRef = firestore.collection("users").document(loggedInUserId);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            String loggedInUserName = documentSnapshot.getString("name");
                            if (loggedInUserName.equals(product.getPostedBy())) {
                                binding.contactSeller.setVisibility(View.GONE);
                            }
                        }
                    }
                }
            });

            binding.contactSeller.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("posted_by",product.getPostedBy().toString());
                    intent.putExtra("from", "productAdapter");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }
    }

    private void showToast(String message){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }

    private Bitmap getProductImg(String encodedImg) {
        byte[] bytes = Base64.decode(encodedImg, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
}

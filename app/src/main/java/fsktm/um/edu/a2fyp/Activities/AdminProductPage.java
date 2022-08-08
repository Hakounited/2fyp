package fsktm.um.edu.a2fyp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import fsktm.um.edu.a2fyp.Adapters.AdminProductAdapter;
import fsktm.um.edu.a2fyp.Models.Product;
import fsktm.um.edu.a2fyp.R;
import fsktm.um.edu.a2fyp.databinding.ActivityAdminProductPageBinding;

public class AdminProductPage extends AppCompatActivity {

    ActivityAdminProductPageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminProductPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        getProducts();
        setListeners();
    }

    private void setListeners() {

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void getProducts() {
        loading(true);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("products").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                loading(false);
                if (task.isSuccessful() && task.getResult() != null) {
                    List<Product> productList = new ArrayList<>();
                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                        Product product = new Product();
                        product.productName = queryDocumentSnapshot.getString("product name");
                        product.productPrice = queryDocumentSnapshot.getString("product price");
                        product.productDescription = queryDocumentSnapshot.getString("product description");
                        product.productImg = queryDocumentSnapshot.getString("product image");
                        product.postedBy = queryDocumentSnapshot.getString("posted by");
                        product.productId = queryDocumentSnapshot.getString("product Id");
                        productList.add(product);

                    }
                    if (productList.size() > 0) {
                        AdminProductAdapter adminProductAdapter = new AdminProductAdapter(productList, getApplicationContext());
                        binding.adminProductsRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        binding.adminProductsRecyclerview.setAdapter(adminProductAdapter);
                    } else {
                        showToast("product list empty");
                    }
                } else {
                    showToast("Error");
                    showToast(task.getException().getMessage());
                }
            }
        });

    }

    private void loading(Boolean isLoading){
        if (isLoading) {
//            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
//            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
    }
}
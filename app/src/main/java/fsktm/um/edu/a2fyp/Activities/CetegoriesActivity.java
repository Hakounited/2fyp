package fsktm.um.edu.a2fyp.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import fsktm.um.edu.a2fyp.Adapters.CategoryAdapter;
import fsktm.um.edu.a2fyp.Adapters.ProductsAdapter;
import fsktm.um.edu.a2fyp.Models.Product;
import fsktm.um.edu.a2fyp.R;
import fsktm.um.edu.a2fyp.databinding.ActivityCategoriesBinding;


public class CetegoriesActivity extends AppCompatActivity {

    ActivityCategoriesBinding binding;
    List<Product> categoryList;
    CategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoriesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        categoryList = new ArrayList<>();
        String chosenCategory = getIntent().getExtras().getString("category");
        binding.chosenCtegory.setText(chosenCategory);
        binding.chosenCtegory.setVisibility(View.VISIBLE);
        setListeners();
        displayProduct(chosenCategory);


    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    private void setListeners() {
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void displayProduct(String chosenCategory) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("products").whereEqualTo("product category",chosenCategory).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    showToast(error.toString());
                    return;
                }
                for (DocumentChange dc : value.getDocumentChanges()) {

                    if (dc.getType()==DocumentChange.Type.ADDED){
                        Product product = new Product();
                        product.setProductName(dc.getDocument().get("product name").toString());
                        product.setProductDescription(dc.getDocument().get("product description").toString());
                        product.setProductPrice(dc.getDocument().get("product price").toString());
                        product.setProductImg(dc.getDocument().get("product image").toString());
                        product.setPostedBy(dc.getDocument().get("posted by").toString());
                        categoryList.add(product);
                    }
                    categoryAdapter.notifyDataSetChanged();



                }

            }
        });

        categoryAdapter = new CategoryAdapter(categoryList, getApplicationContext());
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.recyclerview.setAdapter(categoryAdapter);

    }
}
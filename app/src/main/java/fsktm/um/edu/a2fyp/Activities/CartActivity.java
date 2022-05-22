package fsktm.um.edu.a2fyp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import fsktm.um.edu.a2fyp.Adapters.CartAdapter;
import fsktm.um.edu.a2fyp.Adapters.CategoryAdapter;
import fsktm.um.edu.a2fyp.Models.Product;
import fsktm.um.edu.a2fyp.R;
import fsktm.um.edu.a2fyp.databinding.ActivityCartBinding;

public class CartActivity extends AppCompatActivity {

    ActivityCartBinding binding;
    List<Product> cartList;
    CartAdapter cartAdapter;

    FirebaseFirestore firestore;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        cartList = new ArrayList<>();

        displayProducts();
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

    private void displayProducts() {
        String userId = mAuth.getCurrentUser().getUid();
        firestore.collection("cart").document(userId).collection("products").orderBy("product name").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    showToast(error.toString());
                    return;
                }
                for (DocumentChange dc : value.getDocumentChanges()) {
                    if (dc.getType()==DocumentChange.Type.ADDED){
                        Product product = new Product();
                        product.setProductName(dc.getDocument().get("product name").toString());
                        product.setProductDescription(dc.getDocument().get("product description").toString());
                        product.setProductPrice(dc.getDocument().get("product price").toString());
//                        product.setProductImg(dc.getDocument().get("product image").toString());
                        product.setPostedBy(dc.getDocument().get("posted_by").toString());
                        cartList.add(product);
                    }
                    cartAdapter.notifyDataSetChanged();
                }
            }
        });

        cartAdapter = new CartAdapter(cartList, getApplicationContext());
        binding.cartRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.cartRecyclerview.setAdapter(cartAdapter);

    }


    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    private void bottomNavSelectItem() {

        binding.bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_navigation_home_meu:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        return true;

                    case R.id.bottom_navigation_cart_menu:
                        return true;

                    case R.id.bottom_navigation_post_menu:
                        startActivity(new Intent(getApplicationContext(),PostProductActivity.class));
                        return true;

                    case R.id.bottom_navigation_chat_menu:
                        return true;

                    case R.id.bottom_navigation_profile_menu:
                        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

    }
}
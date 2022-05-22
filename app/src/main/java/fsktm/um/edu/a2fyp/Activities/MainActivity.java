package fsktm.um.edu.a2fyp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import fsktm.um.edu.a2fyp.Adapters.ProductsAdapter;
import fsktm.um.edu.a2fyp.Models.Product;
import fsktm.um.edu.a2fyp.R;
import fsktm.um.edu.a2fyp.databinding.ActivityMainBinding;
import fsktm.um.edu.a2fyp.utilities.PreferenceManager;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    PreferenceManager preferenceManager;
    BottomNavigationView bottomNavigationView;

    List<Product> productList;
    ProductsAdapter productsAdapter;

    FirebaseFirestore firestore;

    ListenerRegistration query;

    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.bottom_navigation_home_meu);

        productList = new ArrayList<>();


        bottomNavSelectItem();
        displayProducts();
        setListeners();

        binding.searchProductEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
               if (editable.toString().isEmpty()) {
                   displayProducts();
               }else {
                   searchProduct(editable.toString());
               }
            }
        });

    }


    private void setListeners() {

        binding.categoryElectronics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String c1 = "Electronics";
                Intent intent = new Intent(getApplicationContext(), CetegoriesActivity.class);
                intent.putExtra("category",c1);
                startActivity(intent);
            }
        });

        binding.categoryClothings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String c2 = "Fashion";
                Intent intent = new Intent(getApplicationContext(), CetegoriesActivity.class);
                intent.putExtra("category",c2);
                startActivity(intent);
            }
        });

        binding.categoryBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String c3 = "Books";
                Intent intent = new Intent(getApplicationContext(), CetegoriesActivity.class);
                intent.putExtra("category",c3);
                startActivity(intent);
            }
        });

//        binding.categoryGames.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String c4 = "Games";
//                Intent intent = new Intent(getApplicationContext(), CetegoriesActivity.class);
//                intent.putExtra("category",c4);
//                startActivity(intent);
//            }
//        });
//
//        binding.searchProductEt.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (s.toString().isEmpty()) {
//                    displayProducts();
//                }else{
//                  query = firestore.collection("products").whereEqualTo("product name",s.toString()).addSnapshotListener(new EventListener<QuerySnapshot>() {
//                        @Override
//                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                            for (DocumentChange dc : value.getDocumentChanges()){
//                                if (dc.getType()==DocumentChange.Type.ADDED) {
//                                    Product product = new Product();
//                                    product.setProductName(dc.getDocument().get("product name").toString());
//                                    product.setProductDescription(dc.getDocument().get("product description").toString());
//                                    product.setProductPrice(dc.getDocument().get("product price").toString());
//                                    product.setProductImg(dc.getDocument().get("product image").toString());
//                                    product.setPostedBy(dc.getDocument().get("posted by").toString());
//                                    productList.add(product);
//                                }
//                                productsAdapter.notifyDataSetChanged();
//
//
//                            }
//                            productsAdapter.notifyDataSetChanged();
//                            productsAdapter = new ProductsAdapter(productList, getApplicationContext());
//                            binding.productRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//                            binding.productRecyclerview.setAdapter(productsAdapter);
//
//                        }
//                    });
//
//                }
//
//            }
//        });
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }


    private void searchProduct(String search) {

            firestore.collection("products").whereEqualTo("product name",search).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    productList.clear();
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        Product product = new Product();
                        product.setProductName(documentSnapshot.getString("product name"));
                        product.setProductPrice(documentSnapshot.getString("product price"));
                        product.setProductDescription(documentSnapshot.getString("product description"));
                        product.setPostedBy(documentSnapshot.getString("posted by"));
                        product.setProductImg(documentSnapshot.getString("product image"));
                        productList.add(product);
                    }
                    productsAdapter.notifyDataSetChanged();

                }

            });
        productsAdapter = new ProductsAdapter(productList, getApplicationContext());
        binding.productRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.productRecyclerview.setAdapter(productsAdapter);

    }

    private void displayProducts() {

        firestore = FirebaseFirestore.getInstance();

        firestore.collection("products").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                        productList.add(product);
                    }
                    productsAdapter.notifyDataSetChanged();



                }

            }
        });

        productsAdapter = new ProductsAdapter(productList, getApplicationContext());
        binding.productRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.productRecyclerview.setAdapter(productsAdapter);

//        firestore.collection("products").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                    showToast("reached on complete");
//                if (task.isSuccessful() && task.getResult() != null) {
//                    List<Product> productList = new ArrayList<>();
//                    showToast("task is successful");
//                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
//                        Product product = new Product();
////                        product.name = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_NAME);
////                        product.description = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_DESCRIPTION);
////                        product.price = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_PRICE);
////                        product.image = queryDocumentSnapshot.getString(Constants.KEY_PRODUCT_IMG);
//
//                        productList.add(product);
//                        showToast("for loop");
//                    }
//                    if (productList.size() > 0){
//                        showToast("2nd if");
//                        ProductsAdapter productsAdapter = new ProductsAdapter(productList);
//                        binding.productRecyclerview.setAdapter(productsAdapter);
//                    }
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                showToast(e.getMessage());
//            }
//        });
    }





    private void bottomNavSelectItem() {

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_navigation_home_meu:
                        return true;

                    case R.id.bottom_navigation_cart_menu:
                        startActivity(new Intent(getApplicationContext(),CartActivity.class));
                        return true;

                    case R.id.bottom_navigation_post_menu:
                        startActivity(new Intent(getApplicationContext(),PostProductActivity.class));
                        return true;

                    case R.id.bottom_navigation_chat_menu:
                        startActivity(new Intent(getApplicationContext(),ChatListActivity.class));
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
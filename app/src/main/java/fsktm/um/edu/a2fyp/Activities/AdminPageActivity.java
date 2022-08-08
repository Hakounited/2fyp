package fsktm.um.edu.a2fyp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

import fsktm.um.edu.a2fyp.Adapters.AdminProductAdapter;
import fsktm.um.edu.a2fyp.Adapters.UsersAdapter;
import fsktm.um.edu.a2fyp.Listeners.UserListener;
import fsktm.um.edu.a2fyp.Models.Product;
import fsktm.um.edu.a2fyp.Models.Users;
import fsktm.um.edu.a2fyp.R;
import fsktm.um.edu.a2fyp.databinding.ActivityAdminPageBinding;
import fsktm.um.edu.a2fyp.databinding.ActivityProfileBinding;
import fsktm.um.edu.a2fyp.utilities.Constants;
import fsktm.um.edu.a2fyp.utilities.PreferenceManager;

public class AdminPageActivity extends AppCompatActivity   {

    ActivityAdminPageBinding  binding;
    PreferenceManager preferenceManager;
    FirebaseAuth mAuth;
    List<Product> productList;
    AdminProductAdapter adminProductAdapter;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        productList = new ArrayList<>();

        setListeners();

    }

    private void setListeners(){
        binding.adminViewUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),AdminUsersPage.class);
                startActivity(intent);
//                binding.adminProductRecyclerview.setVisibility(View.GONE);
//                getUsers();
            }
        });

        binding.adminViewProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),AdminProductPage.class);
                startActivity(intent);
            }
        });

        binding.adminViewOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),AdminOrdersActivity.class);
                startActivity(intent);
            }
        });

//        binding.adminViewProduts.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                binding.adminProductRecyclerview.setVisibility(View.GONE);
//                getProducts();
//            }
//        });

        binding.adminLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });
    }

    private void logOut(){
        preferenceManager.clear();
        Intent intent = new Intent(getApplicationContext(), StartActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
    }


    private void deleteUser() {

    }

    private void getUsers() {
        loading(true);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        loading(false);
                        String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                        if (task.isSuccessful() && task.getResult() != null) {
                            List<Users> users = new ArrayList<>();
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
//                                if (currentUserId.equals(queryDocumentSnapshot.getId())){
//                                    continue;
//                                }
                                Users users1 = new Users();
                                users1.username = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                                users1.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                                users1.img = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                                users1.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                                users1.id = queryDocumentSnapshot.getId();

                                users.add(users1);
                            }
                            if (users.size() > 0) {
                                UsersAdapter usersAdapter = new UsersAdapter(users,getApplicationContext());
//                                binding.adminRecyclerview.setAdapter(usersAdapter);
//                                binding.adminRecyclerview.setVisibility(View.VISIBLE);
                            } else {
                                showErrorMessage();
                            }
                        } else {
                            showErrorMessage();
                        }
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
                        productList.add(product);

                    }
                    if (productList.size() > 0) {
                        AdminProductAdapter adminProductAdapter = new AdminProductAdapter(productList, getApplicationContext());
//                        binding.adminProductRecyclerview.setAdapter(adminProductAdapter);
//                        binding.adminProductRecyclerview.setVisibility(View.VISIBLE);
                    } else {
                        showErrorMessage();
                    }
                } else {
                    showErrorMessage();
                }
            }
        });

    }


    private void showErrorMessage() {
//        binding.adminErroMsg.setText(String.format("%s", "No users available"));
//        binding.adminErroMsg.setVisibility(View.VISIBLE);

    }

    private void loading(Boolean isLoading){
        if (isLoading) {
//            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
//            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

}
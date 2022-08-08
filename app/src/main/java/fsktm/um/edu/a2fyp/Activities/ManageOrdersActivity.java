package fsktm.um.edu.a2fyp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import fsktm.um.edu.a2fyp.Adapters.ManageOrdersAdapter;
import fsktm.um.edu.a2fyp.Adapters.OrderAdapter;
import fsktm.um.edu.a2fyp.Models.Order;
import fsktm.um.edu.a2fyp.R;
import fsktm.um.edu.a2fyp.databinding.ActivityManageOrdersBinding;

public class ManageOrdersActivity extends AppCompatActivity {

    ActivityManageOrdersBinding binding;
    BottomNavigationView bottomNavigationView;

    FirebaseFirestore firestore;
    FirebaseAuth mAuth;

    String buyerName;
    List<Order> ordersPlacedList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bottomNavigationView = findViewById(R.id.bottom_nav);


        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        ordersPlacedList = new ArrayList<>();

        setListeners();
        bottomNavSelectItem();
        getOrdersPlaced();
    }

    private void setListeners() {

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }

    private void getOrdersPlaced() {

        firestore.collection("Orders")
                .whereEqualTo("seller_id",mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ordersPlacedList = new ArrayList<>();

                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                Order order = new Order();
                                order.order_id = documentSnapshot.getString("order_id");
                                order.buyer_id = documentSnapshot.getString("buyer_id");
                                order.buyer_name = documentSnapshot.getString("buyer_name");
                                order.product_name = documentSnapshot.getString("product_name");
                                order.order_status = documentSnapshot.getString("order_status");
                                order.product_img = documentSnapshot.getString("order_img");
                                order.order_product_id = documentSnapshot.getString("product_id");
                                order.order_quantity = documentSnapshot.getString("order_quantity");
                                order.phone_num = documentSnapshot.getString("phone_num");
                                ordersPlacedList.add(order);
                            }
                            if (ordersPlacedList.size() > 0 ){
                                ManageOrdersAdapter manageOrdersAdapter = new ManageOrdersAdapter(ordersPlacedList, getApplicationContext());
                                binding.manageOrderRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                binding.manageOrderRecyclerview.setAdapter(manageOrdersAdapter);

                            }else {
                                showToast("no orders");
                            }
                        }else {
                            showToast(task.getException().getMessage());
                        }

                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
    }


    private void bottomNavSelectItem() {

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_navigation_home_meu:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
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
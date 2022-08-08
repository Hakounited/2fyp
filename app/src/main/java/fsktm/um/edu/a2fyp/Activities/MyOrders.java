package fsktm.um.edu.a2fyp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import fsktm.um.edu.a2fyp.Adapters.OrderAdapter;
import fsktm.um.edu.a2fyp.Models.Order;
import fsktm.um.edu.a2fyp.databinding.ActivityMyOrdersBinding;

public class MyOrders extends AppCompatActivity {

    ActivityMyOrdersBinding binding;
    FirebaseFirestore firestore;
    FirebaseAuth mAuth;

    OrderAdapter orderAdapter;
    List<Order> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();





        setListeners();
        getOrders();
    }

    private void setListeners() {

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



    }

    private void getOrders() {
        firestore.collection("Orders")
                .whereEqualTo("buyer_id",mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            orderList = new ArrayList<>();
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                               Order order = new Order();
                               order.product_name = documentSnapshot.getString("product_name");
                               order.order_status = documentSnapshot.getString("order_status");
                               order.order_id = documentSnapshot.getString("order_id");
                               order.product_img = documentSnapshot.getString("order_img");
                               order.order_product_id = documentSnapshot.getString("product_id");
                               orderList.add(order);
                            }
                            if (orderList.size() > 0 ){
                                orderAdapter = new OrderAdapter(orderList, getApplicationContext());
                                binding.myOrdersReycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                binding.myOrdersReycler.setAdapter(orderAdapter);
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
}
package fsktm.um.edu.a2fyp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import fsktm.um.edu.a2fyp.Adapters.AdminOrderAdapter;
import fsktm.um.edu.a2fyp.Adapters.AdminProductAdapter;
import fsktm.um.edu.a2fyp.Models.Order;
import fsktm.um.edu.a2fyp.R;
import fsktm.um.edu.a2fyp.databinding.ActivityAdminOrdersBinding;

public class AdminOrdersActivity extends AppCompatActivity {

    ActivityAdminOrdersBinding binding;

    FirebaseFirestore firestore;
    FirebaseAuth mAuth;

    String sellerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminOrdersBinding.inflate(getLayoutInflater());
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

        firestore.collection("Orders").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    List<Order> orderList = new ArrayList<>();
                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                        Order order = new Order();
                        order.setBuyer_name(queryDocumentSnapshot.getString("buyer_name"));
                        order.setOrder_id(queryDocumentSnapshot.getString("order_id")) ;
                        String orderSellerId = queryDocumentSnapshot.getString("seller_id");
                        order.setSeller_name(queryDocumentSnapshot.getString("seller_name"));

                        FirebaseFirestore fstore = FirebaseFirestore.getInstance();
//                        fstore.collection("users").document(orderSellerId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                if (task.isSuccessful()){
//                                    DocumentSnapshot ds = task.getResult();
//                                    if (ds.exists()) {
//                                        sellerName = ds.getString("name");
//                                        order.setSeller_name(sellerName);
//                                        showToast(sellerName);
//                                    } else {
//                                        showToast("no document");
//                                    }
//                                } else {
//                                    showToast("task unsuccessful");
//                                }
//                            }
//                        });


                        orderList.add(order);
                    } if (orderList.size() > 0){
                        AdminOrderAdapter adminOrderAdapter = new AdminOrderAdapter(orderList, getApplicationContext());
                        binding.adminOrdersRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        binding.adminOrdersRecyclerview.setAdapter(adminOrderAdapter);
                    } else {
                        showToast("No orders available");
                    }
                } else {
                    showToast("task unsuccessful");
                }
            }
        });
    }

    private String getName(String Id){

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("users").document(Id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot ds = task.getResult();
                    if (ds.exists()) {
                        sellerName = ds.getString("name");

                    } else {
                        showToast("no document");
                    }
                }else {
                    showToast("task unsuccessful");
                }
            }
        });

        return sellerName;
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }


}
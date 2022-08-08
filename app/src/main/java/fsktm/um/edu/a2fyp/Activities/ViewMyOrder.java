package fsktm.um.edu.a2fyp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

import fsktm.um.edu.a2fyp.Adapters.ManageOrdersAdapter;
import fsktm.um.edu.a2fyp.Models.Order;
import fsktm.um.edu.a2fyp.R;
import fsktm.um.edu.a2fyp.databinding.ActivityViewMyOrderBinding;
import fsktm.um.edu.a2fyp.utilities.Constants;

public class ViewMyOrder extends AppCompatActivity {

    ActivityViewMyOrderBinding binding;

    FirebaseFirestore firestore;
    FirebaseAuth mAuth;
    DocumentReference docRef;


    Boolean isChecked ;
    Boolean btnClicked;
    String name, orderId, orderStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewMyOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        name = getIntent().getExtras().getString("order_title");
        orderId = getIntent().getExtras().getString("order_id");
        orderStatus = getIntent().getExtras().getString("order_status");


        binding.myOrdersOrderId.setText(orderId);


        if (orderStatus.equals("Received")) {
            binding.orderConfirmedCheckBox.setChecked(true);
            binding.orderRecievedCheckBox.setChecked(true);
            binding.orderReceivedTv.setText("Order Received");
            checkIfUserHasRated();

        } else if (orderStatus.equals("Cancelled")) {
            binding.orderConfirmedCheckBox.setChecked(true);
            binding.orderRecievedCheckBox.setChecked(false);
            binding.orderConfirmedTv2.setText("Order Cancelled");
            binding.orderConfirmedTv.setText("your order has been cancelled");
        } else {
            checkIfOrderIsReceived();
            checkIfOrderConfirmed();
        }



        setListeners();


    }

    private void checkIfOrderConfirmed() {
        docRef = firestore.collection("Orders").document(orderId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot ds = task.getResult();
                    if (ds.exists()) {
                        orderStatus = ds.getString("order_status");
                        if (orderStatus.equals("Confirmed")) {
                            orderConfirmed(true);
                        }else {
                            orderConfirmed(false);
                        }
                    }
                } else {
                    showToast(task.getException().getMessage());
                }
            }
        });



    }

    private void checkIfOrderIsReceived(){
        DocumentReference orderRef = firestore.collection("Orders").document(orderId);
        orderRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot ds = task.getResult();
                    if (ds.exists()) {
                        orderStatus = ds.getString("order_status");
                        showToast(orderStatus);
                    }
                }else {
                    showToast(task.getException().getMessage());
                }
            }
        });
    }

    private void orderConfirmed(Boolean orderIsConfirmed) {
        if (orderIsConfirmed) {
            binding.orderConfirmedCheckBox.setChecked(true);
            orderReceived();
        } else {
            binding.orderConfirmedCheckBox.setChecked(false);
        }
    }

    private void orderReceived() {
        binding.orderRecievedCheckBox.setClickable(true);
        binding.orderRecievedCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
                DocumentReference docRef = firestore.collection("Orderes").document(orderId);
                docRef.update("order_status", "Received");
                Order order = new Order();
                order.setOrder_status("Received");

            }
        });
//        binding.orderRecievedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                openDialog();
//                binding.rateProductBtn.setVisibility(View.VISIBLE);
//                binding.rateProductBtn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        binding.productRatingbar.setVisibility(View.VISIBLE);
//                        binding.rateProductBtn.setVisibility(View.GONE);
//                        binding.orderRecievedCheckBox.setClickable(false);
//
//                    }
//                });
//            }
//        });
    }

    private void setListeners() {
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

//        showToast("before if");
//        if (binding.orderRecievedCheckBox.isClickable()){
//            showToast("heeeeeeeeee");
//            binding.orderRecievedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                    binding.rateProductBtn.setVisibility(View.VISIBLE);
//                    binding.orderRecievedCheckBox.setClickable(false);
//                }
//            });
//        }

        binding.contactAdminBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("userId", mAuth.getCurrentUser().getUid());
                DocumentReference ref = firestore.collection("users").document(mAuth.getCurrentUser().getUid());
                ref.get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot ds = task.getResult();
                                    if (ds.exists()) {
                                        String userName = ds.getId();
                                        intent.putExtra("from", "viewOrder");
                                        intent.putExtra("username",userName);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                } else {
                                    showToast("task failed ");
                                    showToast(task.getException().getMessage());
                                }
                            }
                        });

            }
        });

        binding.rateProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRatingDialog();
            }
        });

    }

    private void openRatingDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view1 = inflater.inflate(R.layout.product_rating_dialog, null);

        MaterialButton dialogRateBtn = view1.findViewById(R.id.alert_rate_btn);
        RatingBar alertRatingBar = view1.findViewById(R.id.rating_bar);
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(view1)
                .create();

        alertDialog.show();
        dialogRateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double rating = alertRatingBar.getRating();
                String pRating = String.valueOf(rating);
                showToast(pRating);
                String productId = getIntent().getExtras().getString("product_id");

                DocumentReference ref = firestore.collection("ratings").document();
                String doc_id = ref.getId();

                HashMap<String, Object> productRating = new HashMap<>();
                productRating.put("product_id", productId);
                productRating.put("rating", pRating);
                productRating.put("doc_id", doc_id);
                productRating.put("order_id", binding.myOrdersOrderId.getText().toString());
                productRating.put("user_id", mAuth.getCurrentUser().getUid());

                ref.set(productRating).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        showToast("rating added");
                        alertDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast("failed to add rating");
                        alertDialog.dismiss();
                    }
                });

            }
        });
    }

    private void checkIfUserHasRated() {

        firestore.collection("ratings").whereEqualTo("order_id", binding.myOrdersOrderId.getText().toString())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot snapshots = task.getResult();
                    if (!snapshots.isEmpty()) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            showToast(documentSnapshot.getString("user_id"));
                            String userId = mAuth.getCurrentUser().getUid();
                            if (userId.equals(documentSnapshot.getString("user_id"))){
                                binding.rateProductBtn.setVisibility(View.GONE);
                            }else{
                                binding.rateProductBtn.setVisibility(View.VISIBLE);
                            }

                        }
                    }else {
                        binding.rateProductBtn.setVisibility(View.VISIBLE);
                    }
                }else{
                    binding.rateProductBtn.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private void openDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.order_received_alert_dialog, null);

        MaterialButton yesBtn = view.findViewById(R.id.received_yes_btn);
        MaterialButton noBtn = view.findViewById(R.id.received_no_btn);

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        alertDialog.show();

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.orderRecievedCheckBox.setChecked(true);
                binding.orderRecievedCheckBox.setClickable(false);
                binding.orderReceivedTv.setText("Order Received");
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                DocumentReference ref = firestore.collection("Orders").document(orderId);
                ref.update("order_status","Received");
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                alertDialog.dismiss();
            }
        });

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.orderRecievedCheckBox.setChecked(false);
                alertDialog.dismiss();
            }
        });


    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }
}
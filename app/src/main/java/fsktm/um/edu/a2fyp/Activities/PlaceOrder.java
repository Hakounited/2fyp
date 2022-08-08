package fsktm.um.edu.a2fyp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

import fsktm.um.edu.a2fyp.R;
import fsktm.um.edu.a2fyp.databinding.ActivityPlaceOrderBinding;

public class PlaceOrder extends AppCompatActivity {

    ActivityPlaceOrderBinding binding;

    FirebaseFirestore firestore;
    FirebaseAuth mAuth;

    String seller_id, price;
    int orderQuantity = 1;
    int totalPrice, ogPrice;

    String buyerName, productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlaceOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        binding.placeOrderPrice.setText(getIntent().getExtras().getString("price"));
        binding.placeOrderProductName.setText(getIntent().getExtras().getString("name"));
        binding.orderImage.setImageBitmap(getProductImg(getIntent().getExtras().getString("image")));

        productId = getIntent().getExtras().getString("product_id");

        setListeners();
    }

    private void setListeners() {

        price = binding.placeOrderPrice.getText().toString();
        totalPrice = Integer.parseInt(price);
        ogPrice = Integer.parseInt(price);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.placeOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.placeOrderAddress.getText().toString().isEmpty()) {
                    showToast("Please enter Address");
                    return;
                } else if (binding.placeOrderPhoneNum.getText().toString().isEmpty()) {
                    showToast("Please Enter Phone number");
                    return;
                }else {
                    addOrderToFireStore();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);

                }
            }
        });

        binding.orderPlusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderQuantity++;
                binding.orderQuantity.setText(String.valueOf(orderQuantity));
                totalPrice = ogPrice*orderQuantity;
                price = String.valueOf(totalPrice);
                binding.placeOrderPrice.setText(price);

            }
        });
        binding.orderMinusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (orderQuantity >= 1){
                    orderQuantity--;
                    binding.orderQuantity.setText(String.valueOf(orderQuantity));
                    totalPrice = totalPrice-ogPrice;
                    binding.placeOrderPrice.setText(String.valueOf(totalPrice));


                }
                if (orderQuantity == 0){
                    binding.placeOrderPrice.setText(String.valueOf(ogPrice));
                    binding.orderQuantity.setText("1");
                    showToast("Order quantity cannot below 1");
                }

            }
        });



    }


    private void addOrderToFireStore() {


        String productName = getIntent().getExtras().getString("name");
        String productPrice = getIntent().getExtras().getString("price");
        String productDescription = getIntent().getExtras().getString("description");
        String seller = getIntent().getExtras().getString("posted_by");
        String productImg = getIntent().getExtras().getString("image");
        String address = binding.placeOrderAddress.getText().toString();
        String phoneNum = binding.placeOrderPhoneNum.getText().toString();
        String orderQuan = binding.orderQuantity.getText().toString();
        String product_id = productId;


        DocumentReference docRef = firestore.collection("users").document(mAuth.getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot ds = task.getResult();
                    if (ds.exists()) {
                        buyerName = ds.getString("name");
                    }
                }
            }
        });


        DocumentReference ref = firestore.collection("Orders").document();
        String orderId = ref.getId();
//        showToast(String.valueOf(totalPrice));
        firestore.collection("users").whereEqualTo("name", seller)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (DocumentSnapshot dc : queryDocumentSnapshots) {
                            seller_id = dc.getId();

//                            showToast(buyerName);
                            HashMap<String, Object> orderDetails = new HashMap<>();

                            orderDetails.put("order_id", orderId );
                            orderDetails.put("product_name", productName);
                            orderDetails.put("product_price", productPrice);
                            orderDetails.put("seller_id", seller_id);
                            orderDetails.put("buyer_id", mAuth.getCurrentUser().getUid());
                            orderDetails.put("address", address);
                            orderDetails.put("phone_num", phoneNum);
                            orderDetails.put("order_quantity", orderQuan);
                            orderDetails.put("total_price", String.valueOf(totalPrice));
                            orderDetails.put("order_status","pending");
                            orderDetails.put("order_img",productImg);
                            orderDetails.put("buyer_name",buyerName);
                            orderDetails.put("product_id", product_id);
                            orderDetails.put("seller_name", seller);


                            firestore.collection("Orders")
                                    .document(orderId)
                                    .set(orderDetails)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            showToast("Order placed!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            showToast("failed to add order" +e.getMessage());
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast(e.getMessage());
                    }
                });



    }

    private Bitmap getProductImg(String encodedImg) {
        byte[] bytes = Base64.decode(encodedImg, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }


    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

}
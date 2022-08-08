package fsktm.um.edu.a2fyp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import fsktm.um.edu.a2fyp.Models.Order;
import fsktm.um.edu.a2fyp.R;
import fsktm.um.edu.a2fyp.databinding.ActivityViewOrderPlacedBinding;

public class ViewOrderPlacedActivity extends AppCompatActivity {

    ActivityViewOrderPlacedBinding binding;

    FirebaseFirestore firestore;
    FirebaseAuth mAuth;
    DocumentReference docRef;
    int total_price;
    String orderId, buyerName, productImg, orderQuantity, orderProductName, totalPrice, phoneNum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewOrderPlacedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        total_price = 0;

        setListeners();
        setOrderDetails();
    }

    private void setListeners() {
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void setOrderDetails() {

        orderId = getIntent().getExtras().getString("order_id");
        buyerName = getIntent().getExtras().getString("buyer_name");
        productImg = getIntent().getExtras().getString("order_img");
        orderQuantity = getIntent().getExtras().getString("order_quantity");
        orderProductName = getIntent().getExtras().getString("product_name");
        totalPrice = getIntent().getExtras().getString("price");
        phoneNum = getIntent().getExtras().getString("phone_num");

        showToast(orderProductName);
        binding.myOrdersOrderId.setText(orderId);
        binding.orderPlacedBy.setText(buyerName);
        binding.orderQuantityy.setText(orderQuantity);
        binding.orderProductTitle.setText(orderProductName);
        binding.orderImg.setImageBitmap(getProductImg(productImg));
        binding.totalOrderPrice.setText(totalPrice);
        binding.phoneNum.setText(phoneNum);

        docRef = firestore.collection("Orders").document(orderId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot ds = task.getResult();
                    if (ds.exists()) {
                        binding.deliveryAddress.setText(ds.getString("address"));
                        binding.orderStatus.setText(ds.getString("order_status"));
                        binding.totalOrderPrice.setText(ds.getString("total_price"));
                    }
                }
            }
        });
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    private Bitmap getProductImg(String encodedImg) {
        byte[] bytes = Base64.decode(encodedImg, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
}
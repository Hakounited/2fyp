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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import fsktm.um.edu.a2fyp.R;
import fsktm.um.edu.a2fyp.databinding.ActivityAdminViewOrderBinding;

public class AdminViewOrder extends AppCompatActivity {

    ActivityAdminViewOrderBinding binding;

    String orderId, buyerName, sellerName;

    FirebaseFirestore firestore;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminViewOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();

        orderId = getIntent().getExtras().getString("order_id");
        buyerName = getIntent().getExtras().getString("buyer_name");
        sellerName = getIntent().getExtras().getString("seller_name");

        binding.adminViewOrderId.setText(orderId);
        binding.adminOrderPlacedBy.setText(buyerName);
        binding.adminOrderSeller.setText(sellerName);

        firestore.collection("Orders").document(orderId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot ds = task.getResult();
                    if (ds.exists()) {
                        binding.adminOrderQuantity.setText(ds.getString("order_quantity"));
                        binding.adminOrderTotalPrice.setText(ds.getString("total_price"));
                        binding.adminOrderTitle.setText(ds.getString("product_name"));
                        binding.adminViewOrderImg.setImageBitmap(getProductImg(ds.getString("order_img")));
                        binding.adminOrderStatus.setText(ds.getString("order_status"));
                    }else{
                        showToast("document does not exist");
                    }
                }else{
                    showToast("task unsuccessful");
                }
            }
        });

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

    private Bitmap getProductImg(String encodedImg) {
        byte[] bytes = Base64.decode(encodedImg, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }


    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }
}
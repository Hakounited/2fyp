package fsktm.um.edu.a2fyp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import fsktm.um.edu.a2fyp.R;
import fsktm.um.edu.a2fyp.databinding.ActivityViewProductBinding;

public class ViewProductActivity extends AppCompatActivity {

    ActivityViewProductBinding binding;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        mAuth = FirebaseAuth.getInstance();

        binding.viewProductName.setText(getIntent().getExtras().getString("product_name"));
        binding.viewProductDescription.setText(getIntent().getExtras().getString("product_description"));
        binding.viewProductChosenPrice.setText(getIntent().getExtras().getString("product_price"));
        binding.viewPostedBy.setText(getIntent().getExtras().getString("posted_by"));

        //pass image here
//        showToast(getIntent().getExtras().getString("product_img"));

        setListeners();

    }

    private void setListeners(){

        binding.addProductToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProductToFireStore();
            }
        });
    }


    private void addProductToFireStore() {

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        String txt_productName,txt_productDescription,txt_productPrice,txt_productCategory, txt_postedBy;
        String userID = mAuth.getCurrentUser().getUid();

        txt_productName = binding.viewProductName.getText().toString().trim();
        txt_productPrice = binding.viewProductChosenPrice.getText().toString().trim();
        txt_productDescription = binding.viewProductDescription.getText().toString().trim();
        txt_postedBy = binding.viewPostedBy.getText().toString();

        HashMap<String, Object> cartProductDetails = new HashMap<>();
        cartProductDetails.put("product name", txt_productName);
        cartProductDetails.put("product description", txt_productDescription);
        cartProductDetails.put("product price", txt_productPrice);
        cartProductDetails.put("posted_by", txt_postedBy);


        firestore.collection("cart").document(mAuth.getCurrentUser().getUid()).collection("products").document(txt_productName).set(cartProductDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                showToast("Saved");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showToast("failed");
            }
        });

    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }
}
package fsktm.um.edu.a2fyp.Activities;

import androidx.annotation.NonNull;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import fsktm.um.edu.a2fyp.R;
import fsktm.um.edu.a2fyp.databinding.ActivityViewProductBinding;

public class ViewProductActivity extends AppCompatActivity {

    ActivityViewProductBinding binding;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;


    String productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        binding.viewProductName.setText(getIntent().getExtras().getString("product_name"));
        binding.viewProductDescription.setText(getIntent().getExtras().getString("product_description"));
        binding.viewProductChosenPrice.setText(getIntent().getExtras().getString("product_price"));
        binding.viewPostedBy.setText(getIntent().getExtras().getString("posted_by"));
        binding.viewProductImg.setImageBitmap(getProductImg(getIntent().getExtras().getString("product_img")));

        productId = getIntent().getExtras().getString("product_id");


        //pass image here
//        showToast(getIntent().getExtras().getString("product_img"));

        setListeners();

    }

    private void setListeners(){

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        String loggedInUserId = mAuth.getCurrentUser().getUid();
        DocumentReference docRef = firestore.collection("users").document(loggedInUserId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        String loggedInUserName = documentSnapshot.getString("name");
                        if (loggedInUserName.equals(binding.viewPostedBy.getText().toString())) {
                            binding.addProductToCart.setVisibility(View.GONE);
                        }else {
                            binding.addProductToCart.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    addProductToFireStore();
                                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                    startActivity(intent);

                                }
                            });
                        }
                    }
                }
            }
        });


        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }


    private void addProductToFireStore() {

        firestore = FirebaseFirestore.getInstance();

        String txt_productName,txt_productDescription,txt_productPrice,txt_productCategory, txt_postedBy, txt_productImg, txt_productId;
        String userID = mAuth.getCurrentUser().getUid();

        txt_productName = binding.viewProductName.getText().toString().trim();
        txt_productPrice = binding.viewProductChosenPrice.getText().toString().trim();
        txt_productDescription = binding.viewProductDescription.getText().toString().trim();
        txt_postedBy = binding.viewPostedBy.getText().toString();
        txt_productImg = getIntent().getExtras().getString("product_img");
        txt_productId = getIntent().getExtras().getString("product_id");

        HashMap<String, Object> cartProductDetails = new HashMap<>();
        cartProductDetails.put("product name", txt_productName);
        cartProductDetails.put("product description", txt_productDescription);
        cartProductDetails.put("product price", txt_productPrice);
        cartProductDetails.put("posted_by", txt_postedBy);
        cartProductDetails.put("product_img", txt_productImg);
        cartProductDetails.put("product_id", txt_productId);

        firestore.collection("cart").document(mAuth.getCurrentUser().getUid()).collection("products").document(txt_productId).set(cartProductDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                showToast("Saved");
            }
        }).addOnFailureListener(new OnFailureListener() {
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
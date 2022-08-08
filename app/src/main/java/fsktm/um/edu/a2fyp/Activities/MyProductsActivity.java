package fsktm.um.edu.a2fyp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

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

import java.util.ArrayList;
import java.util.List;

import fsktm.um.edu.a2fyp.Adapters.MyProductsAdapter;
import fsktm.um.edu.a2fyp.Models.Product;
import fsktm.um.edu.a2fyp.databinding.ActivityMyProductsBinding;

public class MyProductsActivity extends AppCompatActivity {

    ActivityMyProductsBinding binding;

    FirebaseFirestore firestore;
    FirebaseAuth mAuth;
    List<Product> myProductList;
    MyProductsAdapter myProductsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyProductsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        myProductList = new ArrayList<>();

        setListeners();
        displayProducts();

    }

   private void setListeners() {
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void displayProducts() {

        showToast(mAuth.getCurrentUser().getUid());
        firestore.collection("products").whereEqualTo("user Id",mAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null ) {
                    showToast(error.toString());
                    return;
                }
                for (DocumentChange dc : value.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED) {
                        Product product = new Product();
                        product.setProductName(dc.getDocument().get("product name").toString());
                        product.setProductDescription(dc.getDocument().get("product description").toString());
                        product.setProductPrice(dc.getDocument().get("product price").toString());
                        product.setProductImg(dc.getDocument().get("product image").toString());
                        product.setProductId(dc.getDocument().get("product Id").toString());
//                        product.setPostedBy(dc.getDocument().get("posted_by").toString());
                        myProductList.add(product);

                    }
                    myProductsAdapter.notifyDataSetChanged();
                }
            }
        });

        myProductsAdapter = new MyProductsAdapter(myProductList,getApplicationContext());
        binding.myProductRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.myProductRecyclerview.setAdapter(myProductsAdapter);


    }


    private void showToast(String message) {
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
    }
}
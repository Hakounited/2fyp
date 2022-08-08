package fsktm.um.edu.a2fyp.Activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

import fsktm.um.edu.a2fyp.databinding.ActivityEditProductBinding;
import fsktm.um.edu.a2fyp.utilities.Constants;
import fsktm.um.edu.a2fyp.utilities.PreferenceManager;

public class EditProduct extends AppCompatActivity {

    ActivityEditProductBinding binding;
    String productImg;

    PreferenceManager preferenceManager;

    FirebaseFirestore firestore;
    DocumentReference docRef;


    private String encodedImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        productImg = getIntent().getExtras().getString("product_img");
        firestore = FirebaseFirestore.getInstance();

        String product_id = getIntent().getExtras().getString("product_id");

        showToast(product_id);
        loadProductDetails(product_id);
        setListeners();


    }

    private void setListeners() {

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.editProductUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pId = getIntent().getExtras().getString("product_id");
                editProductDetails(pId);
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });

        binding.editProductChooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);
            }
        });
    }

    private void editProductDetails(String productId) {
        showToast(productId);
        docRef = firestore.collection("products").document(productId);
        HashMap<String, Object> updateProduct = new HashMap<>();



        updateProduct.put("product price", binding.editProductPrice.getText().toString());
        updateProduct.put("product description", binding.editProductDescription.getText().toString());
        updateProduct.put("product name", binding.editProductName.getText().toString());
        updateProduct.put("product image", encodedImg);

        docRef.update(updateProduct).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                showToast("product updated");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showToast("Failed to update product");
            }
        });

//        docRef.update("product description", binding.editProductDescription.getText().toString());
//        docRef.update("product name", binding.editProductName.getText().toString());
//        docRef.update("product price", binding.editProductPrice.getText().toString());


    }

    private String encodedImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes,Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK){
                    if (result.getData() != null){
                        Uri imgUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imgUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.editProductImg.setImageBitmap(bitmap);
                            encodedImg = encodedImage(bitmap);
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );


    private void loadProductDetails(String pName) {
        showToast("load method");
        DocumentReference ref = firestore.collection("products").document(pName);
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot ds = task.getResult();
                    if (ds.exists()) {
                        showToast("ds exists");
                        binding.editProductName.setText(ds.getString("product name"));
                        binding.editProductDescription.setText(ds.getString("product description"));
                        binding.editProductPrice.setText(ds.getString("product price"));
                        binding.editProductImg.setImageBitmap(getImg(ds.getString("product image")));

                    }
                }else {
                    showToast(task.getException().getMessage());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showToast(e.getMessage());
            }
        });

    }

    private Bitmap getImg(String encodedImg) {
        byte[] bytes = Base64.decode(encodedImg, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }


    private void showToast(String message) {
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
    }

}
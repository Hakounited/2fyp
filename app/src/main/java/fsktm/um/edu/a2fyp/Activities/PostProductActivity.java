package fsktm.um.edu.a2fyp.Activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

import fsktm.um.edu.a2fyp.R;
import fsktm.um.edu.a2fyp.databinding.ActivityPostProductBinding;
import fsktm.um.edu.a2fyp.utilities.Constants;

public class PostProductActivity extends AppCompatActivity {

    ActivityPostProductBinding binding;

    ArrayAdapter<String> arrayAdapterList;
    FirebaseAuth mAuth;
    private String encodedImg;


    AutoCompleteTextView autoCompleteTextView;
    TextInputLayout textInputLayout;
    ArrayAdapter<String> arrayAdapter;
    String txt_username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        String[] categories= new String[] {"Electronics","Fashion","Books","Games"};

        arrayAdapter = new ArrayAdapter<String>(this,R.layout.dropdown_list,categories);

        binding.autoCompleteTxt.setAdapter(arrayAdapter);


        setListeners();

    }

    private void setListeners(){

        binding.chooseProductImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);
            }
        });

        binding.autoCompleteTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String chosenItem = adapterView.getItemAtPosition(i).toString();
                binding.itemSelected.setHint("");
                binding.itemSelected.setText(chosenItem);
            }
        });

        binding.postProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProductToFirestore();
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    private String encodedImg(Bitmap bitmap){
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getHeight();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap,previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
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
                            binding.postProductImg.setImageBitmap(bitmap);
                            encodedImg = encodedImg(bitmap);
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    private void addProductToFirestore(){

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String txt_productName,txt_productDescription,txt_productPrice,txt_productCategory, txt_userId, txt_name;
        String userID = mAuth.getCurrentUser().getUid();

        txt_productName = binding.postProductName.getText().toString().trim();
        txt_productPrice = binding.postProductPrice.getText().toString().trim();
        txt_productDescription = binding.postProductDescription.getText().toString().trim();
        txt_productCategory = binding.itemSelected.getText().toString().trim();
        txt_userId = mAuth.getCurrentUser().getUid();

        firestore.collection(Constants.KEY_COLLECTION_USERS).document(mAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                txt_username = value.getString("name");


                HashMap<String, Object> productDetails = new HashMap<>();
                productDetails.put("product name", txt_productName);
                productDetails.put("product category",txt_productCategory);
                productDetails.put("product description",txt_productDescription);
                productDetails.put("product price", txt_productPrice);
                productDetails.put("product image",encodedImg);
                productDetails.put("user Id",txt_userId);
                productDetails.put("posted by",txt_username);


                firestore.collection("products").document(txt_productName).set(productDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        showToast("success!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast(e.getMessage());
                    }
                });
            }
        });
//        showToast(txt_username);
        firestore.collection(Constants.KEY_COLLECTION_USERS).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {


                            }
                        }
                    }
                });





    }


}
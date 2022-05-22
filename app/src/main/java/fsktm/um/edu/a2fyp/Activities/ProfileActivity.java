package fsktm.um.edu.a2fyp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

import fsktm.um.edu.a2fyp.R;
import fsktm.um.edu.a2fyp.databinding.ActivityProfileBinding;
import fsktm.um.edu.a2fyp.utilities.Constants;
import fsktm.um.edu.a2fyp.utilities.PreferenceManager;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding binding;
    PreferenceManager preferenceManager;
    BottomNavigationView bottomNavigationView;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());
        firestore = FirebaseFirestore.getInstance();

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavSelectItem();
        loadUserDetails();
        getToken();
        setListeners();
    }



    private void setListeners() {
        binding.profileLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

        binding.profileRmyProdcuts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MyProductsActivity.class);
                startActivity(intent);
            }
        });

        binding.profileUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = binding.profileEditName.getText().toString();
                String newEmail = binding.profileEditEmail.getText().toString();
                updateProfile(username, newEmail);
            }
        });
    }

    private void updateProfile(String name, String email) {
        String userid = preferenceManager.getString(Constants.KEY_USER_ID);
        preferenceManager.putString(Constants.KEY_NAME,name);

        DocumentReference documentReference = firestore.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_ID));
        documentReference.update(Constants.KEY_NAME,name).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                showToast("updated");
            }
        });
    }

    private void loadUserDetails() {
        binding.profileEditName.setText(preferenceManager.getString(Constants.KEY_NAME));
        binding.profileEditEmail.setText(preferenceManager.getString(Constants.KEY_EMAIL));
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE),Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        binding.profileImg.setImageBitmap(bitmap);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
    }

    private void updateToken(String token) {

        DocumentReference documentReference =
                firestore.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_ID));
        documentReference.update(Constants.KEY_FCM_TOKEN,token)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
//                        showToast("Token updated");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showToast("unable to update token");
            }
        });
    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                updateToken(s);
            }
        });
    }

    private void signOut() {
        showToast("Signing out...");

        DocumentReference documentReference =
                firestore.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID));
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        preferenceManager.clear();
                        startActivity(new Intent(getApplicationContext(), StartActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast("unable to sign out");
                    }
                });

    }

    private void bottomNavSelectItem() {

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_navigation_home_meu:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.bottom_navigation_cart_menu:
                        return true;

                    case R.id.bottom_navigation_post_menu:
                        return true;

                    case R.id.bottom_navigation_chat_menu:
                        return true;

                    case R.id.bottom_navigation_profile_menu:
                        return true;
                }
                return false;
            }
        });

    }
}
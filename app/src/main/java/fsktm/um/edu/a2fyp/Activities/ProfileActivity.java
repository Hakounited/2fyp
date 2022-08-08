package fsktm.um.edu.a2fyp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.List;

import fsktm.um.edu.a2fyp.R;
import fsktm.um.edu.a2fyp.databinding.ActivityProfileBinding;
import fsktm.um.edu.a2fyp.utilities.Constants;
import fsktm.um.edu.a2fyp.utilities.PreferenceManager;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding binding;
    PreferenceManager preferenceManager;
    BottomNavigationView bottomNavigationView;
    FirebaseFirestore firestore;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    String userId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        userId = mAuth.getCurrentUser().getUid();

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
//                openDialog();
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

                String allowedDomain = "siswa.um.edu.my";
                String[] email = newEmail.split("@");

                if (!email[1].equals(allowedDomain)) {
                    showToast("invalid email!");
                    return;
                }

                updateProfile(username, newEmail);
            }
        });

        binding.profileDeleteAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

        binding.profileMyOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MyOrders.class));

            }
        });

        binding.ordersPlaced.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ManageOrdersActivity.class));
            }
        });

    }

    private void deleteUser() {
        firebaseUser = mAuth.getCurrentUser();
        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    showToast("Account deleted");
                    String username = preferenceManager.getString(Constants.KEY_NAME);
                    deleteUserFromFirestore(username);
                    deleteCart();
                    preferenceManager.clear();
                    startActivity(new Intent(getApplicationContext(), StartActivity.class));
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showToast(e.toString());
            }
        });


    }

    private void deleteCart() {
        firestore = FirebaseFirestore.getInstance();
        DocumentReference ref = firestore.collection("cart").document(userId);
        ref.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
//                    showToast("cart deleted");
                    firestore.collection("cart").document(userId).collection("products").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                firestore.collection("cart").document().collection("products").document(queryDocumentSnapshot.getId()).delete();
                            }
                        }
                    });
                }else {
                    showToast("failed to delete cart");
                    showToast(task.getException().getMessage());
                }
            }
        });
    }

    private void deleteUserFromFirestore(String username) {
        firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").whereEqualTo("name", username)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                        WriteBatch batch = FirebaseFirestore.getInstance().batch();
                        for (DocumentSnapshot dc : snapshotList) {
                            batch.delete(dc.getReference());
                        }
                        batch.commit()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
//                                        showToast("user deleted from firestore");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
//                                        showToast("failed to delete from firestore");
                                    }
                                });

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showToast("Failed to get user from firestore");
                showToast(e.getMessage());
            }
        });

        firestore.collection("products").whereEqualTo("posted by", username)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                        WriteBatch batch = FirebaseFirestore.getInstance().batch();

                        for (DocumentSnapshot dc : snapshotList) {
                            batch.delete(dc.getReference());
                        }
                        batch.commit()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        showToast("products deleted");

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showToast("failed to delete user products");
                            }
                        });
                    }

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showToast("failed to get user products");
            }
        });

    }


    private void openDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.alert_dialog, null);

        MaterialButton yesBtn = view.findViewById(R.id.yes_btn);
        MaterialButton noBtn = view.findViewById(R.id.no_btn);



        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        alertDialog.show();

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteUser();
                alertDialog.dismiss();
            }
        });

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
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
                firestore.collection("products").whereEqualTo("user Id", preferenceManager.getString(Constants.KEY_USER_ID))
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String docId = document.getId();
                                DocumentReference ref = firestore.collection("products").document(docId);
                                ref.update("posted by", name);
                                showToast("update products posted by!");
                            }
                        }else{
                            showToast(task.getException().getMessage());
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showToast(e.getMessage());
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
                        startActivity(new Intent(getApplicationContext(),CartActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.bottom_navigation_post_menu:
                        startActivity(new Intent(getApplicationContext(),PostProductActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.bottom_navigation_chat_menu:
                        startActivity(new Intent(getApplicationContext(),ChatActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.bottom_navigation_profile_menu:
                        return true;
                }
                return false;
            }
        });

    }
}
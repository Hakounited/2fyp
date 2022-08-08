package fsktm.um.edu.a2fyp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;

import fsktm.um.edu.a2fyp.Activities.ChatActivity;
import fsktm.um.edu.a2fyp.Models.Users;
import fsktm.um.edu.a2fyp.databinding.UsersCardBinding;
import fsktm.um.edu.a2fyp.utilities.Constants;
import fsktm.um.edu.a2fyp.utilities.PreferenceManager;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private final List<Users> usersList;
    Context context;

    public UsersAdapter(List<Users> users, Context context) {
        this.usersList = users;
        this.context = context;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UsersCardBinding usersCardBinding = UsersCardBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new UserViewHolder(usersCardBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {

        holder.setUserData(usersList.get(position));

    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        UsersCardBinding binding;

        UserViewHolder(UsersCardBinding usersCardBinding) {
            super(usersCardBinding.getRoot());
            binding = usersCardBinding;
        }

        void setUserData(Users users) {
            PreferenceManager preferenceManager = new PreferenceManager(context);
            binding.adminUserName.setText(users.username);
            String username = binding.adminUserName.getText().toString();
            binding.adminUserEmail.setText(users.email);
            binding.userImg.setImageBitmap(getUserImg(users.img));

            binding.chatUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("user_id", preferenceManager.getString(Constants.KEY_USER_ID));
                    intent.putExtra("admin_username",preferenceManager.getString(Constants.KEY_NAME));
                    intent.putExtra("from", "userAdapter");
                    intent.putExtra("receiver_name", binding.adminUserName.getText().toString());
//                    intent.putExtra(Constants.KEY_USER, users);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });

            binding.deleteUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteUser(username);
                    usersList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                }
            });

        }
    }


    private void deleteUser(String username) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users")
                .whereEqualTo("name", username)
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
                                        showToast("Deleted");
                                        deleteUserProducts(username);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        showToast("delete Failed");
                                        showToast(e.getMessage());
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast("failed to get collection");
                        showToast(e.getMessage());
                    }
                });

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
    }

    private void deleteUserProducts(String username) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("products")
                .whereEqualTo("posted by", username)
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
//                                        showToast("user products deleted");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        showToast("failed to delete user products");
                                        showToast(e.getMessage());
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast("Failed to get document");
                        showToast(e.getMessage());
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show();
    }

    private Bitmap getUserImg(String encodedImg) {
        byte[] bytes = Base64.decode(encodedImg, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
}

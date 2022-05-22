package fsktm.um.edu.a2fyp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

import fsktm.um.edu.a2fyp.Activities.ChatActivity;
import fsktm.um.edu.a2fyp.Listeners.UserListener;
import fsktm.um.edu.a2fyp.Models.Users;
import fsktm.um.edu.a2fyp.databinding.UsersCardBinding;
import fsktm.um.edu.a2fyp.utilities.Constants;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private final List<Users> users;
    Context context;

    public UsersAdapter(List<Users> users, Context context) {
        this.users = users;
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

        holder.setUserData(users.get(position));

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        UsersCardBinding binding;

        UserViewHolder(UsersCardBinding usersCardBinding) {
            super(usersCardBinding.getRoot());
            binding = usersCardBinding;
        }

        void setUserData(Users users) {
            binding.adminUserName.setText(users.username);
            binding.adminUserEmail.setText(users.email);
            binding.userImg.setImageBitmap(getUserImg(users.img));
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra(Constants.KEY_USER, users);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                }
            });

        }
    }

    private Bitmap getUserImg(String encodedImg) {
        byte[] bytes = Base64.decode(encodedImg, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
}

package fsktm.um.edu.a2fyp.Adapters;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import fsktm.um.edu.a2fyp.Activities.AdminViewOrder;
import fsktm.um.edu.a2fyp.Activities.ViewProductActivity;
import fsktm.um.edu.a2fyp.Models.Order;
import fsktm.um.edu.a2fyp.databinding.AdminOrdersCardBinding;


public class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.AdminOrderViewHolder> {

    private final List<Order> orderList;
    Context context;

    public AdminOrderAdapter(List<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
    }

    @NonNull
    @Override
    public AdminOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdminOrdersCardBinding adminOrdersCardBinding = AdminOrdersCardBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new AdminOrderViewHolder(adminOrdersCardBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminOrderViewHolder holder, int position) {
        holder.setOrderDetails(orderList.get(position));

        holder.binding.deleteOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getRootView().getContext());
                builder.setTitle("Warning");
                builder.setMessage("Are you sure you want to delete this order?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        deleteOrder(holder.binding.adminOrderId.getText().toString());
                        orderList.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    class AdminOrderViewHolder extends RecyclerView.ViewHolder {

        AdminOrdersCardBinding binding;

        AdminOrderViewHolder(AdminOrdersCardBinding adminOrdersCardBinding){
            super(adminOrdersCardBinding.getRoot());
            binding = adminOrdersCardBinding;
        }

        void setOrderDetails(Order order) {
            binding.adminOrderId.setText(order.getOrder_id());
            binding.buyerName.setText(order.getBuyer_name());
            binding.orderSellerName.setText(order.getSeller_name());

            binding.adminOrderIdTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, AdminViewOrder.class);
                    intent.putExtra("order_id", binding.adminOrderId.getText().toString());
                    intent.putExtra("buyer_name", binding.buyerName.getText().toString());
                    intent.putExtra("seller_name", binding.orderSellerName.getText().toString());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });


        }

    }

    private void deleteOrder(String orderId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference ref = firestore.collection("Orders").document(orderId);
        ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                showToast("deleted");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showToast("Failed to delete product");
            }
        });
    }

    private Bitmap getOrderImg(String encodedImg) {
        byte[] bytes = Base64.decode(encodedImg, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void showToast(String message) {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show();
    }

}

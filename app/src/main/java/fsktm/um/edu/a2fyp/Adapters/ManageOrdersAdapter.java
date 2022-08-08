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
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import fsktm.um.edu.a2fyp.Activities.PlaceOrder;
import fsktm.um.edu.a2fyp.Activities.ViewOrderPlacedActivity;
import fsktm.um.edu.a2fyp.Models.Order;
import fsktm.um.edu.a2fyp.databinding.ManageOrdersCardBinding;

public class ManageOrdersAdapter extends RecyclerView.Adapter<ManageOrdersAdapter.ManageOrdersViewHolder>{

    private final List<Order> ordersPlaced;
    Context context;

    public ManageOrdersAdapter(List<Order> ordersPlaced, Context context) {
        this.ordersPlaced = ordersPlaced;
        this.context = context;
    }


    @NonNull
    @Override
    public ManageOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ManageOrdersCardBinding manageOrdersCardBinding = ManageOrdersCardBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ManageOrdersViewHolder(manageOrdersCardBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageOrdersViewHolder holder, int position) {
        holder.setOrderDetails(ordersPlaced.get(position));
        FirebaseFirestore fstore = FirebaseFirestore.getInstance();
        DocumentReference docRef = fstore.collection("Orders").document(holder.binding.orderId.getText().toString());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot ds = task.getResult();
                    if (ds.exists()) {
                        String orderStatus = ds.getString("order_status");
                        showToast(orderStatus);
                        if (orderStatus.equals("Confirmed")){
                            holder.binding.confirmOrderTv.setText("Order Confirmed");
                            holder.binding.orderStatus.setVisibility(View.VISIBLE);
                            holder.binding.confirmOrderBtn.setVisibility(View.GONE);
                            holder.binding.cancelOrderBtn.setVisibility(View.GONE);
                            holder.binding.manageOrderConfirmedTv.setVisibility(View.VISIBLE);
                        } else if (orderStatus.equals("Received")) {
                            holder.binding.confirmOrderTv.setText("Order Received");
                            holder.binding.orderStatus.setVisibility(View.VISIBLE);
                            holder.binding.confirmOrderBtn.setVisibility(View.GONE);
                            holder.binding.cancelOrderBtn.setVisibility(View.GONE);
                            holder.binding.manageOrderConfirmedTv.setText(orderStatus);
                            holder.binding.manageOrderConfirmedTv.setVisibility(View.VISIBLE);
                        } else if (orderStatus.equals("Cancelled")) {
                            holder.binding.confirmOrderTv.setText("Order Cancelled");
                            holder.binding.orderStatus.setVisibility(View.VISIBLE);
                            holder.binding.confirmOrderBtn.setVisibility(View.GONE);
                            holder.binding.cancelOrderBtn.setVisibility(View.GONE);
                            holder.binding.manageOrderConfirmedTv.setText(orderStatus);
                            holder.binding.manageOrderConfirmedTv.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });

        holder.binding.confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                DocumentReference docRef = firestore.collection("Orders").document(holder.binding.orderId.getText().toString());
                docRef.update("order_status", "Confirmed");
                holder.binding.confirmOrderBtn.setVisibility(View.GONE);
                holder.binding.cancelOrderBtn.setVisibility(View.GONE);
                holder.binding.manageOrderConfirmedTv.setVisibility(View.VISIBLE);
                holder.binding.orderStatus.setVisibility(View.VISIBLE);
            }
        });

        holder.binding.cancelOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                DocumentReference docRef = firestore.collection("Orders").document(holder.binding.orderId.getText().toString());
                docRef.update("order_status", "Cancelled");
                holder.binding.confirmOrderBtn.setVisibility(View.GONE);
                holder.binding.cancelOrderBtn.setVisibility(View.GONE);
                holder.binding.manageOrderConfirmedTv.setText("Cancelled");
                holder.binding.manageOrderConfirmedTv.setVisibility(View.VISIBLE);
                holder.binding.orderStatus.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public int getItemCount() {
        return ordersPlaced.size();
    }

    class ManageOrdersViewHolder extends RecyclerView.ViewHolder {

        ManageOrdersCardBinding binding;

        ManageOrdersViewHolder(ManageOrdersCardBinding manageOrdersCardBinding) {
            super(manageOrdersCardBinding.getRoot());
            binding = manageOrdersCardBinding;
        }

        void setOrderDetails(Order order) {
            binding.orderId.setText(order.order_id);
            binding.buyerName.setText(order.buyer_name);




            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            DocumentReference ref = firestore.collection("products").document(order.order_product_id);
            ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot ds = task.getResult();
                        if (ds.exists()) {
                            String productImg = ds.getString("product image");
                            binding.myOrderImg.setImageBitmap(getProductImg(productImg));

                            binding.myOrderImg.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(context, ViewOrderPlacedActivity.class);
                                    intent.putExtra("order_id", order.order_id);
                                    intent.putExtra("buyer_name", order.buyer_name);
                                    intent.putExtra("product_name", order.product_name);
                                    intent.putExtra("order_img", productImg);
                                    intent.putExtra("order_quantity", order.order_quantity);
                                    intent.putExtra("price", order.total_price);
                                    intent.putExtra("phone_num", order.phone_num);

                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                }
                            });
                        }
                    }
                }
            });

        }

    }


    private Bitmap getProductImg(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }

    private void showToast(String message) {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show();
    }
}

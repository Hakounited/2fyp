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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import fsktm.um.edu.a2fyp.Activities.ChatActivity;
import fsktm.um.edu.a2fyp.Activities.ViewMyOrder;
import fsktm.um.edu.a2fyp.Models.Order;
import fsktm.um.edu.a2fyp.databinding.MyOrdersCardBinding;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private final List<Order> orders;
    Context context;

    public OrderAdapter(List<Order> orders, Context context) {
        this.orders = orders;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MyOrdersCardBinding myOrdersCardBinding = MyOrdersCardBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new OrderViewHolder(myOrdersCardBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.setOderDetails(orders.get(position));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {

        MyOrdersCardBinding binding;

        OrderViewHolder(MyOrdersCardBinding myOrdersCardBinding) {
            super(myOrdersCardBinding.getRoot());
            binding = myOrdersCardBinding;
        }

        void setOderDetails(Order order){
            binding.myOrderTitle.setText(order.product_name);
            binding.myOrderStatus.setText(order.order_status);
            binding.orderCardId.setText(order.order_id);
            String orderProductId = order.order_product_id;
//            binding.myOrderImg.setImageBitmap(getProductImg(order.product_img));
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
                        }
                    }
                }
            });

            binding.myOrderImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ViewMyOrder.class);
                    intent.putExtra("order_title", order.product_name);
                    intent.putExtra("order_id", order.order_id);
                    intent.putExtra("product_id", order.order_product_id);
                    intent.putExtra("order_status", order.order_status);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });

        }
    }

    private Bitmap getProductImg(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
}

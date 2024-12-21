package Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.temu.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import Models.CartItem;
import Models.Product;

public class CartRecyclerView extends RecyclerView.Adapter<CartRecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<CartItem> cartItems;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public CartRecyclerView(Context context, ArrayList<CartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.cart_show, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        holder.dateAdded.setText(formatter.format(cartItem.getTimestamp()));
        holder.productBought.setText("Amount: "+cartItem.getPurchasedAmount());
        firebaseDatabase.getReference().child("products").child(cartItem.getProductId()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Product product = task.getResult().getValue(Product.class);
                if (product.getEndingTime().before(new Date())){

                    cartItems.remove(cartItem);
                    notifyItemRemoved(position);
                }

                holder.dateAdded.setText(formatter.format(cartItem.getTimestamp()));
                holder.productImage.setImageBitmap(convertToBitmap(product.getImage()));
                holder.productName.setText(product.getName());
                holder.productPrice.setText(String.format("Rp. %,.2f", product.getCurrentPrice()));
                holder.dateDue.setText("Due: "+formatter.format(product.getEndingTime()));
                holder.total.setText("Total:"+String.format("Rp. %,.2f", product.getCurrentPrice() * cartItem.getPurchasedAmount()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateAdded;
        ImageView productImage;
        TextView productName;
        TextView productPrice;
        TextView productBought;
        TextView dateDue;
        TextView total;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateAdded = itemView.findViewById(R.id.cart_dateAdded);
            productImage = itemView.findViewById(R.id.cart_image);
            productName = itemView.findViewById(R.id.cart_name);
            productPrice = itemView.findViewById(R.id.cart_price);
            productBought = itemView.findViewById(R.id.cart_amount);
            dateDue = itemView.findViewById(R.id.cart_due);
            total = itemView.findViewById(R.id.cart_total);
        }
    }
    private Bitmap convertToBitmap(String encoded) {
        byte[] decodedString = Base64.decode(encoded, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}

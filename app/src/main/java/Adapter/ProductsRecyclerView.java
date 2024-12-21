package Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.temu.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import Models.CartItem;
import Models.Product;

public class ProductsRecyclerView extends RecyclerView.Adapter<ProductsRecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<Product> products;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public ProductsRecyclerView(Context context, ArrayList<Product> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.products_show, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        final int[] currentSold = {product.getBoughtAmount()};
        holder.productImage.setImageBitmap(convertToBitmap(product.getImage()));
        holder.productName.setText(product.getName());
        String formattedPrice  = String.format("Rp. %,.2f", product.getCurrentPrice());
        holder.productPrice.setText(formattedPrice);
        holder.productSold.setText(String.valueOf(currentSold[0])+ " sold");
        SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy HH:mm");
        holder.date.setText("Due: "+format.format(product.getEndingTime()));
        int requiredAmount=0;
        double nextPrice=0;
        List<String> keys = new ArrayList<>(product.getPrices().keySet());
        keys.sort(Comparator.comparingInt(Integer::parseInt));
        for(String key : keys) {
            int amount = Integer.parseInt(key);
            if(product.getBoughtAmount()<amount){
                requiredAmount = amount;
                nextPrice = product.getPrices().get(key);
                break;
            }
        }
        String text;
        if (requiredAmount==0)text = "Minimal Price Already Reached";
        else text = "Buy " + requiredAmount + " for " + nextPrice + " ";
        holder.productWholesale.setText(text);
        holder.addToCart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("How many items do you want to buy?");
                final EditText input = new EditText(context);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int amount = Integer.parseInt(input.getText().toString());
                        CartItem cartItem = new CartItem();
                        cartItem.setProductId(product.getProductId());
                        cartItem.setPurchasedAmount(amount);
                        cartItem.setTimestamp(new Date());
                        currentSold[0] += amount;
                        product.setBoughtAmount(currentSold[0]);
                        double updatedPrice;
                        List<String> keys = new ArrayList<>(product.getPrices().keySet());
                        keys.sort(Comparator.comparingInt(Integer::parseInt));
                        for(String key : keys) {
                            int amount2 = Integer.parseInt(key);
                            Log.d("Amount", "" + amount2);
                            if(currentSold[0]<amount2){
                                break;
                            }
                            updatedPrice = product.getPrices().get(key);
                            product.setCurrentPrice((double) updatedPrice);
                        }
                        firebaseDatabase.getReference().child("cart").child(firebaseAuth.getUid()).push().setValue(cartItem);
                        String id = product.getProductId();
                        product.setProductId(null);
                        firebaseDatabase.getReference().child("products").child(id).setValue(product);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productPrice;
        TextView productSold;
        TextView productWholesale;
        TextView date;
        Button addToCart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            productSold = itemView.findViewById(R.id.product_sold);
            productWholesale = itemView.findViewById(R.id.product_wholesale);
            addToCart = itemView.findViewById(R.id.product_addToCart);
            date = itemView.findViewById(R.id.product_date);
        }
    }
    private Bitmap convertToBitmap(String encoded) {
        byte[] decodedString = Base64.decode(encoded, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}

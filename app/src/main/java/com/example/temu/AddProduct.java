package com.example.temu;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.temu.databinding.ActivityAddProductBinding;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

import Models.Product;

public class AddProduct extends AppCompatActivity {
    ArrayList<EditText> amounts;
    ArrayList<EditText> prices;
    ActivityAddProductBinding binding;
    FirebaseDatabase firebaseDatabase;
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult activityResult) {
                    int result = activityResult.getResultCode();
                    if(result == RESULT_OK){
                        Intent data = activityResult.getData();
                        Uri uri = data.getData();
                        binding.image.setImageURI(uri);
                    }
                    else{
                        Toast.makeText(AddProduct.this, "No image selected", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        Calendar date = new GregorianCalendar();
        int year = date.get(GregorianCalendar.YEAR);
        int month = date.get(GregorianCalendar.MONTH);
        int day = date.get(GregorianCalendar.DATE);
        amounts = new ArrayList<>();
        prices = new ArrayList<>();

        binding.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                activityResultLauncher.launch(intent);
            }
        });
        binding.date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focus) {
                 if (focus){
                     DatePickerDialog datePickerDialog = new DatePickerDialog(AddProduct.this, new DatePickerDialog.OnDateSetListener() {
                         @Override
                         public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                             binding.date.setText(String.valueOf(year) + "-" +(month+1) + "-" + day);
                             binding.date.clearFocus();
                         }
                     },year,month, day);
                     datePickerDialog.show();
                 }
            }
        });
        binding.time.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focus) {
                if (focus){
                    TimePickerDialog timePickerDialog = new TimePickerDialog(AddProduct.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int i, int i1) {
                            String time = "";
                            if (i<10) time +="0";
                            time += String.valueOf(i);
                            if (i1<10) time +=":0";
                            else time +=":";
                            time += String.valueOf(i1);
                            binding.time.setText(time);
                            binding.time.clearFocus();
                        }
                    },23,59,true);
                    timePickerDialog.show();
                }
            }
        });
        binding.addPrice.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                layoutParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED,1f);
                GridLayout.LayoutParams layoutParams2 = new GridLayout.LayoutParams();
                layoutParams2.columnSpec = GridLayout.spec(GridLayout.UNDEFINED,1f);
                TextView textView = new TextView(AddProduct.this);
                textView.setText("≥ ");
                textView.setTextSize(30);
                binding.wholesalePrices.addView(textView);
                EditText editText = new EditText(AddProduct.this);
                editText.setHint("Amount");
                editText.setLayoutParams(layoutParams);
                editText.setBackgroundColor(Color.WHITE);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setPadding(0,0,0,0);
                amounts.add(editText);
                binding.wholesalePrices.addView(editText);
                TextView textView1 = new TextView(AddProduct.this);
                textView1.setText(" : ");
                textView1.setTextSize(30);
                binding.wholesalePrices.addView(textView1);
                EditText editText1 = new EditText(AddProduct.this);
                editText1.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText1.setHint("Price");
                editText1.setLayoutParams(layoutParams2);
                editText1.setBackgroundColor(Color.WHITE);
                editText1.setPadding(0,0,0,0);
                prices.add(editText1);
                binding.wholesalePrices.addView(editText1);
            }
        });
        binding.addProducts.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                try {
                    saveProduct();
                    finish();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void saveProduct() throws ParseException {
        HashMap<String, Double> pricesMap = new HashMap<>();
        Product product = new Product();
        product.setName(binding.name.getText().toString());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date parsedDate = simpleDateFormat.parse(binding.date.getText().toString() + " " + binding.time.getText().toString());
        BitmapDrawable bitmapDrawable = (BitmapDrawable) binding.image.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();
        String image = convertToString(bitmap);
        product.setImage(image);
        product.setEndingTime(parsedDate);
        product.setCurrentPrice(Double.parseDouble(binding.price.getText().toString()));
        pricesMap.put("-1", Double.parseDouble(binding.price.getText().toString()));
        for (int i = 0; i < amounts.size(); i++) {
            pricesMap.put(amounts.get(i).getText().toString(), Double.parseDouble(prices.get(i).getText().toString()));
        };
        product.setPrices(pricesMap);
        firebaseDatabase.getReference().child("products").push().setValue(product);
        firebaseDatabase.getReference().child("products").push();
    }
    private  String convertToString(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
}
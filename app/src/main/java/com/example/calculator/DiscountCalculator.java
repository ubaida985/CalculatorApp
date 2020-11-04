package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

public class DiscountCalculator extends AppCompatActivity {

    double mrp, discount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discount_calculator);

        final TextView textMRP, textDiscount, textResult;
        Button buttonEquals, buttonBack;

        textMRP = findViewById(R.id.textMRP);
        textDiscount = findViewById(R.id.textDiscount);
        textResult = findViewById(R.id.textResult);
        buttonBack = findViewById(R.id.buttonBack);
        buttonEquals = findViewById(R.id.buttonEquals);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DiscountCalculator.this, MainActivity.class);
                startActivity(intent);
            }
        });
        buttonEquals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(textDiscount.getWindowToken(), 0);
                mrp = Double.parseDouble(textMRP.getText().toString());
                discount = Double.parseDouble(textDiscount.getText().toString());
                double finalPrice = mrp - ((discount*mrp) /100);
                textResult.setText(Double.toString(finalPrice));
            }
        });
    }
}
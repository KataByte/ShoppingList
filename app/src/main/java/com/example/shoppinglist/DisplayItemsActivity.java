package com.example.shoppinglist;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class DisplayItemsActivity extends AppCompatActivity {
    private ArrayList<String> shoppingItems;
    private TextView displayTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_item);

        // Haetaan ostoslistan kohteet
        shoppingItems = getIntent().getStringArrayListExtra("shoppingItems");
        displayTextView = findViewById(R.id.tvItems);

        displayShoppingItems();
    }

    //  Metodi suoritetaan, kun käyttäjä napsauttaa "Close" -painiketta.
    //  Tyhjennettän ostoslista, asetetaan tulos RESULT_OK ja suljetaan aktiviteetti.
    public void onCloseClick(View view) {
        shoppingItems.clear();
        setResult(RESULT_OK);
        finish();
    }

    // Näytetään ostoslistan kohteet displayTextView:ssä
    private void displayShoppingItems() {
        StringBuilder builder = new StringBuilder();
        for (String item : shoppingItems) {
            builder.append(item).append("\n");
        }
        displayTextView.setText(builder.toString());
    }
}

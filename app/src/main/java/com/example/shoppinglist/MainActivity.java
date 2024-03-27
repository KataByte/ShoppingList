package com.example.shoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final ArrayList<String> shoppingItems = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private EditText inputEditText;
    private ActivityResultLauncher<Intent> displayItemsLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, shoppingItems);

        displayItemsLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        inputEditText.getText().clear();
                        shoppingItems.clear();
                        adapter.notifyDataSetChanged();
                    }
                }
        );

        Button saveButton = findViewById(R.id.btnSave);
        Button doneButton = findViewById(R.id.btnDone);
        Button addItemButton = findViewById(R.id.btnAddItem);
        Button showListButton = findViewById(R.id.btnShowList);

        inputEditText = findViewById(R.id.txtAdd);

        ListView shoppingListView = findViewById(R.id.shoppingListView);
        shoppingListView.setAdapter(adapter);


        shoppingListView.setOnItemLongClickListener((parent, view, position, id) -> {
            String selectedItem = shoppingItems.get(position);
            shoppingItems.remove(position);
            showSnackbar("Item deleted: " + selectedItem);
            adapter.notifyDataSetChanged();
            return true;
        });

        saveButton.setOnClickListener(v -> {
            String item = inputEditText.getText().toString();
            if (isValidItem(item)) {
                showConfirmationDialog(item);
            } else {
                showValidationErrorDialog();
            }
        });

        doneButton.setOnClickListener(v -> showDoneConfirmationDialog());

        addItemButton.setOnClickListener(v -> showAddItemDialog());

        showListButton.setOnClickListener(v -> openDisplayItemsActivityForResult());
    }

    private void openDisplayItemsActivityForResult() {
        Intent intent = new Intent(this, DisplayItemsActivity.class);
        intent.putStringArrayListExtra("shoppingItems", shoppingItems);
        displayItemsLauncher.launch(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                inputEditText.getText().clear();
                shoppingItems.clear();
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void showAddItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Item");

        View dialogLayout = getLayoutInflater().inflate(R.layout.dialog_add_item, null);
        builder.setView(dialogLayout);

        EditText itemNameEditText = dialogLayout.findViewById(R.id.editTextItemName);
        EditText itemCountEditText = dialogLayout.findViewById(R.id.editTextItemCount);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String itemName = itemNameEditText.getText().toString();
            String itemCountString = itemCountEditText.getText().toString();

            if (itemName.length() >= 3 && !itemCountString.isEmpty()) {
                int itemCount = Integer.parseInt(itemCountString);
                showSnackbar("Item added: " + itemName + " " + itemCount + "pcs");
                shoppingItems.add(itemName + " " + itemCount + "pcs");
            } else {
                showSnackbar("Invalid item. Please check the input.");
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // User canceled the dialog
        });

        builder.show();
    }

    private void showSnackbar(String message) {
        View rootView = findViewById(R.id.mainLayout);
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
    }

    private boolean isValidItem(String item) {
        return item.length() >= 5 && item.length() <= 10 && !item.contains(" ");
    }

    private void showConfirmationDialog(String item) {
        new AlertDialog.Builder(this)
                .setMessage("Data is correct. Add item to the list?")
                .setPositiveButton("Add", (dialog, which) -> {
                    shoppingItems.add(item);
                    inputEditText.getText().clear();
                    adapter.notifyDataSetChanged();
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void showDoneConfirmationDialog() {
        CharSequence[] options = {"Show items", "Send SMS", "Quit"};

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Choose an option");
        alertDialogBuilder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                openDisplayItemsActivityForResult();
            } else if (which == 1) {
                Toast.makeText(MainActivity.this, "User selected 'Send SMS' option", Toast.LENGTH_SHORT).show();
            } else if (which == 2) {
                finish();
            }
        });
        alertDialogBuilder.create().show();
    }

    private void showValidationErrorDialog() {
        new AlertDialog.Builder(this)
                .setMessage("Data is not in the correct format.")
                .setPositiveButton("OK", null)
                .create()
                .show();
    }
}

package com.example.tailormanagementsystem;
import com.example.tailormanagementsystem.Customer;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.tailormanagementsystem.AppDatabase;
import com.example.tailormanagementsystem.DatabaseClient;


import java.util.List;

public class CustomerAdapter extends ArrayAdapter<Customer> {

    public CustomerAdapter(Context context, List<Customer> customers) {
        super(context, 0, customers);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Customer customer = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_customer, parent, false);
        }

        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvMobile = convertView.findViewById(R.id.tvMobile);
        TextView tvAddress = convertView.findViewById(R.id.tvAddress);
        TextView tvGarment = convertView.findViewById(R.id.tvGarment);
        TextView tvMeasurements = convertView.findViewById(R.id.tvMeasurements);
        Button btnMeasurements = convertView.findViewById(R.id.btnMeasurements);

        tvName.setText(customer.getName());
        tvMobile.setText(" " + customer.getMobile());
        tvAddress.setText(" " + customer.getAddress());
        tvGarment.setText("🧥 Garment: " + (customer.getGarment() != null ? customer.getGarment() : "N/A"));

        //  Format measurements
        String measurements = String.format(
                "📐 Chest: %.1f | Waist: %.1f | Hip: %.1f\nShoulder: %.1f | Sleeve: %.1f | Inseam: %.1f",
                customer.getChest(), customer.getWaist(), customer.getHip(),
                customer.getShoulder(), customer.getSleeve(), customer.getInseam());

        // Hide if all are zero
        if (customer.getChest() == 0 && customer.getWaist() == 0 && customer.getHip() == 0 &&
                customer.getShoulder() == 0 && customer.getSleeve() == 0 && customer.getInseam() == 0) {
            tvMeasurements.setVisibility(View.GONE);
        } else {
            tvMeasurements.setVisibility(View.VISIBLE);
            tvMeasurements.setText(measurements);
        }
        // Show dialog on button click
        btnMeasurements.setOnClickListener(v -> {
            showMeasurementDialog(getContext(), customer);
        });


        return convertView;
    }

    private void showMeasurementDialog(Context context, Customer customer) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_measurements, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();

        EditText inputChest = view.findViewById(R.id.inputChest);
        EditText inputWaist = view.findViewById(R.id.inputWaist);
        EditText inputHip = view.findViewById(R.id.inputHip);
        EditText inputShoulder = view.findViewById(R.id.inputShoulder);
        EditText inputSleeve = view.findViewById(R.id.inputSleeve);
        EditText inputInseam = view.findViewById(R.id.inputInseam);
        Button saveBtn = view.findViewById(R.id.saveMeasurementsBtn);

        // Pre-fill existing values
        inputChest.setText(String.valueOf(customer.getChest()));
        inputWaist.setText(String.valueOf(customer.getWaist()));
        inputHip.setText(String.valueOf(customer.getHip()));
        inputShoulder.setText(String.valueOf(customer.getShoulder()));
        inputSleeve.setText(String.valueOf(customer.getSleeve()));
        inputInseam.setText(String.valueOf(customer.getInseam()));

        saveBtn.setOnClickListener(v -> {
            try {
                customer.setChest(Float.parseFloat(inputChest.getText().toString()));
                customer.setWaist(Float.parseFloat(inputWaist.getText().toString()));
                customer.setHip(Float.parseFloat(inputHip.getText().toString()));
                customer.setShoulder(Float.parseFloat(inputShoulder.getText().toString()));
                customer.setSleeve(Float.parseFloat(inputSleeve.getText().toString()));
                customer.setInseam(Float.parseFloat(inputInseam.getText().toString()));

                AppDatabase db = DatabaseClient.getInstance(context).getAppDatabase();
                db.customerDao().insertCustomer(customer);

                Toast.makeText(context, "Measurements saved ✅", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } catch (Exception e) {
                Toast.makeText(context, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
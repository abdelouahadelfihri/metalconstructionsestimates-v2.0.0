package com.example.steelquotes.recyclerviewadapters;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.steelquotes.R;
import com.example.steelquotes.models.Customer;
import com.example.steelquotes.modules.customers.CustomerDetails;

import java.util.ArrayList;

public class CustomersListAdapter extends RecyclerView.Adapter<CustomersListAdapter.ViewHolder> {

    private ArrayList<Customer> customersList;
    private Activity activity;

    // Constructor to accept an Activity and a list of Steel objects
    public CustomersListAdapter(Activity activity, ArrayList<Customer> customersList) {
        this.customersList = customersList;
        this.activity = activity;
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewCustomerId;
        public TextView textViewCustomerName;
        public TextView textViewCustomerTel;
        public TextView textViewCustomerMobile;

        public ViewHolder(View view) {
            super(view);
            textViewCustomerId = view.findViewById(R.id.recycler_view_customer_id);
            textViewCustomerName = view.findViewById(R.id.recycler_view_name_customer);
            textViewCustomerTel = view.findViewById(R.id.recycler_view_tel_customer);
            textViewCustomerMobile = view.findViewById(R.id.recycler_view_mobile_customer);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.customer_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Customer customer = customersList.get(position);
        String customerId = "Id Client : " + customer.getId().toString();
        String customerName = "Nom : " + customer.getName();
        String customerTel = "Tel : " + customer.getTelephone();
        String customerMobile = "Mobile : " + customer.getMobile();
        holder.textViewCustomerId.setText(customerId);
        holder.textViewCustomerName.setText(customerName);
        holder.textViewCustomerTel.setText(customerTel);
        holder.textViewCustomerMobile.setText(customerMobile);

        // Set an onClick listener for the item view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int layoutPosition = holder.getLayoutPosition();
                Customer clickedCustomer = customersList.get(layoutPosition);  // Get the Steel item at this position
                Intent intent;
                ComponentName callingActivity = activity.getCallingActivity();
                if (callingActivity == null) {
                    intent = new Intent(v.getContext(), CustomerDetails.class);
                    intent.putExtra("customerIdExtra", clickedCustomer.getId().toString());
                    v.getContext().startActivity(intent);
                } else {
                    Intent returnIntent = activity.getIntent();
                    returnIntent.putExtra("customerIdExtraResult", clickedCustomer.getId().toString());
                    activity.setResult(Activity.RESULT_OK, returnIntent);
                    activity.finish();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return customersList.size();
    }
}
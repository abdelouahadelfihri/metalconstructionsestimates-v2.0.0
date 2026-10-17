package com.example.metalconstructionsestimates.recyclerviewadapters;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.models.Customer;
import com.example.metalconstructionsestimates.modules.customers.CustomerDetails;

import java.util.ArrayList;
import java.util.Locale;

public class CustomersListAdapter extends RecyclerView.Adapter<CustomersListAdapter.ViewHolder> {

    private ArrayList<Customer> customersList;
    private Activity activity;

    private static final String NOT_SPECIFIED = "Not specified";

    // Constructor to accept an Activity and a list of Customer objects
    public CustomersListAdapter(Activity activity, ArrayList<Customer> customersList) {
        this.customersList = customersList;
        this.activity = activity;
    }

    public void updateCustomers(ArrayList<Customer> customers) {
        this.customersList = customers;
        notifyDataSetChanged();
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewCustomerAvatar;
        public TextView textViewCustomerId;
        public TextView textViewCustomerName;
        public TextView textViewCustomerTel;
        public TextView textViewCustomerMobile;

        public ViewHolder(View view) {
            super(view);
            textViewCustomerAvatar = view.findViewById(R.id.recycler_view_customer_avatar);
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
        String name = customer.getName() == null ? "" : customer.getName().trim();
        holder.textViewCustomerAvatar.setText(name.isEmpty() ? "?" : name.substring(0, 1).toUpperCase(Locale.getDefault()));
        holder.textViewCustomerId.setText("#" + customer.getId());
        holder.textViewCustomerName.setText(name.isEmpty() ? NOT_SPECIFIED : name);
        holder.textViewCustomerTel.setText("Tel : " + orNotSpecified(customer.getTelephone()));
        holder.textViewCustomerMobile.setText("Mobile : " + orNotSpecified(customer.getMobile()));

        // Set an onClick listener for the item view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int layoutPosition = holder.getLayoutPosition();
                Customer clickedCustomer = customersList.get(layoutPosition);
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

        // Long press: quick contact actions (call, SMS, email, copy number)
        holder.itemView.setOnLongClickListener(v -> {
            showContactActions(v, customersList.get(holder.getLayoutPosition()));
            return true;
        });
    }

    private void showContactActions(View anchor, Customer customer) {
        PopupMenu popup = new PopupMenu(activity, anchor);
        popup.getMenu().add(0, 1, 0, R.string.customerActionCall);
        popup.getMenu().add(0, 2, 1, R.string.customerActionSms);
        popup.getMenu().add(0, 3, 2, R.string.customerActionEmail);
        popup.getMenu().add(0, 4, 3, R.string.customerActionCopy);
        popup.setOnMenuItemClickListener(item -> {
            String phone = phoneOf(customer);
            switch (item.getItemId()) {
                case 1:
                    if (phone == null) {
                        toast(R.string.noPhoneNumber);
                    } else {
                        launch(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(phone))));
                    }
                    return true;
                case 2:
                    if (phone == null) {
                        toast(R.string.noPhoneNumber);
                    } else {
                        launch(new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + Uri.encode(phone))));
                    }
                    return true;
                case 3:
                    String email = customer.getEmail();
                    if (email == null || email.trim().isEmpty()) {
                        toast(R.string.noEmail);
                    } else {
                        launch(new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + Uri.encode(email.trim()))));
                    }
                    return true;
                case 4:
                    if (phone == null) {
                        toast(R.string.noPhoneNumber);
                    } else {
                        ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboard.setPrimaryClip(ClipData.newPlainText("phone", phone));
                        toast(R.string.phoneCopied);
                    }
                    return true;
                default:
                    return false;
            }
        });
        popup.show();
    }

    // Prefers the mobile number, falls back to the landline
    private static String phoneOf(Customer customer) {
        if (customer.getMobile() != null && !customer.getMobile().trim().isEmpty()) {
            return customer.getMobile().trim();
        }
        if (customer.getTelephone() != null && !customer.getTelephone().trim().isEmpty()) {
            return customer.getTelephone().trim();
        }
        return null;
    }

    private void launch(Intent intent) {
        try {
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            toast(R.string.noAppToHandle);
        }
    }

    private void toast(int messageRes) {
        Toast.makeText(activity, messageRes, Toast.LENGTH_SHORT).show();
    }

    private static String orNotSpecified(String value) {
        return value == null || value.trim().isEmpty() ? NOT_SPECIFIED : value;
    }

    @Override
    public int getItemCount() {
        return customersList.size();
    }
}

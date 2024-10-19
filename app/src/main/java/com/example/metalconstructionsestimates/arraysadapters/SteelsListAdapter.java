package com.example.metalconstructionsestimates.arraysadapters;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.models.Steel;
import com.example.metalconstructionsestimates.modules.steels.SteelDetails;
import java.util.ArrayList;


public class SteelsListAdapter extends RecyclerView.Adapter<SteelsListAdapter.ViewHolder> {

    private ArrayList<Steel> steelsList;
    private Activity activity;

    // Constructor to accept an Activity and a list of Steel objects
    public SteelsListAdapter(Activity activity, ArrayList<Steel> steelList) {
        this.steelsList = steelList;
        this.activity = activity;
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewSteelId;
        public TextView textViewSteelType;
        public TextView textViewGeometricShape;
        public TextView textViewSteelWeight;

        public ViewHolder(View view) {
            super(view);
            textViewSteelId = view.findViewById(R.id.recycler_view_steel_id);
            textViewSteelType = view.findViewById(R.id.recycler_view_steel_type);
            textViewGeometricShape = view.findViewById(R.id.recycler_view_steel_geometric_shape);
            textViewSteelWeight = view.findViewById(R.id.recycler_view_steel_weight);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.steel_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Steel steel = steelsList.get(position);
        String steelId = "Id Acier : " + steel.getId().toString();
        String steelType = "Type Acier : " + steel.getType();
        String geometricShape = "Forme Géometrique : " + steel.getGeometricShape();
        String steelWeight = "Poids : " + steel.getWeight().toString();
        holder.textViewSteelId.setText(steelId);
        holder.textViewSteelType.setText(steelType);
        holder.textViewGeometricShape.setText(geometricShape);
        holder.textViewSteelWeight.setText(steelWeight);

        // Set an onClick listener for the item view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int layoutPosition = holder.getLayoutPosition();
                Steel clickedSteel = steelsList.get(layoutPosition);  // Get the Steel item at this position
                Intent intent;
                ComponentName callingActivity = activity.getCallingActivity();
                if (callingActivity == null) {
                    intent = new Intent(v.getContext(), SteelDetails.class);
                    intent.putExtra("steelIdExtra", clickedSteel.getId().toString());
                    v.getContext().startActivity(intent);
                } else {
                    Intent returnIntent = activity.getIntent();
                    returnIntent.putExtra("steelIdExtraResult", clickedSteel.getId().toString());
                    activity.setResult(Activity.RESULT_OK, returnIntent);
                    activity.finish();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return steelsList.size();
    }
}

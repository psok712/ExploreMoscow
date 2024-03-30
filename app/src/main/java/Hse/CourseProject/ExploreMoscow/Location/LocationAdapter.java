package Hse.CourseProject.ExploreMoscow.Location;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Objects;

import Hse.CourseProject.ExploreMoscow.R;

public class LocationAdapter extends RecyclerView.Adapter<LocationViewHolder> {

    private final List<Location> _locations;

    public LocationAdapter(List<Location> locations) {
        _locations = locations;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.location_item_rv, parent, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        holder.locationNameTv.setText(_locations.get(position).getNameLocation());

        FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Location")
                .child(_locations.get(position).getNameLocation())
                .child("image")
                .get()
                .addOnCompleteListener(task -> {
                    try {
                        String locationImage = Objects.requireNonNull(task.getResult().getValue()).toString();

                        if (!locationImage.isEmpty()) {
                            Glide.with(holder.itemView.getContext())
                                    .load(locationImage)
                                    .into(holder.locationIv);
                        }
                    } catch (Exception e) {
                        Toast.makeText(holder.itemView.getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return _locations.size();
    }
}

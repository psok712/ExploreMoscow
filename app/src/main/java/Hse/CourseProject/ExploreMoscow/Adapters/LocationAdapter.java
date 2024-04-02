package Hse.CourseProject.ExploreMoscow.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Objects;

import Hse.CourseProject.ExploreMoscow.R;
import Hse.CourseProject.ExploreMoscow.Models.Location;
import Hse.CourseProject.ExploreMoscow.BottomNavigation.Location.LocationDetailsFragment;
import Hse.CourseProject.ExploreMoscow.Holders.LocationViewHolder;

public class LocationAdapter extends RecyclerView.Adapter<LocationViewHolder> {

    private final List<Location> _locations;

    private static final String LOCATION_NODE = "Location";

    public LocationAdapter(List<Location> locations) {
        _locations = locations;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var view = createView(parent);
        return new LocationViewHolder(view);
    }

    private View createView(@NonNull ViewGroup parent) {
        return LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_rv_location, parent, false);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        bindLocationData(holder, position);
        setItemClick(holder, position);
    }

    private void bindLocationData(@NonNull LocationViewHolder holder, int position) {
        holder.locationNameTv.setText(_locations.get(position).nameLocation());

        try {
            loadLocationImage(holder, position);
        } catch (Exception e) {
            showErrorMessage(holder.itemView.getContext(), e);
        }
    }

    private void loadLocationImage(@NonNull LocationViewHolder holder, int position) {
        FirebaseDatabase
                .getInstance()
                .getReference()
                .child(LOCATION_NODE)
                .child(_locations.get(position).nameLocation())
                .child("image")
                .get()
                .addOnCompleteListener(task -> {
                    try {
                        var locationImage = Objects.requireNonNull(task.getResult().getValue()).toString();

                        if (!locationImage.isEmpty()) {
                            displayLocationImage(holder, locationImage);
                        }
                    } catch (Exception e) {
                        showErrorMessage(holder.itemView.getContext(), e);
                    }
                });
    }

    private void displayLocationImage(@NonNull LocationViewHolder holder, String locationImage) {
        Glide.with(holder.itemView.getContext())
                .load(locationImage)
                .into(holder.locationIv);
    }

    private void setItemClick(@NonNull LocationViewHolder holder, int position) {
        holder.itemView.setOnClickListener(v -> {
            var selectedLocation = _locations.get(position);
            openLocationDetailsFragment(holder.itemView.getContext(), selectedLocation.nameLocation());
        });
    }

    private void openLocationDetailsFragment(@NonNull Context contextView, String locationName) {
        var detailsFragment = LocationDetailsFragment.newInstance(locationName);

        var transaction = ((FragmentActivity) contextView).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, detailsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void showErrorMessage(@NonNull Context contextView, Exception e) {
        Toast.makeText(contextView, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return _locations.size();
    }
}

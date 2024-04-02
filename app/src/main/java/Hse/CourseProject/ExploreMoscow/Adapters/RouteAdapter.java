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
import Hse.CourseProject.ExploreMoscow.BottomNavigation.Routes.RouteDetailsFragment;
import Hse.CourseProject.ExploreMoscow.Holders.RouteViewHolder;

public class RouteAdapter extends RecyclerView.Adapter<RouteViewHolder> {

    private final List<Location> _locations;
    private static final String ROUTES_NODE = "Routes";

    public RouteAdapter(List<Location> locations) {
        _locations = locations;
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var view = inflateLayout(parent);
        return new RouteViewHolder(view);
    }

    private View inflateLayout(@NonNull ViewGroup parent) {
        return LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rv_route, parent, false);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder holder, int position) {
        var currentRoute = _locations.get(position).nameLocation();
        bindRouteName(holder, currentRoute);
        loadImage(holder, currentRoute);
        setOnClickListener(holder, position);
    }

    private void bindRouteName(@NonNull RouteViewHolder holder, String currentRoute) {
        holder.routeNameTv.setText(currentRoute);
    }

    private void loadImage(@NonNull RouteViewHolder holder, String currentRoute) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child(ROUTES_NODE)
                .child(currentRoute)
                .child("image")
                .get()
                .addOnCompleteListener(task -> {
                    try {
                        var routeImage = Objects.requireNonNull(task.getResult().getValue()).toString();

                        if (!routeImage.isEmpty()) {
                            loadRouteImage(holder, routeImage);
                        }
                    } catch (Exception e) {
                        showErrorMessage(holder.itemView.getContext(), e);
                    }
                });
    }

    private void loadRouteImage(@NonNull RouteViewHolder holder, String routeImage) {
        Glide.with(holder.itemView.getContext())
                .load(routeImage)
                .into(holder.routeIv);
    }

    private void setOnClickListener(@NonNull RouteViewHolder holder, int position) {
        holder.itemView.setOnClickListener(v -> {
            var selectedLocation = _locations.get(position);
            navigateToRouteDetails(holder.itemView.getContext(), selectedLocation.nameLocation());
        });
    }

    private void navigateToRouteDetails(@NonNull Context context, String locationName) {
        var detailsFragment = RouteDetailsFragment.newInstance(locationName);
        var transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, detailsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void showErrorMessage(@NonNull Context context, Exception e) {
        Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return _locations.size();
    }
}

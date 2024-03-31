package Hse.CourseProject.ExploreMoscow.Ribbons.RouteRibbon;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Objects;

import Hse.CourseProject.ExploreMoscow.Ribbons.Location;
import Hse.CourseProject.ExploreMoscow.R;

public class RouteAdapter extends RecyclerView.Adapter<RouteViewHolder> {

    private final List<Location> _locations;

    public RouteAdapter(List<Location> locations) {
        _locations = locations;
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.route_item_rv, parent, false);
        return new RouteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder holder, int position) {
        holder.routeNameTv.setText(_locations.get(position).nameLocation());

        FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Routes")
                .child(_locations.get(position).nameLocation())
                .child("image")
                .get()
                .addOnCompleteListener(task -> {
                    try {
                        String routeImage = Objects.requireNonNull(task.getResult().getValue()).toString();

                        if (!routeImage.isEmpty()) {
                            Glide.with(holder.itemView.getContext())
                                    .load(routeImage)
                                    .into(holder.routeIv);
                        }
                    } catch (Exception e) {
                        Toast.makeText(holder.itemView.getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT)
                                .show();
                    }
                });

        holder.itemView.setOnClickListener(v -> {
            Location selectedLocation = _locations.get(position);

            RouteDetailsFragment detailsFragment = RouteDetailsFragment.newInstance(selectedLocation.nameLocation());

            FragmentTransaction transaction = ((FragmentActivity) holder.itemView.getContext()).getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, detailsFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }

    @Override
    public int getItemCount() {
        return _locations.size();
    }
}
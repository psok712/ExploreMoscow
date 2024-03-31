package Hse.CourseProject.ExploreMoscow.Route;

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

import Hse.CourseProject.ExploreMoscow.R;

public class RouteAdapter extends RecyclerView.Adapter<RouteViewHolder> {

    private final List<Route> _routes;

    public RouteAdapter(List<Route> routes) {
        _routes = routes;
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
        holder.routeNameTv.setText(_routes.get(position).nameRoute());

        FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Routes")
                .child(_routes.get(position).nameRoute())
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
            Route selectedRoute = _routes.get(position);

            RouteDetailsFragment detailsFragment = RouteDetailsFragment.newInstance(selectedRoute.nameRoute());

            FragmentTransaction transaction = ((FragmentActivity) holder.itemView.getContext()).getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, detailsFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }

    @Override
    public int getItemCount() {
        return _routes.size();
    }
}
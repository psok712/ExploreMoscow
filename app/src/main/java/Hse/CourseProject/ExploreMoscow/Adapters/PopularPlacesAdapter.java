package Hse.CourseProject.ExploreMoscow.Adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Objects;

import Hse.CourseProject.ExploreMoscow.R;
import Hse.CourseProject.ExploreMoscow.Models.Location;
import Hse.CourseProject.ExploreMoscow.BottomNavigation.PopularPlaces.PopularPlacesDetailsFragment;
import Hse.CourseProject.ExploreMoscow.Holders.PopularPlacesViewHolder;

public class PopularPlacesAdapter extends RecyclerView.Adapter<PopularPlacesViewHolder> {

    private final List<Location> _places;
    private static final String LOCATION_NODE = "Location";
    private static final String ROUTES_NODE = "Routes";

    public PopularPlacesAdapter(List<Location> places) {
        _places = places;
    }

    @NonNull
    @Override
    public PopularPlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_rv_popular_places, parent, false);
        return new PopularPlacesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularPlacesViewHolder holder, int position) {
        bindPlaceName(holder, position);
        loadImageForNode(holder, position, LOCATION_NODE);
        loadImageForNode(holder, position, ROUTES_NODE);
        setOnClickListener(holder, position);
    }

    private void bindPlaceName(@NonNull PopularPlacesViewHolder holder, int position) {
        holder.placeNameTv.setText(_places.get(position).nameLocation());
    }

    private void loadImageForNode(@NonNull PopularPlacesViewHolder holder, int position, String node) {
        FirebaseDatabase.getInstance()
                .getReference(node)
                .child(_places.get(position).nameLocation())
                .child("image")
                .get()
                .addOnCompleteListener(task -> {
                    try {
                        String placeImage = Objects.requireNonNull(task.getResult().getValue()).toString();
                        if (!placeImage.isEmpty()) {
                            loadImage(holder, placeImage);
                        }
                    } catch (Exception ignored) {
                    }
                });
    }

    private void loadImage(@NonNull PopularPlacesViewHolder holder, String imageUrl) {
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .into(holder.placeIv);
    }

    private void setOnClickListener(@NonNull PopularPlacesViewHolder holder, int position) {
        holder.itemView.setOnClickListener(v -> {
            var selectedPlace = _places.get(position);
            var detailsFragment = PopularPlacesDetailsFragment.newInstance(selectedPlace.nameLocation());
            var transaction = ((FragmentActivity) holder.itemView.getContext()).getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, detailsFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }

    @Override
    public int getItemCount() {
        return _places.size();
    }
}

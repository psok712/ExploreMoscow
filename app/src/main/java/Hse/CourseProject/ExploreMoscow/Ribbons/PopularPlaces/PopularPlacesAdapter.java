package Hse.CourseProject.ExploreMoscow.Ribbons.PopularPlaces;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Objects;

import Hse.CourseProject.ExploreMoscow.R;
import Hse.CourseProject.ExploreMoscow.Ribbons.Location;

public class PopularPlacesAdapter extends RecyclerView.Adapter<PopularPlacesViewHolder> {

    private final List<Location> _places;

    public PopularPlacesAdapter(List<Location> places) {
        _places = places;
    }

    @NonNull
    @Override
    public PopularPlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.popular_places_item_rv, parent, false);
        return new PopularPlacesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularPlacesViewHolder holder, int position) {
        holder.placeNameTv.setText(_places.get(position).nameLocation());

        FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Location")
                .child(_places.get(position).nameLocation())
                .child("image")
                .get()
                .addOnCompleteListener(task -> {
                    try {
                        String placeImage = Objects.requireNonNull(task.getResult().getValue()).toString();

                        if (!placeImage.isEmpty()) {
                            Glide.with(holder.itemView.getContext())
                                    .load(placeImage)
                                    .into(holder.placeIv);
                        }
                    } catch (Exception ignored) {}
                });

        FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Routes")
                .child(_places.get(position).nameLocation())
                .child("image")
                .get()
                .addOnCompleteListener(task -> {
                    try {
                        String placeImage = Objects.requireNonNull(task.getResult().getValue()).toString();

                        if (!placeImage.isEmpty()) {
                            Glide.with(holder.itemView.getContext())
                                    .load(placeImage)
                                    .into(holder.placeIv);
                        }
                    } catch (Exception ignored) {}
                });

        holder.itemView.setOnClickListener(v -> {
            Location selectedPlace = _places.get(position);

            PopularPlacesDetailsFragment detailsFragment = PopularPlacesDetailsFragment.newInstance(selectedPlace.nameLocation());

            FragmentTransaction transaction = ((FragmentActivity) holder.itemView.getContext()).getSupportFragmentManager().beginTransaction();
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
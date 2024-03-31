package Hse.CourseProject.ExploreMoscow.BottomNavigation.PopularPlaces;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Hse.CourseProject.ExploreMoscow.Ribbons.Location;
import Hse.CourseProject.ExploreMoscow.Ribbons.PopularPlaces.PopularPlacesAdapter;
import Hse.CourseProject.ExploreMoscow.databinding.FragmentPopularPlacesBinding;

public class PopularPlacesFragment extends Fragment {
    private FragmentPopularPlacesBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPopularPlacesBinding.inflate(inflater, container, false);

        loadAllPlaces();

        return binding.getRoot();
    }

    private void loadAllPlaces() {
        List<Location> places = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference("Location")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot locationSnapshot : snapshot.getChildren()) {
                            String nameLocation = locationSnapshot.getKey();
                            String loadPictureLocation = Objects.requireNonNull(locationSnapshot.child("image").getValue()).toString();
                            String history = Objects.requireNonNull(locationSnapshot.child("history").getValue()).toString();
                            String mainInfo = Objects.requireNonNull(locationSnapshot.child("mainInfo").getValue()).toString();

                            Location location = new Location(nameLocation, loadPictureLocation, history, mainInfo);
                            places.add(location);
                        }

                        PopularPlacesAdapter adapter = new PopularPlacesAdapter(places);
                        binding.placesRv.setLayoutManager(new LinearLayoutManager(getContext()));
                        binding.placesRv.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to read locations.", Toast.LENGTH_SHORT).show();
                    }
                });

        FirebaseDatabase.getInstance().getReference("Routes")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot routeSnapshot : snapshot.getChildren()) {
                            String nameRoute = routeSnapshot.getKey();
                            String loadPictureRoute = Objects.requireNonNull(routeSnapshot.child("image").getValue()).toString();
                            String history = Objects.requireNonNull(routeSnapshot.child("history").getValue()).toString();
                            String mainInfo = Objects.requireNonNull(routeSnapshot.child("mainInfo").getValue()).toString();

                            Location location = new Location(nameRoute, loadPictureRoute, history, mainInfo);
                            places.add(location);
                        }

                        PopularPlacesAdapter adapter = new PopularPlacesAdapter(places);
                        binding.placesRv.setLayoutManager(new LinearLayoutManager(getContext()));
                        binding.placesRv.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to read Routes.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}

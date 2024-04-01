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

import Hse.CourseProject.ExploreMoscow.Models.Location;
import Hse.CourseProject.ExploreMoscow.Adapters.PopularPlacesAdapter;
import Hse.CourseProject.ExploreMoscow.databinding.FragmentPopularPlacesBinding;

public class PopularPlacesFragment extends Fragment {

    private FragmentPopularPlacesBinding binding;
    private static final String LOCATION_NODE = "Location";
    private static final String ROUTES_NODE = "Routes";
    private final List<Location> places = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentPopularPlacesBinding.inflate(inflater, container, false);
        loadAllPlaces();
        return binding.getRoot();
    }

    private void loadAllPlaces() {
        places.clear();
        loadPlacesFromNode(LOCATION_NODE);
        places.clear();
        loadPlacesFromNode(ROUTES_NODE);
    }

    private void loadPlacesFromNode(String node) {
        FirebaseDatabase.getInstance().getReference(node)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (var placeSnapshot : snapshot.getChildren()) {
                            var name = placeSnapshot.getKey();
                            var image = Objects.requireNonNull(placeSnapshot.child("image").getValue()).toString();
                            var history = Objects.requireNonNull(placeSnapshot.child("history").getValue()).toString();
                            var mainInfo = Objects.requireNonNull(placeSnapshot.child("mainInfo").getValue()).toString();

                            var location = new Location(name, image, history, mainInfo);
                            places.add(location);
                        }

                        displayPlaces(places);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        handleDatabaseError(node);
                    }
                });
    }

    private void displayPlaces(List<Location> places) {
        var adapter = new PopularPlacesAdapter(places);
        binding.placesRv.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.placesRv.setAdapter(adapter);
    }

    private void handleDatabaseError(String node) {
        Toast.makeText(getContext(), "Не удалось загрузить " + node + ".", Toast.LENGTH_SHORT).show();
    }
}

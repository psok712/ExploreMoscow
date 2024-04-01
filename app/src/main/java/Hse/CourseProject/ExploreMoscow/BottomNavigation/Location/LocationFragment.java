package Hse.CourseProject.ExploreMoscow.BottomNavigation.Location;

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
import Hse.CourseProject.ExploreMoscow.Adapters.LocationAdapter;
import Hse.CourseProject.ExploreMoscow.databinding.FragmentLocationBinding;

public class LocationFragment extends Fragment
{

    private FragmentLocationBinding binding;
    private static final String LOCATION_NODE = "Location";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentLocationBinding.inflate(inflater, container, false);
        loadLocations();
        return binding.getRoot();
    }

    private void loadLocations() {
        var locationsRef = FirebaseDatabase.getInstance().getReference(LOCATION_NODE);
        locationsRef.addListenerForSingleValueEvent(new LocationValueEventListener());
    }

    private class LocationValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            var locations = new ArrayList<Location>();
            for (var locationSnapshot : snapshot.getChildren()) {
                var nameLocation = locationSnapshot.getKey();
                var loadPictureLocation = Objects.requireNonNull(locationSnapshot.child("image").getValue()).toString();
                var history = Objects.requireNonNull(locationSnapshot.child("history").getValue()).toString();
                var mainInfo = Objects.requireNonNull(locationSnapshot.child("mainInfo").getValue()).toString();

                var location = new Location(nameLocation, loadPictureLocation, history, mainInfo);
                locations.add(location);
            }

            displayLocations(locations);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            handleDatabaseError();
        }
    }

    private void displayLocations(List<Location> locations) {
        var adapter = new LocationAdapter(locations);
        binding.locationRv.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.locationRv.setAdapter(adapter);
    }

    private void handleDatabaseError() {
        Toast.makeText(getContext(), "Не удалось загрузить локации.", Toast.LENGTH_SHORT).show();
    }
}

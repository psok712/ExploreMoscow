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

import Hse.CourseProject.ExploreMoscow.Location.Location;
import Hse.CourseProject.ExploreMoscow.Location.LocationAdapter;
import Hse.CourseProject.ExploreMoscow.databinding.FragmentLocationBinding;

public class LocationFragment extends Fragment
{
    private FragmentLocationBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLocationBinding.inflate(inflater, container, false);

        loadLocations();

        return binding.getRoot();
    }

    private void loadLocations() {
        List<Location> locations = new ArrayList<>();

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
                            locations.add(location);
                        }

                        LocationAdapter adapter = new LocationAdapter(locations);
                        binding.locationRv.setLayoutManager(new LinearLayoutManager(getContext()));
                        binding.locationRv.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to read locations.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

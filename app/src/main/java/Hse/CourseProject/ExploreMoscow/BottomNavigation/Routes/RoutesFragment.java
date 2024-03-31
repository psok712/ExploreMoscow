package Hse.CourseProject.ExploreMoscow.BottomNavigation.Routes;

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
import Hse.CourseProject.ExploreMoscow.Ribbons.RouteRibbon.RouteAdapter;
import Hse.CourseProject.ExploreMoscow.databinding.FragmentRoutesBinding;

public class RoutesFragment extends Fragment {
    private FragmentRoutesBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRoutesBinding.inflate(inflater, container, false);

        loadRoutes();

        return binding.getRoot();
    }

    private void loadRoutes() {
        List<Location> routes = new ArrayList<>();

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
                            routes.add(location);
                        }

                        RouteAdapter adapter = new RouteAdapter(routes);
                        binding.routesRv.setLayoutManager(new LinearLayoutManager(getContext()));
                        binding.routesRv.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to read Routes.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

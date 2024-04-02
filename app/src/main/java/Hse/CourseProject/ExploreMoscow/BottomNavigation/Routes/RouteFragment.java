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

import Hse.CourseProject.ExploreMoscow.Models.Location;
import Hse.CourseProject.ExploreMoscow.Adapters.RouteAdapter;
import Hse.CourseProject.ExploreMoscow.databinding.FragmentRouteBinding;

public class RouteFragment extends Fragment {

    private FragmentRouteBinding binding;
    private static final String ROUTES_NODE = "Routes";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentRouteBinding.inflate(inflater, container, false);
        loadRoutes();
        return binding.getRoot();
    }

    private void loadRoutes() {
        var routesRef = FirebaseDatabase.getInstance().getReference(ROUTES_NODE);
        routesRef.addListenerForSingleValueEvent(new RoutesValueEventListener());
    }

    private class RoutesValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            var routes = new ArrayList<Location>();
            for (var routeSnapshot : snapshot.getChildren()) {
                var nameRoute = routeSnapshot.getKey();
                var loadPictureRoute = Objects.requireNonNull(routeSnapshot.child("image").getValue()).toString();
                var history = Objects.requireNonNull(routeSnapshot.child("history").getValue()).toString();
                var mainInfo = Objects.requireNonNull(routeSnapshot.child("mainInfo").getValue()).toString();

                var location = new Location(nameRoute, loadPictureRoute, history, mainInfo);
                routes.add(location);
            }

            displayRoutes(routes);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            handleDatabaseError();
        }
    }

    private void displayRoutes(List<Location> routes) {
        var adapter = new RouteAdapter(routes);
        binding.routesRv.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.routesRv.setAdapter(adapter);
    }

    private void handleDatabaseError() {
        Toast.makeText(getContext(), "Не удалось загрузить маршруты.", Toast.LENGTH_SHORT).show();
    }
}

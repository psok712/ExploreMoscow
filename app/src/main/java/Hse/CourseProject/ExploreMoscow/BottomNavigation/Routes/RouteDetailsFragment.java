package Hse.CourseProject.ExploreMoscow.BottomNavigation.Routes;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import Hse.CourseProject.ExploreMoscow.R;
import Hse.CourseProject.ExploreMoscow.databinding.FragmentRouteDetailsBinding;

public class RouteDetailsFragment extends Fragment {

    private FragmentRouteDetailsBinding binding;
    private final boolean[] onClickButton = {false, false, false};
    private static final String ROUTES_NODE = "Routes";

    public static RouteDetailsFragment newInstance(String routeId) {
        var fragment = new RouteDetailsFragment();
        var args = new Bundle();
        args.putString("routeId", routeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRouteDetailsBinding.inflate(inflater, container, false);

        setupButtonsClickListeners();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            var routeName = getArguments().getString("routeId");
            loadRouteData(routeName);
        }
    }

    private void setupButtonsClickListeners() {
        binding.favoriteRouteBtn.setOnClickListener(v -> changeColorFavoriteRouteBtn());
        binding.checkRouteBtn.setOnClickListener(v -> changeColorCheckRouteBtn());
        binding.postponeRouteBtn.setOnClickListener(v -> changeColorPostponeRouteBtn());
        binding.backToRoutesBtn.setOnClickListener(v -> navigateToRoutes());
    }

    private void loadRouteData(String routeId) {
        FirebaseDatabase.getInstance().getReference(ROUTES_NODE)
                .child(routeId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            var name = Objects.requireNonNull(snapshot.getKey());
                            var imageUrl = snapshot.child("image").getValue(String.class);
                            var history = snapshot.child("history").getValue(String.class);
                            var mainInfo = snapshot.child("mainInfo").getValue(String.class);

                            displayDataInfo(name, history, mainInfo);
                            loadImage(imageUrl);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void displayDataInfo(String name, String history, String mainInfo) {
        binding.nameRouteDetailsTv.setText(name);
        binding.historyRouteTv.setText(history);
        binding.mainInfoRouteTv
                .setText(Html.fromHtml(Objects.requireNonNull(mainInfo), Html.FROM_HTML_MODE_COMPACT));
    }

    private void navigateToRoutes() {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new RouteFragment())
                .addToBackStack(null)
                .commit();
    }

    private void changeColorPostponeRouteBtn() {
        if (onClickButton[2]) {
            binding.postponeRouteBtn.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
            onClickButton[2] = false;
        } else {
            var colorBlue = Color.parseColor("#336699");
            binding.postponeRouteBtn.setColorFilter(colorBlue, PorterDuff.Mode.SRC_IN);
            onClickButton[2] = true;
        }
    }

    private void changeColorCheckRouteBtn() {
        if (onClickButton[1]) {
            binding.checkRouteBtn.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
            onClickButton[1] = false;
        } else {
            var colorGreen = Color.parseColor("#2A9518");
            binding.checkRouteBtn.setColorFilter(colorGreen, PorterDuff.Mode.SRC_IN);
            onClickButton[1] = true;
        }
    }

    private void changeColorFavoriteRouteBtn() {
        if (onClickButton[0]) {
            binding.favoriteRouteBtn.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
            onClickButton[0] = false;
        } else {
            binding.favoriteRouteBtn.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            onClickButton[0] = true;
        }
    }

    private void loadImage(String imageUrl) {
        Glide.with(requireContext())
                .load(imageUrl)
                .into(binding.routeDetailsIv);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

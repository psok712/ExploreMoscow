package Hse.CourseProject.ExploreMoscow.Ribbons.RouteRibbon;

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

import Hse.CourseProject.ExploreMoscow.BottomNavigation.Routes.RoutesFragment;
import Hse.CourseProject.ExploreMoscow.R;
import Hse.CourseProject.ExploreMoscow.databinding.FragmentRouteDetailsBinding;

public class RouteDetailsFragment extends Fragment {

    private FragmentRouteDetailsBinding binding;
    private final boolean[] onClickButton = {false, false, false};

    public static RouteDetailsFragment newInstance(String routeId) {
        RouteDetailsFragment fragment = new RouteDetailsFragment();
        Bundle args = new Bundle();
        args.putString("routeId", routeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRouteDetailsBinding.inflate(inflater, container, false);

        binding.favoriteRouteBtn.setOnClickListener(v -> changeColorFavoriteRouteBtn());
        binding.checkRouteBtn.setOnClickListener(v -> changeColorCheckRouteBtn());
        binding.postponeRouteBtn.setOnClickListener(v -> changeColorPostponeRouteBtn());
        binding.backToRoutesBtn.setOnClickListener(v -> navigateToRoutes());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            String routeName = getArguments().getString("routeId");
            loadRouteData(routeName);
        }
    }

    private void loadRouteData(String routeId) {
        FirebaseDatabase.getInstance().getReference("Routes")
                .child(routeId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String name = Objects.requireNonNull(snapshot.getKey());
                            String imageUrl = snapshot.child("image").getValue(String.class);
                            String history = snapshot.child("history").getValue(String.class);
                            String mainInfo = snapshot.child("mainInfo").getValue(String.class);

                            binding.nameRouteDetailsTv.setText(name);
                            binding.historyRouteTv.setText(history);
                            binding.mainInfoRouteTv.setText(Html.fromHtml(Objects.requireNonNull(mainInfo), Html.FROM_HTML_MODE_COMPACT));

                            loadImage(imageUrl);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void navigateToRoutes() {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new RoutesFragment())
                .addToBackStack(null)
                .commit();
    }

    private void changeColorPostponeRouteBtn() {
        if (onClickButton[2]) {
            binding.postponeRouteBtn.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
            onClickButton[2] = false;
        } else {
            binding.postponeRouteBtn.setColorFilter(Color
                    .parseColor("#336699"), PorterDuff.Mode.SRC_IN);
            onClickButton[2] = true;
        }
    }

    private void changeColorCheckRouteBtn() {
        if (onClickButton[1]) {
            binding.checkRouteBtn.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
            onClickButton[1] = false;
        } else {
            binding.checkRouteBtn
                    .setColorFilter(Color.parseColor("#2A9518"), PorterDuff.Mode.SRC_IN);
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

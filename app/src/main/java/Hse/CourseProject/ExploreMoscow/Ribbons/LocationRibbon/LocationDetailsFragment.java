package Hse.CourseProject.ExploreMoscow.Ribbons.LocationRibbon;

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

import Hse.CourseProject.ExploreMoscow.BottomNavigation.Location.LocationFragment;
import Hse.CourseProject.ExploreMoscow.R;
import Hse.CourseProject.ExploreMoscow.databinding.FragmentLocationDetailsBinding;

public class LocationDetailsFragment extends Fragment {

    private FragmentLocationDetailsBinding binding;
    private final boolean[] onClickButton = {false, false, false};

    public static LocationDetailsFragment newInstance(String locationId) {
        LocationDetailsFragment fragment = new LocationDetailsFragment();
        Bundle args = new Bundle();
        args.putString("locationId", locationId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLocationDetailsBinding.inflate(inflater, container, false);

        binding.favoriteLocationBtn.setOnClickListener(v -> changeColorFavoriteLocationBtn());
        binding.checkLocationBtn.setOnClickListener(v -> changeColorCheckLocationBtn());
        binding.postponeLocationBtn.setOnClickListener(v -> changeColorPostponeLocationBtn());
        binding.backToLocationsBtn.setOnClickListener(v -> navigateToLocations());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            String locationName = getArguments().getString("locationId");
            loadLocationData(locationName);
        }
    }

    private void loadLocationData(String locationId) {
        FirebaseDatabase.getInstance().getReference("Location")
                .child(locationId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String name = Objects.requireNonNull(snapshot.getKey());
                            String imageUrl = snapshot.child("image").getValue(String.class);
                            String history = snapshot.child("history").getValue(String.class);
                            String mainInfo = snapshot.child("mainInfo").getValue(String.class);

                            binding.nameLocationDetailsTv.setText(name);
                            binding.historyLocationTv.setText(history);
                            binding.mainInfoLocationTv.setText(Html.fromHtml(Objects.requireNonNull(mainInfo), Html.FROM_HTML_MODE_COMPACT));

                            loadImage(imageUrl);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void navigateToLocations() {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new LocationFragment())
                .addToBackStack(null)
                .commit();
    }

    private void changeColorPostponeLocationBtn() {
        if (onClickButton[2]) {
            binding.postponeLocationBtn.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
            onClickButton[2] = false;
        } else {
            binding.postponeLocationBtn.setColorFilter(Color
                    .parseColor("#336699"), PorterDuff.Mode.SRC_IN);
            onClickButton[2] = true;
        }
    }

    private void changeColorCheckLocationBtn() {
        if (onClickButton[1]) {
            binding.checkLocationBtn.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
            onClickButton[1] = false;
        } else {
            binding.checkLocationBtn
                    .setColorFilter(Color.parseColor("#2A9518"), PorterDuff.Mode.SRC_IN);
            onClickButton[1] = true;
        }
    }

    private void changeColorFavoriteLocationBtn() {
        if (onClickButton[0]) {
            binding.favoriteLocationBtn.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
            onClickButton[0] = false;
        } else {
            binding.favoriteLocationBtn.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            onClickButton[0] = true;
        }
    }

    private void loadImage(String imageUrl) {
        Glide.with(requireContext())
                .load(imageUrl)
                .into(binding.locationDetailsIv);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

package Hse.CourseProject.ExploreMoscow.Ribbons.PopularPlaces;

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

import Hse.CourseProject.ExploreMoscow.BottomNavigation.PopularPlaces.PopularPlacesFragment;
import Hse.CourseProject.ExploreMoscow.R;
import Hse.CourseProject.ExploreMoscow.databinding.FragmentPopularPlacesDetailsBinding;

public class PopularPlacesDetailsFragment extends Fragment {
    private FragmentPopularPlacesDetailsBinding binding;

    private final boolean[] onClickButton = {false, false, false};

    public static PopularPlacesDetailsFragment newInstance(String placeId) {
        PopularPlacesDetailsFragment fragment = new PopularPlacesDetailsFragment();
        Bundle args = new Bundle();
        args.putString("placeId", placeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPopularPlacesDetailsBinding.inflate(inflater, container, false);

        binding.favoritePlaceBtn.setOnClickListener(v -> changeColorFavoritePlaceBtn());
        binding.checkPlaceBtn.setOnClickListener(v -> changeColorCheckPlaceBtn());
        binding.postponePlaceBtn.setOnClickListener(v -> changeColorPostponePlaceBtn());
        binding.backToPlacesBtn.setOnClickListener(v -> navigateToPlaces());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            String placeName = getArguments().getString("placeId");
            loadPlaceData(placeName);
        }
    }

    private void loadPlaceData(String placeId) {
        FirebaseDatabase.getInstance().getReference("Location")
                .child(placeId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String name = Objects.requireNonNull(snapshot.getKey());
                            String imageUrl = snapshot.child("image").getValue(String.class);
                            String history = snapshot.child("history").getValue(String.class);
                            String mainInfo = snapshot.child("mainInfo").getValue(String.class);

                            binding.namePlaceDetailsTv.setText(name);
                            binding.historyPlaceTv.setText(history);
                            binding.mainInfoPlaceTv.setText(Html.fromHtml(Objects.requireNonNull(mainInfo), Html.FROM_HTML_MODE_COMPACT));

                            loadImage(imageUrl);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

        FirebaseDatabase.getInstance().getReference("Routes")
                .child(placeId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String name = Objects.requireNonNull(snapshot.getKey());
                            String imageUrl = snapshot.child("image").getValue(String.class);
                            String history = snapshot.child("history").getValue(String.class);
                            String mainInfo = snapshot.child("mainInfo").getValue(String.class);

                            binding.namePlaceDetailsTv.setText(name);
                            binding.historyPlaceTv.setText(history);
                            binding.mainInfoPlaceTv.setText(Html.fromHtml(Objects.requireNonNull(mainInfo), Html.FROM_HTML_MODE_COMPACT));

                            loadImage(imageUrl);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void navigateToPlaces() {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new PopularPlacesFragment())
                .addToBackStack(null)
                .commit();
    }

    private void changeColorPostponePlaceBtn() {
        if (onClickButton[2]) {
            binding.postponePlaceBtn.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
            onClickButton[2] = false;
        } else {
            binding.postponePlaceBtn.setColorFilter(Color
                    .parseColor("#336699"), PorterDuff.Mode.SRC_IN);
            onClickButton[2] = true;
        }
    }

    private void changeColorCheckPlaceBtn() {
        if (onClickButton[1]) {
            binding.checkPlaceBtn.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
            onClickButton[1] = false;
        } else {
            binding.checkPlaceBtn
                    .setColorFilter(Color.parseColor("#2A9518"), PorterDuff.Mode.SRC_IN);
            onClickButton[1] = true;
        }
    }

    private void changeColorFavoritePlaceBtn() {
        if (onClickButton[0]) {
            binding.favoritePlaceBtn.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
            onClickButton[0] = false;
        } else {
            binding.favoritePlaceBtn.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            onClickButton[0] = true;
        }
    }

    private void loadImage(String imageUrl) {
        Glide.with(requireContext())
                .load(imageUrl)
                .into(binding.placeDetailsIv);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}

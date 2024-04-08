package Hse.CourseProject.ExploreMoscow.BottomNavigation.PopularPlaces;

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
import Hse.CourseProject.ExploreMoscow.databinding.FragmentPopularPlacesDetailsBinding;

public class PopularPlacesDetailsFragment extends Fragment {
    private FragmentPopularPlacesDetailsBinding binding;
    private static final String LOCATION_NODE = "Location";
    private static final String ROUTES_NODE = "Routes";
    private final boolean[] onClickButton = {false, false, false};

    public static PopularPlacesDetailsFragment newInstance(String placeId) {
        var fragment = new PopularPlacesDetailsFragment();
        var args = new Bundle();
        args.putString("placeId", placeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentPopularPlacesDetailsBinding.inflate(inflater, container, false);
        setupClickListeners();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            String placeId = getArguments().getString("placeId");
            loadPlaceData(placeId);
        }
    }

    private void setupClickListeners() {
        binding.favoritePlaceBtn.setOnClickListener(v -> changeColorFavoritePlaceBtn());
        binding.checkPlaceBtn.setOnClickListener(v -> changeColorCheckPlaceBtn());
        binding.postponePlaceBtn.setOnClickListener(v -> changeColorPostponePlaceBtn());
        binding.backToPlacesBtn.setOnClickListener(v -> navigateToPlaces());
    }

    private void loadPlaceData(String placeId) {
        loadDataForNode(placeId, LOCATION_NODE);
        loadDataForNode(placeId, ROUTES_NODE);
    }

    private void loadDataForNode(String placeId, String node) {
        FirebaseDatabase.getInstance().getReference(node)
                .child(placeId)
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
        binding.namePlaceDetailsTv.setText(name);
        binding.historyPlaceTv.setText(
                Html.fromHtml(Objects.requireNonNull(history), Html.FROM_HTML_MODE_COMPACT));
        binding.mainInfoPlaceTv
                .setText(Html.fromHtml(Objects.requireNonNull(mainInfo), Html.FROM_HTML_MODE_COMPACT));
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
            var colorBlue = Color.parseColor("#336699");
            binding.postponePlaceBtn.setColorFilter(colorBlue, PorterDuff.Mode.SRC_IN);
            onClickButton[2] = true;
        }
    }

    private void changeColorCheckPlaceBtn() {
        if (onClickButton[1]) {
            binding.checkPlaceBtn.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
            onClickButton[1] = false;
        } else {
            var colorGreen = Color.parseColor("#2A9518");
            binding.checkPlaceBtn.setColorFilter(colorGreen, PorterDuff.Mode.SRC_IN);
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

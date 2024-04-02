package Hse.CourseProject.ExploreMoscow;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;

import Hse.CourseProject.ExploreMoscow.Authorization.LoginActivity;
import Hse.CourseProject.ExploreMoscow.BottomNavigation.Location.LocationFragment;
import Hse.CourseProject.ExploreMoscow.BottomNavigation.Map.MapFragment;
import Hse.CourseProject.ExploreMoscow.BottomNavigation.Profile.ProfileFragment;
import Hse.CourseProject.ExploreMoscow.BottomNavigation.PopularPlaces.PopularPlacesFragment;
import Hse.CourseProject.ExploreMoscow.BottomNavigation.Routes.RouteFragment;
import Hse.CourseProject.ExploreMoscow.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = Hse.CourseProject.ExploreMoscow.databinding.ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bottomNavigationView = findViewById(R.id.bottom_nav);

        checkUserAuthentication();
        setupInitialFragment();
        setupBottomNavigation();
        setupTouchListeners();
        showBottomNavigation();
    }

    private void checkUserAuthentication() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }

    private void setupInitialFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(binding.fragmentContainer.getId(), new PopularPlacesFragment())
                .commit();
        binding.bottomNav.setSelectedItemId(R.id.ribbon);
    }

    private void setupBottomNavigation() {
        var fragmentMap = new HashMap<Integer, Fragment>();
        fragmentMap.put(R.id.profile, new ProfileFragment());
        fragmentMap.put(R.id.routes, new RouteFragment());
        fragmentMap.put(R.id.ribbon, new PopularPlacesFragment());
        fragmentMap.put(R.id.location, new LocationFragment());
        fragmentMap.put(R.id.map, new MapFragment());

        binding.bottomNav.setOnItemSelectedListener(item -> {
            var fragment = fragmentMap.get(item.getItemId());
            assert fragment != null;

            getSupportFragmentManager().beginTransaction()
                    .replace(binding.fragmentContainer.getId(), fragment)
                    .commit();
            return true;
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupTouchListeners() {
        binding.getRoot().setOnTouchListener((v, event) -> {
            hideKeyboard();
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                var focusedView = getCurrentFocus();
                if (focusedView instanceof EditText) {
                    focusedView.clearFocus();
                }
            }
            return false;
        });
    }

    private void hideKeyboard() {
        var inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void hideBottomNavigation() {
        bottomNavigationView.setVisibility(View.GONE);
    }

    public void showBottomNavigation() {
        bottomNavigationView.setVisibility(View.VISIBLE);
    }
}

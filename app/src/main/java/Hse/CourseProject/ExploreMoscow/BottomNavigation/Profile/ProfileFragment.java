package Hse.CourseProject.ExploreMoscow.BottomNavigation.Profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import Hse.CourseProject.ExploreMoscow.MainActivity;
import Hse.CourseProject.ExploreMoscow.R;
import Hse.CourseProject.ExploreMoscow.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private static final String USERS_NODE = "Users";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        ((MainActivity) requireActivity()).showBottomNavigation();

        loadUserInfo();

        binding.changeToEditProfileBtn.setOnClickListener(v -> navigateToEditProfile());

        return binding.getRoot();
    }

    private void loadUserInfo() {
        var userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseDatabase.getInstance().getReference().child(USERS_NODE).child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            var username = Objects.requireNonNull(snapshot.child("username").getValue()).toString();
                            var email = Objects.requireNonNull(snapshot.child("email").getValue()).toString();
                            var profileImage = Objects.requireNonNull(snapshot.child("profileImage").getValue()).toString();

                            displayUser(username, email, profileImage);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        handleDatabaseError();
                    }
                });
    }

    private void displayUser(String username, String email, String profileImage) {
        binding.usernameTv.setText(username);
        binding.emailProfileTv.setText(email);

        if (!profileImage.isEmpty()) {
            Glide.with(requireContext()).load(profileImage).into(binding.profileIv);
        }
    }

    private void handleDatabaseError() {
        Toast.makeText(getContext(), "Не получилось загрузить данные пользователя.",
                Toast.LENGTH_SHORT).show();
    }

    private void navigateToEditProfile() {
        binding.changeToEditProfileBtn.setOnClickListener(v -> getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new ProfileEditFragment())
                .addToBackStack(null)
                .commit());
    }
}


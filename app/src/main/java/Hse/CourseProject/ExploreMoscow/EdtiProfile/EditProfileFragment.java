package Hse.CourseProject.ExploreMoscow.EdtiProfile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.IOException;
import java.util.Objects;

import Hse.CourseProject.ExploreMoscow.BottomNavigation.Profile.ProfileFragment;
import Hse.CourseProject.ExploreMoscow.Authorization.LoginActivity;
import Hse.CourseProject.ExploreMoscow.MainActivity;
import Hse.CourseProject.ExploreMoscow.R;
import Hse.CourseProject.ExploreMoscow.databinding.FragmentEditProfileBinding;

public class EditProfileFragment extends Fragment {

    private FragmentEditProfileBinding binding;
    private Uri imageFilePath;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false);

        ((MainActivity) requireActivity()).hideBottomNavigation();
        loadUserInfo();

        binding.exitToLoginBtn.setOnClickListener(v -> startActivity(new Intent(getContext(), LoginActivity.class)));
        binding.btnChangePhotoTv.setOnClickListener(v -> selectImage());
        binding.readyChangeToProfileActivityBtn.setOnClickListener(v -> {
            updateProfile();
            navigateToProfile();
        });

        binding.backToProfileActivityBtn.setOnClickListener(v -> navigateToProfile());
        binding.arrowEditEmailBtn.setOnClickListener(v -> navigateToEditEmailProfile());
        binding.emailProfileEt.setOnClickListener(v -> navigateToEditEmailProfile());
        return binding.getRoot();
    }

    private void navigateToProfile() {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new ProfileFragment())
                .addToBackStack(null)
                .commit();
    }

    private void updateProfile() {
        String username = binding.usernameProfileEt.getText().toString().trim();
        String profileImageUrl = imageFilePath != null
                ? imageFilePath.toString()
                : "";
        UserProfileChangeRequest profileUpdates;

        if (!profileImageUrl.isEmpty()) {
            profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .setPhotoUri(imageFilePath)
                    .build();
        } else {
            profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build();
        }

        Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                    }
                });


        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (!profileImageUrl.isEmpty()) {
            FirebaseDatabase.getInstance().getReference().child("Users").child(uid)
                    .child("username").setValue(username);
            FirebaseDatabase.getInstance().getReference().child("Users").child(uid)
                    .child("profileImage").setValue(profileImageUrl);
        } else {
            FirebaseDatabase.getInstance().getReference().child("Users").child(uid)
                    .child("username").setValue(username);
        }
    }

    private void loadUserInfo() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String username = Objects.requireNonNull(snapshot.child("username").getValue()).toString();
                        String profileImage = Objects.requireNonNull(snapshot.child("profileImage").getValue()).toString();

                        binding.usernameProfileEt.setText(username);

                        if (!profileImage.isEmpty()) {
                            Glide.with(EditProfileFragment.this).load(profileImage).into(binding.profileIv);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    ActivityResultLauncher<Intent> pickImageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                        imageFilePath = result.getData().getData();

                        try {
                            Bitmap bitmap = MediaStore.Images.Media
                                    .getBitmap(
                                            requireContext().getContentResolver(),
                                            imageFilePath
                                    );
                            binding.profileIv.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        uploadImage();
                    }
                }
            }
    );

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        pickImageActivityResultLauncher.launch(intent);
    }

    private void uploadImage() {
        if (imageFilePath != null) {
            String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

            FirebaseStorage.getInstance().getReference().child("images/" + uid)
                    .putFile(imageFilePath).addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(getContext(), "Photo upload complete", Toast.LENGTH_SHORT).show();

                        FirebaseStorage.getInstance().getReference().child("images/" + uid).getDownloadUrl()
                                .addOnSuccessListener(uri -> FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child("profileImage").setValue(uri.toString()));
                    });
        }
    }

    private void navigateToEditEmailProfile() {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new EditEmailProfileFragment())
                .addToBackStack(null)
                .commit();
    }
}

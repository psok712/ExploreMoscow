package Hse.CourseProject.ExploreMoscow.BottomNavigation.Profile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Objects;

import Hse.CourseProject.ExploreMoscow.Authorization.LoginActivity;
import Hse.CourseProject.ExploreMoscow.MainActivity;
import Hse.CourseProject.ExploreMoscow.R;
import Hse.CourseProject.ExploreMoscow.databinding.FragmentProfileEditBinding;

public class ProfileEditFragment extends Fragment {

    private FragmentProfileEditBinding binding;
    private Uri imageFilePath;
    private ActivityResultLauncher<Intent> pickImageActivityResultLauncher;

    private static final String USERS_NODE = "Users";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentProfileEditBinding.inflate(inflater, container, false);

        ((MainActivity) requireActivity()).hideBottomNavigation();
        loadUserInfo();
        setupButtons();
        setupActivityResultLauncher();

        return binding.getRoot();
    }

    private void setupButtons() {
        binding.exitToLoginBtn.setOnClickListener(v -> startActivity(new Intent(getContext(), LoginActivity.class)));
        binding.btnChangePhotoTv.setOnClickListener(v -> selectImage());
        binding.readyChangeToProfileActivityBtn.setOnClickListener(v -> {
            updateProfile();
            navigateToProfile();
        });

        binding.backToProfileActivityBtn.setOnClickListener(v -> navigateToProfile());
        binding.arrowEditEmailBtn.setOnClickListener(v -> navigateToEditEmailProfile());
        binding.emailProfileEt.setOnClickListener(v -> navigateToEditEmailProfile());
    }

    private void setupActivityResultLauncher() {
        pickImageActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK
                            && result.getData() != null
                            && result.getData().getData() != null
                    ) {
                        imageFilePath = result.getData().getData();
                        displaySelectedImage();
                        uploadImage();
                    }
                }
        );
    }

    private void loadUserInfo() {
        FirebaseDatabase.getInstance()
                .getReference()
                .child(USERS_NODE)
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        var username = Objects.requireNonNull(
                                snapshot.child("username").getValue()).toString();
                        var profileImage = Objects.requireNonNull(
                                snapshot.child("profileImage").getValue()).toString();

                        binding.usernameProfileEt.setText(username);

                        if (!profileImage.isEmpty()) {
                            displayProfileImage(profileImage);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        handleDatabaseError();
                    }
                });
    }

    private void handleDatabaseError() {
        Toast.makeText(getContext(), "Не получилось загрузить данные пользователя.",
                Toast.LENGTH_SHORT).show();
    }

    private void updateProfile() {
        var username = binding.usernameProfileEt.getText().toString().trim();
        var profileImageUrl = imageFilePath != null ? imageFilePath.toString() : "";

        var profileUpdates = buildProfileUpdates(username, profileImageUrl);
        updateUserProfile(profileUpdates);

        var uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        updateUsernameInDatabase(uid, username);

        if (!profileImageUrl.isEmpty()) {
            updateProfileImageInDatabase(uid, profileImageUrl);
        }
    }

    private UserProfileChangeRequest buildProfileUpdates(String username, String profileImageUrl) {
        var builder = new UserProfileChangeRequest.Builder().setDisplayName(username);

        if (!profileImageUrl.isEmpty()) {
            builder.setPhotoUri(imageFilePath);
        }

        return builder.build();
    }

    private void updateUserProfile(UserProfileChangeRequest profileUpdates) {
        Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser())
                .updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(requireContext(),
                                "Профиль успешно обновлен!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(),
                                "Не удалось обновить профиль:(", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUsernameInDatabase(String uid, String username) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child(USERS_NODE)
                .child(uid)
                .child("username")
                .setValue(username);
    }

    private void updateProfileImageInDatabase(String uid, String profileImageUrl) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child(USERS_NODE)
                .child(uid)
                .child("profileImage")
                .setValue(profileImageUrl);
    }


    private void selectImage() {
        var intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        pickImageActivityResultLauncher.launch(intent);
    }

    private void displaySelectedImage() {
        try {
            var bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(),
                    imageFilePath);
            binding.profileIv.setImageBitmap(bitmap);
        } catch (IOException e) {
            Toast.makeText(requireContext(),
                    "Не удалось загрузить изображение:(", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage() {
        if (imageFilePath != null) {
            var uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            FirebaseStorage.getInstance()
                    .getReference()
                    .child("images/" + uid)
                    .putFile(imageFilePath)
                    .addOnSuccessListener(this::onImageUploadSuccess);
        }
    }

    private void onImageUploadSuccess(UploadTask.TaskSnapshot taskSnapshot) {
        var uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        Toast.makeText(getContext(), "Загрузка фото завершена!", Toast.LENGTH_SHORT).show();

        FirebaseStorage.getInstance()
                .getReference()
                .child("images/" + uid)
                .getDownloadUrl()
                .addOnSuccessListener(uri -> updateProfileImageUri(uri.toString()));
    }

    private void updateProfileImageUri(String imageUrl) {
        var uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseDatabase.getInstance()
                .getReference()
                .child(USERS_NODE)
                .child(uid)
                .child("profileImage")
                .setValue(imageUrl);
    }


    private void displayProfileImage(String profileImage) {
        Glide.with(ProfileEditFragment.this).load(profileImage).into(binding.profileIv);
    }

    private void navigateToProfile() {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new ProfileFragment())
                .addToBackStack(null)
                .commit();
    }

    private void navigateToEditEmailProfile() {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new ProfileEditEmailFragment())
                .addToBackStack(null)
                .commit();
    }
}

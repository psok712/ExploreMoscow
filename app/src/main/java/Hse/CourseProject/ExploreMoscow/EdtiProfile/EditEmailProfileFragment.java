package Hse.CourseProject.ExploreMoscow.EdtiProfile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthActionCodeException;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWebException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import Hse.CourseProject.ExploreMoscow.BottomNavigation.Profile.ProfileFragment;
import Hse.CourseProject.ExploreMoscow.MainActivity;
import Hse.CourseProject.ExploreMoscow.R;
import Hse.CourseProject.ExploreMoscow.databinding.FragmentEditEmailProfileBinding;

public class EditEmailProfileFragment extends Fragment {
    private FragmentEditEmailProfileBinding binding;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEditEmailProfileBinding.inflate(inflater, container, false);

        ((MainActivity) requireActivity()).hideBottomNavigation();
        loadUserInfo();

        binding.readyChangeEmailToProfileActivityBtn.setOnClickListener(v -> changeEmail());
        binding.backToEditProfileActivityBtn.setOnClickListener(v -> navigateToEditProfile());
        return binding.getRoot();
    }

    private void loadUserInfo() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String username = Objects.requireNonNull(snapshot.child("email").getValue()).toString();
                        binding.emailEditProfileEt.setText(username);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void changeEmail() {
        String newEmail = binding.emailEditProfileEt.getText().toString().trim();

        if (TextUtils.isEmpty(newEmail)) {
            Toast.makeText(requireContext(), "Email cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidEmail(newEmail)) {
            Toast.makeText(requireContext(), "Invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }

        Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).verifyBeforeUpdateEmail(newEmail)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(requireContext(), "Email updated successfully", Toast.LENGTH_SHORT).show();
                        navigateToProfile();
                    } else {
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (FirebaseAuthActionCodeException e) {
                            Toast.makeText(requireContext(), "Invalid action code", Toast.LENGTH_SHORT).show();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            Toast.makeText(requireContext(), "Invalid credentials", Toast.LENGTH_SHORT).show();
                        } catch (FirebaseAuthUserCollisionException e) {
                            Toast.makeText(requireContext(), "Email already exists", Toast.LENGTH_SHORT).show();
                        } catch (FirebaseAuthWebException e) {
                            Toast.makeText(requireContext(), "Web network exception", Toast.LENGTH_SHORT).show();
                        } catch (FirebaseAuthEmailException e) {
                            Toast.makeText(requireContext(), "Invalid email address", Toast.LENGTH_SHORT).show();
                        } catch (FirebaseAuthRecentLoginRequiredException e) {
                            Toast.makeText(requireContext(), "User needs to log in again", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(requireContext(), "Failed to update email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void navigateToProfile() {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new ProfileFragment())
                .commit();
    }

    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private void navigateToEditProfile() {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new EditProfileFragment())
                .commit();
    }
}
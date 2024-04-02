package Hse.CourseProject.ExploreMoscow.BottomNavigation.Profile;

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
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWebException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import Hse.CourseProject.ExploreMoscow.MainActivity;
import Hse.CourseProject.ExploreMoscow.R;
import Hse.CourseProject.ExploreMoscow.databinding.FragmentProfileEditEmailBinding;

public class ProfileEditEmailFragment extends Fragment {
    private FragmentProfileEditEmailBinding binding;

    private static final String USERS_NODE = "Users";

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentProfileEditEmailBinding.inflate(inflater, container, false);

        ((MainActivity) requireActivity()).hideBottomNavigation();
        loadUserInfo();

        setupButtons();
        return binding.getRoot();
    }

    private void setupButtons() {
        binding.readyChangeEmailToProfileActivityBtn.setOnClickListener(v -> changeEmail());
        binding.backToEditProfileActivityBtn.setOnClickListener(v -> navigateToEditProfile());
    }

    private void loadUserInfo() {
        FirebaseDatabase.getInstance()
                .getReference()
                .child(USERS_NODE)
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
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
        var newEmail = binding.emailEditProfileEt.getText().toString().trim();
        var currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (TextUtils.isEmpty(newEmail)) {
            Toast.makeText(requireContext(), "Поле Email не может быть пустым", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidEmail(newEmail)) {
            Toast.makeText(requireContext(), "Неверный формат email", Toast.LENGTH_SHORT).show();
            return;
        }

        Objects.requireNonNull(currentUser)
                .verifyBeforeUpdateEmail(newEmail)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(requireContext(), "Email успешно обновлен",
                                Toast.LENGTH_SHORT).show();
                        navigateToProfile();
                    } else {
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (FirebaseAuthUserCollisionException e) {
                            Toast.makeText(requireContext(), "Email уже существует",
                                    Toast.LENGTH_SHORT).show();
                        } catch (FirebaseAuthWebException e) {
                            Toast.makeText(requireContext(), "Ошибка сети",
                                    Toast.LENGTH_SHORT).show();
                        } catch (FirebaseAuthEmailException e) {
                            Toast.makeText(requireContext(), "Неверный адрес электронной почты",
                                    Toast.LENGTH_SHORT).show();
                        } catch (FirebaseAuthRecentLoginRequiredException e) {
                            Toast.makeText(requireContext(), "Пользователь должен войти повторно",
                                    Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(requireContext(), "Не удалось обновить email: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
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
                .replace(R.id.fragment_container, new ProfileEditFragment())
                .commit();
    }
}

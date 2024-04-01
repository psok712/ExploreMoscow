package Hse.CourseProject.ExploreMoscow.Authorization;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

import Hse.CourseProject.ExploreMoscow.MainActivity;
import Hse.CourseProject.ExploreMoscow.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupRegistrationButton();
        setupBackToLoginButton();
        setupHideKeyboardOnTouch();
        setupHideKeyboardOnEditorAction();
    }

    private void setupRegistrationButton() {
        binding.registrationBtn.setOnClickListener(v -> {
            if (isInputValid()) {
                registerUser();
            } else {
                showInputError();
            }
        });
    }

    private boolean isInputValid() {
        return !binding.emailEt.getText().toString().isEmpty()
                && !binding.passwordEt.getText().toString().isEmpty()
                && !binding.usernameEt.getText().toString().isEmpty();
    }

    private void registerUser() {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                        binding.emailEt.getText().toString(),
                        binding.passwordEt.getText().toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        saveUserInfo();
                        startMainActivity();
                    } else {
                        showRegistrationError();
                    }
                });
    }

    private void saveUserInfo() {
        var userInfo = new HashMap<>();
        userInfo.put("email", binding.emailEt.getText().toString());
        userInfo.put("username", binding.usernameEt.getText().toString());
        userInfo.put("profileImage", "");

        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .setValue(userInfo);
    }

    private void showRegistrationError() {
        Toast.makeText(getApplicationContext(),
                "Ошибка регистрации: вы ввели недопустимые значения.",
                Toast.LENGTH_SHORT).show();
    }

    private void showInputError() {
        Toast.makeText(getApplicationContext(),
                "Ошибка регистрации: поля не могут оставаться пустыми.",
                Toast.LENGTH_SHORT).show();
    }

    private void setupBackToLoginButton() {
        binding.backToLoginActivityBtn.setOnClickListener(v ->
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupHideKeyboardOnTouch() {
        binding.getRoot().setOnTouchListener((v, event) -> {
            hideKeyboard();
            return false;
        });
    }

    private void setupHideKeyboardOnEditorAction() {
        binding.emailEt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == KeyEvent.KEYCODE_ENTER) {
                hideKeyboard();
                return true;
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

    private void startMainActivity() {
        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
    }
}

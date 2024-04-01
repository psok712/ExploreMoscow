package Hse.CourseProject.ExploreMoscow.Authorization;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import Hse.CourseProject.ExploreMoscow.MainActivity;
import Hse.CourseProject.ExploreMoscow.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupLoginButton();
        setupCreateAccountTextView();
        setupHideKeyboardOnTouch();
        setupHideKeyboardOnEditorAction();
    }

    private void setupLoginButton() {
        binding.loginBtn.setOnClickListener(v -> {
            if (isInputValid()) {
                performLogin();
            } else {
                showInputError();
            }
        });
    }

    private boolean isInputValid() {
        return !binding.emailEt.getText().toString().isEmpty()
                && !binding.passwordEt.getText().toString().isEmpty();
    }

    private void performLogin() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(
                        binding.emailEt.getText().toString(),
                        binding.passwordEt.getText().toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        startMainActivity();
                    } else {
                        showLoginFailedError();
                    }
                });
    }

    private void showLoginFailedError() {
        Toast.makeText(getApplicationContext(),
                "Вы ввели неверные данные!", Toast.LENGTH_SHORT).show();
    }

    private void showInputError() {
        Toast.makeText(getApplicationContext(),
                "Пожалуйста, заполните все поля!", Toast.LENGTH_SHORT).show();
    }

    private void setupCreateAccountTextView() {
        binding.createAccountActivityTv.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
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
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
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
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }
}

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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.registrationBtn.setOnClickListener(v -> {
            if (binding.emailEt.getText().toString().isEmpty()
                    || binding.passwordEt.getText().toString().isEmpty()
                    || binding.usernameEt.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(),
                        "Поля не могут оставаться пустыми",
                        Toast.LENGTH_SHORT).show();
            } else {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                                binding.emailEt.getText().toString(),
                                binding.passwordEt.getText().toString())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                HashMap<String, String> userInfo = new HashMap<>();
                                userInfo.put("email", binding.emailEt.getText().toString());
                                userInfo.put("username", binding.usernameEt.getText().toString());
                                userInfo.put("profileImage", "");

                                FirebaseDatabase.getInstance().getReference().child("Users")
                                        .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                        .setValue(userInfo);

                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Ошибка регистрации: вы ввели недопустимые значения.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        binding.backToLoginActivityBtn.setOnClickListener(v -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));


        binding.getRoot().setOnTouchListener((v, event) -> {
            hideKeyboard();
            return false;
        });

        binding.emailEt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == KeyEvent.KEYCODE_ENTER) {
                hideKeyboard();
                return true;
            }
            return false;
        });
    }

    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
package Hse.CourseProject.ExploreMoscow.BottomNavigation.Ribbon;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import Hse.CourseProject.ExploreMoscow.databinding.FragmentRibbonBinding;

public class RibbonFragment extends Fragment {
    private FragmentRibbonBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRibbonBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }
}

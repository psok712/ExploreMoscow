package Hse.CourseProject.ExploreMoscow.Location;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import Hse.CourseProject.ExploreMoscow.R;

@SuppressLint("ViewConstructor")
public class LocationViewHolder extends RecyclerView.ViewHolder {

    ImageView locationIv;
    TextView locationNameTv;

    public LocationViewHolder(@NotNull View itemView) {
        super(itemView);

        locationIv = itemView.findViewById(R.id.locationIv);
        locationNameTv = itemView.findViewById(R.id.nameLocationTv);
    }
}

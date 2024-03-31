package Hse.CourseProject.ExploreMoscow.Ribbons.PopularPlaces;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import Hse.CourseProject.ExploreMoscow.R;

public class PopularPlacesViewHolder extends RecyclerView.ViewHolder {

    ImageView placeIv;
    TextView placeNameTv;

    public PopularPlacesViewHolder(@NotNull View itemView) {
        super(itemView);

        placeIv = itemView.findViewById(R.id.placesIv);
        placeNameTv = itemView.findViewById(R.id.namePlacesTv);
    }
}

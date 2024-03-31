package Hse.CourseProject.ExploreMoscow.Route;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import Hse.CourseProject.ExploreMoscow.R;

@SuppressLint("ViewConstructor")
public class RouteViewHolder extends RecyclerView.ViewHolder {

    ImageView routeIv;
    TextView routeNameTv;

    public RouteViewHolder(@NotNull View itemView) {
        super(itemView);

        routeIv = itemView.findViewById(R.id.routeIv);
        routeNameTv = itemView.findViewById(R.id.nameRouteTv);
    }
}

package com.syc.go4lunch.ui.slideshow;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import com.syc.go4lunch.R;
import com.syc.go4lunch.model.RestaurantModel;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RestaurantRecyclerViewAdapter extends RecyclerView.Adapter<RestaurantRecyclerViewAdapter.RestaurantViewHolder> {
    //list of restaurant
    private List<RestaurantModel> restaurantModels;
    //
    private Context context;
    //Glide object
    private RequestManager glide;
    /**
     * Glide image traitement
     */
    private RequestOptions options = new RequestOptions()
            .override(75,75)
            .placeholder(R.drawable.baseline_error_outline_black_48)
            .error(R.drawable.baseline_error_outline_black_48);

    public RestaurantRecyclerViewAdapter(List<RestaurantModel> restaurantModels, RequestManager glide, Context context ) {
        this.restaurantModels = restaurantModels;
        this.glide = glide;
        this.context = context;
    }

    public static class RestaurantViewHolder extends RecyclerView.ViewHolder{
        //@BindView(R.id.slideshow_rv_item_adress) ImageView itemImg;
        @BindView(R.id.slideshow_rv_item_adress) TextView itemAdress;
        @BindView(R.id.slideshow_rv_item_phone) TextView itemPhone;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    // Create item views (invoked by the layout manager)
    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.slideshow_rv_item, parent, false);

        return new RestaurantViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantRecyclerViewAdapter.RestaurantViewHolder holder, int position) {



    }

    @Override
    public int getItemCount() {
        return this.restaurantModels.size();
    }
}







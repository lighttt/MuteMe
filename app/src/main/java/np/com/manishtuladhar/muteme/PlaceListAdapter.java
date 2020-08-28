package np.com.manishtuladhar.muteme;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.places.PlaceBuffer;

public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.PlaceViewHolder> {

    private Context mContext;
    private PlaceBuffer places;

    public PlaceListAdapter(Context context, PlaceBuffer places)
    {
        this.mContext = context;
        this.places = places;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_place_view,parent,false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        String placeName = places.get(position).getName().toString();
        String placeAddress = places.get(position).getAddress().toString();
        holder.nameTV.setText(placeName);
        holder.addressTV.setText(placeAddress);
    }

    @Override
    public int getItemCount() {
        if(places ==null) return 0;
        return places.getCount();
    }

    public  void swapPlaces(PlaceBuffer newPlaces)
    {
        places = newPlaces;
        if(places !=null)
        {
            this.notifyDataSetChanged();
        }
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder {
        TextView nameTV;
        TextView addressTV;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTV = itemView.findViewById(R.id.name_text_view);
            addressTV = itemView.findViewById(R.id.address_text_view);

        }
    }
}

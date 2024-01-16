package org.ziegelbauer.travelbuddy.UI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.ziegelbauer.travelbuddy.R;
import org.ziegelbauer.travelbuddy.entities.Excursion;

import java.util.List;

public class ExcursionAdaptor extends RecyclerView.Adapter<ExcursionAdaptor.ExcursionViewHolder> {
    private final Context context;
    private List<Excursion> excursions;
    private final LayoutInflater inflater;

    public ExcursionAdaptor(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
    }
    @NonNull
    @Override
    public ExcursionAdaptor.ExcursionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.excursion_list_item, parent, false);
        return new ExcursionAdaptor.ExcursionViewHolder(itemView);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull ExcursionAdaptor.ExcursionViewHolder holder, int position) {
        if(excursions != null) {
            Excursion currentExcursion = excursions.get(position);
            String name = currentExcursion.getExcursionName();
            String date = currentExcursion.getExcursionDate();
            holder.excursionName.setText(name);
            holder.excursionDate.setText(date);
        } else {
            holder.excursionName.setText("No Excursion Name");
            holder.excursionDate.setText("No Vacation ID");
        }
    }

    @Override
    public int getItemCount() {
        if (excursions == null) {
            return 0;
        }
        return excursions.size();
    }

    public void setExcursions(List<Excursion> excursions) {
        this.excursions = excursions;
        notifyDataSetChanged();
    }

    public class ExcursionViewHolder extends RecyclerView.ViewHolder {
        private final TextView excursionName;
        private final TextView excursionDate;
        public ExcursionViewHolder(@NonNull View itemView) {
            super(itemView);

            excursionName = itemView.findViewById(R.id.excursionName);
            excursionDate = itemView.findViewById(R.id.excursionDate);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    final Excursion currentExcursion = excursions.get(position);
                    Intent intent = new Intent(context, ExcursionDetails.class);
                    intent.putExtra("id", currentExcursion.getExcursionID());
                    intent.putExtra("name", currentExcursion.getExcursionName());
                    intent.putExtra("excursionDate", currentExcursion.getExcursionDate());
                    intent.putExtra("vacationID", currentExcursion.getVacationID());
                    context.startActivity(intent);
                }
            });
        }
    }
}

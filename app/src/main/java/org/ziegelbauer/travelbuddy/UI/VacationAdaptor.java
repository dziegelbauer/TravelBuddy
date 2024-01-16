package org.ziegelbauer.travelbuddy.UI;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.ziegelbauer.travelbuddy.R;
import org.ziegelbauer.travelbuddy.entities.Vacation;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class VacationAdaptor extends RecyclerView.Adapter<VacationAdaptor.VacationViewHolder> {
    private final Context context;
    private List<Vacation> vacations;
    private final LayoutInflater inflater;
    private final String dateFormat = "MM/dd/yy";
    private final SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);

    public VacationAdaptor(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public VacationAdaptor.VacationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.vacation_list_item, parent, false);
        return new VacationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VacationAdaptor.VacationViewHolder holder, int position) {
        if(vacations != null) {
            Vacation currentVacation = vacations.get(position);
            String name = currentVacation.getVacationName();
            String dates = currentVacation.getStartDate() + " - " + currentVacation.getEndDate();
            holder.vacationName.setText(name);
            holder.vacationDates.setText(dates);
        } else {
            holder.vacationName.setText("No Vacation Name");
            holder.vacationDates.setText("No vacation dates");
        }
    }

    @Override
    public int getItemCount() {
        if (vacations == null) {
            return 0;
        }
        return vacations.size();
    }

    public void setVacations(List<Vacation> vacations) {
        this.vacations = vacations;
        notifyDataSetChanged();
    }

    public class VacationViewHolder extends RecyclerView.ViewHolder {
        private final TextView vacationName;
        private final TextView vacationDates;

        public VacationViewHolder(@NonNull View itemView) {
            super(itemView);

            vacationName = itemView.findViewById(R.id.vacationName);
            vacationDates = itemView.findViewById(R.id.vacationDates);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    final Vacation currentVacation = vacations.get(position);
                    Intent intent = new Intent(context, VacationDetails.class);
                    intent.putExtra("id", currentVacation.getVacationID());
                    intent.putExtra("name", currentVacation.getVacationName());
                    intent.putExtra("lodging", currentVacation.getVacationLodging());
                    intent.putExtra("startDate", currentVacation.getStartDate());
                    intent.putExtra("endDate", currentVacation.getEndDate());
                    context.startActivity(intent);
                }
            });
        }
    }
}

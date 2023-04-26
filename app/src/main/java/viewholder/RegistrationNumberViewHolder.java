package viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.elijah.ukeme.projectinformation.R;

public class RegistrationNumberViewHolder extends RecyclerView.ViewHolder {
    public TextView regNumberTV;
    public RegistrationNumberViewHolder(@NonNull View itemView) {
        super(itemView);
        regNumberTV = itemView.findViewById(R.id.regNumber_card_list);
    }
}

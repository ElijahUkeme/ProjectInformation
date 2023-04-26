package viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.elijah.ukeme.projectinformation.R;

public class SupervisorViewHolder extends RecyclerView.ViewHolder {
    public TextView supervisorTV;
    public SupervisorViewHolder(@NonNull View itemView) {
        super(itemView);
        supervisorTV = itemView.findViewById(R.id.supervisor_card_list_name);
    }
}

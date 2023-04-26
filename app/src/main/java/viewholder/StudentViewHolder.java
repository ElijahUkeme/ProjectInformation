package viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.elijah.ukeme.projectinformation.R;

import de.hdodenhof.circleimageview.CircleImageView;
import interfaces.ItemClickListener;

public class StudentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView regNumber;
    public CircleImageView profileImage;
    public ItemClickListener listener;
    public StudentViewHolder(@NonNull View itemView) {
        super(itemView);
        regNumber = itemView.findViewById(R.id.regNumber_student_card_list);
        profileImage = itemView.findViewById(R.id.image_profile_recyclerview_list);
    }

    @Override
    public void onClick(View view) {
        listener.onClick(view,getAdapterPosition(),false);
    }
    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }
}

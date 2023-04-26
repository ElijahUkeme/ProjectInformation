package activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.elijah.ukeme.projectinformation.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import model.Supervisor;
import viewholder.SupervisorViewHolder;

public class ViewAllSupervisorActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    DatabaseReference supervisorRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_supervisor);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        recyclerView = findViewById(R.id.recyclerview_supervisor_list);
        supervisorRef = FirebaseDatabase.getInstance().getReference().child("Supervisors");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Supervisor> options = new FirebaseRecyclerOptions.Builder<Supervisor>()
                .setQuery(supervisorRef,Supervisor.class)
                .build();

        FirebaseRecyclerAdapter<Supervisor, SupervisorViewHolder> adapter = new FirebaseRecyclerAdapter<Supervisor, SupervisorViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull SupervisorViewHolder holder, int position, @NonNull Supervisor model) {
                holder.supervisorTV.setText(model.getName());

                holder.itemView.setOnClickListener(view -> {
                    Intent intent = new Intent(ViewAllSupervisorActivity.this,SupervisorActivity.class);
                    intent.putExtra("name",model.getName());
                    intent.putExtra("capacity",String.valueOf(model.getCapacity()));
                    intent.putExtra("numberAssigned",String.valueOf(model.getNumberAssigned()));
                    intent.putExtra("status",model.getStatus());
                    intent.putExtra("sid",model.getSid());
                    startActivity(intent);
                });
            }

            @NonNull
            @Override
            public SupervisorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.supervisor_item_list,parent,false);
                return new SupervisorViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}
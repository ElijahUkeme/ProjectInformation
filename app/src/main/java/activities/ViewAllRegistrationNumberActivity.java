package activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import model.RegistrationNumber;
import viewholder.RegistrationNumberViewHolder;

public class ViewAllRegistrationNumberActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference regNumberRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_registration_number);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        recyclerView = findViewById(R.id.recyclerview_regNumber_list);
        regNumberRef = FirebaseDatabase.getInstance().getReference().child("Registration Numbers");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<RegistrationNumber> options = new FirebaseRecyclerOptions.Builder<RegistrationNumber>()
                .setQuery(regNumberRef,RegistrationNumber.class)
                .build();

        FirebaseRecyclerAdapter<RegistrationNumber, RegistrationNumberViewHolder> adapter = new FirebaseRecyclerAdapter<RegistrationNumber, RegistrationNumberViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RegistrationNumberViewHolder holder, int position, @NonNull RegistrationNumber model) {
                holder.regNumberTV.setText(model.getRegistrationNumber());
            }

            @NonNull
            @Override
            public RegistrationNumberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.registration_number_item_list,parent,false);

                return new RegistrationNumberViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}
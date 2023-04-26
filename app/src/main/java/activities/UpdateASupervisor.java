package activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.elijah.ukeme.projectinformation.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class UpdateASupervisor extends AppCompatActivity {

    private EditText name,capacity,numberAssigned,status;
    private Button updateBtn;
    private DatabaseReference supervisorRef;
    String supervisorKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_a_supervisor);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        name = findViewById(R.id.supervisor_name_update);
        capacity = findViewById(R.id.supervisor_capacity_update);
        numberAssigned = findViewById(R.id.supervisor_numberAssigned_update);
        status = findViewById(R.id.supervisor_status_update);
        updateBtn = findViewById(R.id.supervisor_update_button);
        supervisorRef = FirebaseDatabase.getInstance().getReference().child("Supervisors");
        supervisorKey = getIntent().getStringExtra("supervisorKey");

        retrieveSupervisorInfo();

        updateBtn.setOnClickListener(view -> {
            updateSupervisorInfo();
        });
    }

    private void updateSupervisorInfo(){
        HashMap<String,Object> supervisorMap = new HashMap<>();
        supervisorMap.put("name",name.getText().toString());
        supervisorMap.put("capacity",capacity.getText().toString());
        supervisorMap.put("numberAssigned",numberAssigned);
        supervisorMap.put("status",status);
        supervisorRef.child(supervisorKey).updateChildren(supervisorMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(UpdateASupervisor.this, "Supervisor info updated Successfully", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(UpdateASupervisor.this, "Was not able to update the supervisor info", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void retrieveSupervisorInfo(){
        supervisorRef.child(supervisorKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String supervisorStatus = snapshot.child("status").toString();
                            String supervisorName = snapshot.child("name").toString();
                            int carryingCapacity = Integer.parseInt(snapshot.child("capacity").toString());
                            int countOfStudentAssgined = Integer.parseInt(snapshot.child("numberAssigned").toString());

                            name.setText(supervisorName);
                            status.setText(supervisorStatus);
                            capacity.setText(String.valueOf(carryingCapacity));
                            numberAssigned.setText(countOfStudentAssgined);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}
package activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.elijah.ukeme.projectinformation.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SystemReportActivity extends AppCompatActivity {
    private TextView totalRegNumber,totalStudentRegistered,totalSupervisor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_report);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        totalRegNumber = findViewById(R.id.regNumber_report);
        totalStudentRegistered = findViewById(R.id.report_total_registered_student);
        totalSupervisor = findViewById(R.id.report_total_supervisor);

        getTotalRegisteredRegNumber();
        getTotalRegisteredStudents();
        getTotalRegisteredSupervisor();
    }

    private void getTotalRegisteredSupervisor(){
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Supervisors");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    List<String> supervisorList = new ArrayList<>();
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        supervisorList.add(dataSnapshot.getKey());
                    }
                    totalSupervisor.setText(String.valueOf(supervisorList.size()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SystemReportActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getTotalRegisteredRegNumber(){
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Registration Numbers");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    List<String> regNumberList = new ArrayList<>();
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        regNumberList.add(dataSnapshot.getKey());
                    }
                    totalRegNumber.setText(String.valueOf(regNumberList.size()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SystemReportActivity.this, "Database Error "+error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalRegisteredStudents(){
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Students");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    List<String> studentList = new ArrayList<>();
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        studentList.add(dataSnapshot.getKey());
                    }
                    totalStudentRegistered.setText(String.valueOf(studentList.size()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SystemReportActivity.this, "Database Error "+error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
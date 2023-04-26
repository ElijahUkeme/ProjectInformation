package activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.elijah.ukeme.projectinformation.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import interfaces.ShowErrorMessage;

public class AdminAddOrRemoveASupervisorActivity extends AppCompatActivity implements ShowErrorMessage {

    private EditText name,capacity,supervisorId;
    private Button button;
    private ProgressBar progressBar;
    private String route;
    private boolean cancel = false;
    DatabaseReference supervisorReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_or_remove_a_supervisor);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        name = findViewById(R.id.supervisor_name_editText);
        capacity = findViewById(R.id.supervisor_capacity_editText);
        supervisorId = findViewById(R.id.supervisor_id_editText);
        button = findViewById(R.id.admin_add_or_remove_supervisor_button);
        route = getIntent().getStringExtra("operation");
        supervisorReference = FirebaseDatabase.getInstance().getReference().child("Supervisors");


        progressBar = findViewById(R.id.progressBar_admin_supervisor);

        Log.d("Main","The operation is "+route);
        if (route.equalsIgnoreCase("remove")){
            button.setText("Remove");
        }else if (route.equalsIgnoreCase("view")){
            button.setText("View");
        }else if (route.equalsIgnoreCase("update")){
            button.setText("Update");
        } else {
            button.setText("Add");
            supervisorId.setVisibility(View.GONE);
            name.setVisibility(View.VISIBLE);
            capacity.setVisibility(View.VISIBLE);
        }
        button.setOnClickListener(view -> {
            checkForAction();
        });
    }

    void checkForAction(){
            if (route.equalsIgnoreCase("add")){
                addSupervisor();
            }else if (route.equalsIgnoreCase("delete")){
                removeSupervisor();
            }else if (route.equalsIgnoreCase("view")){
                checkStudentBySupervisorName();
            }else if (route.equalsIgnoreCase("update")){
                updateSupervisor();
            }else {
                showMessage("Error","Unknown Route");
            }
        }

    void addSupervisor(){
        String supervisorName =  name.getText().toString();
        int carryCapacity = Integer.parseInt(capacity.getText().toString());
        int numberOfStudentAssigned = 0;
        if (supervisorName.isEmpty()){
            name.setError("Please Enter the supervisor name");
            cancel = true;
            name.requestFocus();
        }else if (capacity.getText().toString().isEmpty()){
            capacity.setError("Please Enter the student capacity for the supervisor");
            cancel = true;
            capacity.requestFocus();
        }else {

            progressBar.setVisibility(View.VISIBLE);
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd MM, yyyy");
            String formattedCurrentDate = currentDate.format(calendar.getTime());

            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
            String formattedCurrentTime = currentTime.format(calendar.getTime());
            String supervisorKey = formattedCurrentDate + formattedCurrentTime;

            HashMap<String,Object> supervisorMap = new HashMap<>();
            supervisorMap.put("sid",supervisorKey);
            supervisorMap.put("name",supervisorName);
            supervisorMap.put("capacity",carryCapacity);
            supervisorMap.put("numberAssigned",numberOfStudentAssigned);
            supervisorMap.put("status","Not Filled");

            Query supervisorQuery = FirebaseDatabase.getInstance().getReference()
                    .child("Supervisors").orderByChild("name").equalTo(supervisorName);
            supervisorQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        showMessage("Error","You have already added this Supervisor");
                    }else {

                        supervisorReference.child(supervisorKey).updateChildren(supervisorMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(AdminAddOrRemoveASupervisorActivity.this, "Supervisor Added Successfully", Toast.LENGTH_SHORT).show();
                                        }else {
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(AdminAddOrRemoveASupervisorActivity.this, "Error Occurred, Please try Again", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(AdminAddOrRemoveASupervisorActivity.this, "Error from db "+error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
    void removeSupervisor() {
        String supervisorKey =  supervisorId.getText().toString();
        if (supervisorKey.isEmpty()){
            supervisorId.setError("Please Enter the unique key of the supervisor");
            cancel = true;
            supervisorId.requestFocus();
        }else {


            progressBar.setVisibility(View.VISIBLE);
            supervisorReference.child(supervisorKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        supervisorReference.child("Supervisors").child(supervisorKey)
                                .removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(AdminAddOrRemoveASupervisorActivity.this, "Supervisor Removed Successfully", Toast.LENGTH_SHORT).show();
                                        } else {
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(AdminAddOrRemoveASupervisorActivity.this, "Error Occurred, Please Try Again", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }else {
                        progressBar.setVisibility(View.GONE);
                        showMessage("Error","There is no such Supervisor in the database");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AdminAddOrRemoveASupervisorActivity.this, "Database Error "+error.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void checkStudentBySupervisorName(){
        String supervisorKey =  supervisorId.getText().toString();
        if (supervisorKey.isEmpty()){
            supervisorId.setError("Please Enter the supervisor unique key");
            cancel = true;
            supervisorId.requestFocus();
        }else {
            Intent intent = new Intent(AdminAddOrRemoveASupervisorActivity.this,AdminHomeActivity.class);
            intent.putExtra("supervisorKey",supervisorKey);
            startActivity(intent);
        }

    }

    private void updateSupervisor(){
        String supervisorKey =  supervisorId.getText().toString();
        if (supervisorKey.isEmpty()){
            supervisorId.setError("Please Enter the supervisor unique key");
            cancel = true;
            supervisorId.requestFocus();
        }else {
            Intent intent = new Intent(AdminAddOrRemoveASupervisorActivity.this,UpdateASupervisor.class);
            intent.putExtra("supervisorKey",supervisorKey);
            startActivity(intent);
        }

    }


    @Override
    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}
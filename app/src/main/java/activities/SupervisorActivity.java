package activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.elijah.ukeme.projectinformation.R;

public class SupervisorActivity extends AppCompatActivity {

    private TextView sName,sSid,sStatus,sCapacity,sNumberAssigned;
    private String name,sid,status,capacity,numberAssigned;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisor);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        sName = findViewById(R.id.name_supervisor_profile);
        sSid = findViewById(R.id.supervisor_id_profile);
        sStatus = findViewById(R.id.supervisor_status_profile);
        sCapacity = findViewById(R.id.supervisor_capacity_profile);
        sNumberAssigned = findViewById(R.id.supervisor_number_assigned_profile);
        name = getIntent().getStringExtra("name");
        sid = getIntent().getStringExtra("sid");
        status = getIntent().getStringExtra("status");
        capacity = getIntent().getStringExtra("capacity");
        numberAssigned = getIntent().getStringExtra("numberAssigned");

        displaySupervisorDetails();
    }

    private void displaySupervisorDetails(){
        sName.setText(name);
        sSid.setText(sid);
        sStatus.setText(status);
        sCapacity.setText(capacity);
        sNumberAssigned.setText(numberAssigned);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.supervisor_students,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.supervisor_view_students_menu_item:
                viewYourStudents();
                return true;
            default:
                Toast.makeText(this, "Wrong Selection", Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
        }
    }
    private void viewYourStudents(){
        Intent intent = new Intent(SupervisorActivity.this,AdminHomeActivity.class);
        intent.putExtra("supervisorKey",sid);
        startActivity(intent);
    }



}
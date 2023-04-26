package activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.elijah.ukeme.projectinformation.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import interfaces.ShowErrorMessage;
import model.Supervisor;

public class SupervisorLoginActivity extends AppCompatActivity implements ShowErrorMessage {
    private EditText sid;
    private Button loginBtn;
    private ProgressBar progressBar;
    private boolean cancel = false;
    private DatabaseReference databaseReference;
    String supervisorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisor_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        sid = findViewById(R.id.supervisor_id_editText_login);
        loginBtn = findViewById(R.id.login_supervisor_button);
        progressBar = findViewById(R.id.progressBar_supervisor_login);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Supervisors");

        loginBtn.setOnClickListener(view -> {
            validateInput();
        });
    }

    private void validateInput(){
        supervisorId = sid.getText().toString();
        if (supervisorId.isEmpty()){
            sid.setError("Please Enter your unique key");
            cancel = true;
            sid.requestFocus();
        }else {
            loginSupervisor();
        }
    }
    private void loginSupervisor(){
        supervisorId = sid.getText().toString();
        progressBar.setVisibility(View.VISIBLE);
        databaseReference.child(supervisorId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    progressBar.setVisibility(View.GONE);
                    Supervisor supervisor = snapshot.getValue(Supervisor.class);
                    Intent intent = new Intent(SupervisorLoginActivity.this,SupervisorActivity.class);
                    intent.putExtra("name",supervisor.getName());
                    intent.putExtra("capacity",String.valueOf(supervisor.getCapacity()));
                    intent.putExtra("numberAssigned",String.valueOf(supervisor.getNumberAssigned()));
                    intent.putExtra("status",supervisor.getStatus());
                    intent.putExtra("sid",supervisor.getSid());
                    startActivity(intent);
                }else {
                    progressBar.setVisibility(View.GONE);
                    showMessage("Error","Incorrect Supervisor Id");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SupervisorLoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
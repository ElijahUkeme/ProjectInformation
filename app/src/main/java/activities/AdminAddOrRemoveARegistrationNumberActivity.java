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
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import interfaces.ShowErrorMessage;
import model.Student;

public class AdminAddOrRemoveARegistrationNumberActivity extends AppCompatActivity implements ShowErrorMessage {

    private EditText editText;
    private Button button;
    private ProgressBar progressBar;
    boolean cancel = false;
    private String route;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_or_remove_a_registration_number);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        editText = findViewById(R.id.admin_add_or_remove_regNumber_editText);
        button = findViewById(R.id.admin_add_or_remove_regNumber_button);
        progressBar = findViewById(R.id.progressBar_admin_regNumber);
        route = getIntent().getStringExtra("operation");


        if (route.equalsIgnoreCase("remove")|| route.equalsIgnoreCase("removeStudent")){
            button.setText("Remove");
        }
        else if (route.equalsIgnoreCase("search")){
            button.setText("Search");
        }else {
            button.setText("Add");
        }

        button.setOnClickListener(view -> {
            validateInput();
        });
    }

    void validateInput(){
        if (editText.getText().toString().isEmpty()){
            editText.setError("Please Enter the registration Number");
            cancel = true;
            editText.requestFocus();
        }else if (editText.getText().toString().contains("/")) {
            editText.setError("Reg Number must not contain slash, use underscore");
            cancel = true;
            editText.requestFocus();
        }
        else {
            if (route.equalsIgnoreCase("add")){
                addRegistrationNumber();
            }else if (route.equalsIgnoreCase("remove")){
                removeRegistrationNumber();
            }else if (route.equalsIgnoreCase("removeStudent")){
                removeAStudent();
            }else if (route.equalsIgnoreCase("search")){
                searchForStudent();
            }else {
                showMessage("Error","Unknown Route");
            }
        }
    }

    void addRegistrationNumber(){
        progressBar.setVisibility(View.VISIBLE);
        String regNumber = editText.getText().toString().toLowerCase();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Registration Numbers").child(regNumber).exists()){
                    progressBar.setVisibility(View.GONE);
                    showMessage("Error","You have Already Added this Registration Number");
                }else {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("registrationNumber",regNumber);
                    databaseReference.child("Registration Numbers").child(regNumber).updateChildren(hashMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(AdminAddOrRemoveARegistrationNumberActivity.this, "Registration Number Added Successfully", Toast.LENGTH_SHORT).show();
                                    }else {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(AdminAddOrRemoveARegistrationNumberActivity.this, "Error Occurred, Please try Again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminAddOrRemoveARegistrationNumberActivity.this, "Database error "+error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    void removeRegistrationNumber() {
        progressBar.setVisibility(View.VISIBLE);
        String regNumber = editText.getText().toString().toLowerCase();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Registration Numbers").child(regNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    databaseReference.child("Registration Numbers").child(regNumber)
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(AdminAddOrRemoveARegistrationNumberActivity.this, "Registration Number deleted Successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(AdminAddOrRemoveARegistrationNumberActivity.this, "Error Occurred, Please Try Again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else {
                    progressBar.setVisibility(View.GONE);
                    showMessage("Error","There is no such Registration Number in the database");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminAddOrRemoveARegistrationNumberActivity.this, "Database Error "+error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void removeAStudent() {
        progressBar.setVisibility(View.VISIBLE);
        String regNumber = editText.getText().toString().toLowerCase();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Students").child(regNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    databaseReference.child("Students").child(regNumber)
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(AdminAddOrRemoveARegistrationNumberActivity.this, "Student Removed Successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(AdminAddOrRemoveARegistrationNumberActivity.this, "Error Occurred, Please Try Again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else {
                    progressBar.setVisibility(View.GONE);
                    showMessage("Error","There is no Student such Registered Student in the database");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminAddOrRemoveARegistrationNumberActivity.this, "Database Error "+error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    void searchForStudent() {
        progressBar.setVisibility(View.VISIBLE);
        String regNumber = editText.getText().toString().toLowerCase();
         DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference()
                .child("Students").child(regNumber);
                studentRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            progressBar.setVisibility(View.GONE);
                            Student student = snapshot.getValue(Student.class);
                            String registrationNumber = student.getRegNumber();
                            Intent intent = new Intent(AdminAddOrRemoveARegistrationNumberActivity.this, StudentProfileActivity.class);
                            intent.putExtra("regNumber", registrationNumber);
                            startActivity(intent);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            showMessage("Error", "There is no Registered Student with the Registration Number");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(AdminAddOrRemoveARegistrationNumberActivity.this, "Database Error " + error.toString(), Toast.LENGTH_SHORT).show();
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
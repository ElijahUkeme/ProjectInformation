package activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.elijah.ukeme.projectinformation.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import interfaces.ShowErrorMessage;
import model.Student;

public class MainActivity extends AppCompatActivity implements ShowErrorMessage {
    private TextView registerTV,welcomeTV;
    private Button loginBtn;
    private EditText regNumberEdt;
    private ProgressBar progressBar;
    String regNumber;
    private boolean cancel = false;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        registerTV = findViewById(R.id.to_register_page);
        loginBtn = findViewById(R.id.login_button);
        regNumberEdt = findViewById(R.id.login_regNumber);
        progressBar = findViewById(R.id.progressBar_Login);
        regNumber = regNumberEdt.getText().toString();
        welcomeTV = findViewById(R.id.admin_home_link);

        loginBtn.setOnClickListener(view -> {
            login();
        });
        registerTV.setOnClickListener(view -> {
            toRegisterPage();
        });
        welcomeTV.setOnClickListener(view -> {
            count+= 1;
            if (count>=3){
                toAdminLoginPage();
            }
        });

    }

    private void login() {
        if (regNumberEdt.getText().toString().isEmpty()) {
            regNumberEdt.setError("Please Enter your Registration Number");
            cancel = true;
            regNumberEdt.requestFocus();
        } else if (regNumberEdt.getText().toString().contains("/")) {
            regNumberEdt.setError("Reg Number must not contain slash, use underscore");
            cancel = true;
            regNumberEdt.requestFocus();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            String registrationNumber = regNumberEdt.getText().toString().toLowerCase();
            final DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference();
            studentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child("Students").child(registrationNumber).exists()) {
                        progressBar.setVisibility(View.GONE);
                        Student student = snapshot.child("Students").child(registrationNumber).getValue(Student.class);
                        if (student.getRegNumber().equalsIgnoreCase(registrationNumber)){
                            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, StudentProfileActivity.class);
                            intent.putExtra("regNumber", student.getRegNumber());
                            startActivity(intent);
                        }else {
                            showMessage("Error","Registration Number doesn't corresponds");
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        showMessage("Error", "There is no Registered Student with this Registration Number");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Database Error " + error.toString(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void toRegisterPage(){
        Intent intent = new Intent(MainActivity.this, StudentRegistrationActivity.class);
        startActivity(intent);
    }
    private void toAdminLoginPage(){
        Intent intent = new Intent(MainActivity.this, AdminLoginActivity.class);
        startActivity(intent);
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
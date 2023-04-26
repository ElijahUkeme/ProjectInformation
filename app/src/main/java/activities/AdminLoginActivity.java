package activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import model.AdminLoginInfo;

public class AdminLoginActivity extends AppCompatActivity implements ShowErrorMessage {
    private EditText username, password;
    private Button loginBtn;
    private ProgressDialog loadingDialog;
    private TextView textView;
    private boolean cancel = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        username = findViewById(R.id.admin_login_username);
        password = findViewById(R.id.admin_login_password);
        loginBtn = findViewById(R.id.admin_login_button);
        loadingDialog = new ProgressDialog(this);
        textView = findViewById(R.id.to_supervisor_login_page);
        loginBtn.setOnClickListener(view -> {
            validateInfo();
        });
        textView.setOnClickListener(view -> {
            toSupervisorLoginPage();
        });
    }

    private void validateInfo() {
        if (username.getText().toString().isEmpty()) {
            username.setError("Please Enter your Username");
            cancel = true;
            username.requestFocus();
        } else if (password.getText().toString().isEmpty()) {
            password.setError("Please Enter your Password");
            cancel = true;
            password.requestFocus();
        } else {
            loginAdmin();
        }
    }

    private void registerAdmin() {
        loadingDialog.setTitle("Account Creation....");
        loadingDialog.setMessage("Please wait while we are checking your credentials");
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.show();

        DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference()
                .child("Admin").child(username.getText().toString());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("username", username.getText().toString());
        hashMap.put("password", password.getText().toString());

        studentRef.updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            loadingDialog.dismiss();
                            Toast.makeText(AdminLoginActivity.this, "Admin Registration Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AdminLoginActivity.this,AdminHomeActivity.class);
                            startActivity(intent);
                        } else {
                            loadingDialog.dismiss();
                            Toast.makeText(AdminLoginActivity.this, "Error registering the Admin", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void loginAdmin(){
        loadingDialog.setTitle("Login Processing....");
        loadingDialog.setMessage("Please wait while we are checking your login details");
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.show();

        String name = username.getText().toString();
        String pass = password.getText().toString();
        DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference();
        studentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child("Admin").child(name).exists()) {

                            loadingDialog.dismiss();
                            Intent intent = new Intent(AdminLoginActivity.this, AdminHomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                    } else {
                        loadingDialog.dismiss();
                        showMessage("Error", "Incorrect Username or Password");
                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.dismiss();
                Toast.makeText(AdminLoginActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toSupervisorLoginPage(){
        Intent intent = new Intent(AdminLoginActivity.this,SupervisorLoginActivity.class);
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




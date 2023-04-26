package activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.elijah.ukeme.projectinformation.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import model.Student;

public class StudentProfileActivity extends AppCompatActivity {

    private CircleImageView imageView;
    private TextView name,regNumber,gender,supervisor,projectTopic;
    private String registrationNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        name = findViewById(R.id.name_profile);
        regNumber = findViewById(R.id.registration_number_profile);
        gender = findViewById(R.id.gender_profile);
        supervisor = findViewById(R.id.supervisor_profile);
        projectTopic = findViewById(R.id.project_topic_profile);
        imageView = findViewById(R.id.image_profile);
        registrationNumber = getIntent().getStringExtra("regNumber");


        displayStudentProfile();

    }
    private void displayStudentProfile(){

        try {

            final DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference();
            studentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.child("Students").child(registrationNumber).exists()) {
                        Student student = snapshot.child("Students").child(registrationNumber).getValue(Student.class);
                        name.setText(student.getName());
                        regNumber.setText(student.getRegNumber());
                        gender.setText(student.getGender());
                        supervisor.setText(student.getSupervisor());
                        projectTopic.setText(student.getProjectTopic());
                        Picasso.get().load(student.getImage()).into(imageView);

                    } else {
                        Toast.makeText(StudentProfileActivity.this, "Can't get the Student's Information", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(StudentProfileActivity.this, "Database Error " + error.toString(), Toast.LENGTH_SHORT).show();
                }
            });


        }catch (Exception exception){
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.student_update_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.student_info_update_menu_item:
                sendStudentInfoForUpdate();
                return true;
            default:
                Toast.makeText(StudentProfileActivity.this, "Unknown Selection", Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
        }
    }
    private void sendStudentInfoForUpdate(){
        Intent intent = new Intent(StudentProfileActivity.this,StudentUpdateActivity.class);
        intent.putExtra("regNumber",registrationNumber);
        startActivity(intent);
    }
}

package activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.elijah.ukeme.projectinformation.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import model.Student;

public class StudentUpdateActivity extends AppCompatActivity {
    private TextView imageUpdateTv;
    private EditText name, gender, projectTopic;
    private CircleImageView imageView;
    private Button updateButton;
    private String regNumber;
    private ProgressBar progressBar;
    private Uri imageUri;
    private String myUri = "";
    private StorageReference profileImageStorageRef;
    private StorageTask uploadTask;
    private boolean cancel = false, profileImageChange = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_update);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        profileImageStorageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        imageUpdateTv = findViewById(R.id.textview_profile_image_picker_update);
        name = findViewById(R.id.editText_name_update);
        gender = findViewById(R.id.editText_gender_update);
        projectTopic = findViewById(R.id.editText_project_topic_update);
        imageView = findViewById(R.id.profile_image_update);
        updateButton = findViewById(R.id.profile_update_button);
        progressBar = findViewById(R.id.idPBLoading_update);
        regNumber = getIntent().getStringExtra("regNumber");

        displayInfoForUpdate();
         imageUpdateTv.setOnClickListener(view -> {
             profileImageChange = true;
         });

         updateButton.setOnClickListener(view -> {
             if (profileImageChange){
                 updateProfileImageAlso();
             }else {
                 updateOnlyDetails();
             }
         });
    }

    private void displayInfoForUpdate() {

        final DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference();
        studentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child("Students").child(regNumber).exists()) {
                    Student student = snapshot.child("Students").child(regNumber).getValue(Student.class);
                    name.setText(student.getName());
                    gender.setText(student.getGender());
                    projectTopic.setText(student.getProjectTopic());
                    Picasso.get().load(student.getImage()).into(imageView);

                } else {
                    Toast.makeText(StudentUpdateActivity.this, "Can't get the Student's Information", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StudentUpdateActivity.this, "Database Error " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void pickProfileImage() {
        CropImage.activity(imageUri)
                .setAspectRatio(1, 1)
                .start(StudentUpdateActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                imageView.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(StudentUpdateActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error Occurred Try Again", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(StudentUpdateActivity.this, StudentRegistrationActivity.class));
            }
        }
    }

    private void updateProfileImageAlso() {
        progressBar.setVisibility(View.VISIBLE);
        if (imageUri != null) {
            final StorageReference fileRef = profileImageStorageRef
                    .child(regNumber + ".jpg");
            uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadedUri = task.getResult();
                        myUri = downloadedUri.toString();

                        DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference()
                                .child("Students").child(regNumber);

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("name", name.getText().toString());
                        hashMap.put("image", myUri);
                        hashMap.put("gender", gender.getText().toString());
                        hashMap.put("projectTopic", projectTopic.getText().toString());
                        studentRef.updateChildren(hashMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(StudentUpdateActivity.this, "Your profile Information has been updated Successfully", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(StudentUpdateActivity.this, "Error updating your profile", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }

            });
        }
    }

    private void updateOnlyDetails(){
        DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference()
                .child("Students").child(regNumber);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", name.getText().toString());
        hashMap.put("gender", gender.getText().toString());
        hashMap.put("projectTopic", projectTopic.getText().toString());
        studentRef.updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(StudentUpdateActivity.this, "Your profile Information has been updated Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(StudentUpdateActivity.this, "Error updating your profile", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}

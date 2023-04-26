package activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import interfaces.ShowErrorMessage;
import model.Supervisor;

public class StudentRegistrationActivity extends AppCompatActivity implements ShowErrorMessage {
    private TextView textView;
    private CircleImageView circleImageView;
    private EditText name, regNumber;
    private Button registerBtn;
    private RadioGroup radioGroup;
    private RadioButton male, female;
    private ProgressDialog loadingDialog;
    private Uri imageUri = null;
    private String myUri = "", selectedGender = null;
    private StorageReference profileImageStorageRef;
    private StorageTask uploadTask;
    private boolean cancel = false;
    private String supervisorChose = "";
    private double supervisorCount = 0;
    private DatabaseReference supervisorRef;
    private boolean success = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_registration);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        profileImageStorageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        textView = findViewById(R.id.textview_profile_image_picker);
        circleImageView = findViewById(R.id.profile_image);
        name = findViewById(R.id.editText_name);
        regNumber = findViewById(R.id.editText_regNumber);
        registerBtn = findViewById(R.id.register_button);
        radioGroup = findViewById(R.id.gender);
        male = findViewById(R.id.radio_male);
        female = findViewById(R.id.radio_female);
        loadingDialog = new ProgressDialog(this);
        supervisorRef = FirebaseDatabase.getInstance().getReference().child("Supervisors");

        textView.setOnClickListener(view -> {
            pickProfileImage();
        });
        registerBtn.setOnClickListener(view -> {
            validateInfo();

        });
    }

    private void validateInfo() {
        boolean genderChecked = false;
        String studentName = name.getText().toString();
        String studentRegNumber = regNumber.getText().toString().toLowerCase();
        if (female.isChecked()) {
            genderChecked = true;
            selectedGender = female.getText().toString();
        } else if (male.isChecked()) {
            genderChecked = true;
            selectedGender = male.getText().toString();
        } else {
            genderChecked = false;
        }
        if (studentName.isEmpty()) {
            name.setError("Please Enter your name");
            cancel = true;
            name.requestFocus();
        } else if (studentRegNumber.isEmpty()) {
            regNumber.setError("Please Enter your Registration Number");
            cancel = true;
            regNumber.requestFocus();
        } else if (studentRegNumber.contains("/")) {
            regNumber.setError("Reg Number must not contains slash, use underscore");
            cancel = true;
            regNumber.requestFocus();
        } else if (imageUri == null) {
            Toast.makeText(this, "Please Pick a Profile Image", Toast.LENGTH_SHORT).show();
        } else if (!genderChecked) {
            Toast.makeText(this, "Please select your gender", Toast.LENGTH_SHORT).show();
        } else {
               getSupervisors();
               authenticateRegistrationNumber(studentRegNumber,studentName);

        }
    }


    private void pickProfileImage() {
        CropImage.activity(imageUri)
                .setAspectRatio(1, 1)
                .start(StudentRegistrationActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                circleImageView.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(StudentRegistrationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error Occurred Try Again", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(StudentRegistrationActivity.this, StudentRegistrationActivity.class));
            }
        }
    }

    private void uploadProfileImage(String studentName) {

        String registrationNumber = regNumber.getText().toString().toLowerCase();
        if (imageUri != null) {
            final StorageReference fileRef = profileImageStorageRef
                    .child(registrationNumber + ".jpg");
            uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        loadingDialog.dismiss();
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
                        retrieveSupervisorInfo(supervisorChose,registrationNumber,myUri,studentName);
                    }
                }
            });
        }
    }

    private String getSupervisors() {

        Query databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Supervisors").orderByChild("status").equalTo("Not Filled");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<String> supervisorsList =new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            supervisorsList.add(dataSnapshot.child("sid").getValue().toString());
                        }
                    supervisorChose = supervisorsList.get(new Random().nextInt(supervisorsList.size()));


                }else {
                    Log.d("Main","There is no supervisor in the database");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StudentRegistrationActivity.this, "Error From the database", Toast.LENGTH_SHORT).show();
            }
        });
        return supervisorChose;
    }


    private void retrieveSupervisorInfo(String supervisorKey, String registrationNumber, String profileImage, String name) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Supervisors").child(supervisorKey).exists()) {
                    Supervisor supervisor = snapshot.child("Supervisors").child(supervisorKey).getValue(Supervisor.class);
                    String supervisorStatus = "";
                    String supervisorName = supervisor.getName();
                    String supervisorUniqueKey = supervisor.getSid();
                    int carryingCapacity = supervisor.getCapacity();
                    int numberAssigned = supervisor.getNumberAssigned();

                    registerStudent(registrationNumber, profileImage, name, supervisorName, supervisorUniqueKey);
                    numberAssigned += 1;

                    if (numberAssigned >= carryingCapacity) {
                        supervisorStatus = "Filled";
                    } else {
                        supervisorStatus = "Not Filled";
                    }

                    updateSupervisorInfo(supervisorKey, supervisorStatus, numberAssigned);
                } else {
                    loadingDialog.dismiss();
                    Log.d("Main", "There is no such supervisor");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.dismiss();
                Toast.makeText(StudentRegistrationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void authenticateRegistrationNumber(String registrationNumber,String studentName) {

        loadingDialog.setTitle("Registration Processing....");
        loadingDialog.setMessage("Please wait while we are checking your credentials");
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.show();

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Registration Numbers").child(registrationNumber);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    loadingDialog.dismiss();
                    showMessage("Error", "This Registration Number does not exist in the database");
                    return;
                } else {

                    final DatabaseReference studentDatabase = FirebaseDatabase.getInstance().getReference()
                            .child("Students").child(registrationNumber);
                    studentDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                loadingDialog.dismiss();
                                showMessage("Error", "Student with this Registration Number has Already Registered");
                                return;
                            } else {
                                if (!getSupervisors().isEmpty()) {
                                    uploadProfileImage(studentName);
                                } else {
                                    loadingDialog.dismiss();
                                    showMessage("Error", "Error getting a Supervisor, please contact the Admin or Try Again Later");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            loadingDialog.dismiss();
                            Toast.makeText(StudentRegistrationActivity.this, "Database Error " + error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.dismiss();
                Toast.makeText(StudentRegistrationActivity.this, "Database Error " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerStudent(String registrationNumber, String profileImage, String name, String supervisorAssigned, String supervisorKey) {

        final DatabaseReference studentDb = FirebaseDatabase.getInstance().getReference()
                .child("Students").child(registrationNumber);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", name);
        hashMap.put("image", profileImage);
        hashMap.put("regNumber", registrationNumber);
        hashMap.put("supervisor", supervisorAssigned);
        hashMap.put("gender", selectedGender);
        hashMap.put("supervisorKey", supervisorKey);
        hashMap.put("projectTopic", "No Approved Topic yet");
        studentDb.updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            loadingDialog.dismiss();
                            Toast.makeText(StudentRegistrationActivity.this, "Student Registration Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(StudentRegistrationActivity.this,MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            loadingDialog.dismiss();
                            Toast.makeText(StudentRegistrationActivity.this, "Registration Not Successful, please Try Again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateSupervisorInfo(String key, String status, int numberAssigned) {
        HashMap<String, Object> supervisorMap = new HashMap<>();
        supervisorMap.put("numberAssigned", numberAssigned);
        supervisorMap.put("status", status);
        supervisorRef.child(key).updateChildren(supervisorMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(StudentRegistrationActivity.this, "Supervisor info updated as well", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(StudentRegistrationActivity.this, "Was not able to update the supervisor info", Toast.LENGTH_SHORT).show();
                        }
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

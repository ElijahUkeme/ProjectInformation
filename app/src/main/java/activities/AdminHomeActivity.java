package activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.elijah.ukeme.projectinformation.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import model.Student;
import viewholder.StudentViewHolder;

public class AdminHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private RecyclerView recyclerView;
    private NavigationView navigationView;
    private FrameLayout frameLayout;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    DatabaseReference studentDatabaseRef;
    private String route = "all";
    private String supervisorKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        recyclerView = findViewById(R.id.recyclerview_students);
        navigationView = findViewById(R.id.nav_view);
        frameLayout = findViewById(R.id.fragment_container);
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolBar_admin_home);
        studentDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Students");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle !=null){
            supervisorKey = getIntent().getStringExtra("supervisorKey");
            route = "notAll";
        }else {
            route = "all";
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_navigation_drawer,R.string.close_navigation_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        if (route.equalsIgnoreCase("notAll")){
            displayRegisteredStudentBySupervisor();
        }else {
            displayRegisteredStudent();
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.regNum_add:
                addRegNumber();
                break;
            case R.id.supervisor_add:
                addSupervisor();
                break;
            case R.id.regNum_remove:
                removeRegNumber();
                break;
            case R.id.supervisor_remove:
                removeSupervisor();
                break;
            case R.id.student_remove:
                removeStudent();
                break;
            case R.id.student_search:
                searchForStudent();
                break;
            case R.id.regNumber_viewAll:
                viewAllRegistrationNumberPage();
                break;
            case R.id.student_by_supervisor:
                viewStudentBySupervisor();
                break;
            case R.id.reports_check:
                checkForReportPage();
                break;
            case R.id.supervisor_viewAll:
                viewAllSupervisorPage();
                break;
            case R.id.supervisor_update:
                updateSupervisor();
                break;

        }
        return true;
    }

    private void addRegNumber(){
        Intent intent = new Intent(AdminHomeActivity.this,AdminAddOrRemoveARegistrationNumberActivity.class);
        intent.putExtra("operation","add");
        startActivity(intent);
    }

    private void removeRegNumber(){
        Intent intent = new Intent(AdminHomeActivity.this,AdminAddOrRemoveARegistrationNumberActivity.class);
        intent.putExtra("operation","remove");
        startActivity(intent);
    }
    private void removeStudent(){
        Intent intent = new Intent(AdminHomeActivity.this,AdminAddOrRemoveARegistrationNumberActivity.class);
        intent.putExtra("operation","removeStudent");
        startActivity(intent);
    }

    private void searchForStudent(){
        Intent intent = new Intent(AdminHomeActivity.this,AdminAddOrRemoveARegistrationNumberActivity.class);
        intent.putExtra("operation","search");
        startActivity(intent);
    }

    private void addSupervisor(){
        Intent intent = new Intent(AdminHomeActivity.this,AdminAddOrRemoveASupervisorActivity.class);
        intent.putExtra("operation","add");
        startActivity(intent);
    }

    private void updateSupervisor(){
        Intent intent = new Intent(AdminHomeActivity.this,AdminAddOrRemoveASupervisorActivity.class);
        intent.putExtra("operation","update");
        startActivity(intent);
    }
    private void removeSupervisor(){
        Intent intent = new Intent(AdminHomeActivity.this,AdminAddOrRemoveASupervisorActivity.class);
        intent.putExtra("operation","remove");
        startActivity(intent);
    }

    private void viewStudentBySupervisor(){
        Intent intent = new Intent(AdminHomeActivity.this,AdminAddOrRemoveASupervisorActivity.class);
        intent.putExtra("operation","view");
        startActivity(intent);
    }
    private void viewAllRegistrationNumberPage(){
        Intent intent = new Intent(AdminHomeActivity.this,ViewAllRegistrationNumberActivity.class);
        startActivity(intent);
    }

    private void viewAllSupervisorPage(){
        Intent intent = new Intent(AdminHomeActivity.this,ViewAllSupervisorActivity.class);
        startActivity(intent);
    }
    private void checkForReportPage(){
        Intent intent = new Intent(AdminHomeActivity.this,SystemReportActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (route.equalsIgnoreCase("notAll")){
            displayRegisteredStudentBySupervisor();
        }else {
            displayRegisteredStudent();
        }
    }

    private void displayRegisteredStudent(){
        FirebaseRecyclerOptions<Student> options = new FirebaseRecyclerOptions.Builder<Student>()
                .setQuery(studentDatabaseRef,Student.class)
                .build();
        
        FirebaseRecyclerAdapter<Student, StudentViewHolder> adapter = new FirebaseRecyclerAdapter<Student, StudentViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull StudentViewHolder holder, int position, @NonNull Student model) {
                holder.regNumber.setText(model.getRegNumber());
                Picasso.get().load(model.getImage()).into(holder.profileImage);

                holder.itemView.setOnClickListener(view -> {
                    Intent intent = new Intent(AdminHomeActivity.this,StudentProfileActivity.class);
                    intent.putExtra("regNumber",model.getRegNumber());
                    startActivity(intent);
                });
            }

            @NonNull
            @Override
            public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_item_list,parent,false);
                return new StudentViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void displayRegisteredStudentBySupervisor(){
        FirebaseRecyclerOptions<Student> options = new FirebaseRecyclerOptions.Builder<Student>()
                .setQuery(studentDatabaseRef.orderByChild("supervisorKey").equalTo(supervisorKey),Student.class)
                .build();

        FirebaseRecyclerAdapter<Student, StudentViewHolder> adapter = new FirebaseRecyclerAdapter<Student, StudentViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull StudentViewHolder holder, int position, @NonNull Student model) {
                holder.regNumber.setText(model.getRegNumber());
                Picasso.get().load(model.getImage()).into(holder.profileImage);

                holder.itemView.setOnClickListener(view -> {
                    Intent intent = new Intent(AdminHomeActivity.this,StudentProfileActivity.class);
                    intent.putExtra("regNumber",model.getRegNumber());
                    startActivity(intent);
                });
            }

            @NonNull
            @Override
            public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_item_list,parent,false);
                return new StudentViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}
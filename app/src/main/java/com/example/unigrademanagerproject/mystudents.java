package com.example.unigrademanagerproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class mystudents extends AppCompatActivity {
    Button btn, btnRefresh;
    ListView lst;
    UniGradeDBClass db;
    ArrayList<HashMap<String, String>> myStudentList;
    String doctorID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mystudents);

        db = new UniGradeDBClass(this);
        lst = findViewById(R.id.lststudents);
        btn = findViewById(R.id.btnback);
        btnRefresh = findViewById(R.id.btnrefresh);

        // Get doctor ID from intent
        doctorID = getIntent().getStringExtra("DoctorID");

        if (doctorID == null || doctorID.isEmpty()) {
            Toast.makeText(this, "Error: Doctor ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Register ListView for Context Menu (for long-press Edit/Delete)
        registerForContextMenu(lst);

        // Load students for this doctor only
        loadMyStudents();

        // Refresh button - reload data from database
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMyStudents();
                Toast.makeText(mystudents.this, "Student list refreshed", Toast.LENGTH_SHORT).show();
            }
        });

        // Back button - return to doctor dashboard
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload students when returning from update activity
        loadMyStudents();
    }

    private void loadMyStudents() {
        // Get only students registered by this doctor
        myStudentList = db.getStudentsByDoctorId(doctorID);
        displayStudents(myStudentList);
    }

    private void displayStudents(ArrayList<HashMap<String, String>> studentList) {
        SimpleAdapter adapter = new SimpleAdapter(
                mystudents.this,
                studentList,
                R.layout.activity_custom2,
                new String[]{"StudentName", "Course", "AttendanceGrade", "MidtermGrade", "FinalGrade", "TotalMarks"},
                new int[]{R.id.colname, R.id.colcourse, R.id.colattendance, R.id.colmidterm, R.id.colfinal, R.id.coltotal}
        );
        lst.setAdapter(adapter);
    }

    // Create Context Menu (shows when long-pressing a student)
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle("Command for: " + myStudentList.get(info.position).get("StudentName"));
        String[] menuItems = getResources().getStringArray(R.array.CmdMenu);
        for (int i = 0; i < menuItems.length; i++) {
            menu.add(Menu.NONE, i, i, menuItems[i]);
        }
    }

    // Handle Context Menu Selection (Edit or Delete)
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems = getResources().getStringArray(R.array.CmdMenu);
        String CmdName = menuItems[menuItemIndex];
        String StudentID = myStudentList.get(info.position).get("StudentID");

        if ("Edit".equals(CmdName)) {
            Intent intent = new Intent(this, updatestudent.class);
            // Pass all student data to update activity
            HashMap<String, String> student = myStudentList.get(info.position);
            intent.putExtra("StudentID", student.get("StudentID"));
            intent.putExtra("StudentName", student.get("StudentName"));
            intent.putExtra("Course", student.get("Course"));
            intent.putExtra("Attendance", student.get("AttendanceGrade"));
            intent.putExtra("Mid", student.get("MidtermGrade"));
            intent.putExtra("Final", student.get("FinalGrade"));

            startActivity(intent);

        } else if ("Delete".equals(CmdName)) {
            boolean deleted = db.deleteStudent(StudentID);
            if (deleted) {
                Toast.makeText(this, "Student deleted successfully", Toast.LENGTH_LONG).show();
                loadMyStudents();
            } else {
                Toast.makeText(this, "Failed to delete student", Toast.LENGTH_LONG).show();
            }
        }

        return true;
    }
}
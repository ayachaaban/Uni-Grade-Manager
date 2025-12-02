package com.example.unigrademanagerproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Toast;

public class managestudents extends AppCompatActivity {
    Button btn, btnRefresh;
    ListView lst;
    SearchView searchView;
    UniGradeDBClass db;
    ArrayList<HashMap<String, String>> originalStudentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_managestudents);

        db = new UniGradeDBClass(this);
        lst = findViewById(R.id.lststudents);
        btn = findViewById(R.id.btncancel);
        btnRefresh = findViewById(R.id.btnrefresh);
        searchView = findViewById(R.id.searchstudent);
        // Register ListView for Context Menu
        registerForContextMenu(lst);

        // Load all students with doctor info
        loadStudents();

        // Refresh button - reload data from database
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadStudents();
                searchView.setQuery("", false);
                searchView.clearFocus();
                Toast.makeText(managestudents.this, "Student list refreshed", Toast.LENGTH_SHORT).show();
            }
        });

        // Cancel button
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(managestudents.this, admindashboard.class);
                startActivity(i);
                finish();
            }
        });

        // Search functionality - filter by student name OR doctor name
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterStudents(newText);
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload students from database when returning to this activity
        loadStudents();
    }

    private void loadStudents() {
        originalStudentList = db.getAllStudents();
        displayStudents(originalStudentList);
    }

    private void displayStudents(ArrayList<HashMap<String, String>> studentList) {
        SimpleAdapter adapter = new SimpleAdapter(
                managestudents.this,
                studentList,
                R.layout.activity_custom2,
                new String[]{"StudentName", "Course", "AttendanceGrade", "MidtermGrade", "FinalGrade", "TotalMarks"},
                new int[]{R.id.colname, R.id.colcourse, R.id.colattendance, R.id.colmidterm, R.id.colfinal, R.id.coltotal}
        );
        lst.setAdapter(adapter);
    }

    private void filterStudents(String query) {
        ArrayList<HashMap<String, String>> filteredList = new ArrayList<>();

        // If search is empty, show all students
        if (query == null || query.trim().isEmpty()) {
            filteredList.addAll(originalStudentList);
        } else {
            String lowerQuery = query.toLowerCase();

            // Filter by student name OR doctor name
            for (HashMap<String, String> student : originalStudentList) {
                String studentName = student.get("StudentName").toLowerCase();
                String doctorName = student.get("DoctorName").toLowerCase();

                // If student name OR doctor name contains the search query
                if (studentName.contains(lowerQuery) || doctorName.contains(lowerQuery)) {
                    filteredList.add(student);
                }
            }
        }

        // Update ListView with filtered results
        displayStudents(filteredList);
    }
    //Add string array to app/src/main/res/values/strings.xml
    //<string-array name="CmdMenu">
    //    <item>Edit</item>
    //    <item>Delete</item>
    //</string-array>
    // Create Context Menu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle("Command for: " + originalStudentList.get(info.position).get("StudentName"));
        String[] menuItems = getResources().getStringArray(R.array.CmdMenu);
        for (int i = 0; i < menuItems.length; i++) {
            menu.add(Menu.NONE, i, i, menuItems[i]);
        }
    }

    // Handle Context Menu Selection
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems = getResources().getStringArray(R.array.CmdMenu);
        String CmdName = menuItems[menuItemIndex];
        String StudentID = originalStudentList.get(info.position).get("StudentID");

        if ("Edit".equals(CmdName)) {
            Intent intent = new Intent(this,updatestudent.class);
            // Pass all student data
            HashMap<String, String> student = originalStudentList.get(info.position);
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
                originalStudentList = db.getAllStudents();
                displayStudents(originalStudentList);
            } else {
                Toast.makeText(this, "Failed to delete student", Toast.LENGTH_LONG).show();
            }
        }

        return true;
    }



}
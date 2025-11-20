package com.example.unigrademanagerproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;

public class managedoctors extends AppCompatActivity {
    Button btn;
    ListView lst;
    SearchView searchView;
    UniGradeDBClass db;
    ArrayList<HashMap<String, String>> originalDoctorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_managedoctors);
        db=new UniGradeDBClass(this);
        lst=findViewById(R.id.lstdoctors);
        btn=findViewById(R.id.btncancel);
        searchView=findViewById(R.id.searchdoctor);
        originalDoctorList = db.getAllDoctors();

        SimpleAdapter adapter= new SimpleAdapter(
                managedoctors.this,//matra7 li bade tbayen fi l simpleadapter
                originalDoctorList, //data source(from the hashmap)
                R.layout.activity_custom1, //custom layout li bade tbayen fi l listview
                new String[] {"DoctorID", "Name","Department","Email","Username"} //keys from the hashmap
                , new int[] {R.id.coldoctorid, R.id.colname,R.id.coldepartment, R.id.colemail,R.id.colusername});//name of text view in custom layout
        lst.setAdapter(adapter);
        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int position, long mylng) {

                // Get the selected doctor's full data
                HashMap<String, String> selectedDoctor = originalDoctorList.get(position);

                // Pass all doctor data to update activity
                Intent i = new Intent(managedoctors.this, updatedoctor.class);
                i.putExtra("DoctorID", selectedDoctor.get("DoctorID"));
                i.putExtra("Name", selectedDoctor.get("Name"));
                i.putExtra("Email", selectedDoctor.get("Email"));
                i.putExtra("Department", selectedDoctor.get("Department"));
                i.putExtra("Username", selectedDoctor.get("Username"));
                startActivity(i);
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(managedoctors.this, admindashboard.class);
                startActivity(i);
                finish();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            // When user presses "Search" button on keyboard (we don't need this)
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;  // Do nothing
            }

            // When user types/deletes text (THIS IS IMPORTANT)
            @Override
            public boolean onQueryTextChange(String newText) {
                filterDoctors(newText);  // Call our filter method
                return true;
            }
        });
    }

private void filterDoctors(String query) {
    // Step 1: Create empty list for filtered results
    ArrayList<HashMap<String, String>> filteredList = new ArrayList<>();

    // Step 2: If search is empty, show all doctors
    if (query == null || query.trim().isEmpty()) {
        filteredList.addAll(originalDoctorList);  // Copy all doctors
    }
    // Step 3: If user typed something, filter
    else {
        String lowerQuery = query.toLowerCase();  // Convert to lowercase for comparison

        // Loop through ALL doctors
        for (HashMap<String, String> doctor : originalDoctorList) {
            // Get doctor's name and username
            String name = doctor.get("Name").toLowerCase();
            String username = doctor.get("Username").toLowerCase();

            // If name OR username contains what user typed → add to filtered list
            if (name.contains(lowerQuery) || username.contains(lowerQuery)) {
                filteredList.add(doctor);
            }
        }
    }

    // Step 4: Update ListView with filtered results
    SimpleAdapter adapter = new SimpleAdapter(
            managedoctors.this,
            filteredList,  // ← Use filtered list instead of original
            R.layout.activity_custom1,
            new String[] {"DoctorID", "Name","Department","Email", "Username"},
            new int[] {R.id.coldoctorid, R.id.colname, R.id.coldepartment,R.id.colemail, R.id.colusername}
    );
    lst.setAdapter(adapter);  // Show filtered results
}}



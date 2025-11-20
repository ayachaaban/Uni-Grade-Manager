package com.example.unigrademanagerproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashMap;

public class updatedoctor extends AppCompatActivity {
    Button btn1,btn2,btn3;
    EditText edt1,edt2,edt4,edt5;
    Spinner spn1;
    UniGradeDBClass db;
    String[] departments = {"Select Department","Computer Science","Computer Engineering","Mathematics","Physics"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_updatedoctor);
        db=new UniGradeDBClass(this);
        btn1=findViewById(R.id.btnupdate);
        btn2=findViewById(R.id.btndelete);
        btn3=findViewById(R.id.btncancel);
        edt1=findViewById(R.id.edtname);
        edt2=findViewById(R.id.edtemail);
        edt4=findViewById(R.id.edtusername);
        edt5=findViewById(R.id.edtpassword);
        spn1=findViewById(R.id.spinnerdepartment);
        Intent i = getIntent();
        String doctorID = i.getStringExtra("DoctorID");
        HashMap<String, String> doctor = db.getDoctorById(doctorID);
        // Pre-fill the form with existing data
        edt1.setText(doctor.get("Name"));
        edt2.setText(doctor.get("Email"));
        edt4.setText(doctor.get("Username"));
        edt5.setText(doctor.get("Password"));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, departments);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn1.setAdapter(adapter);

// Set spinner to current department
        String currentDept = doctor.get("Department");
        int deptPosition = adapter.getPosition(currentDept);
        if (deptPosition >= 0) {
            spn1.setSelection(deptPosition);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edt1.getText().toString().trim();
                String email = edt2.getText().toString().trim();
                String username = edt4.getText().toString().trim();
                String password = edt5.getText().toString().trim();
                String department = spn1.getSelectedItem().toString();

                if (name.isEmpty() || email.isEmpty() || department.isEmpty() ||
                        username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(updatedoctor.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean success = db.updateDoctor(doctorID, name, email, department, username, password);

                if (success) {
                    Toast.makeText(updatedoctor.this, "Doctor updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(updatedoctor.this, "Failed to update doctor", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(updatedoctor.this)
                        .setTitle("Delete Doctor")
                        .setMessage("Are you sure you want to delete this doctor?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                boolean success = db.deleteDoctor(doctorID);

                                if (success) {
                                    Toast.makeText(updatedoctor.this, "Doctor deleted successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(updatedoctor.this, "Failed to delete doctor", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}}
package com.example.unigrademanagerproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class registerdoctor extends AppCompatActivity {
    Button btn, btnBackToLogin;
    EditText edt1,edt2,edt3,edt4;
    Spinner spinnerDepartment;
    String selectedDepartment;
    UniGradeDBClass db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registerdoctor);
        db=new UniGradeDBClass(this);
        btn=findViewById(R.id.btnregister);
        btnBackToLogin=findViewById(R.id.btnbacktologin);
        edt1=findViewById(R.id.edtname);
        edt2=findViewById(R.id.edtemail);
        edt3=findViewById(R.id.edtusername);
        edt4=findViewById(R.id.edtpassword);
        spinnerDepartment=findViewById(R.id.spinnerdep);
        String[] department = {"Select Department","Computer Science","Computer Engineering","Mathematics","Physics"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, department);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepartment.setAdapter(adapter);
        spinnerDepartment.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                selectedDepartment = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                selectedDepartment = null;
            }
        });

        // Back to Login button
        btnBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(registerdoctor.this, loginpage.class);
                startActivity(i);
                finish();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edt1.getText().toString();
                String email = edt2.getText().toString();
                String username = edt3.getText().toString();
                String password = edt4.getText().toString();
                if (name.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(registerdoctor.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (selectedDepartment == null || selectedDepartment.equals("Select Department")) {
                    Toast.makeText(registerdoctor.this, "Please select a department", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (db.isUsernameExists(username)) {

                    Toast.makeText(registerdoctor.this, "Username already exists", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean isRegistered = db.registerDoctor(username, password, name, email, selectedDepartment);
                if (isRegistered) {
                    Toast.makeText(registerdoctor.this, "Doctor registered successfully", Toast.LENGTH_SHORT).show();
                    edt1.setText("");
                    edt2.setText("");
                    edt3.setText("");
                    edt4.setText("");
                    spinnerDepartment.setSelection(0);
                } else {
                    Toast.makeText(registerdoctor.this, "Registration failed", Toast.LENGTH_SHORT).show();
                }



            }
        });

    }
}
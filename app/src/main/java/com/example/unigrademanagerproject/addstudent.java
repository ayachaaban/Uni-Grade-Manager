package com.example.unigrademanagerproject;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class addstudent extends AppCompatActivity {
    Button btn;
    EditText edt1,edt2,edt3,edt4,edt5;
    Spinner spn1,spn2;
    TextView txt1,txt2;
    UniGradeDBClass db;
    String selectedcourse;
    String selectedsemester;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_addstudent);
        String doctorID = getIntent().getStringExtra("DoctorID");
        btn=findViewById(R.id.btnaddstudent);
        edt1=findViewById(R.id.edtstudentid);
        edt2=findViewById(R.id.edtstudentname);
        edt3=findViewById(R.id.edtattendance);
        edt4=findViewById(R.id.edtmidterm);
        edt5=findViewById(R.id.edtfinal);
        spn1=findViewById(R.id.spinnercourse);
        spn2=findViewById(R.id.spinnersemester);
        txt1=findViewById(R.id.txttotalvalue);
        txt2=findViewById(R.id.txtgradevalue);
        db=new UniGradeDBClass(this);
        String[] course = {"Select Course","Calculus 1","Calculus 2","Linear Algebra 1","Electric Circuits","Data Structures","Algorithms","Operating Systems","Database Systems"};
        String[] semester ={"Select Semester","Fall 2025","Spring 2025","Summer 2025","Fall 2024","Spring 2024","Summer 2024"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, course);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn1.setAdapter(adapter1);
        spn1.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                 selectedcourse = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                selectedcourse = null;
            }
        });

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, semester);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn2.setAdapter(adapter2);
        spn2.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                selectedsemester = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                selectedsemester = null;
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = edt1.getText().toString().trim();
                String name = edt2.getText().toString().trim();
                String attendance = edt3.getText().toString().trim();
                String midterm = edt4.getText().toString().trim();
                String fin = edt5.getText().toString().trim();
                if (id.isEmpty() || name.isEmpty() || attendance.isEmpty() || midterm.isEmpty() || fin.isEmpty()
                        || selectedcourse == null || selectedcourse.startsWith("Select")
                        || selectedsemester == null || selectedsemester.startsWith("Select")) {
                    Toast.makeText(addstudent.this, "Please complete all fields and select course/semester", android.widget.Toast.LENGTH_SHORT).show();
                    return;
                }

                double attend, mid, finalExam;
                try {
                    attend = Double.parseDouble(attendance);
                    mid = Double.parseDouble(midterm);
                    finalExam = Double.parseDouble(fin);
                } catch (NumberFormatException e) {
                    Toast.makeText(addstudent.this, "Enter valid numbers", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Validate ranges
                if (attend < 0 || attend > 100) {
                    Toast.makeText(addstudent.this, "Attendance grade must be between 0 and 100", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mid < 0 || mid > 40) {
                    Toast.makeText(addstudent.this, "Midterm grade must be between 0 and 40", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (finalExam < 0 || finalExam > 60) {
                    Toast.makeText(addstudent.this, "Final grade must be between 0 and 60", Toast.LENGTH_SHORT).show();
                    return;
                }

                double total = attend * 0.10 + mid * 0.40 + finalExam * 0.50;
                String grade;
                if (total >= 90) {
                    grade="A";
                } else if (total >= 80) {
                    grade="B";
                } else if (total >= 70) {
                    grade="C";
                } else if (total >= 60) {
                    grade="D";
                } else {
                    grade="F";
                }
                txt1.setText(String.format(java.util.Locale.US, "%.2f", total));
                txt2.setText(grade);

                boolean inserted = db.addStudent (id, name, selectedcourse, selectedsemester, attend, mid, finalExam, total, grade,doctorID);
                if (inserted) {
                    Toast.makeText(addstudent.this, "Student added", Toast.LENGTH_SHORT).show();
                    edt1.setText("");
                    edt2.setText("");
                    edt3.setText("");
                    edt4.setText("");
                    edt5.setText("");
                    spn1.setSelection(0);
                    spn2.setSelection(0);
                    txt1.setText("");
                    txt2.setText("");
                } else {
                    Toast.makeText(addstudent.this, "Failed to add student", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }
};
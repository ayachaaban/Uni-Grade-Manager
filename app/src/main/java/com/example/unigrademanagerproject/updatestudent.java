package com.example.unigrademanagerproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class updatestudent extends AppCompatActivity {
    EditText edt1,edt2,edt3,edt4,edtCourse;
    Button btn1,btn2;
    TextView txt1,txt2;
    UniGradeDBClass db;
    String studentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_updatestudent);
        db=new UniGradeDBClass(this);
        edtCourse=findViewById(R.id.edtcourse);
        edt1=findViewById(R.id.edtattendance);
        edt2=findViewById(R.id.edtmidterm);
        edt3=findViewById(R.id.edtfinal);
        edt4=findViewById(R.id.edtname);
        txt1=findViewById(R.id.txttotalmarks);
        txt2=findViewById(R.id.txtgrademarks);
        btn1=findViewById(R.id.btnupdate);
        btn2=findViewById(R.id.btncancel);

        // Make student name and course name read-only
        edt4.setEnabled(false);
        edt4.setTextColor(getResources().getColor(android.R.color.darker_gray));
        edtCourse.setEnabled(false);
        edtCourse.setTextColor(getResources().getColor(android.R.color.darker_gray));

        // Get student data from intent
        studentID = getIntent().getStringExtra("StudentID");
        String studentName = getIntent().getStringExtra("StudentName");
        String courseName = getIntent().getStringExtra("Course");
        String attendance = getIntent().getStringExtra("Attendance");
        String mid = getIntent().getStringExtra("Mid");
        String finalExam = getIntent().getStringExtra("Final");

        edt4.setText(studentName);
        edtCourse.setText(courseName);
        edt1.setText(attendance);
        edt2.setText(mid);
        edt3.setText(finalExam);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String courseName = edtCourse.getText().toString().trim();
                String attendStr = edt1.getText().toString().trim();
                String midStr = edt2.getText().toString().trim();
                String finalStr = edt3.getText().toString().trim();


                if (attendStr.isEmpty() || midStr.isEmpty() || finalStr.isEmpty()) {
                    Toast.makeText(updatestudent.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                double attendance = Double.parseDouble(attendStr);
                double mid = Double.parseDouble(midStr);
                double finalExam = Double.parseDouble(finalStr);

                if (attendance < 0 || attendance > 10) {
                    Toast.makeText(updatestudent.this, "Attendance must be 0-10", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mid < 0 || mid > 30) {
                    Toast.makeText(updatestudent.this, "Mid must be 0-30", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (finalExam < 0 || finalExam > 60) {
                    Toast.makeText(updatestudent.this, "Final must be 0-60", Toast.LENGTH_SHORT).show();
                    return;
                }

                double total = attendance + mid + finalExam;
                String grade;
                if (total >= 90) grade = "A";
                else if (total >= 80) grade = "B";
                else if (total >= 70) grade = "C";
                else if (total >= 60) grade = "D";
                else grade = "F";

                // Display total and grade FIRST
                txt1.setText(String.valueOf(total));
                txt2.setText(grade);

                boolean updated = db.updateStudent(studentID, courseName, attendance, mid, finalExam, total, grade);
                if (updated) {
                    Toast.makeText(updatestudent.this, "Student updated successfully!\nTotal: " + total + " | Grade: " + grade, Toast.LENGTH_LONG).show();

                    // Delay before going back to manage students so user can see the values
                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 2000); // 2 second delay
                } else {
                    Toast.makeText(updatestudent.this, "Update failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });




    }
}
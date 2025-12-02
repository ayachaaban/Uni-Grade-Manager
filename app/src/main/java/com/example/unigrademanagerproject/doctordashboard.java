package com.example.unigrademanagerproject;

import android.os.Bundle;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.view.Menu;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;


public class doctordashboard extends TabActivity {

    private TabHost tbh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctordashboard);
        String doctorID = getIntent().getStringExtra("DoctorID");
        tbh = getTabHost();
        // hay awal tab
        TabSpec ts1 = tbh.newTabSpec("AddStudent");
        ts1.setIndicator("Add Student"); //hon bsame l tabhost
        Intent addStudentIntent = new Intent(this, addstudent.class);
        addStudentIntent.putExtra("DoctorID", doctorID); // forward doctor id
        ts1.setContent(addStudentIntent);
       // ts1.setContent(new Intent(this, addstudent.class));
        //7atet bi alb l tab 1 l main activity
        tbh.addTab(ts1);

        TabSpec ts2 = tbh.newTabSpec("MyStudents");
        ts2.setIndicator("My Students");
        Intent myStudentsIntent = new Intent(this, mystudents.class);
        myStudentsIntent.putExtra("DoctorID", doctorID); // forward doctor id
        ts2.setContent(myStudentsIntent);
        tbh.addTab(ts2);




    }



}
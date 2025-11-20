package com.example.unigrademanagerproject;

import android.os.Bundle;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.view.Menu;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;


public class admindashboard extends TabActivity {

    private TabHost tbh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admindashboard);

        tbh = getTabHost();
        // hay awal tab
        TabSpec ts1 = tbh.newTabSpec("RegisterDoctor");
        ts1.setIndicator("Register Doctor"); //hon bsame l tabhost
        ts1.setContent(new Intent(this, registerdoctor.class));
        //7atet bi alb l tab 1 l main activity
        tbh.addTab(ts1);

        TabSpec ts2 = tbh.newTabSpec("ManageDoctors");
        ts2.setIndicator("Manage Doctors");
        ts2.setContent(new Intent(this, managedoctors.class));
        tbh.addTab(ts2);

        TabSpec ts3 = tbh.newTabSpec("ManageStudents");
        ts3.setIndicator("Manage Students");
        ts3.setContent(new Intent(this, managestudents.class));
        tbh.addTab(ts3);


    }



}

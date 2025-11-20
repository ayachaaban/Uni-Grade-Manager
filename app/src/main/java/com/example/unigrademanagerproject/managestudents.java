package com.example.unigrademanagerproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;

public class managestudents extends AppCompatActivity {
    Button btn;
    ListView lst;
    SearchView searchView;
    UniGradeDBClass db;
    ArrayList<HashMap<String, String>> originalStudentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_managestudents);
        db=new UniGradeDBClass(this);
        lst=findViewById(R.id.lststudents);
        btn=findViewById(R.id.btncancel);
        searchView=findViewById(R.id.searchstudent);



    }
}
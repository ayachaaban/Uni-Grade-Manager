package com.example.unigrademanagerproject;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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

public class loginpage extends AppCompatActivity {
    Button btn;
    EditText edt1,edt2;
    Spinner spinnerUserRole;
    String selectedRole;
    UniGradeDBClass db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_loginpage);
        //Initialize database
        db=new UniGradeDBClass(this);
        btn=findViewById(R.id.btnlogin);
        edt1=findViewById(R.id.edttextusername);
        edt2=findViewById(R.id.edttextpassword);
        spinnerUserRole=findViewById(R.id.spinneruserrole);
        // Create array of roles
        String[] roles = {"Select Role", "Admin", "Doctor"};
        // Create ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerUserRole.setAdapter(adapter);
        spinnerUserRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRole = parent.getItemAtPosition(position).toString();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedRole = null;
            }


        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edt1.getText().toString().trim();
                String password = edt2.getText().toString().trim();


                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(loginpage.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (selectedRole == null || selectedRole.equals("Select Role")) {
                    Toast.makeText(loginpage.this, "Please select a role", Toast.LENGTH_SHORT).show();
                    return;
                }


                Cursor cursor = db.loginUser(username, password, selectedRole);
                if (cursor != null && cursor.moveToFirst()) {

                    //bekhod l data mn l cursor
                    //column 0 = UserID, column 1 = Role
                    int userID = cursor.getInt(0);
                    String role = cursor.getString(1);
                    cursor.close();

                    Toast.makeText(loginpage.this, "Login Successful as " + role, Toast.LENGTH_SHORT).show();


                    if (role.equals("Admin")) {
                        Intent i = new Intent(loginpage.this, admindashboard.class);
                        startActivity(i);
                    } else if (role.equals("Doctor")) {

                        Intent i = new Intent(loginpage.this, doctordashboard.class);
                        String doctorID = db.getDoctorIdByUserId(userID) ;
                        i.putExtra("DoctorID", doctorID);
                        startActivity(i);
                    }
                } else {

                    Toast.makeText(loginpage.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    if (cursor != null) cursor.close();
                }
            }
        });

    }
}

package com.example.unigrademanagerproject;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class UniGradeDBClass extends SQLiteOpenHelper {


    // Database Version
    //bass nsewe update
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "UniGradeDB";

    // Table Name
    private static final String TABLE_USERS = "users";
    private static final String TABLE_DOCTORS = "doctors";
    private static final String TABLE_STUDENTS = "students";

    // Constructor
    public UniGradeDBClass(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    //create table
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users Table
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                "UserID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Username TEXT NOT NULL UNIQUE, " +
                "Password TEXT NOT NULL, " +
                "Role TEXT NOT NULL);");

        // Create Doctors Table
        db.execSQL("CREATE TABLE " + TABLE_DOCTORS + " (" +
                "DoctorID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "DoctorName TEXT NOT NULL, " +
                "Email TEXT, " +
                "Department TEXT, " +
                "UserID INTEGER, " +
                "FOREIGN KEY(UserID) REFERENCES " + TABLE_USERS + "(UserID));");

        // Create Students Table
        db.execSQL("CREATE TABLE " + TABLE_STUDENTS + " (" +
                "StudentID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "StudentName TEXT NOT NULL, " +
                "StudentNumber TEXT NOT NULL, " +
                "Course TEXT, " +
                "Semester INTEGER, " +
                "MidtermGrade INTEGER CHECK(MidtermGrade >= 0 AND MidtermGrade <= 30), " +
                "AttendanceGrade INTEGER CHECK(AttendanceGrade >= 0 AND AttendanceGrade <= 10), " +
                "FinalGrade INTEGER CHECK(FinalGrade >= 0 AND FinalGrade <= 60), " +
                "TotalMarks INTEGER, " +
                "LetterGrade TEXT, " +
                "DoctorID INTEGER, " +
                "FOREIGN KEY(DoctorID) REFERENCES " + TABLE_DOCTORS + "(DoctorID));");

        // Insert Default Admin
        ContentValues admin = new ContentValues();
        admin.put("Username", "admin");
        admin.put("Password", "admin123");
        admin.put("Role", "Admin");
        db.insert(TABLE_USERS, null, admin);

        Log.d("CREATE TABLE", "All tables created successfully.");
    }
    public Cursor loginUser(String username, String password, String role) {
        SQLiteDatabase db = this.getReadableDatabase();
        //db.query() - Android's method to query SQLite database
        //TABLE_USERS - The table name ("users")
        //new String[]{"UserID", "Role"}:like SELECT UserID, Role FROM users
        //  "Username=? AND Password=? AND Role=?": the WHERE clause
        // new String[]{username, password, role}: the values to replace the ? in the WHERE clause
        //null, null, null: groupBy, having, orderBy (not used here)
        return db.query(TABLE_USERS,
                new String[]{"UserID", "Role"},
                "Username=? AND Password=? AND Role=?",
                new String[]{username, password, role},
                null, null, null);
        //A Cursor is like a finger pointing at rows in this table
        //The return statement gives you this Cursor object so you can check if login was successful and get the user's data
        // If a matching user is found, the Cursor will contain that user's UserID and Role
        // If no match is found, the Cursor will be empty
    }

    //  Check if Username Exists
    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{"UserID"},
                "Username=?",
                new String[]{username},
                null, null, null);

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }


    //  Register Doctor
    public boolean registerDoctor(String username, String password, String doctorName, String email, String department) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            // Step 1: Insert into users table first
            ContentValues userValues = new ContentValues();
            userValues.put("Username", username);
            userValues.put("Password", password);
            userValues.put("Role", "Doctor");
            // Insert user and get the auto generated UserID(primary key)
            long userID = db.insert(TABLE_USERS, null, userValues);
            if (userID == -1) {
                Log.e("DB_ERROR", "Failed to insert user");
                return false;
            }

            // Step 2: Insert into doctors table
            ContentValues doctorValues = new ContentValues();
            doctorValues.put("DoctorName", doctorName);
            doctorValues.put("Email", email);
            doctorValues.put("Department", department);
            doctorValues.put("UserID", userID);// Foreign key reference to users table

            long doctorID = db.insert(TABLE_DOCTORS, null, doctorValues);

            if (doctorID == -1) {
                Log.e("DB_ERROR", "Failed to insert doctor");
                return false;
            }
            Log.d("DB_SUCCESS", "Doctor registered with DoctorID: " + doctorID);
            return true;

        } catch (Exception e) {
            Log.e("DB_ERROR", "Error registering doctor: " + e.getMessage());
            return false;
        }
    }
    // Get All Doctors
    public ArrayList<HashMap<String, String>> getAllDoctors() {
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // JOIN users and doctors tables
        String query = "SELECT d.DoctorID, d.DoctorName, d.Department, d.Email, u.Username " +
                "FROM " + TABLE_DOCTORS + " d " +
                "INNER JOIN " + TABLE_USERS + " u ON d.UserID = u.UserID " +
                "WHERE u.Role = 'Doctor'";

        Cursor cursor = db.rawQuery(query, null);

        Log.d("DB_DEBUG", "Query executed. Row count: " + cursor.getCount());

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put("DoctorID", cursor.getString(0));      // d.DoctorID
                map.put("Name", cursor.getString(1));          // d.DoctorName
                map.put("Department", cursor.getString(2));    // d.Department
                map.put("Email", cursor.getString(3));         // d.Email
                map.put("Username", cursor.getString(4));      // u.Username

                Log.d("DOCTOR_DATA", "ID: " + cursor.getString(0) +
                        ", Name: " + cursor.getString(1) +
                        ", Dept: " + cursor.getString(2) +
                        ", Email: " + cursor.getString(3) +
                        ", User: " + cursor.getString(4));

                list.add(map);
            } while (cursor.moveToNext());
        } else {
            Log.d("DB_DEBUG", "No doctors found in database!");
        }
        cursor.close();
        db.close();

        Log.d("DB_DEBUG", "Returning list with " + list.size() + " doctors");
        return list;
    }

    // Update doctor
    public boolean updateDoctor(String doctorID, String name, String email, String department, String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            Cursor cursor = db.rawQuery("SELECT UserID FROM " + TABLE_DOCTORS + " WHERE DoctorID = ?", new String[]{doctorID});
            if (!cursor.moveToFirst()) {
                cursor.close();
                return false;
            }
            int userID = cursor.getInt(0);
            cursor.close();

            ContentValues doctorValues = new ContentValues();
            doctorValues.put("DoctorName", name);
            doctorValues.put("Email", email);
            doctorValues.put("Department", department);
            int doctorRows = db.update(TABLE_DOCTORS, doctorValues, "DoctorID = ?", new String[]{doctorID});

            ContentValues userValues = new ContentValues();
            userValues.put("Username", username);
            userValues.put("Password", password);
            int userRows = db.update(TABLE_USERS, userValues, "UserID = ?", new String[]{String.valueOf(userID)});

            return (doctorRows > 0 && userRows > 0);
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error updating doctor: " + e.getMessage());
            return false;
        } finally {
            db.close();
        }
    }

    // Delete doctor
    public boolean deleteDoctor(String doctorID) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            Cursor cursor = db.rawQuery("SELECT UserID FROM " + TABLE_DOCTORS + " WHERE DoctorID = ?", new String[]{doctorID});
            if (!cursor.moveToFirst()) {
                cursor.close();
                return false;
            }
            int userID = cursor.getInt(0);
            cursor.close();

            int doctorRows = db.delete(TABLE_DOCTORS, "DoctorID = ?", new String[]{doctorID});
            int userRows = db.delete(TABLE_USERS, "UserID = ?", new String[]{String.valueOf(userID)});

            return (doctorRows > 0 && userRows > 0);
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error deleting doctor: " + e.getMessage());
            return false;
        } finally {
            db.close();
        }
    }
    public boolean addStudent(String id, String name, String course, String semester,
                              double attendance, double midterm, double finalExam,
                              double total, String grade,String doctorID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("StudentNumber", id);
        values.put("StudentName", name);
        values.put("Course", course);
        values.put("Semester", semester);
        values.put("AttendanceGrade", attendance);
        values.put("MidtermGrade", midterm);
        values.put("FinalGrade", finalExam);
        values.put("TotalMarks", total);
        values.put("LetterGrade", grade);
        values.put("DoctorID", doctorID);

        long result = db.insert(TABLE_STUDENTS, null, values);
        db.close();
        return result != -1;
    }
    // Get Doctor by ID
    public HashMap<String, String> getDoctorById(String doctorID) {
        HashMap<String, String> doctor = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT d.DoctorID, d.DoctorName, d.Department, d.Email, u.Username, u.Password " +
                "FROM " + TABLE_DOCTORS + " d " +
                "INNER JOIN " + TABLE_USERS + " u ON d.UserID = u.UserID " +
                "WHERE d.DoctorID = ?";

        Cursor cursor = db.rawQuery(query, new String[]{doctorID});

        if (cursor.moveToFirst()) {
            doctor.put("DoctorID", cursor.getString(0));
            doctor.put("Name", cursor.getString(1));
            doctor.put("Department", cursor.getString(2));
            doctor.put("Email", cursor.getString(3));
            doctor.put("Username", cursor.getString(4));
            doctor.put("Password", cursor.getString(5));
        }

        cursor.close();
        db.close();
        return doctor;
    }
    public ArrayList<HashMap<String, String>> getStudentsByDoctorName(String doctorName) {
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT s.* FROM " + TABLE_STUDENTS + " s " +
                "INNER JOIN " + TABLE_DOCTORS + " d ON s.DoctorID = d.DoctorID " +
                "WHERE d.DoctorName LIKE ?";
        Cursor cursor = db.rawQuery(query, new String[]{"%" + doctorName + "%"});
        while (cursor.moveToNext()) {
            HashMap<String, String> map = new HashMap<>();
            map.put("StudentID", cursor.getString(cursor.getColumnIndex("StudentID")));
            map.put("StudentName", cursor.getString(cursor.getColumnIndex("StudentName")));
            // Add other fields as needed
            list.add(map);
        }
        cursor.close();
        db.close();
        return list;
    }
    public String getDoctorIdByUserId(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String doctorId = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT DoctorID FROM " + TABLE_DOCTORS + " WHERE UserID = ?", new String[]{String.valueOf(userId)});
            if (cursor != null && cursor.moveToFirst()) {
                doctorId = cursor.getString(0);
            }
        } catch (Exception e) {
            android.util.Log.e("DB_ERROR", "getDoctorIdByUserId: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return doctorId;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOCTORS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

}

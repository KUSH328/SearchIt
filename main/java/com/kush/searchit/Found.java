package com.kush.searchit;

import android.app.DatePickerDialog;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.N)
public class Found extends AppCompatActivity  implements AdapterView.OnItemSelectedListener{

    Spinner whatFound;
    String[] documents ={"Driving License","Passport","ID card","ATM card","Election card","Other"};

    EditText docNumber,issueDate,dateOfBirth;
    String docNum,issue,dob,document;
    TextView error;

    int temp = 0;

    Calendar myCalendar = Calendar.getInstance();

    private final ArrayList<String> details = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found);

        whatFound = findViewById(R.id.whatfound);

        whatFound.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the doc list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,documents);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        whatFound.setAdapter(aa);

        docNumber = findViewById(R.id.uniqueNumber);
        issueDate = findViewById(R.id.issueDate);
        dateOfBirth = findViewById(R.id.birthDate);
        error = findViewById(R.id.txt_error);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        issueDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(Found.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final DatePickerDialog.OnDateSetListener dateOne = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelBirth();
            }

        };

        dateOfBirth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(Found.this, dateOne, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    private void updateLabel() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        issueDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateLabelBirth() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dateOfBirth.setText(sdf.format(myCalendar.getTime()));
    }

    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        if(position==0)
        {
            document = "Driving License";

        }
        else if(position==1)
        {
            document = "Passport";

        }
        else if(position==2)
        {
            document = "ID card";

        }
        else if(position==3)
        {
            document = "ATM card";

        }
        else if(position==4)
        {
            document = "Election card";

        } else if(position==5)
        {
            document = "Other";

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    int empty=1;
    int i=1;
    String x="";
    public void post(View view) {
        docNum = docNumber.getText().toString();
        issue = issueDate.getText().toString();
        dob = dateOfBirth.getText().toString();

        if(!docNum.isEmpty())
        {
            if(!issue.isEmpty())
            {
                if(!dob.isEmpty())
                {
                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    final DatabaseReference[] myRef = {database.getReference(docNum)};
                    myRef[0].addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                            String value = dataSnapshot.getValue(String.class);
                            assert value != null;
                            if(!value.isEmpty())
                            {
                                empty=0;
                            }
                            details.add(value);

                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    if(empty==1)
                    {
                        myRef[0] = database.getReference(docNum).child("dateOfIssue");
                        myRef[0].setValue(issue);
                        myRef[0] = database.getReference(docNum).child("dateOfBirth");
                        myRef[0].setValue(dob);
                        //insertUserData();

                        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        assert currentFirebaseUser != null;
                        myRef[0] = database.getReference(currentFirebaseUser.getUid());
                        myRef[0].addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                                String value = dataSnapshot.getValue(String.class);

                                if(i==3) {
                                    //Toast.makeText(getApplicationContext(), value, Toast.LENGTH_LONG).show();
                                    myRef[0] = database.getReference(docNum).child("emailId");
                                    myRef[0].setValue(value);
                                }
                                else if(i==4)
                                {
                                    x=x+value+" ";
                                }
                                else if(i==5)
                                {
                                    x=x+value;
                                    myRef[0] = database.getReference(docNum).child("name");
                                    myRef[0].setValue(x);
                                }
                                else if(i==7)
                                {
                                    myRef[0] = database.getReference(docNum).child("mobilNumber");
                                    myRef[0].setValue(value);
                                }
                                i++;
                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        //empty=0;
                        Toast.makeText(Found.this, "Posted Successfully. Stay Active!!!", Toast.LENGTH_LONG).show();
                    }

                    else if(empty==0)
                    {
                        Toast.makeText(Found.this,"Already posted",Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    error.setText("Date of birth is mandatory");
                }
            }
            else
            {
                error.setText("Issue date is mandatory");
            }
        }
        else
        {
            error.setText("Document number is mandatory");
        }
    }
}
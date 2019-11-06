package ca.bcit.lin_kong;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    EditText editTextUserId;
    EditText editTextSystolic;
    EditText editTextDiastolic;
    Button buttonAdd;

    DatabaseReference databasePressure;


    ListView lvPressure;
    List<Pressure> pressureList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databasePressure = FirebaseDatabase.getInstance().getReference("Measurement");

        editTextUserId = findViewById(R.id.editTextUserId);
        editTextSystolic = findViewById(R.id.editTextSystolic);
        editTextDiastolic = findViewById(R.id.editTextDiastolic);
        buttonAdd = findViewById(R.id.buttonAdd);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPressure();
            }
        });

        lvPressure = findViewById(R.id.lvPressures);
        pressureList = new ArrayList<Pressure>();
//
//        lvPressure.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                Pressure pressure = pressureList.get(position);
//
//                showUpdateDialog(pressure.getUserId(), pressure.getSystolic(),
//                        pressure.getDiastolic(),
//                        pressure.getReadDate()
//                        );
//                return false;
//            }
//        });
    }

    private void addPressure() {
        String userId = editTextUserId.getText().toString().trim();
        String systolic = editTextSystolic.getText().toString().trim();
        String diastolic = editTextDiastolic.getText().toString().trim();


        if (TextUtils.isEmpty(userId)) {
            Toast.makeText(this, "You must enter a user name.", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(systolic)) {
            Toast.makeText(this, "You must enter the systolic.", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(diastolic)) {
            Toast.makeText(this, "You must enter the diastolic.", Toast.LENGTH_LONG).show();
            return;
        }

        String id = databasePressure.push().getKey();

        Pressure pressure = new Pressure(id, userId, Calendar.getInstance().getTime(), Integer.parseInt(systolic), Integer.parseInt(diastolic));

        Task setValueTask = databasePressure.child(id).setValue(pressure);

        setValueTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(MainActivity.this,
                        "Measurement added.",Toast.LENGTH_LONG).show();

                editTextDiastolic.setText("");
                editTextSystolic.setText("");
                editTextUserId.setText("");
            }
        });

        setValueTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,
                        "something went wrong.\n" + e.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        databasePressure.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pressureList.clear();
                for (DataSnapshot pressureSnapshot : dataSnapshot.getChildren()) {
                    Pressure pressure = pressureSnapshot.getValue(Pressure.class);
                    pressureList.add(pressure);
                }

                PressureListAdapter adapter = new PressureListAdapter(MainActivity.this, pressureList);
                lvPressure.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
}

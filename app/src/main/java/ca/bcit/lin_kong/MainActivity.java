package ca.bcit.lin_kong;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    final DateFormat DATE = new SimpleDateFormat("yyyy/MM/dd");
    final DateFormat TIME = new SimpleDateFormat("hh:mm:ss");

    FloatingActionButton FBtnAdd;

    DatabaseReference databasePressure;

    ListView lvPressure;
    List<Pressure> pressureList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databasePressure = FirebaseDatabase.getInstance().getReference("Measurement");

        FBtnAdd = findViewById(R.id.FBtnAdd);

        FBtnAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showAddDialog();
            }
        });

        lvPressure = findViewById(R.id.lvPressures);
        pressureList = new ArrayList<>();

        lvPressure.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Pressure pressure = pressureList.get(position);

                showUpdateDialog(pressure.getId(), pressure.getUserId(), pressure.getReadDate(), pressure.getSystolic(),
                        pressure.getDiastolic());
                return false;
            }
        });
    }

    private void addPressure(String userId, Date date, String systolic, String diastolic) {
        String id = databasePressure.push().getKey();

        Pressure pressure = new Pressure(id, userId, date, Integer.parseInt(systolic), Integer.parseInt(diastolic));

        Task setValueTask = databasePressure.child(id).setValue(pressure);

        setValueTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(MainActivity.this,
                        "Measurement added.", Toast.LENGTH_LONG).show();
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

    private void showAddDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.add_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextUserId = dialogView.findViewById(R.id.editTextUserId);
        final EditText editTextSystolic = dialogView.findViewById(R.id.editTextSystolic);
        final EditText editTextDiastolic = dialogView.findViewById(R.id.editTextDiastolic);
        final EditText editTextDate = dialogView.findViewById(R.id.editTextDate);
        final Date date = new Date();
        editTextDate.setEnabled(false);
        editTextDate.setText(DATE.format(date));

        final EditText editTextTime = dialogView.findViewById(R.id.editTextTime);
        editTextTime.setEnabled(false);
        editTextTime.setText(TIME.format(date));


        final Button btnAdd = dialogView.findViewById(R.id.btnAdd);

        dialogBuilder.setTitle("add measurement");
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        btnAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String userId = editTextUserId.getText().toString().trim();
                String systolic = editTextSystolic.getText().toString().trim();
                String diastolic = editTextDiastolic.getText().toString().trim();

                if (TextUtils.isEmpty(userId)) {
                    editTextUserId.setError("You must enter a user name.");
                    return;
                } else if (TextUtils.isEmpty(systolic)) {
                    editTextSystolic.setError("You must enter the systolic.");
                    return;
                } else if (TextUtils.isEmpty(diastolic)) {
                    editTextDiastolic.setError("You must enter the diastolic.");
                    return;
                }
                addPressure(userId, date, systolic, diastolic);

                alertDialog.dismiss();
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
                    System.out.println(pressureSnapshot.getValue());
                    Pressure pressure = pressureSnapshot.getValue(Pressure.class);
                    pressureList.add(pressure);
                }

                PressureListAdapter adapter = new PressureListAdapter(MainActivity.this, pressureList);
                lvPressure.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void updatePressure(String id, String userId, Date date, String systolic, String diastolic) {
        DatabaseReference dbRef = databasePressure.child(id);

        Pressure pressure = new Pressure(id, userId, date, Integer.parseInt(systolic), Integer.parseInt(diastolic));

        Task setValueTask = dbRef.setValue(pressure);

        setValueTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(MainActivity.this,
                        "measurement Updated.", Toast.LENGTH_LONG).show();
            }
        });

        setValueTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,
                        "Something went wrong.\n" + e.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showUpdateDialog(final String id, String userId, final Date date, int systolic, int diastolic) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.update_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextUserId = dialogView.findViewById(R.id.editTextUserId);
        editTextUserId.setText(userId);

        final EditText editTextDate = dialogView.findViewById(R.id.editTextDate);
        editTextDate.setEnabled(false);
        editTextDate.setText(DATE.format(date));

        final EditText editTextTime = dialogView.findViewById(R.id.editTextTime);
        editTextTime.setEnabled(false);
        editTextTime.setText(TIME.format(date));

        final EditText editTextSystolic = dialogView.findViewById(R.id.editTextSystolic);
        editTextSystolic.setText(systolic + "");

        final EditText editTextDiastolic = dialogView.findViewById(R.id.editTextDiastolic);
        editTextDiastolic.setText(diastolic + "");

        final Button btnUpdate = dialogView.findViewById(R.id.btnUpdate);

        dialogBuilder.setTitle("update measurement for" + userId);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        btnUpdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String userId = editTextUserId.getText().toString().trim();
                String systolic = editTextSystolic.getText().toString().trim();
                String diastolic = editTextDiastolic.getText().toString().trim();

                if (TextUtils.isEmpty(userId)) {
                    editTextUserId.setError("You must enter a user name.");
                    return;
                } else if (TextUtils.isEmpty(systolic)) {
                    editTextSystolic.setError("You must enter the systolic.");
                    return;
                } else if (TextUtils.isEmpty(diastolic)) {
                    editTextDiastolic.setError("You must enter the diastolic.");
                    return;
                }

                updatePressure(id, userId, date, systolic, diastolic);

                alertDialog.dismiss();
            }
        });

        final Button btnDelete = dialogView.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                deletePressure(id);

                alertDialog.dismiss();
            }
        });
    }

    private void deletePressure(String id) {
        DatabaseReference dbRef = databasePressure.child(id);

        Task setRemoveTask = dbRef.removeValue();
        setRemoveTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(MainActivity.this,
                        "Pressure Deleted.", Toast.LENGTH_LONG).show();
            }
        });

        setRemoveTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,
                        "Something went wrong.\n" + e.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}

package com.example.hallmate.userside;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hallmate.R;
import com.example.hallmate.adapter.MonthlyBillAdapter;
import com.example.hallmate.model.MonthlyBill;
import com.example.hallmate.model.PenaltyRecord;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MonthlyBillActivity extends AppCompatActivity {

    private RecyclerView billsRecyclerView;
    private MonthlyBillAdapter billAdapter;
    private List<MonthlyBill> billList;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_bill);

        billsRecyclerView = findViewById(R.id.billsRecyclerView);
        billsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        billList = new ArrayList<>();
        billAdapter = new MonthlyBillAdapter(billList);
        billsRecyclerView.setAdapter(billAdapter);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        if (currentUser != null) {
            fetchBillsAndPenalties();
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchBillsAndPenalties() {
        String userId = currentUser.getUid();
        db.collection("MonthlyBills").document(userId)
                .get().addOnCompleteListener(task -> {
                    billList.clear();
                    if (task.isSuccessful() && task.getResult().exists()) {
                        DocumentSnapshot document = task.getResult();
                        // Assuming MonthlyBill is a single object stored in the document
                        MonthlyBill bill = document.toObject(MonthlyBill.class);
                        if (bill != null) {
                            billList.add(bill);
                        }

                        fetchPenalties(userId); // Load penalties after bills
                    } else {
                        Toast.makeText(MonthlyBillActivity.this, "Failed to load bill", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void fetchPenalties(String userId) {
        db.collection("PenaltyRecords")
                .document(userId)
                .collection("penalties")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (int i = 0; i < billList.size(); i++) {
                            if (i < task.getResult().size()) {
                                DocumentSnapshot document = task.getResult().getDocuments().get(i);
                                PenaltyRecord penalty = document.toObject(PenaltyRecord.class);
                                if (penalty != null) {
                                    billList.get(i).penaltyAmount = penalty.penaltyAmount;
                                    billList.get(i).penaltyDays = penalty.penaltyDays;
                                }
                            }
                        }
                        billAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Failed to load penalties", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
package com.example.hallmate.userside;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class DefaultMealStatusSetter {

    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private final int N_DAYS = 3; // Number of previous days to check

    public DefaultMealStatusSetter() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    public void setDefaultMealStatusIfNotExists() {
        String userId = auth.getCurrentUser().getUid();

        db.collection("UsersDirectory").document(userId).get()
                .addOnSuccessListener(snapshot -> {
                    String hallName = snapshot.getString("hallName");
                    if (hallName != null) {
                        DocumentReference userRef = db.collection("Users").document(hallName)
                                .collection("members").document(userId);

                        userRef.get().addOnSuccessListener(userDoc -> {
                            Map<String, Object> mealStatusMap = (Map<String, Object>) userDoc.get("mealStatus");
                            if (mealStatusMap == null) mealStatusMap = new HashMap<>();

                            Map<String, Object> updateMap = new HashMap<>();
                            List<String> lastNDates = getLastNDates(N_DAYS);
                            for (String date : lastNDates) {
                                if (!mealStatusMap.containsKey(date)) {
                                    updateMap.put("mealStatus." + date, true);
                                }
                            }

                            if (!updateMap.isEmpty()) {
                                userRef.update(updateMap);
                            }
                        });
                    }
                });
    }

    private List<String> getLastNDates(int n) {
        List<String> dateList = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        for (int i = 0; i < n; i++) {
            dateList.add(sdf.format(cal.getTime()));
            cal.add(Calendar.DATE, -1);
        }

        return dateList;
    }
}

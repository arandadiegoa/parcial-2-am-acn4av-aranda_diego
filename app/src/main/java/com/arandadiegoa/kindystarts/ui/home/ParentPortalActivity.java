package com.arandadiegoa.kindystarts.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.arandadiegoa.kindystarts.R;
import com.arandadiegoa.kindystarts.ui.auth.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ParentPortalActivity extends AppCompatActivity {

    private static final String TAG = "ParentPortalActivity";

    //Vistas
    private TextView textViewWelcome;
    private ProgressBar progressBar;
    private ScrollView contentScrollView;
    private CardView cardAttendance, cardMessage, cardSurvey, cardMoments, cardDocuments;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_portal);

        //Iniciar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //Enlazar Vistas
        textViewWelcome = findViewById(R.id.textViewWelcome);
        progressBar = findViewById(R.id.progressBarPortal);
        contentScrollView = findViewById(R.id.contentScrollViewPortal);

        cardAttendance = findViewById(R.id.cardAttendance);
        cardMessage = findViewById(R.id.cardMessage);
        cardSurvey = findViewById(R.id.cardSurvey);
        cardMoments = findViewById(R.id.cardMoments);
        cardDocuments = findViewById(R.id.cardDocuments);

        //Estado inicial
        contentScrollView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        //Cargar los datos del usuario
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            goToLogin();
            return;
        }

        loadUserData(currentUser);

        //Listeners
        View.OnClickListener placeholderListener = v ->
                Toast.makeText(this, "Próximamente...", Toast.LENGTH_SHORT).show();

        cardAttendance.setOnClickListener(placeholderListener);

        cardMessage.setOnClickListener(placeholderListener);

        cardSurvey.setOnClickListener(v -> {
            Intent intent = new Intent(ParentPortalActivity.this, SurveysActivity.class);
            startActivity(intent);
        });

        cardMoments.setOnClickListener(placeholderListener);

        cardDocuments.setOnClickListener(placeholderListener);
    }

    private void loadUserData(FirebaseUser currentUser) {
        String uid = currentUser.getUid();
        DocumentReference userDocRef = db.collection("users").document(uid);

        userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                progressBar.setVisibility(View.GONE);
                contentScrollView.setVisibility(View.VISIBLE);

                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document != null && document.exists()){
                        String childName = document.getString("name");
                        if(childName != null && !childName.isEmpty()){
                            textViewWelcome.setText(getString(R.string.text_welcome, childName.trim()));
                        }
                    }
                }else {
                    // La tarea de Firestore falló
                    Log.w(TAG, "Error al obtener datos de Firestore: ", task.getException());
                    textViewWelcome.setText("¡Hola, " + currentUser.getEmail() + "!");
                }
            }
        });
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}

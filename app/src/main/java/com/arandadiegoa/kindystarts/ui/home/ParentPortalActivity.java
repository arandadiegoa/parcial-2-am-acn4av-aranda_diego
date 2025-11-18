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
        cardAttendance.setOnClickListener(v ->
                        Toast.makeText(this, "Abriendo Confirmar Asistencia", Toast.LENGTH_SHORT).show());
        
        cardMessage.setOnClickListener(v ->
                        Toast.makeText(this, "Abriendo Dejar un mensaje", Toast.LENGTH_SHORT).show());
        cardSurvey.setOnClickListener(v ->
                Toast.makeText(this, "Abriendo Encuesta", Toast.LENGTH_SHORT).show());
        cardMoments.setOnClickListener(v ->
                Toast.makeText(this, "Abiendo Momentos", Toast.LENGTH_SHORT).show());
        cardDocuments.setOnClickListener(v ->
                Toast.makeText(this, "Abriendo Adjunter documentos", Toast.LENGTH_SHORT).show());
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
                        String childName = document.getString("childName");
                        if(childName != null && !childName.isEmpty()){
                            textViewWelcome.setText(getString(R.string.text_welcome, childName));
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

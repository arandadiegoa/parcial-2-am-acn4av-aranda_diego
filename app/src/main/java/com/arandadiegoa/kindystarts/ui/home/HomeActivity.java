package com.arandadiegoa.kindystarts.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.arandadiegoa.kindystarts.R;
import com.arandadiegoa.kindystarts.ui.auth.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

    //Vistas
    private TextView textViewWelcome;
    private Button buttonLogout;
    private ProgressBar progressBar;
    private ScrollView contentScrollView;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Iniciar firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //Enlazar vistas
        textViewWelcome = findViewById(R.id.textViewWelcome);
        buttonLogout = findViewById(R.id.buttonLogout);
        progressBar = findViewById(R.id.progressBarHome);
        contentScrollView = findViewById(R.id.contentScrollView);

        //Estado inicial: Ocultar contenido, mostrar cargando
        contentScrollView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        //Obtener el usuario actual
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
           goToLogin();
           return;
        }

        //Logica Bienvenida Firestore

        //Obtener el id actual
        String uid = currentUser.getUid();

        //Referencia
        DocumentReference userDocRef = db.collection("users").document(uid);

        userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                //Mostrar el contenido y ocultar el loading
                progressBar.setVisibility(View.GONE);
                contentScrollView.setVisibility(View.VISIBLE);

                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document != null && document.exists()){
                        //obtener el nombre
                        String childName = document.getString("childName");


                        if(childName != null && !childName.isEmpty()) {
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

            //Button logout
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //desloguear Firebase
                mAuth.signOut();

                //Mostrar mensaje
                Toast.makeText(HomeActivity.this, "Sesión cerrada.", Toast.LENGTH_SHORT).show();

                //Volver a la pantalla login
                goToLogin();
            }
        });
    }

    private void goToLogin() {
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

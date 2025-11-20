package com.arandadiegoa.kindystarts.ui.home;

import android.content.Context;
import android.graphics.Color;
import android.inputmethodservice.InputMethodService;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.arandadiegoa.kindystarts.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SurveysActivity  extends AppCompatActivity {

    //Vistas
    private CardView cardVeryGood, cardGood, cardRegular;
    private LinearLayout optionCheek, optionAlert, optionCross;
    private EditText editTextFreeText;
    private Button buttonSubmit;
    private ProgressBar progressBar;

    //Datos
    private String answerExperience = null;
    private String answerClarity = null;

    //Colores
    private int colorSelected = Color.parseColor("#FFF3E0");
    private int colorCardUnselected = Color.TRANSPARENT;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surveys);

        //Iniciar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //Enlazar Vistas
        cardVeryGood = findViewById(R.id.cardVeryGood);
        cardGood = findViewById(R.id.cardGood);
        cardRegular = findViewById(R.id.cardRegular);

        optionCheek = findViewById(R.id.optionCheck);
        optionAlert = findViewById(R.id.optionAlert);
        optionCross = findViewById(R.id.optionCross);

        editTextFreeText = findViewById(R.id.editTextFreeText);

        buttonSubmit = findViewById(R.id.buttonSubmitSurvey);
        progressBar = findViewById(R.id.progressBarSurvey);

        //Color original
        colorCardUnselected = cardVeryGood.getCardBackgroundColor().getDefaultColor();

        //Listener Preg 1 (Experiencia)
        cardVeryGood.setOnClickListener(v -> selectExperienceOption("Muy buena", cardVeryGood));
        cardGood.setOnClickListener(v -> selectExperienceOption("Buena", cardGood));
        cardRegular.setOnClickListener(v -> selectExperienceOption("Regular", cardRegular));

        //Listener Preg 2 (Claridad)
        optionCheek.setOnClickListener(v -> selectClarityOption("Clara", optionCheek));
        optionAlert.setOnClickListener(v -> selectClarityOption("Confusa", optionAlert));
        optionCross.setOnClickListener(v -> selectClarityOption("Insuficiente", optionCross));

        //Enviar respuesta
        buttonSubmit.setOnClickListener(v -> submitSurvey());
    }

    // Lógica para la Pregunta 1
    private void selectExperienceOption(String answer, CardView selectCard) {
    this.answerExperience = answer; //Guarda la respuesta

    //Resetear visualmente todas las opciones
        cardVeryGood.setCardBackgroundColor(colorCardUnselected);
        cardGood.setCardBackgroundColor(colorCardUnselected);
        cardRegular.setCardBackgroundColor(colorCardUnselected);

        //Resaltar opcion
        selectCard.setCardBackgroundColor(colorSelected);

        //Darle elevación(efect sombra)
        cardVeryGood.setCardElevation(1f);
        cardGood.setCardElevation(1f);
        cardRegular.setCardElevation(1f);
        selectCard.setCardElevation(8f);
    }

    //Logica para la pregunta 2
    private void selectClarityOption(String answer, LinearLayout selectedLayout) {
        this.answerClarity = answer; //Guarda la respuesta

        //Resetear visualmente las opciones
        optionCheek.setBackgroundResource(R.drawable.bg_survey_option);
        optionAlert.setBackgroundResource(R.drawable.bg_survey_option);
        optionCross.setBackgroundResource(R.drawable.bg_survey_option);

        //Resaltar lo seleccionado
        selectedLayout.setBackgroundResource(R.drawable.bg_survey_option_selected);
    }

    private void submitSurvey() {
        String freeText = editTextFreeText.getText().toString().trim();

        //Validaciones
        if(answerExperience == null || answerClarity == null) {
            Toast.makeText(this, "Por favor responda la pregunta", Toast.LENGTH_SHORT).show();
            return;
        }

        //Mostrar carga
        progressBar.setVisibility(View.VISIBLE);
        buttonSubmit.setEnabled(false);

        //Datos para Firebase
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = (user != null) ? user.getUid() : "desconocido";
        String userEmail = (user != null) ? user.getEmail() : "desconocido";

        //Mapeo de data
        Map<String, Object> surveyData = new HashMap<>();
        surveyData.put("userId", userId);
        surveyData.put("UserEmail", userEmail);
        surveyData.put("answer1_experience", answerExperience);
        surveyData.put("answer2.clarity", (answerClarity != null) ? answerClarity : "No respondido");
        surveyData.put("comments", freeText);
        surveyData.put("timestamp", com.google.firebase.Timestamp.now());

        //Guardar collecion de surveys de Firestore
        db.collection("surveys")
                .add(surveyData)
                .addOnCompleteListener(documentReference -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Gracias por tu opinión", Toast.LENGTH_SHORT).show();
                    finish(); //termina y vuelve a la Home
                })
                .addOnFailureListener(e ->{
                    progressBar.setVisibility(View.GONE);
                    buttonSubmit.setEnabled(true);
                    Toast.makeText(SurveysActivity.this, "Error al enviar" +
                             e.getMessage(), Toast.LENGTH_SHORT).show();
                });


    }
}

package com.arandadiegoa.kindystarts.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.arandadiegoa.kindystarts.R;
import com.arandadiegoa.kindystarts.ui.home.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    //Vistas
    private TextInputEditText editTextEmail, editTextPassword;
    private TextInputLayout textInputLayoutEmail, textInputLayoutPass;
    private Button buttonLogin;
    private TextView textViewGoToRegister;
    private ProgressBar progressBar;

    //Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Inicio firebase
        mAuth = FirebaseAuth.getInstance();

        //Get Vistas
        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPass = findViewById(R.id.textInputLayoutPass);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewGoToRegister = findViewById(R.id.textViewGoToRegister);
        progressBar = findViewById(R.id.progressBar);

        //Listener button login
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        //Listener Registrarse
        textViewGoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inicia la Actividad de Registro.
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        //comprueba si el user esta logueado
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d(TAG, "Usuario ya logueado: " + currentUser.getEmail());
            navigateToMain();
        }
    }

    private void loginUser() {
        String email = Objects.requireNonNull(editTextEmail.getText()).toString().trim();
        String password = Objects.requireNonNull(editTextPassword.getText()).toString().trim();

        //Limpiar errores anteriores
        textInputLayoutEmail.setError(null);
        textInputLayoutPass.setError(null);

        boolean isValid = true;

        //Validaciones
        if (TextUtils.isEmpty(email)) {
            textInputLayoutEmail.setError("Email es requerido.");
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {
            textInputLayoutPass.setError("Contraseña es requerida.");
            isValid = false;
        }

        if(!isValid) {
            if(TextUtils.isEmpty(email)){
                editTextEmail.requestFocus();
            } else if (TextUtils.isEmpty(password)) {
                editTextPassword.requestFocus();
            }
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        buttonLogin.setEnabled(false);

        //Login con Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        buttonLogin.setEnabled(true);

                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail: success");
                            Toast.makeText(LoginActivity.this, "Login exitoso.", Toast.LENGTH_SHORT).show();
                            navigateToMain();
                        } else {
                            Log.w(TAG, "signInWithEmail: failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}

package com.arandadiegoa.kindystarts.ui.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import com.arandadiegoa.kindystarts.R
import com.arandadiegoa.kindystarts.ui.base.BaseActivity
import com.arandadiegoa.kindystarts.ui.home.HomeActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore // <-- AÑADIDO
import com.google.firebase.firestore.firestore

class RegisterActivity : BaseActivity(), PhotoPickerListener {

    companion object {
        const val extraChildName= "child_Name"
        private const val TAG = "RegisterActivity" //Logs
    }

    // Declara el 'ayudante' que usaremos para pedir fotos.
    private lateinit var photoPickerHelper: PhotoPickerHelper
    private var selectedImageUri: Uri? = null

    //Firebase
    private lateinit var mAuth: FirebaseAuth
    private lateinit var progressBar: ProgressBar
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Aplicamos el manejo de teclado a la vista raíz del layout
        val rootView = findViewById<View>(R.id.register_layout)

        //evitar que el teclado se abra automáticamente en el calendario
        rootView.post {
            rootView.requestFocus()
        }

        //Iniciar Firebase
        mAuth = FirebaseAuth.getInstance()
        db = Firebase.firestore

        // La promesa 'lateinit' se cumple
        photoPickerHelper = PhotoPickerHelper(this, this)

        // Referencias
        val parentName = findViewById<TextInputEditText>(R.id.editTextParentName)
        val email = findViewById<TextInputEditText>(R.id.editTextEmail)
        val phone = findViewById<TextInputEditText>(R.id.editTextPhoneNumber)
        val password = findViewById<TextInputEditText>(R.id.editTextPassword)
        val childName = findViewById<TextInputEditText>(R.id.editTextChildName)
        val birthDate = findViewById<TextInputEditText>(R.id.editTextDateOfBirth)
        val uploadPhoto = findViewById<TextInputEditText>(R.id.edit_text_upload_photo)
        val submitButton = findViewById<Button>(R.id.buttonSubmitRegister)

        //ProgressBar
        progressBar = findViewById(R.id.progressBarRegister)

        //Calendar
        setupDatePicker(birthDate)


        //abre la galería de imágenes
        uploadPhoto.setOnClickListener {
            photoPickerHelper.launchImagePicker()
        }


        /*Options Halls */

        //obtener las salas
        val halls = resources.getStringArray(R.array.option_hall)

        // Creamos el adaptador que une los datos con la vista
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, halls)

        //Obtener la referencia
        val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.autoCompleteHall)

        //Mostrar las opciones
        autoCompleteTextView.setAdapter(adapter)


        //validaciones al seleccionar registrar
        submitButton.setOnClickListener {
            val strParent = parentName.text.toString()
            val strEmail = email.text.toString()
            val strPhone = phone.text.toString()
            val strPassword = password.text.toString()
            val strName = childName.text.toString()
            val strBirthDate = birthDate.text.toString()
            val strHall = autoCompleteTextView.text.toString().trim()

            if (strParent.isBlank() || strEmail.isBlank() || strPhone.isBlank() ||
                strPassword.isBlank() || strName.isBlank() || strBirthDate.isBlank() ||
                strHall.isBlank()

                ) {
                if(strHall.isBlank()){
                    Toast.makeText(this, "Por favor seleccioná una sala", Toast.LENGTH_SHORT).show()
                    autoCompleteTextView.requestFocus()
                }else {
                    Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT)
                        .show()
                }
                return@setOnClickListener
                //Validacion de Firebase
            }
            if (strPassword.length < 4) {
                Toast.makeText(this, "La contraseña debe tener al menos 4 caracteres", Toast.LENGTH_SHORT).show()
                password.requestFocus() //foco en el campo
                return@setOnClickListener
            }
            //Logica de firebase

                progressBar.visibility = View.VISIBLE
                submitButton.isEnabled = false

                //Creación usario Firebase
                mAuth.createUserWithEmailAndPassword(strEmail, strPassword)
                    .addOnCompleteListener(this) { task ->
                        if(task.isSuccessful) {
                            Log.d(TAG, "createUserWithEmail:success")

                            val user = mAuth.currentUser
                            val uid = user?.uid

                            if(uid != null){
                                //mapear los datos
                                val userMap = hashMapOf(
                                    "parentName" to strParent,
                                    "email" to strEmail,
                                    "phone" to strPhone,
                                    "name" to strName,
                                    "birthDate" to strBirthDate,
                                    "hall" to strHall,
                                    "uid" to uid,
                                    "role" to "family"
                                )
                                //Guardar el mapa en la colexion users
                                db.collection("users").document(uid)
                                    .set(userMap)
                                    .addOnCompleteListener {
                                        Log.d(TAG, "DocumentSnapshot, successfully written!")
                                        progressBar.visibility = View.GONE
                                        submitButton.isEnabled = true
                                        Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this, HomeActivity::class.java)
                                        intent.putExtra(extraChildName, strName)
                                        startActivity(intent)
                                        finish()
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w(TAG, "Error written document", e)
                                        progressBar.visibility = View.GONE
                                        submitButton.isEnabled = true
                                        Toast.makeText(this, "Error al guardar datos: ${e.message}",
                                            Toast.LENGTH_SHORT).show()
                                    }
                            }


                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            progressBar.visibility = View.GONE
                            submitButton.isEnabled = true
                            Toast.makeText(this, "Error en el registro: ${task.exception?.message}",
                                Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }

    //Se recibe la foto
    override fun onPhotoSelected(uri: Uri) {
        selectedImageUri = uri //variable recibe su valor cuando llega la foto.

        val uploadEditText = findViewById<TextInputEditText>(R.id.edit_text_upload_photo)
        uploadEditText.setText(getString(R.string.text_photo_select)) //feedback al usuario
    }

}


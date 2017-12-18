package com.example.adri9ps.quicktrade;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.adri9ps.quicktrade.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText nombreU;
    private EditText apellidosU;
    private EditText correoU;
    private EditText direccionU;
    private EditText usuarioU;
    private Button btnNuevoUsuario;
    private Button btnModificar;
    private ListView lv;
    ArrayList<String> listadoUsuarios;

    DatabaseReference bbdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nombreU = (EditText) findViewById(R.id.editTextNombreUsuario);
        apellidosU = (EditText) findViewById(R.id.editTextApellidosUsuario);
        correoU = (EditText) findViewById(R.id.editTextCorreoUsuario);
        usuarioU = (EditText) findViewById(R.id.editUsuario);
        direccionU = (EditText) findViewById(R.id.editTextDireccionUsuario);
        btnNuevoUsuario = (Button) findViewById(R.id.btnNuevoUsuario);
        btnModificar = (Button) findViewById(R.id.btnModificar);
        lv = (ListView) findViewById(R.id.listView);

        bbdd = FirebaseDatabase.getInstance().getReference(getString(R.string.nodo_usuarios));
        bbdd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayAdapter<String> adaptador;
                ArrayList<String> listado = new ArrayList<String>();
                listadoUsuarios = new ArrayList<String>();

                for (DataSnapshot datasnapshot : dataSnapshot.getChildren()) {
                    Usuario usuario = datasnapshot.getValue(Usuario.class);

                    String nombre = usuario.getNombre();
                    String usu = usuario.getUsuario();
                    listado.add(nombre);
                    listadoUsuarios.add(usu);
                }
                adaptador = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, listado);
                lv.setAdapter(adaptador);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnNuevoUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nombreU.getText().toString().isEmpty() || apellidosU.getText().toString().isEmpty() ||
                        correoU.getText().toString().isEmpty() || direccionU.getText().toString().isEmpty()
                        || usuarioU.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Faltan datos por rellenar", Toast.LENGTH_SHORT).show();
                } else {
                    boolean valido = true;

                    for (int i = 0; i < listadoUsuarios.size(); i++) {
                        if (usuarioU.getText().toString().equals(listadoUsuarios.get(i))) {
                            Toast.makeText(MainActivity.this, "El usuario ya existe", Toast.LENGTH_SHORT).show();
                            valido = false;
                        }
                    }
                    if (valido) {
                        String clave = bbdd.push().getKey();
                        Usuario u = new Usuario(nombreU.getText().toString(), apellidosU.getText().toString(), correoU.getText().toString(), direccionU.getText().toString(), usuarioU.getText().toString());
                        bbdd.child(clave).setValue(u);

                        Toast.makeText(MainActivity.this, "Usuario añadido", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        btnModificar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                String usuario = usuarioU.getText().toString();

                if (!usuario.isEmpty()) {
                    Query q = bbdd.orderByChild(getString(R.string.campo_usuario)).equalTo(usuario);

                    q.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for (DataSnapshot datasnapshot : dataSnapshot.getChildren()) {
                                String clave = datasnapshot.getKey();

                                if (!nombreU.getText().toString().isEmpty()) {
                                    bbdd.child(clave).child(getString(R.string.campo_nombre)).setValue(nombreU.getText().toString());
                                }
                                if (!apellidosU.getText().toString().isEmpty()) {
                                    bbdd.child(clave).child(getString(R.string.campo_apellidos)).setValue(apellidosU.getText().toString());
                                }
                                if (!direccionU.getText().toString().isEmpty()) {
                                    bbdd.child(clave).child(getString(R.string.campo_direccion)).setValue(direccionU.getText().toString());
                                }
                                if (!correoU.getText().toString().isEmpty()) {
                                    bbdd.child(clave).child(getString(R.string.campo_correo)).setValue(correoU.getText().toString());
                                }


                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    Toast.makeText(MainActivity.this, "Los datos se han modificado con éxito", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}

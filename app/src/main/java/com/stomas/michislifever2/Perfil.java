package com.stomas.michislifever2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Perfil extends AppCompatActivity {

    Spinner spSpinner;
    String[] categorias = new String[]{"Juguetes", "Alimentación", "Higiene"};

    EditText edtNom, edtDes, edtCost, edtIdProd;
    ListView lista;

    LinearLayout lnCarga, lnFormulario;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        CargarListaFireStore();
        db = FirebaseFirestore.getInstance();

        edtIdProd = findViewById(R.id.edtIdProd);
        edtNom = findViewById(R.id.edtNom);
        edtDes = findViewById(R.id.edtDes);
        edtCost = findViewById(R.id.edtCost);
        spSpinner = findViewById(R.id.spSpinner);
        lista = findViewById(R.id.lstLista);

        //Pantalla de carga
        lnCarga = findViewById(R.id.lnCarga);
        lnFormulario = findViewById(R.id.lnFormulario);

        //AsyncTask
        MyAsyncTask asyncTask = new MyAsyncTask();
        asyncTask.execute();

        //Poblar Spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categorias);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSpinner.setAdapter(spinnerAdapter);

        //Cargar la Lista
        //CargarLista();
    }

    public void CargarListaFireStore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("productos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> listaProductos = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String linea = "||" + document.getString("IdProducto") + "||" +
                                        document.getString("nombre") + "||" +
                                        document.getString("descripcion") + "||" +
                                        document.getString("costo") + "||" +
                                        document.getString("categoria");
                                listaProductos.add(linea);
                            }
                            ArrayAdapter<String> adaptador = new ArrayAdapter<>(Perfil.this,
                                    android.R.layout.simple_list_item_1, listaProductos);
                            lista.setAdapter(adaptador);
                        } else {
                            Log.e("TAG", "Error al obtener datos de Firestore", task.getException());
                        }
                    }
                });
    }

    public void onClickAgregar(View view) {
        String IdProducto = edtIdProd.getText().toString();
        String nombre = edtNom.getText().toString();
        String descripcion = edtDes.getText().toString();
        String costo = edtCost.getText().toString();
        String categoria = spSpinner.getSelectedItem().toString();

        Map<String, Object> producto = new HashMap<>();
        producto.put("IdProducto", IdProducto);
        producto.put("nombre", nombre);
        producto.put("descripcion", descripcion);
        producto.put("costo", costo);
        producto.put("categoria", categoria);

        db.collection("productos")
                .document(IdProducto)
                .set(producto)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Perfil.this, "Datos enviados a FireStore correctamente", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Perfil.this, "Error al enviar datos a Firestore" + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        /* Codigo anterior con DataHelper

        DataHelper dh = new DataHelper(this, "producto.db", null, 1);
        SQLiteDatabase bd = dh.getWritableDatabase();
        ContentValues reg = new ContentValues();
        reg.put("nom", edtNom.getText().toString());
        reg.put("des", edtDes.getText().toString());
        reg.put("cost", edtCost.getText().toString());
        reg.put("cat", spSpinner.getSelectedItem().toString());
        long resp = bd.insert("producto", null, reg);
        bd.close();
        if (resp == -1){
            Toast.makeText(this,"No se pudo Ingresar",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this,"Dato Ingresado Correctamente",Toast.LENGTH_LONG).show();
        }
        CargarLista();*/
        Limpiar();
    }

    public void CargarLista(View view) {
        CargarListaFireStore();
    }

    /*public void onClickModificar(View view){
        DataHelper dh = new DataHelper(this, "producto.db", null, 1);
        SQLiteDatabase bd = dh.getWritableDatabase();
        ContentValues reg = new ContentValues();
        reg.put("nom", edtNom.getText().toString());
        reg.put("des", edtDes.getText().toString());
        reg.put("cost", edtCost.getText().toString());
        reg.put("cat", spSpinner.getSelectedItem().toString());
        long resp  = bd.update("producto", reg, "idProd=?", new String[]
                {edtIdProd.getText().toString()});
        bd.close();
        if(resp == -1){
            Toast.makeText(this, "No se pudo Modificar",
                    Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Dato Modificado",
                    Toast.LENGTH_LONG).show();
        }
        CargarLista();
        Limpiar();
    }

    public void onClickEliminar(View view){
        DataHelper dh = new DataHelper(this, "producto.db",  null, 1);
        SQLiteDatabase bd = dh.getWritableDatabase();
        String bIdProd = edtIdProd.getText().toString();
        long resp = bd.delete("producto", "idProd="+ bIdProd, null);
        bd.close();
        if(resp ==-1){
            Toast.makeText(this, "No se pudo Eliminar",
                    Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "Dato Eliminado",
                    Toast.LENGTH_LONG).show();
        }
        Limpiar();
        CargarLista();
    }*/

    public void Limpiar(){
        edtIdProd.setText("");
        edtDes.setText("");
        edtNom.setText("");
        edtCost.setText("");
    }

    public class MyAsyncTask extends AsyncTask<Void, Void, String>{
        @Override
        protected String doInBackground(Void... voids){
            try{
                Thread.sleep(3000); //Duración de la pantalla de carga.
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            return "Bienvenido";
        }
        @Override
        protected void onPostExecute(String result){
            //Desaparecer pantalla de carga
            lnCarga.setVisibility(lnCarga.INVISIBLE);
            //Aparecer formulario y lista
            lnFormulario.setVisibility(lnFormulario.VISIBLE);
            lista.setVisibility(lista.VISIBLE);
        }
    }
}

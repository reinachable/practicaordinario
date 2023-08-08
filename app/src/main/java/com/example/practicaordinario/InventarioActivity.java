package com.example.practicaordinario;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InventarioActivity extends AppCompatActivity {

    private OrdinarioBDHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario);

        dbHelper = new OrdinarioBDHelper(this);

        Button btnInsertar = findViewById(R.id.btnInsertarProducto);
        Button btnEliminar = findViewById(R.id.btnEliminarProducto);
        Button btnActualizar = findViewById(R.id.btnActualizarProducto);

        btnInsertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoInsertar();
            }
        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoEliminar();
            }
        });

        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoActualizar();
            }
        });
    }

    private void mostrarDialogoInsertar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Insertar Producto");

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_insertar_producto, null);
        builder.setView(dialogView);

        final EditText etNombre = dialogView.findViewById(R.id.etNombre);
        final EditText etPrecio = dialogView.findViewById(R.id.etPrecio);
        final EditText etCantidad = dialogView.findViewById(R.id.etCantidad);
        // Agregar más campos de acuerdo a tus necesidades

        builder.setPositiveButton("Insertar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nombre = etNombre.getText().toString();
                String precioStr = etPrecio.getText().toString();
                String cantidadStr = etCantidad.getText().toString();
                // Obtener más valores de campos

                if (!TextUtils.isEmpty(nombre) && !TextUtils.isEmpty(precioStr) && !TextUtils.isEmpty(cantidadStr)) {
                    double precio = Double.parseDouble(precioStr);
                    int cantidad = Integer.parseInt(cantidadStr);

                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("nombre", nombre);
                    values.put("precio", precio);
                    values.put("cantidad", cantidad);
                    values.put("fecha", obtenerFechaActual());

                    long newRowId = db.insert("productos", null, values);

                    if (newRowId != -1) {
                        // Insertado exitoso
                        // Actualizar la lista de productos o realizar otra acción necesaria
                    } else {
                        // Error al insertar
                    }
                }
            }
        });

        builder.setNegativeButton("Cancelar", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void mostrarDialogoEliminar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Eliminar Producto");
        final EditText etIdProducto = new EditText(this);
        etIdProducto.setHint("ID del Producto");
        builder.setView(etIdProducto);

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String idProductoStr = etIdProducto.getText().toString();
                if (!TextUtils.isEmpty(idProductoStr)) {
                    int idProducto = Integer.parseInt(idProductoStr);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    String selection = "id = ?";
                    String[] selectionArgs = { String.valueOf(idProducto) };
                    int deletedRows = db.delete("productos", selection, selectionArgs);

                    if (deletedRows > 0) {
                        // Eliminación exitosa
                        // Actualizar la lista de productos o realizar otra acción necesaria
                    } else {
                        // No se encontró el producto
                    }
                }
            }
        });

        builder.setNegativeButton("Cancelar", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void mostrarDialogoActualizar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Actualizar Producto");

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_actualizar_producto, null);
        builder.setView(dialogView);

        final EditText etIdActualizar = dialogView.findViewById(R.id.etIdActualizar);
        final EditText etPrecioActualizar = dialogView.findViewById(R.id.etPrecioActualizar);
        final EditText etCantidadActualizar = dialogView.findViewById(R.id.etCantidadActualizar);

        builder.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String idProductoStr = etIdActualizar.getText().toString();
                String precioStr = etPrecioActualizar.getText().toString();
                String cantidadStr = etCantidadActualizar.getText().toString();

                if (!TextUtils.isEmpty(idProductoStr) && !TextUtils.isEmpty(precioStr) && !TextUtils.isEmpty(cantidadStr)) {
                    int idProducto = Integer.parseInt(idProductoStr);
                    double precio = Double.parseDouble(precioStr);
                    int cantidad = Integer.parseInt(cantidadStr);

                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("precio", precio);
                    values.put("cantidad", cantidad);

                    String selection = "id = ?";
                    String[] selectionArgs = { String.valueOf(idProducto) };
                    int updatedRows = db.update("productos", values, selection, selectionArgs);

                    if (updatedRows > 0) {
                        // Actualización exitosa
                        // Actualizar la lista de productos o realizar otra acción necesaria
                    } else {
                        // No se encontró el producto
                    }
                }
            }
        });

        builder.setNegativeButton("Cancelar", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private String obtenerFechaActual() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(new Date());
    }
}

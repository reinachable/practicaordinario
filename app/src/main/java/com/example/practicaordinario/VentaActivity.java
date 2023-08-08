package com.example.practicaordinario;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class VentaActivity extends AppCompatActivity {

    private OrdinarioBDHelper dbHelper;
    private List<Producto> listaProductos;
    private ArrayAdapter<Producto> adapter;
    private double totalVenta = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venta);

        dbHelper = new OrdinarioBDHelper(this);
        listaProductos = new ArrayList<>();

        ListView listViewProductos = findViewById(R.id.listViewProductos);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaProductos);
        listViewProductos.setAdapter(adapter);

        listViewProductos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mostrarDialogoVenta(listaProductos.get(position));
            }
        });

        findViewById(R.id.btnFinalizarVenta).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarResumenVenta();
            }
        });

        cargarProductosDesdeBD();
    }

    private void cargarProductosDesdeBD() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("productos", null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
            double precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"));
            Producto producto = new Producto(id, nombre, precio);
            listaProductos.add(producto);
        }

        cursor.close();
        adapter.notifyDataSetChanged();
    }

    private void mostrarDialogoVenta(final Producto producto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Venta de Producto: " + producto.getNombre());

        View view = getLayoutInflater().inflate(R.layout.dialog_venta, null);
        final EditText etCantidad = view.findViewById(R.id.etCantidad);
        builder.setView(view);

        builder.setPositiveButton("Vender", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String cantidadStr = etCantidad.getText().toString();
                if (!cantidadStr.isEmpty()) {
                    int cantidad = Integer.parseInt(cantidadStr);
                    double importe = producto.getPrecio() * cantidad;
                    totalVenta += importe;

                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("id_producto", producto.getId());
                    values.put("cantidad", cantidad);
                    values.put("precio", producto.getPrecio());
                    values.put("importe", importe);

                    long newRowId = db.insert("venta", null, values);
                    if (newRowId != -1) {
                        // Venta registrada exitosamente
                    } else {
                        // Error al registrar la venta
                    }
                }
            }
        });

        builder.setNegativeButton("Cancelar", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void mostrarResumenVenta() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Resumen de Venta");

        StringBuilder resumenBuilder = new StringBuilder();
        for (Producto producto : listaProductos) {
            resumenBuilder.append(producto.getNombre()).append(": $").append(producto.getPrecio()).append("\n");
        }
        resumenBuilder.append("Total de la Venta: $").append(totalVenta);

        builder.setMessage(resumenBuilder.toString());

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Limpiar la lista de productos vendidos y volver a la pantalla anterior
                listaProductos.clear();
                adapter.notifyDataSetChanged();
                totalVenta = 0;
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}



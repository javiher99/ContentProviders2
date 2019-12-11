package com.rut.contentproviders2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainProviders2 extends AppCompatActivity implements View.OnClickListener {
    protected final int SOLICITUD_PERMISO_CONTACTOS=0;
    private static final String A1 ="ABC" ;
    ArrayList<ElementoLista> datos_contactos = new ArrayList<ElementoLista>();

    // Instancias
    Button btShow, btFilter;
    EditText etNombre,etNumero;
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lo_main_providers2);

        // Botones
        btShow = findViewById(R.id.btMostrar);
        btFilter = findViewById(R.id.btFilter);
        // Lista
        list = findViewById(R.id.milista);
        // EditText
        etNombre = findViewById(R.id.etNombre);
        etNumero = findViewById(R.id.etNumero);

        btShow.setOnClickListener(this);
        btFilter.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btMostrar:
                SolicitarPermisos();
                break;
            case R.id.btFilter:
                lanzarActividad();
                break;
            default:
                break;
        }
    }



    public void SolicitarPermisos (){

        Boolean permiso_concedido=false;
        int tengo_permiso = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);

        // Here, thisActivity is the current activity
        if (tengo_permiso!= PackageManager.PERMISSION_GRANTED  ) {

            // Should we show an explanation?
            Boolean deboexplicar = ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS);
            if (deboexplicar) {
                Toast.makeText(this, "Esta APP NO TIENE PERMISOS para leer contactos. Necesitas conceder permisos!!! ", Toast.LENGTH_LONG);
            } else {
                // Solicito el permiso al usuario
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        SOLICITUD_PERMISO_CONTACTOS);
            }
        } else {
            MostrarAgenda();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case SOLICITUD_PERMISO_CONTACTOS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //Permiso concedido
                    MostrarAgenda();

                } else {
                    Toast.makeText(this,"Permiso no concedido", Toast.LENGTH_LONG);
                    finish();
                }

                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void MostrarAgenda(){
        String ident, nombre, tlfn, tipo;
        ElementoLista contacto;
        ElementoLista filtro;

        String name = etNombre.getText().toString();
        String number = etNumero.getText().toString();

        /*  contactsCursor = getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,  // The content URI of the words table
                mProjection,                       // The columns to return for each row
                mSelectionClause                   // Either null. Condición del WHERE
                mSelectionArgs,                    // Either empty, or LOS VALORES DE LA SELECCIÓN
                mSortOrder);                       // The sort order for the returned rows
        */

        // Voy rellenando los datos de la query
        String[] proyeccion = new String[] {ContactsContract.Data._ID,
                ContactsContract.Data.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.TYPE};

        /* Aquí vamos a poner la selección. Vamos a obtener aquellos contactos que sean de tipo teléfono (no email por ejemplo)
         * cuyo teléfono no sea null y cuyo nombre empiece por A
         * */
        String seleccion = ContactsContract.Data.MIMETYPE + "= '" +
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "' AND " +
                ContactsContract.CommonDataKinds.Phone.NUMBER + " IS NOT NULL";

        String orden = ContactsContract.Data.DISPLAY_NAME + " ASC";


        Cursor micursor = getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                proyeccion, seleccion, null, orden);

        if (micursor != null){
            int i=0;
            while (micursor.moveToNext()){
                ident = micursor.getString(0);
                nombre = micursor.getString(1);
                tlfn = micursor.getString(2);
                tipo = micursor.getString(3);
                //texto.append ("Identificador : " + ident + "\nNombre : " + nombre + "\nTeléfono : " + tlfn + "\nTIpo : " + tipo + "\n");

                    Log.v("name", nombre);
                    Log.v("name", tlfn);

                contacto = new ElementoLista(nombre, tlfn) ;
                datos_contactos.add(contacto);

                i++;
            }

            int j = 0;
            while (micursor.moveToNext()){
                ident = micursor.getString(0);
                nombre = micursor.getString(1);
                tlfn = micursor.getString(2);
                tipo = micursor.getString(3);
                //texto.append ("Identificador : " + ident + "\nNombre : " + nombre + "\nTeléfono : " + tlfn + "\nTIpo : " + tipo + "\n");

                Log.v("name", nombre);
                Log.v("name", tlfn);

                if(nombre.contains(name) && tlfn.contains(number)){
                    filtro = new ElementoLista(nombre, tlfn);
                }

                i++;
            }

        } else {
            Toast.makeText(this,"El cursor está vacío", Toast.LENGTH_LONG);
        }

        // Creo el adaptador para el viewholder
        AdaptadorListas adaptador = new AdaptadorListas(this, datos_contactos);
        list.setAdapter(adaptador);

    }

    // Hago la nueva clase ViewHolder
    static class ViewHolder {
        TextView p_nombre;
        TextView p_telefono;
    }

    // Nueva clase AdaptadorListas
    public class AdaptadorListas extends ArrayAdapter<ElementoLista> {

        public AdaptadorListas(Context context, ArrayList<ElementoLista> datos) {
            super(context, R.layout.lo_item_lista, datos);
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            View item_reusado = convertView;
            ViewHolder holder;

            if (item_reusado == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                item_reusado = inflater.inflate(R.layout.lo_item_lista, null);

                holder = new ViewHolder();
                holder.p_nombre = (TextView)item_reusado.findViewById(R.id.nom_contact);
                holder.p_telefono = (TextView)item_reusado.findViewById(R.id.phone_contact);

                item_reusado.setTag(holder);
            }else
            {
                holder = (ViewHolder)item_reusado.getTag();
            }


            holder.p_nombre.setText(datos_contactos.get(position).getNombre());
            holder.p_telefono.setText(datos_contactos.get(position).getTelefono());

            return(item_reusado);
        }
    }


    public void lanzarActividad(){
        Intent i = new Intent(MainProviders2.this, Activity2.class);


    }
}

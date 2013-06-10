package es.upm.dit.adsw.teleconote;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author -- ponga aquí su nombre --
 * @version 20130501
 */
public class DetalleNotaActivity extends Activity {
	private static final String TAG = DetalleNotaActivity.class.getSimpleName();
	private boolean estaCrifrado;
	
	private EditText editTitulo;
	private EditText editContenido;
	private AutoCompleteTextView editCategoria;
	
	private Nota nota;
	private Long posicion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");		
		setContentView(R.layout.detalle_nota);
		estaCrifrado = false;
		editTitulo = (EditText) findViewById(R.id.editText1);
		editContenido = (EditText) findViewById(R.id.editText2);
		editCategoria = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
		Bundle extras = getIntent().getExtras();
		
		final int requestCode = extras.getInt(ListaNotasActivity.REQUEST_CODE);
		if (requestCode == ListaNotasActivity.MODIFICA_NOTA){
			nota = (Nota) extras.getSerializable(Nota.NOTA);
			posicion = extras.getLong(NotaDbAdaptador.COL_ID);
			editTitulo.setText(nota.getTitulo());
			editContenido.setText(nota.getContenido());
			editCategoria.setText(nota.getCategoria());
			estaCrifrado = nota.isCifrado();
		}
		
		
		// Crear o modificar nota
		final Button guardar = (Button) findViewById(R.id.guardar);
		guardar.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				if (requestCode == ListaNotasActivity.MODIFICA_NOTA)
					bundle.putLong(NotaDbAdaptador.COL_ID, posicion);
				Intent miIntent = new Intent();
				String titulo = editTitulo.getText().toString();
				String contenido = editContenido.getText().toString();
				String categoria = editCategoria.getText().toString();
				Nota n = new Nota(titulo, contenido, categoria, estaCrifrado);
				bundle.putSerializable(Nota.NOTA, n);
				miIntent.putExtras(bundle);
				setResult(RESULT_OK, miIntent);
				finish();
			}
		});
		
		// Cifrado
		final Context context = this;
		LayoutInflater inflater = this.getLayoutInflater();
		final View view = inflater.inflate(R.layout.key, null);
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		final Button cifrar = (Button) findViewById(R.id.cifrar);
		cifrar.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				builder.setTitle("Cifrado");
				builder.setView(view);
				builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						EditText keyView = (EditText) view.findViewById(R.id.keyCifrate);
						String key = keyView.getText().toString();
						Cifrador cif = new Cifrador(key);
						if (estaCrifrado) {
							editContenido.setText(cif.descifra(editContenido.getText().toString()));
							estaCrifrado = false;
						}
						else { 
							editContenido.setText(cif.cifra(editContenido.getText().toString()));
							estaCrifrado = true;
						}
						show("Nota cifrada/descifrada correctamente");
					}
					private void show(String text) {
						int duration = Toast.LENGTH_LONG;
						Toast toast = Toast.makeText(context, text, duration);
						toast.show();
					}
				});
				AlertDialog dialog = builder.create();
				dialog.show();
			}
		});
		

		
		// Cancelar la edicion de nota
		final Button cancelar = (Button) findViewById(R.id.Cancelar);
		cancelar.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	// Metodo guardar
	// Metodo cifrar

	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, "onStart");
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, "onPause");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.d(TAG, "onStop");
	}
}

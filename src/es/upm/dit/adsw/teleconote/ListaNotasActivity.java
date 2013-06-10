package es.upm.dit.adsw.teleconote;


import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

/**
 * @author -- Sergio Díaz Gallo --
 * @version 20130501
 */
public class ListaNotasActivity 
  extends ListActivity  {
	
	private final String TAG = ListaNotasActivity.class.getSimpleName();
	public static final String REQUEST_CODE = "requestCode";
	public static final int CREA_NOTA = 0;
	public static final int MODIFICA_NOTA = 1;
	private NotaDbAdaptador bdNotas;
	private Cursor notasCursor;
	private SimpleCursorAdapter curAdapater;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");		
		setContentView(R.layout.lista_notas);
		
		bdNotas = new NotaDbAdaptador(this);
		notasCursor = bdNotas.recuperaTodasLasNotas();
		fillView(notasCursor);		
	}
	
	@SuppressWarnings("deprecation")
	private void fillView(Cursor cursor) {
		startManagingCursor(cursor);

		String[] from = { NotaDbAdaptador.COL_TITULO, NotaDbAdaptador.COL_CATEGORIA };
		int[] to = new int[] { R.id.textView1, R.id.textView2 };
	
		curAdapater= new SimpleCursorAdapter(this, R.layout.simple_list, cursor, from, to);
		setListAdapter(curAdapater);
	}

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
	
	protected void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);
		Cursor c = notasCursor;
		c.moveToPosition(position);
		Nota nota = new Nota(c.getString(c.getColumnIndexOrThrow(NotaDbAdaptador.COL_TITULO)), c.getString(c.getColumnIndexOrThrow(NotaDbAdaptador.COL_CONTENIDO)), c.getString(c.getColumnIndexOrThrow(NotaDbAdaptador.COL_CATEGORIA)), int2boolean(c.getColumnIndexOrThrow(NotaDbAdaptador.COL_CIFRADO)));
		Intent i = new Intent(this, DetalleNotaActivity.class);
		i.putExtra(NotaDbAdaptador.COL_ID, id);
		i.putExtra(Nota.NOTA, nota);
		i.putExtra(REQUEST_CODE, MODIFICA_NOTA);
		startActivityForResult(i, MODIFICA_NOTA);
	}

	private boolean int2boolean(int b){
		if (b==0) return false;
		return true;
	}



	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_lista, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.crear: {
			Intent creacion = new Intent (this, DetalleNotaActivity.class);
			creacion.putExtra(REQUEST_CODE, CREA_NOTA);
			startActivityForResult(creacion, CREA_NOTA);
			return true;
		}
		case R.id.ayuda: {
			Toast.makeText(this, getString(R.string.msg_ayuda), Toast.LENGTH_SHORT).show();
			return true;
		}
		case R.id.borrar: {
			final String text = "¿Deseas borrar todas las nota?";

			final Context context = this;
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("Borrar");
			builder.setMessage(text);
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					bdNotas.borraTodasLasNotas();
					show(text);
				}
				private void show(String text) {
					int duration = Toast.LENGTH_LONG;
					Toast toast = Toast.makeText(context, text, duration);
					toast.show();
				}
			});
			builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {			
				public void onClick(DialogInterface dialog, int id) {					
				}
			});
			AlertDialog dialog = builder.create();
			dialog.show();
		}
		default:
			return false;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_CANCELED) return;
		Bundle extras = data.getExtras();
		if (extras == null) return;
		Nota nota = (Nota) extras.getSerializable(Nota.NOTA);
		switch (requestCode) {
			case CREA_NOTA: {
				bdNotas.creaNota(nota);
				break;
			}
			case MODIFICA_NOTA: {
				long posicion = extras.getLong(NotaDbAdaptador.COL_ID);
				bdNotas.actualizaNota(posicion, nota);
				break;
			}
			default: {
				
			}
		}
	}
}

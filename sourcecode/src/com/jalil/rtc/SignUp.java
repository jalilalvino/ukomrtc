package com.jalil.rtc;

import android.app.Activity;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class SignUp extends Activity {//implements OnClickListener {
	
	private EditText textUser, textNama, textAlamat, textKTP, textHP, textEmail, textPass;
	private Button btnDaftar, btnCancel;
	
	// Progress Dialog
	   private ProgressDialog pDialog;

	// JSON parser class
	   JSONParser jsonParser = new JSONParser();

	   private static final String TAMBAH_DATA = "http://192.168.97.43/rtc/tambah_data.php";
	   private static final String TAG_SUCCESS = "success";
	   
	   

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		
		textUser = (EditText) findViewById(R.id.editUsername);
		textNama = (EditText) findViewById(R.id.editNama);
		textAlamat = (EditText) findViewById(R.id.editAlamat);
		textKTP = (EditText) findViewById(R.id.editKTP);
		textHP = (EditText) findViewById(R.id.editHP);
		textEmail = (EditText) findViewById(R.id.editEmail);
		textPass = (EditText) findViewById(R.id.editPass);
		
		btnDaftar = (Button) findViewById(R.id.btnDaftar);
		btnCancel = (Button) findViewById(R.id.btnKembali);
		
		btnCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(SignUp.this, SignIn.class);
				finish();
			    startActivity(i);
			}
		});
		
		btnDaftar.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			new CreateUser().execute();	
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sign_up, menu);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	class CreateUser extends AsyncTask<String, String, String> {

		boolean failure = false;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(SignUp.this);
			pDialog.setMessage("Creating User...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();

		}
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			int success;
			String username = textUser.getText().toString();
			String nama = textNama.getText().toString();
			String alamat = textAlamat.getText().toString();
			String no_ktp = textKTP.getText().toString();
			String no_hp = textHP.getText().toString();
			String email = textEmail.getText().toString();
			String password = textPass.getText().toString();
			try {
			// Building Parameters
		    List<NameValuePair> param = new ArrayList<NameValuePair>();
		    param.add(new BasicNameValuePair("username", username));
		    param.add(new BasicNameValuePair("nama", nama));
		    param.add(new BasicNameValuePair("alamat", alamat));
		    param.add(new BasicNameValuePair("no_ktp", no_ktp));
		    param.add(new BasicNameValuePair("no_hp", no_hp));
		    param.add(new BasicNameValuePair("email", email));
		    param.add(new BasicNameValuePair("password", password));

		    Log.d("request!", "starting");
		    //Posting user data to script
		    JSONObject json = jsonParser.makeHTTPRequest(
		                 	TAMBAH_DATA, "POST", param);
		    // full json response
		    Log.d("Login attempt", json.toString());

		    // json success tag
		    success = json.getInt(TAG_SUCCESS);

		    if (success == 1) {
		    Log.d("User Created!", json.toString());
		    Intent i = new Intent(SignUp.this, SignIn.class);
		    startActivity(i);
		    return json.getString(TAG_SUCCESS);
		    } else {
		    Log.d("Login Failure!", json.getString(TAG_SUCCESS));
		    return json.getString(TAG_SUCCESS);
		    }
		    
		} catch (JSONException e) {
			e.printStackTrace();
		}
			return null;
	}
	
	protected void onPostExecute(String file_url) {
		pDialog.dismiss();
		if (file_url != null) {
			Toast.makeText(SignUp.this, file_url, Toast.LENGTH_LONG).show();
	}
	}
}
}
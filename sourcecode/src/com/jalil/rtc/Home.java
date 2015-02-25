package com.jalil.rtc;

import java.io.File;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.telephony.SmsManager;

public class Home extends Activity implements LocationListener {
	String[] kejahatan = {
			"Tindak asusila",
			"Perjudian",
			"Perampokan",
			"Penghinaan atau pencemaran nama baik",
			"Pemerasan atau pengancaman",
			"Berita bohong yang menyesatkan",
			"Pemalsuan informasi",
			"Pembunuhan",
			"Pemerkosaan",
			"Pencopetan",
			"Tabrak Lari",
			"Narkoba",
			"Pencurian",
			"Prostitusi"};

	  private TextView uname;
	  private EditText jeniskejahatan, kronologi;
	  private TextView latituteField;
	  private TextView longitudeField;
	  private LocationManager locationManager;
	  private String provider;
	  // Google Map
	  private GoogleMap googleMap;
	  final int POTRET = 0;
	  ImageView imageViewHasil;
	  double latitudeBaru, longitudeBaru;
	  String latitudeX,longitudeX,waktu;
	  
//	  private ProgressDialog pDialog;
	  
	  String email, subject, message, attachmentFile;
      Uri URI = null;
      private static final int PICK_FROM_GALLERY = 101;
      int columnIndex;

		// JSON parser class
		//   JSONParser jsonParser = new JSONParser();

//		   private static final String TAMBAH_DATA = "http://192.168.1.103/rtc/history_kejahatan.php";

//		   private static final String TAG_SUCCESS = "success";
//		   private static final String TAG_MESSAGE = "message";
	  
	/** Called when the activity is first created. */

	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_home);
	    
	    uname = (TextView) findViewById(R.id.uname);

        Intent intent = getIntent();
        String str = intent.getStringExtra("mytext");
        uname.setText(str);
	    
	    jeniskejahatan = (EditText) findViewById(R.id.editJenisKejahatan);
	    jeniskejahatan.getText().toString();
	    kronologi = (EditText) findViewById(R.id.editKronologi);
	    kronologi.getText().toString();
	    
	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
	    		this, android.R.layout.simple_dropdown_item_1line, kejahatan);
	    
	    AutoCompleteTextView editJenisKejahatan = (AutoCompleteTextView)
	    		findViewById(R.id.editJenisKejahatan);
	    editJenisKejahatan.setText("");
	    editJenisKejahatan.setThreshold(1);
	    editJenisKejahatan.setAdapter(adapter);
	    
	    imageViewHasil = (ImageView) findViewById(R.id.imageViewHasil);
        ImageView tombolFoto = (ImageView) findViewById(R.id.buttonFoto);
        tombolFoto.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				File berkas = new File(Environment.getExternalStorageDirectory(),
                        "hasil.jpg");
				Uri UriBerkasKeluaran = Uri.fromFile(berkas);
 
				// Buat Intent
				Intent in = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				in.putExtra(MediaStore.EXTRA_OUTPUT, UriBerkasKeluaran);
 
				// Tampilkan aplikasi kamera bawaan
				startActivityForResult(in, POTRET);
			}
		});
        
        
        
	    try {
            // Loading map
            initilizeMap();
 
        } catch (Exception e) {
            e.printStackTrace();
        }
	  
	    latituteField = (TextView) findViewById(R.id.TextView02);
	    longitudeField = (TextView) findViewById(R.id.TextView04);

	    // Get the location manager
	    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    // Define the criteria how to select the locatioin provider -> use
	    // default
	    Criteria criteria = new Criteria();
	    provider = locationManager.getBestProvider(criteria, true);
	    Location location = locationManager.getLastKnownLocation(provider);

	    // Initialize the location fields
	    if (location != null) {
	      System.out.println("Provider " + provider + " has been selected.");
	      onLocationChanged(location);
	    } else {
	      latituteField.setText("Location not available");
	      longitudeField.setText("Location not available");
	    }
	  }
	  private void initilizeMap() {
	        if (googleMap == null) {
	            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
	 
	            // check if map is created successfully or not
	            if (googleMap == null) {
	                Toast.makeText(getApplicationContext(),
	                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
	                        .show();
	            }
	        }
	    }
	  /* Request updates at startup */
	    
	  @Override
	  protected void onResume() {
	    super.onResume();
	    locationManager.requestLocationUpdates(provider, 400, 1, this);
	    initilizeMap();
	  }

	  /* Remove the locationlistener updates when Activity is paused */
	  @Override
	  protected void onPause() {
	    super.onPause();
	    locationManager.removeUpdates(this);
	  }

	  @Override
	  public void onLocationChanged(Location location) {
	    double lat = (double) (location.getLatitude());
	    double lng = (double) (location.getLongitude());
	    latituteField.setText(String.valueOf(lat));
	    longitudeField.setText(String.valueOf(lng));
	    
	    // create marker
	    MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lng)).title("Anda Disni");
	    
	    // adding marker
	    googleMap.addMarker(marker);
	    
	    // add zoom button
	    googleMap.getUiSettings().setZoomControlsEnabled(true);
	    googleMap.getUiSettings().setZoomGesturesEnabled(true);
	    LatLngBounds bounds = this.googleMap.getProjection().getVisibleRegion().latLngBounds;
	    if(!bounds.contains(new LatLng(location.getLatitude(), location.getLongitude())))
        {
	    googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
	    Toast.makeText(this,  "Location changed!",
	    		Toast.LENGTH_SHORT).show();	    
        }
	    }

	  @Override
	  public void onStatusChanged(String provider, int status, Bundle extras) {
	    // TODO Auto-generated method stub
		  Toast.makeText(this, provider + "'s status changed to "+status +"!",
				  Toast.LENGTH_SHORT).show();
	  }

	  @Override
	  public void onProviderEnabled(String provider) {
	    Toast.makeText(this, "Enabled new provider " + provider,
	        Toast.LENGTH_SHORT).show();

	  }

	  @Override
	  public void onProviderDisabled(String provider) {
	    Toast.makeText(this, "Disabled provider " + provider,
	        Toast.LENGTH_SHORT).show();
	  }	  
	  @Override
	    protected void onActivityResult(int requestCode,
	                                    int resultCode, Intent data) {
	        if (requestCode == POTRET && resultCode == RESULT_OK) {
	            Bitmap bitmap = BitmapFactory.decodeFile(
	            			        Environment.getExternalStorageDirectory() +
	                                "/hasil.jpg");
	            	
	            imageViewHasil.setImageBitmap(bitmap); 
	        }
	        else if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK) {
                /**
                 * Get Path
                 */
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
                cursor.moveToFirst();
                columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                attachmentFile = cursor.getString(columnIndex);
                Log.e("Attachment Path:", attachmentFile);
                URI = Uri.parse("file://" + attachmentFile);
                cursor.close();
         }
	  
	        final EditText nmrpolisi = (EditText) findViewById(R.id.editNomor);
	        final EditText editTextEmail = (EditText) findViewById(R.id.editEmailPolisi);
	        Button tombolKirim = (Button) findViewById(R.id.buttonKirim);
	        latitudeX=Double.toString(latitudeBaru);
	        longitudeX=Double.toString(longitudeBaru);
	        
	        tombolKirim.setOnClickListener(new View.OnClickListener() {
	        	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			    
			    	String nomor = nmrpolisi.getText().toString();
				    String pesan = "Panggilan kriminal oleh: "+uname.getText()+". Di: http://google.com/m/maps?q="+longitudeField.getText()+"%2c"+latituteField.getText()+"";
				    
				    SmsManager sms = SmsManager.getDefault();
				    sms.sendTextMessage(nomor, null, pesan, null, null);
                    email = editTextEmail.getText().toString();
                    subject = "Panggilan kriminal";
                    message = "Panggilan kriminal oleh user : "+uname.getText()+". Dengan kejahatan : "+jeniskejahatan.getText()+" Lokasi di: http://google.com/m/maps?q="+longitudeField.getText()+"%2c"+latituteField.getText()+"" +
                    		"  Kronologi kejahatan: "+kronologi.getText()+"";

                    final Intent emailIntent = new Intent(
                                  android.content.Intent.ACTION_SEND);
                    emailIntent.setType("plain/text");
                    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                                  new String[] { email });
                    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                                  subject);
                    if (URI != null) {
                           emailIntent.putExtra(Intent.EXTRA_STREAM, URI);
                    }
                    emailIntent
                                  .putExtra(android.content.Intent.EXTRA_TEXT, message);
                    startActivity(Intent.createChooser(emailIntent,
                                  "Mengirim email..."));
                    Toast.makeText(getApplicationContext(), 
			    		       "SMS telah dikirim", 
			    		       Toast.LENGTH_SHORT).show(); 

			}
		});
		
	    ImageView buttonAttachment = (ImageView) findViewById(R.id.buttonAttachment);
		buttonAttachment.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openGallery();
			}
		});
}
	  	public void openGallery() {
	  		Intent intent = new Intent();
	  		intent.setType("image/*");
	  		intent.setAction(Intent.ACTION_GET_CONTENT);
	  		intent.putExtra("return-data", true);
	  		startActivityForResult(
                       Intent.createChooser(intent, "Complete action using"),
                       PICK_FROM_GALLERY);

   }
}
package com.myapplicationdev.android.demoshowsms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    TextView tvSms;
    Button btnRetrive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvSms = findViewById(R.id.tv);
        btnRetrive = findViewById(R.id.btnRetrieve);

        btnRetrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int permissionCheck = PermissionChecker.checkSelfPermission(
                        MainActivity.this, Manifest.permission.READ_SMS
                );

                if (permissionCheck != PermissionChecker.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_SMS}, 0);

                    //stops the action from proceeding further as permission not granted yet
                   //If permission is found not granted upon runtime check,
                    //The SMS retrieval code will not run
                    return;
                }

                //Create all message URI
                Uri uri = Uri.parse("content://sms");

                //The columns we want:
                //date - when did the messages took place
                //address - number of the other party
                //body - message content
                //type 1 - received
                //type 2 - sent
                String[] reqCols = new String[]{"date", "address", "body", "type"};

                //Get Content Resolver Object , to query the content provider
                ContentResolver cr = getContentResolver();

                //The filter String
                String filter = "body LIKE ? AND body LIKE ?";
                //The matches for the ?
                String[] filterArgs = {"%late%", "%min%"};
                //Fetch SMS message from Built-in Content Provider
                Cursor cursor = cr.query(uri, reqCols, filter, filterArgs, null);
                String smsBody = "";
                if (cursor.moveToFirst()) {
                    do {
                        long dateInMillis = cursor.getLong(0);
                        String date = (String) DateFormat
                                .format("dd MMM yyyy h:mm:ss aa", dateInMillis);
                        String address = cursor.getString(1);
                        String body = cursor.getString(2);
                        String type = cursor.getString(3);
                        if (type.equalsIgnoreCase("1")) {
                            type = "Inbox: ";
                        } else {
                            type = "Sent: ";
                        }
                        smsBody += type + " " + address + "\n at " + date
                                + "\n\"" + body + "\"\n\n";
                    } while (cursor.moveToNext());
                }
                tvSms.setText(smsBody);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case 0: {
                //If request is cancelled, result arrays are empty.
                if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    //PERMISSION was granted, yay! Do the read sms
                    btnRetrive.performClick();
                } else {
                    //PERMISSION denied.. notify user
                    Toast.makeText(MainActivity.this,
                            "Permission not granted", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}

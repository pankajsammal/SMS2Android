package com.example.sam29.sms2android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.provider.Telephony;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextView textMain;
    private static final int READ_REQUEST_CODE = 42;
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textMain = (TextView) findViewById(R.id.textMain);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                performFileSearch();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */
    public void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("*/*");

        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                Log.i(TAG, "Uri: " + uri.toString());
                dumpImageMetaData(uri);
                try {
                    readTextFromUri(uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //showImage(uri);
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }


    public void dumpImageMetaData(Uri uri) {

        // The query, since it only applies to a single document, will only return
        // one row. There's no need to filter, sort, or select fields, since we want
        // all fields for one document.
        Cursor cursor = this.getContentResolver()
                .query(uri, null, null, null, null, null);

        try {
            // moveToFirst() returns false if the cursor has 0 rows.  Very handy for
            // "if there's anything to look at, look at it" conditionals.
            if (cursor != null && cursor.moveToFirst()) {

                // Note it's called "Display Name".  This is
                // provider-specific, and might not necessarily be the file name.
                String displayName = cursor.getString(
                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                Log.i(TAG, "Display Name: " + displayName);

                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                // If the size is unknown, the value stored is null.  But since an
                // int can't be null in Java, the behavior is implementation-specific,
                // which is just a fancy term for "unpredictable".  So as
                // a rule, check if it's null before assigning to an int.  This will
                // happen often:  The storage API allows for remote files, whose
                // size might not be locally known.
                String size = null;
                if (!cursor.isNull(sizeIndex)) {
                    // Technically the column stores an int, but cursor.getString()
                    // will do the conversion automatically.
                    size = cursor.getString(sizeIndex);
                } else {
                    size = "Unknown";
                }
                Log.i(TAG, "Size: " + size);
            }
        } finally {
            cursor.close();
        }
    }


    private String readTextFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
//        fileInputStream.close();
//        parcelFileDescriptor.close();

        String xmlString = stringBuilder.toString();
        //textMain.setText(xmlString);
        InputStream inputStream2 = getContentResolver().openInputStream(uri);
        List<XMLParser.Entry> doc = null;
        try {
            doc = XMLParser.parse(inputStream2);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        // textMain.setText((CharSequence) doc);
        restoreSMS(doc);
        return xmlString;
    }

    private void restoreSMS(List<XMLParser.Entry> doc) {
//        Context context = this;
//        String defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(context);
//        Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
//        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, context.getPackageName());
//        startActivity(intent);
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "SMS2Android" + "/" + "userData.xml");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        final String xmlFile = "userData";
        String userNAme = "username";
        String password = "password";
        //for (XMLParser.Entry entry : doc) {}


        try {

            //FileOutputStream fos = new  FileOutputStream("userData.xml");
            //FileOutputStream fileos= getApplicationContext().openFileOutput(xmlFile, Context.MODE_PRIVATE);
            XmlSerializer xmlSerializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            xmlSerializer.setOutput(writer);
            xmlSerializer.startDocument("UTF-8", true);
            xmlSerializer.startTag(null, "smses");
            xmlSerializer.attribute(null, "count", String.valueOf(doc.size()));
            for (XMLParser.Entry entry : doc) {
                xmlSerializer.startTag(null, "sms");
                xmlSerializer.attribute(null, "protocol", "0");
                //xmlSerializer.attribute(null, "time", entry.LocalTimestamp);
                long time = Long.parseLong(entry.LocalTimestamp) / 10000 - 11644473600000L;
                if (entry.IsIncoming.equals("true")) {
                    xmlSerializer.attribute(null, "address", entry.Sender);
                    xmlSerializer.attribute(null, "date", String.valueOf(time));
                    xmlSerializer.attribute(null, "type", "1");

                } else {
                    xmlSerializer.attribute(null, "address", entry.Recepients);
                    xmlSerializer.attribute(null, "date", String.valueOf(time));
                    xmlSerializer.attribute(null, "type", "2");

                }
                xmlSerializer.attribute(null, "subject", "null");
                xmlSerializer.attribute(null, "body", entry.Body);
                xmlSerializer.attribute(null, "toa", "null");
                xmlSerializer.attribute(null, "sc_toa", "null");
                xmlSerializer.attribute(null, "service_center", "null");
                xmlSerializer.attribute(null, "read", "1");
                xmlSerializer.attribute(null, "service_center", "null");
                xmlSerializer.attribute(null, "status", "-1");
                xmlSerializer.attribute(null, "locked", "0");
                xmlSerializer.endTag(null, "sms");


            }

            xmlSerializer.endTag(null, "smses");
            xmlSerializer.endDocument();
            xmlSerializer.flush();
            String dataWrite = writer.toString();
            fos.write(dataWrite.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        //Intent intent = new Intent(context, Sms.Intents.ACTION_CHANGE_DEFAULT);
//        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, defaultSmsApp);
//        startActivity(intent);
    }

    //@TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean isDefaultSmsApp(Context context) {
        return context.getPackageName().equals(Telephony.Sms.getDefaultSmsPackage(context));
    }


}

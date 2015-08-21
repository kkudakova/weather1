package com.example.kskhom.weather;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    private int year;
    private int month;
    private int day;

    private TextView tvDisplayDate;

    private TextView temp;
    private TextView humidity;

    int DIALOG_DATE = 1;

    ArrayList<WeatherView> listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvDisplayDate = (TextView) findViewById(R.id.tvDisplayDate);
        setCurrentDateOnView();

        listView = new ArrayList<WeatherView>();

        listView.add((WeatherView) findViewById(R.id.view1));
        listView.add((WeatherView) findViewById(R.id.view2));
        listView.add((WeatherView) findViewById(R.id.view3));
    }

    public void onclick(View view) {
        showDialog(DIALOG_DATE);
    }


    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_DATE) {
            DatePickerDialog tpd = new DatePickerDialog(this, myCallBack, year, month, day);
            return tpd;
        }
        return super.onCreateDialog(id);
    }

    DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int changed_year, int monthOfYear,
                              int dayOfMonth) {
            year = changed_year;
            month = monthOfYear;
            day = dayOfMonth;
            tvDisplayDate.setText(new StringBuilder()
                    // Month is 0 based, just add 1
                    .append(day).append("-").append(month + 1).append("-")
                    .append(year).append(" "));

            WeatherTask task = new WeatherTask();
            task.execute("http://xml.meteoservice.ru/export/gismeteo/point/120.xml");
        }
    };

    // display current date
    public void setCurrentDateOnView() {
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

//        // set current date into textview
        tvDisplayDate.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(day).append("-").append(month + 1).append("-")
                .append(year).append(" "));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private class WeatherTask extends AsyncTask<String, Void, List<Entry>> {

        @Override
        protected List<Entry> doInBackground(String... urls) {
            try {
                return loadXmlFromNetwork(urls[0]);
            } catch (IOException e) {
                Log.d("Error", getResources().getString(R.string.connection_error));
            } catch (XmlPullParserException e) {
                Log.d("Error", getResources().getString(R.string.xml_error));
            }
            return null;
        }

        private List<Entry> loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
            InputStream stream = null;
            // Instantiate the parser
            WeatherXmlParser weatherXmlParser = new WeatherXmlParser();
            List<Entry> entries = null;
            try {
                stream = downloadUrl(urlString);
                entries = weatherXmlParser.parse(stream);
                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }
            return entries;
        }

        protected void onPostExecute(List<Entry> entries) {
            int i = 0;
            boolean found = false;
            // Displays the HTML string in the UI via a WebView
            if (entries != null) {
                for (Entry entry : entries) {
                    if (!found && (entry.day == day) && (entry.month - 1 == month) && (entry.year == year)) {
                        found = true;
                        ImageView mImage = (ImageView) findViewById(R.id.weather_pic);
                        switch (entry.cloudiness) {
                            case "1":
                                mImage.setImageResource(R.drawable.im1);
                                break;
                            case "2":
                                mImage.setImageResource(R.drawable.im2);
                                break;
                            case "3":
                                mImage.setImageResource(R.drawable.im3);
                                break;
                            case "4":
                                mImage.setImageResource(R.drawable.im4);
                                break;
                        }

                        tvDisplayDate.setText(new StringBuilder()
                                .append(entry.day).append("-").append(entry.month).append("-")
                                .append(entry.year).append(" "));
                        temp = (TextView) findViewById(R.id.temp);
                        humidity = (TextView) findViewById(R.id.humidity);
                        temp.setText(entry.temperature + "°C");
                        humidity.setText(entry.relwet + "%");
                    } else {
                        if (i < 3) {
                            listView.get(i).setParams(entry);
                            i++;
                        } else {
                            ImageView mImage = (ImageView) findViewById(R.id.weather_pic);
                            switch (entry.cloudiness) {
                                case "1":
                                    mImage.setImageResource(R.drawable.im1);
                                    break;
                                case "2":
                                    mImage.setImageResource(R.drawable.im2);
                                    break;
                                case "3":
                                    mImage.setImageResource(R.drawable.im3);
                                    break;
                                case "4":
                                    mImage.setImageResource(R.drawable.im4);
                                    break;
                            }

                            temp = (TextView) findViewById(R.id.temp);
                            humidity = (TextView) findViewById(R.id.humidity);
                            temp.setText(entry.temperature + "°C");
                            humidity.setText(entry.relwet + "%");
                            tvDisplayDate.setText(new StringBuilder()
                                    // Month is 0 based, just add 1
                                    .append(entry.day).append("-").append(entry.month).append("-")
                                    .append(entry.year).append(" "));

                        }
                    }
                }
            }
        }
    }


    public class WeatherXmlParser {
        // We don't use namespaces
        private final String ns = null;

        public List parse(InputStream in) throws XmlPullParserException, IOException {
            try {
                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(in, null);
                parser.nextTag();
                return readFeed(parser);
            } finally {
                in.close();
            }
        }

        private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
            List entries = new ArrayList();

            parser.require(XmlPullParser.START_TAG, ns, "MMWEATHER");
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                // Starts by looking for the entry tag
                if (name.equals("FORECAST")) {
                    entries.add(readEntry(parser));
                } else {
                    skip(parser);
                }
            }
            return entries;
        }

        private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                throw new IllegalStateException();
            }
            int depth = 1;
            while (depth != 2) {
                switch (parser.next()) {
                    case XmlPullParser.END_TAG:
                        depth--;
                        break;
                    case XmlPullParser.START_TAG:
                        depth++;
                        break;
                }
            }
        }

        // Parses the contents of an entry. If it encounters a PHENOMENA, TEMPERATURE, or RELWET tag, hands them off
// to their respective "read" methods for processing. Otherwise, skips the tag.
        private Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
            parser.require(XmlPullParser.START_TAG, ns, "FORECAST");
            String cloudiness = null;
            String temperature = null;
            String relwet = null;

            int day = readDay(parser);
            int month = readMonth(parser);
            int year = readYear(parser);
            while (!((parser.next() == XmlPullParser.END_TAG) && (parser.getName().equals("FORECAST")))) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                if (name.equals("PHENOMENA")) {
                    cloudiness = readCloudiness(parser);
                } else if (name.equals("TEMPERATURE")) {
                    temperature = readMaxTemperature(parser);
                } else if (name.equals("RELWET")) {
                    relwet = readMaxRelwet(parser);
                }
            }
            return new Entry(cloudiness, temperature, relwet, day, month, year);
        }

        // Processes PHENOMENA in the feed.
        private String readCloudiness(XmlPullParser parser) throws IOException, XmlPullParserException {
            parser.require(XmlPullParser.START_TAG, ns, "PHENOMENA");
            String relwet = readText(parser, "cloudiness");
            return relwet;
        }

        // Processes day in the feed.
        private int readDay(XmlPullParser parser) throws IOException, XmlPullParserException {
            parser.require(XmlPullParser.START_TAG, ns, "FORECAST");
            int day = Integer.parseInt(readText(parser, "day"));
            return day;
        }

        // Processes month in the feed.
        private int readMonth(XmlPullParser parser) throws IOException, XmlPullParserException {
            parser.require(XmlPullParser.START_TAG, ns, "FORECAST");
            int month = Integer.parseInt(readText(parser, "month"));
            return month;
        }

        // Processes year in the feed.
        private int readYear(XmlPullParser parser) throws IOException, XmlPullParserException {
            parser.require(XmlPullParser.START_TAG, ns, "FORECAST");
            int year = Integer.parseInt(readText(parser, "year"));
            return year;
        }

        // Processes TEMPERATURE tags in the feed.
        private String readMaxTemperature(XmlPullParser parser) throws IOException, XmlPullParserException {
            parser.require(XmlPullParser.START_TAG, ns, "TEMPERATURE");
            String relwet = readText(parser, "max");
            return relwet;
        }

        // Processes RELWET tags in the feed.
        private String readMaxRelwet(XmlPullParser parser) throws IOException, XmlPullParserException {
            parser.require(XmlPullParser.START_TAG, ns, "RELWET");
            String relwet = readText(parser, "max");
            return relwet;
        }

        private String readText(XmlPullParser parser, String attr) throws IOException, XmlPullParserException {
            String result = "";
            result = parser.getAttributeValue(null, attr);
            return result;
        }
    }

    // Given a string representation of a URL, sets up a connection and gets
// an input stream.
    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }
}

package com.lunasystems.librarymanager;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;

// Include Intent classes from barcode scanner ZXing
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ScanFragment extends Fragment {
    private String barcodeID = null;
    private BookClient client = null;

    class MyButtonScanListener implements View.OnClickListener {
        private ScanFragment parent;

        public MyButtonScanListener(ScanFragment s) {
            parent = s;
        }

        public void onClick(View v) {
            IntentIntegrator integrator = new IntentIntegrator(parent);
            integrator.initiateScan();
        }
    }

    class MyButtonSearchListener implements View.OnClickListener {
        public void onClick(View v) {
            Log.d("ButtonPressed", "Button Pressed");
            Log.d("BarcodeID", "safetyEdit.getText() = " + safetyEdit.getText());
            barcodeID = safetyEdit.getText().toString();
            fetchBooks(barcodeID);
        }
    }

    // The fragment argument representing the section number for this fragment.
    private static final String ARG_SECTION_NUMBER = "section_number";

    // ScanButton listener
    private MyButtonScanListener scanListener = null;
    private MyButtonSearchListener searchListener = null;

    // Pre-define the elements
    private Button scanButton = null;
    private Button safetySearchButton = null;
    private TextView barcodeText = null;
    private TextView titleText = null;
    private TextView authorText = null;
    private TextView preTitleText = null;
    private TextView preAuthorText = null;
    private TextView preBarcodeText = null;
    private TextView errorText = null;
    private EditText safetyEdit = null;
    private ImageView coverImage = null;

    public ScanFragment() {
    }

    // Create a new instance of ScanFragment with the specified section number.
    public ScanFragment(int sectionNumber) {
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        scanListener = new MyButtonScanListener(this);
        searchListener = new MyButtonSearchListener();
        setArguments(args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scan, container, false);
        scanButton = (Button) rootView.findViewById(R.id.button_scan);
        safetySearchButton = (Button) rootView.findViewById(R.id.button_safety_search);
        scanButton.setOnClickListener(scanListener);
        safetySearchButton.setOnClickListener(searchListener);

        barcodeText = (TextView) rootView.findViewById(R.id.text_barcode);
        titleText = (TextView) rootView.findViewById(R.id.text_title);
        authorText = (TextView) rootView.findViewById(R.id.text_author);
        preTitleText = (TextView) rootView.findViewById(R.id.text_pretitle);
        preAuthorText = (TextView) rootView.findViewById(R.id.text_preauthor);
        preBarcodeText = (TextView) rootView.findViewById(R.id.text_prebarcode);
        errorText = (TextView) rootView.findViewById(R.id.text_error);

        coverImage = (ImageView) rootView.findViewById(R.id.image_cover);

        safetyEdit = (EditText) rootView.findViewById(R.id.edit_barcode);

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            barcodeID = scanResult.getContents();
            if (scanResult != null)
            {
                // handle scan result
                barcodeText.setText(barcodeID);

                // access book info
                fetchBooks(barcodeID);
            }
        }
    }

    // Executes an API call to the OpenLibrary search endpoint, parses the results
    // Converts them into an array of book objects and adds them to the adapter
    private void fetchBooks(String barcode_number) {
        Log.d("fetchBooks", "Books being fetched...");
        client = new BookClient();
        final String error = "{\"start\":0,\"num_found\":0,\"numFound\":0,\"docs\":[]}";

        client.getBooks(barcode_number, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray docs = null;
                    if(response != null) {
                        if (response.toString().equals(error)) {
                            // Deal with invalid barcode
                            Log.d("ReceievedError", "received error");
                            errorText.setText("Error: Could not find book " + barcodeID);
                        } else {
                            Log.d("debug", "response = " + response);
                            // Get the docs json array
                            docs = response.getJSONArray("docs");
                            // Parse json array into array of model objects
                            final ArrayList<Book> books = Book.fromJson(docs);
                            // retrieve first book out of array
                            Book myBook = books.get(0);
                            String title = "Title:";
                            String author = "Author";
                            String barcode = "Barcode ID:";

                            titleText.setText(myBook.getTitle());
                            authorText.setText(myBook.getAuthor());
                            preTitleText.setText(title);
                            preAuthorText.setText(author);
                            preBarcodeText.setText(barcode);

                            Log.d("Picasso", "getContext() = " + getContext());

                            Picasso.with(getContext()).load(Uri.parse(myBook.getLargeCoverUrl())).error(R.drawable.ic_nocover).into(coverImage);
                        }
                    }
                } catch (JSONException e) {
                    // Invalid JSON format
                    Log.d("ReceievedError", "received error");
                    errorText.setText("Error: Received invalid JSON format");
                }
            }
        });
    }
}
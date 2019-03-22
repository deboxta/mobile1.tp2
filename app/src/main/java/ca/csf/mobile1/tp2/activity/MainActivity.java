package ca.csf.mobile1.tp2.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import ca.csf.mobile1.tp2.R;
import ca.csf.mobile1.util.view.CharactersFilter;
import ca.csf.mobile1.util.view.KeyPickerDialog;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;

import java.io.IOException;
import java.util.List;

import static org.koin.java.standalone.KoinJavaComponent.get;

public class MainActivity extends AppCompatActivity implements FetchPostAsyncTask.Listener{

    private static final int KEY_LENGTH = 5;
    private static final int MAX_KEY_VALUE = (int) Math.pow(10, KEY_LENGTH) - 1;

    private OkHttpClient okHttpClient;
    private ObjectMapper objectMapper;
    private Intent intent;

    private View rootView;
    private EditText inputEditText;
    private TextView outputTextView;
    private Button encryptButton;
    private Button decryptButton;
    private FloatingActionButton selectKeyButton;
    private TextView currentKeyTextView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createView();
        createDependencies();

        selectKeyButton.setOnClickListener(this::onSelectKeyButtonPressed);
        encryptButton.setEnabled(false);
        decryptButton.setEnabled(false);
        currentKeyTextView.setText("");

        intent = getIntent();
        if("text/plain".equals(intent.getType())) {
            inputEditText.setText(intent.getStringExtra(Intent.EXTRA_TEXT));
            openKeyPickerDialog();
        }
    }

    private void createDependencies() {
        okHttpClient = get(OkHttpClient.class);
        objectMapper = get(ObjectMapper.class);
    }

    private void createView() {
        setContentView(R.layout.activity_main);

        rootView = findViewById(R.id.rootView);
        progressBar = findViewById(R.id.progressBar);
        inputEditText = findViewById(R.id.inputEditText);
        inputEditText.setFilters(new InputFilter[]{new CharactersFilter()});
        outputTextView = findViewById(R.id.outputTextView);
        encryptButton = findViewById(R.id.encryptButton);
        decryptButton = findViewById(R.id.decryptButton);
        selectKeyButton = findViewById(R.id.selectKeyButton);
        currentKeyTextView = findViewById(R.id.currentKeyTextView);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FetchPostAsyncTask task = new FetchPostAsyncTask(this::onPostFetched, this::onNotFoundError, this::onConnectivityError, this::onServerError);

        task.execute();
    }

    private void onSelectKeyButtonPressed(View view){
        openKeyPickerDialog();
    }

    private void openKeyPickerDialog() {
        //TODO : Compléter la création et l'ouverture du "KeyPickerDialog" dans cette fonction.
        KeyPickerDialog.make(this, KEY_LENGTH)
                .setKey(1337)
                .setConfirmAction(this::theConfirmFunctionToCall)
                .setCancelAction(this::theCancelFunctionToCall)
                .show();
    }

    private void theConfirmFunctionToCall(Integer i ) {
        currentKeyTextView.setText(i.toString());
        decryptButton.setEnabled(true);
        encryptButton.setEnabled(true);
    }

    private void theCancelFunctionToCall() {
        if (intent.getType() != null && currentKeyTextView.getText().equals(""))
            finish();
    }

    @SuppressWarnings("ConstantConditions")
    private void putTextInClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText(getResources().getString(R.string.clipboard_encrypted_text), text));
    }

    private void onNotFoundError(){
        Snackbar.make(rootView,R.string.error_not_found, Snackbar.LENGTH_LONG).show();
    }

    private void onConnectivityError(){
        Snackbar.make(rootView,R.string.error_connectivity, Snackbar.LENGTH_INDEFINITE).setAction("Retry", this::fetchPosts);
    }

    private void fetchPosts(View view){

        FetchPostAsyncTask task = new FetchPostAsyncTask(this::onPostFetched, this::onNotFoundError, this::onConnectivityError, this::onServerError);

        task.execute();
    }

    private void onServerError(){
        Snackbar.make(rootView,R.string.error_server, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onPostFetched(String post) {

    }
}

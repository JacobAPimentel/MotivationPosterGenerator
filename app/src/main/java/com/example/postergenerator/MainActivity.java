package com.example.postergenerator;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends BaseActivity //BaseActivity has the navigation codes.
{
    // Views
    private ConstraintLayout posterView;
    private ImageView posterImage;
    private TextView posterTitleView;
    private TextView posterQuoteView;
    private View posterConstraint;
    private View line;
    private EditText editTitle;
    private EditText editQuote;

    // Variables
    private boolean firstExecution = true; // determine if onResume should occur or not.

    //Codes
    public static final int CAMERA_REQUEST_CODE = 1;
    public static final int FILE_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        super.createToolbar(R.string.poster_generator);

        //Get posterView and inflate poster into the view.
        posterView = findViewById(R.id.posterView);
        posterConstraint = getLayoutInflater().inflate(R.layout.poster_constraint, posterView, false); //
        posterView.addView(posterConstraint);

        //posterView views.
        posterImage = posterView.findViewById(R.id.posterImage);
        posterTitleView = posterView.findViewById(R.id.posterTitleView);
        posterQuoteView = posterView.findViewById(R.id.posterQuoteView);
        line = posterView.findViewById(R.id.line);

        //Other views.
        editTitle = findViewById(R.id.editTitle);
        editQuote = findViewById(R.id.editQuote);

        // VERIFY IF THERE'S  INTENTS (FROM SHARING!)
        Intent intent = getIntent();
        String type = intent.getType();

        if(type != null) // there's a type...
        {
            if("text/plain".equals(type)) // set the quote
                posterQuoteView.setText(intent.getStringExtra(Intent.EXTRA_TEXT));
            else if(type.startsWith("image/")) //Set the image if uri found.
            {
                Settings.photoUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM); // get the file uri

                if(Settings.photoUri != null) // just in case.
                    posterImage.setImageURI(Settings.photoUri);
            }
        }

        //VERIFY SETTING IN CASE OF ORIENTATION CHANGE
        VerifySettings();

        //EDIT TEXT focusListener
        View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View view, boolean hasFocus)
            {
                EditText editTextView = (EditText) view; // current EditText
                if (!hasFocus)
                {
                    HideKeyboard(view); // hide the keyboard when focus is gone.

                    //Apply the editText text to the poster.
                    TextView tv; // textView to be changed
                    String text; // User's input text from EditText

                    if (view.getId() == R.id.editTitle)
                    {
                        tv = posterTitleView;
                        text = spaceText(editTextView.getText().toString());
                    } else {
                        tv = posterQuoteView;
                        text = editTextView.getText().toString();
                    }

                    tv.setText(text);
                }
            }
        };

        //EditText clickedDone
        EditText.OnEditorActionListener clickDoneEvent = new EditText.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent keyEvent)
            {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    view.clearFocus(); // clears the focus (activates onFocusChange)
                    return true;
                }
                return false;
            }
        };

        //Set the listeners to the editTexts.
        editTitle.setOnFocusChangeListener(focusListener);
        editTitle.setOnEditorActionListener(clickDoneEvent);
        editQuote.setOnFocusChangeListener(focusListener);
        editQuote.setOnEditorActionListener(clickDoneEvent);
    }// onCreate

    protected void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);

        /* Before, photoUri was not stored in settings.
        if (Settings.photoUri != null)
            bundle.putParcelable("photo", Settings.photoUri);
         */
        bundle.putString("title",posterTitleView.getText().toString());
        bundle.putString("quote",posterQuoteView.getText().toString());
    }//onSaveInstanceState

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        posterTitleView.setText(savedInstanceState.getString("title"));
        posterQuoteView.setText(savedInstanceState.getString("quote"));

        //Uri uri = savedInstanceState.getParcelable("photo");
        if (Settings.photoUri != null)
        {
            posterImage.setImageURI(Settings.photoUri);
        }
    }//onSaveInstanceState

    public void VerifySettings() // Checks the global changes and make changes according to it.
    {
        line.setVisibility((Settings.lineOn ? View.VISIBLE : View.INVISIBLE));
        posterImage.setScaleType(Settings.cropOn ? ImageView.ScaleType.CENTER_CROP : ImageView.ScaleType.FIT_XY);

        int colorId = getResources().getIdentifier(Settings.backgroundColor, "color", getPackageName());
        posterConstraint.setBackgroundResource(colorId);
    }//VerifySettings

    public void VerifyPresets() // check if there's a preset intent bundle and apply it
    {
        Intent intent = getIntent();
        String presetTitle = intent.getStringExtra("presetTitle");
        String presetQuote = intent.getStringExtra("presetQuote");

        if (presetTitle != null)
        {
            posterTitleView.setText(spaceText(presetTitle));
            editTitle.setText(presetTitle);
        }

        if (presetQuote != null)
        {
            posterQuoteView.setText(presetQuote);
            editQuote.setText(presetQuote);
        }
    }//VerifyPresets

    @Override
    public void onResume()
    {
        super.onResume();
        if(firstExecution) // disables firstExecution; this is so onResume won't be called for the first run.
            firstExecution = false;
        else
        { //Verify settings and presets
            VerifyPresets();
            VerifySettings();
        }
    }//onResume

    @Override
    protected void onNewIntent(Intent intent) //Need this because of doing reorder flags.
    {
        super.onNewIntent(intent);
        if(intent != null)
            setIntent(intent);
    }//onNewIntent

    public String spaceText(String text) //Automatically space the title text for users.
    {
        // check if title spacing is enabled.
        if (Settings.spaceOn)
            return text.replace(""," ").trim(); // replace all nonempty with space, and then trim the first and final space.
        else
            return text;
    }//spaceText

    public void SaveImage(View view) // saves the image to gallery.
    {
        posterView.setDrawingCacheEnabled(true); // turn on cache
        Bitmap image = posterView.getDrawingCache(); //turn the view into an image.

        //Old way I used to save image, had reduced quality.
        //MediaStore.Images.Media.insertImage(getContentResolver(), image, "poster", "description");

        // Create the values to be put into the gallery.
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "poster");
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "poster");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Generated using Poster Generator App");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/"+Settings.fileType);

        //Insert and create the URI where the image will be saved at.
        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        OutputStream outputStream;
        try {
            outputStream = getContentResolver().openOutputStream(uri); //open the stream based on the file uri.

            //Determine which file and compress it.
            if (Settings.fileType.equals("png"))
                image.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            else
                image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            outputStream.close(); //close the stream, not needed any more.

            Toast toast = Toast.makeText(this, R.string.image_saved, Toast.LENGTH_SHORT);
            toast.show();
        } catch (Exception e)
        {
            System.err.println("Error!" + e);
        }

        posterView.setDrawingCacheEnabled(false); // turn it off (if I did not do that, the same images would be saved)
    }//SaveImage

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) //used for retrieving File and Camera images.
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) //Was successful..
        {
            // camera
            if (requestCode == FILE_REQUEST_CODE)
            { // uses file path
                if (data == null) {return;} // just in case
                Settings.photoUri = data.getData();
            }
            if (Settings.photoUri != null)
                posterImage.setImageURI(Settings.photoUri);
        }
    }//onActivityResult

    public File createImageFile() throws IOException //used to store image from Camera.
    {
        // Create a temp image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                timeStamp, //TITLE
                ".jpg", //SUFFIX
                storageDir //DIRECTORY
        );

        //imageFilePath = image.getAbsolutePath();
        return image;
    }//createImageFile

    public void UseCameraImage(View view) // get image from camera
    {
        File photoFile = null;
        try
        {
            photoFile = createImageFile(); //create the file path.
        }catch(IOException e)
        {
            System.err.println("Error!" + e);
        }

        if(photoFile != null)
        {
            Intent intent = new Intent(); //implicit
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

            Settings.photoUri = FileProvider.getUriForFile(this, "com.example.postergenerator.fileprovider", photoFile);

            intent.putExtra(MediaStore.EXTRA_OUTPUT,Settings.photoUri);
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        }
    }//UseCameraImage

    public void GetFileImage(View view)
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*"); //open apps that allows image/*.
        startActivityForResult(intent, FILE_REQUEST_CODE);
    }//GetFileImage

    public void HideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    } // HideKeyboard
}//MainActivity
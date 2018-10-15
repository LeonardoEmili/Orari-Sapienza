package com.sterbsociety.orarisapienza.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sterbsociety.orarisapienza.MailTask;
import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.utils.AppUtils;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.jaredrummler.android.device.DeviceName;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.util.ArrayList;

@SuppressWarnings("FieldCanBeLocal")
public class BugReportActivity extends AppCompatActivity {

    private static final int PICK_FROM_GALLERY = 1;
    private final int PREVIEW_WIDTH = 220;
    private final int PREVIEW_HEIGHT = 260;
    private final int PREVIEW_DEFAULT = 200;
    private final int ATTACHMENT_NOT_FOUND = -1;
    private final int MINIMUM_DESCRIPTION_LENGTH = 20;
    private LinearLayout linear;
    private View.OnClickListener attachImageListener;
    private View.OnClickListener removeImageListener;
    private EditText editText;
    private ArrayList<String> mAttachmentList;
    private Spinner spinner1, spinner2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppUtils.applyTheme(BugReportActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bug_report);

        initActivity();

        addAttachmentSample();
    }


    /**
     * This method responsible for: setting up the Toolbar (Actionbar here is used), handling the view's focus
     * and the keyboard behaviour and so on.
     */
    private void initActivity() {

        AppUtils.setLocale(BugReportActivity.this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getResources().getString(R.string.report_a_bug));
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close);
        }

        final LinearLayout linearLayout = findViewById(R.id.main_wrap);
        linearLayout.setOnClickListener(view -> linearLayout.requestFocus());

        findViewById(R.id.wrapper_main);
        editText = findViewById(R.id.edit_text);
        editText.setOnFocusChangeListener((view, b) -> {
            if (!b) {
                AppUtils.hideSoftKeyboard(BugReportActivity.this, view);
                if (editText.getText().toString().trim().length() < MINIMUM_DESCRIPTION_LENGTH) {
                    editText.setError(getResources().getString(R.string.error));
                }
            } else {
                editText.setError(null);
            }
        });

        final String appVersion = getResources().getString(R.string.app_version);
        final String manufacturer = getResources().getString(R.string.manufacturer);
        final String deviceName = getResources().getString(R.string.device_name);
        final String deviceModel = getResources().getString(R.string.device_model);
        final String versionOS = getResources().getString(R.string.os_version);

        // This library helps us for retrieving these useful info, more info at:
        // https://github.com/jaredrummler/AndroidDeviceNames
        DeviceName.with(BugReportActivity.this).request((info, error) -> {
            String mText = "\n\n" +
                    appVersion    + ": "  + AppUtils.APP_VERSION        + "\n" +
                    manufacturer  + ": "  + info.manufacturer           + "\n" +
                    deviceName    + info.marketName             + "\n" +
                    deviceModel   + info.model                  + "\n" +
                    versionOS     + Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK_INT + ")";
            editText.setText(mText, TextView.BufferType.EDITABLE);
        });

        linear = findViewById(R.id.linear);

        attachImageListener = view -> AppUtils.pickImage(BugReportActivity.this);

        removeImageListener = this::showAlertDialog;

        spinner1 = findViewById(R.id.spinner1);
        spinner2 = findViewById(R.id.spinner2);

        mAttachmentList = new ArrayList<>();

        AppUtils.customizeSpinner(BugReportActivity.this, spinner1, getResources().getStringArray(R.array.issue_type));
        AppUtils.customizeSpinner(BugReportActivity.this, spinner2, getResources().getStringArray(R.array.frequency_type));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {

            case PICK_FROM_GALLERY:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, PICK_FROM_GALLERY);
                } else {
                    StyleableToast.makeText(BugReportActivity.this, getResources().getString(R.string.gallery_permission_denied),
                            Toast.LENGTH_LONG, R.style.errorToast).show();
                }
                break;
        }
    }

    /**
     * This method is responsible for handling the PICK_FROM_GALLERY action.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK && null != data) {

            try {

                ImageView imgView = (ImageView) ((RelativeLayout) (linear.getChildAt(linear.getChildCount() - 1))).getChildAt(0);
                imgView.setLayoutParams(new RelativeLayout.LayoutParams(PREVIEW_WIDTH, PREVIEW_HEIGHT));

                // This library helps us in resizing the picture before loading it. It saved us. (even better than Picasso).
                Glide.with(BugReportActivity.this).load(data.getData()).into(imgView);

                // Adds the picture to the attachment list
                String filePath = FileUtils.getPath(BugReportActivity.this, data.getData());
                mAttachmentList.add(filePath);
                addAttachmentSample();

            } catch (OutOfMemoryError ex) {

                // This would be an huge disaster ..
                StyleableToast.makeText(BugReportActivity.this,
                        getResources().getString(R.string.attachment_too_many),
                        Toast.LENGTH_LONG, R.style.errorToast).show();
            }
        }
    }


    /**
     * This method allows to go back to ContactUsActivity
     */
    @Override
    public boolean onSupportNavigateUp() {

        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_send_mail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.send_mail:
                sendReport();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * This method checks if the user has completely filled the form send us the report.
     */
    private void sendReport() {

        if (editText.getText().toString().trim().length() < MINIMUM_DESCRIPTION_LENGTH) {
            editText.requestFocus();
            editText.setError(null);
            editText.setError(getResources().getString(R.string.error));

        } else if (spinner1.getSelectedItemPosition() == 0) {
            ((TextView) spinner1.getSelectedView()).setError(getResources().getString(R.string.error));

        } else if (spinner2.getSelectedItemPosition() == 0) {
            ((TextView) spinner2.getSelectedView()).setError(getResources().getString(R.string.error));

        } else {

            String subject = "BugReport";
            String body = "--------------------------------------------"           + "\n"
                    + "Tipo di problema: " + spinner1.getSelectedItem().toString() + "\n"
                    + "Frequenza:        " + spinner2.getSelectedItem().toString() + "\n"
                    + "--------------------------------------------"               + "\n"
                    + "Descrizione:" + "\n\n"
                    + editText.getText().toString();

            String progressBarTitle = BugReportActivity.this.getResources().getString(R.string.sending_report);
            String progressBarMessage = BugReportActivity.this.getResources().getString(R.string.please_wait);
            String positiveMsg = BugReportActivity.this.getResources().getString(R.string.report_sent);
            String negativeMsg = BugReportActivity.this.getResources().getString(R.string.report_not_sent);
            new MailTask(BugReportActivity.this, progressBarTitle, progressBarMessage, positiveMsg, negativeMsg).
                    execute(subject, body, mAttachmentList);
        }
    }

    /**
     * @param view is the remove icon's View which has been clicked, we need to remove the associated ImageView,
     *             then what we do is require its parent (RelativeLayout) and eliminate it
     *             (ImageView and RemoveIcon get eliminated too).
     *             This method is used to show an AlertDialog which asks the user the confirm to remove the selected attachment.
     */
    private void showAlertDialog(View view) {

        RelativeLayout parent = (RelativeLayout) view.getParent();

        // We need to know which children to eliminate from the LinearLayout , then the index is provided.
        final int index = getIndexOf(parent);

        // An error is occurred, killAll the previews.
        if (index == ATTACHMENT_NOT_FOUND) {

            linear.removeAllViews();
            mAttachmentList.clear();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(BugReportActivity.this);
        builder.setCancelable(true);
        builder.setTitle(getResources().getString(R.string.attachment_remove));
        builder.setMessage(getResources().getString(R.string.attachment_remove_question));
        builder.setPositiveButton(getResources().getString(R.string.confirm),
                (dialog, which) -> {
                    linear.removeViewAt(index);
                    mAttachmentList.remove(index);
                });
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
            // Nothing to do here if the user clicks on cancel.
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * This method is responsible of two things:
     * 1) It shows the user a new addImage placeHolder to allow the user to attach a new picture.
     * 2) If this method gets called it means that a new picture has been attached to the user's report
     * then it is responsible for properly setting it up by providing it the RemoveIcon and the relative
     * onClickListener and the previous listener is removed (set to null).
     */
    private void addAttachmentSample() {

        // Look at JavaDoc above (2)
        if (linear.getChildCount() > 0) {
            RelativeLayout currentRl = (RelativeLayout) linear.getChildAt(linear.getChildCount() - 1);

            // XML stuff.
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            ImageView removeIcon = new ImageView(BugReportActivity.this);
            removeIcon.setLayoutParams(new LinearLayout.LayoutParams(42, 42));
            removeIcon.setImageResource(R.drawable.ic_remove);
            currentRl.addView(removeIcon, params);

            // Now the RelativeLayout has no more onClickListeners.
            currentRl.setOnClickListener(null);

            removeIcon.setOnClickListener(removeImageListener);
        }

        // Look at JavaDoc above (1)
        RelativeLayout mRelativeLayout = new RelativeLayout(BugReportActivity.this);
        RelativeLayout.LayoutParams mParams = new RelativeLayout.LayoutParams(PREVIEW_WIDTH, PREVIEW_HEIGHT);

        ImageView img = new ImageView(BugReportActivity.this);

        // This parameters change when the user selects an image, just to beautify it.
        img.setLayoutParams(new LinearLayout.LayoutParams(PREVIEW_DEFAULT, PREVIEW_DEFAULT));

        img.setImageResource(R.drawable.ic_add_attachment);
        img.setScaleType(ImageView.ScaleType.FIT_XY);
        img.setPadding(15, 10, 15, 10);

        mRelativeLayout.addView(img);
        mRelativeLayout.setLayoutParams(mParams);
        mRelativeLayout.setOnClickListener(attachImageListener);

        linear.addView(mRelativeLayout);
    }

    /**
     * @param v is a child of the LinearLayout (list of attachments).
     *          This method returns the index of the View, if present.
     */
    private int getIndexOf(View v) {

        for (int i = 0; i < linear.getChildCount(); i++) {
            if (linear.getChildAt(i) == v)
                return i;
        }
        return ATTACHMENT_NOT_FOUND;
    }

}
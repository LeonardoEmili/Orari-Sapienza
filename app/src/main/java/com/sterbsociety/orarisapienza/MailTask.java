package com.sterbsociety.orarisapienza;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.util.ArrayList;

/**
 * Useful thread on StackOverflow which explains everything:
 * https://stackoverflow.com/questions/20067508/get-real-path-from-uri-android-kitkat-new-storage-access-framework
 */
public class MailTask extends AsyncTask<Object, Void, Boolean> {

    private static final int BUGREPORT_PARAMS = 3;
    private ProgressDialog pd;
    private String pdTitle, pdMessage;
    private String positiveMsg, negativeMsg;
    @SuppressLint("StaticFieldLeak")
    private Context mContext;
    private GMailSender sender;

    public MailTask(Context context, String pdTitle, String pdMessage, String positiveMsg, String negativeMsg) {
        super();
        mContext = context;
        this.pdTitle = pdTitle;
        this.pdMessage = pdMessage;
        this.positiveMsg = positiveMsg;
        this.negativeMsg = negativeMsg;
    }

    /**
     * This method is responsible for properly setting up the ProgressDialog.
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pd = new ProgressDialog(mContext);
        pd.setTitle(pdTitle);
        pd.setMessage(pdMessage);
        pd.setCancelable(false);
        pd.show();
    }

    @Override
    protected Boolean doInBackground(Object... params) {

        sender = new GMailSender();
        boolean outCome;

        if (params.length == BUGREPORT_PARAMS) {
            outCome = sendReport(params);
        } else {
            outCome = sendMail(params);
        }

        return outCome;
    }


    /**
     * @param result is the the mail sending's outcome
     */
    protected void onPostExecute(Boolean result) {
        pd.dismiss();
        if (result) {
            StyleableToast.makeText(mContext,
                    positiveMsg,
                    Toast.LENGTH_LONG,
                    R.style.successToast).show();
        } else {
            StyleableToast.makeText(mContext,
                    negativeMsg,
                    Toast.LENGTH_LONG,
                    R.style.errorToast).show();
        }
        // Once the report/feedback has been sent the calling activity gets closed
        ((Activity)mContext).finish();
    }

    /**
     * @param params is composed by:
     *               [subject, body, mAttachmentList]
     */
    private boolean sendReport(Object... params) {

        String subject = (String) params[0];
        String body = (String) params[1];
        ArrayList<String> mAttachmentList = (ArrayList<String>) params[2];

        try {
            for (String attachmentFileName: mAttachmentList) {
                sender.addAttachment(attachmentFileName);
            }

            if (mAttachmentList.isEmpty())
                sender.sendMail(subject, body);
            else
                sender.sendMailWithAttachments(subject, body);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @param params is composed by:
     *      *               [subject, body, userName, userEmail]
     */
    private boolean sendMail(Object... params) {

        String subject = (String) params[0];
        String mBody = (String) params[1];
        String userName = (String) params[2];
        String userEmail = (String) params[3];
        String body = mBody + "\n" + "User name: " + userName
                + "\n" + "User email: " + userEmail;

        sender.sendMail(subject, body);
        return true;
    }
}
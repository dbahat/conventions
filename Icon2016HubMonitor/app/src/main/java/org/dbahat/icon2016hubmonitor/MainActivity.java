package org.dbahat.icon2016hubmonitor;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.dbahat.icon2016hubmonitor.messages.ApnMessage;
import org.dbahat.icon2016hubmonitor.messages.GcmMessage;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.dbahat.icon2016hubmonitor.NotificationHubInfo.ApiUrl;

public class MainActivity extends AppCompatActivity {

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue = Volley.newRequestQueue(this);

        final TextView remainingCharsCountView = (TextView)this.findViewById(R.id.remainingCharsCount);
        final EditText editText = (EditText)this.findViewById(R.id.editText);

        // Count the remaining characters as the user types the message
        if(editText != null && remainingCharsCountView != null) {
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    remainingCharsCountView.setText(String.format(Locale.US, "%d/1900", editText.getText().length()));
                }
            });
        }
    }

    public void getRegistrationsPerCategoryOnClick(View caller) {
        int checkedRadioButtonId = ((RadioGroup)this.findViewById(R.id.radio_group)).getCheckedRadioButtonId();
        if(checkedRadioButtonId == -1) {
            Toast.makeText(this, "יש לבחור קטגוריה", Toast.LENGTH_SHORT).show();
            return;
        }

        String tag = getTagById(checkedRadioButtonId);
        Toast.makeText(this, "בודק...", Toast.LENGTH_SHORT).show();
        new RegistrationsPerTagRetriever(requestQueue).retrieve(tag, new RegistrationsPerTagRetriever.Callback() {
            public void onComplete(Registrations registrations) {
                Toast.makeText(MainActivity.this, registrations.toString(), Toast.LENGTH_SHORT).show();
            }

            public void onError() {
                Toast.makeText(MainActivity.this, "הבדיקה נכשלה", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sendAndroidNotificationButtonOnClick(View view) {
        showAreYouSureDialogAndSend(DialogType.Android, new SendNotificationTask() {
            @Override
            public void run(String tag) {
                sendGcmRequest(tag);
            }
        });
    }

    public void sendiOSNotificationButtonOnClick(View view) {
        showAreYouSureDialogAndSend(DialogType.iOS, new SendNotificationTask() {
            @Override
            public void run(String tag) {
                sendApnRequest(tag);
            }
        });
    }

    public void sendBothNotificationButtonOnClick(View view) {
        showAreYouSureDialogAndSend(DialogType.Both, new SendNotificationTask() {
            @Override
            public void run(String tag) {
                sendApnRequest(tag);
                sendGcmRequest(tag);
            }
        });
    }

    private interface SendNotificationTask {
        void run(String tag);
    }

    private enum DialogType {
        iOS,
        Android,
        Both
    }

    private void showAreYouSureDialogAndSend(final DialogType dialogType, final SendNotificationTask sendNotificationTask) {
        int checkedRadioButtonId = ((RadioGroup)findViewById(R.id.radio_group)).getCheckedRadioButtonId();
        if(checkedRadioButtonId == -1) {
            Toast.makeText(this, "יש לבחור קטגוריה", Toast.LENGTH_SHORT).show();
        } else {
            final String editTextView = ((EditText)this.findViewById(R.id.editText)).getText().toString();
            if(TextUtils.isEmpty(editTextView)) {
                Toast.makeText(this, "יש לכתוב הודעה", Toast.LENGTH_SHORT).show();
            } else if(editTextView.length() > 1900) {
                Toast.makeText(this, "ההודעה ארוכה מ-1900 תווים. בבקשה לקצר", Toast.LENGTH_SHORT).show();
            } else {
                final String tag = getTagById(checkedRadioButtonId);
                Toast.makeText(this, "מתחיל לשלוח...", Toast.LENGTH_SHORT).show();
                new RegistrationsPerTagRetriever(requestQueue).retrieve(tag, new RegistrationsPerTagRetriever.Callback() {
                    public void onComplete(Registrations registrations) {
                        int numberOfDevices = dialogType == DialogType.Both ? registrations.getTotal()
                                : dialogType == DialogType.iOS ? registrations.getiOS()
                                : registrations.getAndroid();
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("הודעה בקטגורית " + editTextView)
                                .setMessage("ההודעה תישלח ל " + numberOfDevices + " מכשירים. האם אתה בטוח? ")
                                .setPositiveButton("שלח", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface var1, int var2) {
                                        sendNotificationTask.run(tag);
                                    }
                                })
                                .setNegativeButton("בטל", null)
                                .create()
                                .show();
                    }

                    public void onError() {
                        Toast.makeText(MainActivity.this, "השליחה נכשלה", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    private String getTagById(int id) {
        return findViewById(id).getTag().toString();
    }

    private void sendApnRequest(final String tag) {
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        final String serializedMessage = new Gson().toJson(
                new ApnMessage()
                        .setApns(new ApnMessage.Aps()
                                .setAlert(message)
                                .setSound("default"))
                        .setCategory(tag)
        );

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiUrl, new Response.Listener<String>() {
            public void onResponse(String response) {
                Toast.makeText(MainActivity.this, "הצלחה!", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "השליחה הצליחה לאנדרואיד ונכשלה לאייפון", Toast.LENGTH_SHORT).show();
            }
        }) {
            public byte[] getBody() throws AuthFailureError {
                try {
                    return serializedMessage.getBytes("UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    throw new RuntimeException(ex);
                }
            }

            public String getBodyContentType() {
                return "application/json;charset=utf-8";
            }

            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", AuthTokenGenerator.generate(ApiUrl));
                headers.put("ServiceBusNotification-Format", "apple");
                headers.put("ServiceBusNotification-Tags", tag);
                headers.put("ServiceBusNotification-Apns-Expiry", "2016-10-21T20:00+02:00");
                return headers;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void sendGcmRequest(final String tag) {
        String message = ((EditText)this.findViewById(R.id.editText)).getText().toString();
        final String serializedMessage = new Gson().toJson(
                new GcmMessage()
                        .setPriority("high")
                        .setData(new GcmMessage.Data()
                                .setMessage(message)
                                .setCategory(tag)))
                ;
        Toast.makeText(this, "...שולח", Toast.LENGTH_SHORT).show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiUrl, new Response.Listener<String>() {
            public void onResponse(String response) {
                Toast.makeText(MainActivity.this, "הצלחה!", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "השליחה נכשלה. בדוק חיבור לאינטרנט", Toast.LENGTH_SHORT).show();
            }
        }) {
            public byte[] getBody() throws AuthFailureError {
                try {
                    return serializedMessage.getBytes("UTF-8");
                } catch (UnsupportedEncodingException var2x) {
                    throw new RuntimeException(var2x);
                }
            }

            public String getBodyContentType() {
                return "application/json;charset=utf-8";
            }

            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", AuthTokenGenerator.generate(ApiUrl));
                headers.put("ServiceBusNotification-Format", "gcm");
                headers.put("ServiceBusNotification-Tags", tag);
                return headers;
            }
        };
        requestQueue.add(stringRequest);
    }
}

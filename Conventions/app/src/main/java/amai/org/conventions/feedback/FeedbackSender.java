package amai.org.conventions.feedback;

import android.content.Context;
import android.os.AsyncTask;
import android.provider.Settings;
import android.view.View;

import amai.org.conventions.model.Feedback;

public abstract class FeedbackSender {
    protected Context context;

    public FeedbackSender(Context context) {
        this.context = context;
    }

    public void send() throws Exception {
        Feedback feedback = getFeedback();
        sendFeedback(feedback);
        feedback.setIsSent(true);
        feedback.removeUnansweredQuestions();
    }

    protected abstract void sendFeedback(Feedback feedback) throws Exception;

    protected abstract Feedback getFeedback();

    protected String getDeviceId() {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static abstract class SendFeedbackOnClickListener implements View.OnClickListener {

        /**
         * Called on UI thread before starting the background task
         */
        protected void beforeStart() {
        }

        /**
         * Called before sending the feedback, in the background
         */
        protected void beforeSend() {
        }

        /**
         * Called after sending the feedback, in the background. The feedback state is updated before calling this method.
         */
        protected void afterSend() {
        }

        /**
         * Called on UI thread after the feedback is sent, before onSuccess and onFailure calls.
         */
        protected void afterEnd(Exception exception) {
        }

        protected void onFailure(Exception exception) {
        }

        protected void onSuccess() {
        }

        protected abstract FeedbackSender getFeedbackSender();

        @Override
        public void onClick(View v) {
            beforeStart();

            new AsyncTask<Void, Void, Exception>() {

                @Override
                protected Exception doInBackground(Void... params) {
                    try {
                        beforeSend();
                        getFeedbackSender().send();
                        afterSend();

                        // In case everything finished successfully, pass null to onPostExecute.
                        return null;
                    } catch (Exception e) {
                        return e;
                    }
                }

                @Override
                protected void onPostExecute(Exception exception) {
                    afterEnd(exception);

                    if (exception != null) {
                        onFailure(exception);
                    } else {
                        onSuccess();
                    }
                }

            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }
    }
}

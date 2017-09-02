package amai.org.conventions.feedback;

import android.os.AsyncTask;
import android.provider.Settings;
import android.view.View;

import amai.org.conventions.ConventionsApplication;
import amai.org.conventions.model.Survey;

public abstract class SurveySender {

    public SurveySender() {
    }

    public void send() throws Exception {
        Survey survey = getSurvey();
        sendSurvey(survey);
        survey.setIsSent(true);
        survey.removeUnansweredQuestions();
    }

    protected abstract void sendSurvey(Survey survey) throws Exception;

    protected abstract Survey getSurvey();

    public static String getDeviceId() {
        return Settings.Secure.getString(ConventionsApplication.getAppContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static abstract class SendSurveyOnClickListener implements View.OnClickListener {

        /**
         * Called on UI thread before starting the background task
         */
        protected void beforeStart() {
        }

        /**
         * Called before sending the survey, in the background
         */
        protected void beforeSend() {
        }

        /**
         * Called after sending the survey, in the background. The survey state is updated before calling this method.
         */
        protected void afterSend() {
        }

        /**
         * Called on UI thread after the survey is sent, before onSuccess and onFailure calls.
         */
        protected void afterEnd(Exception exception) {
        }

        protected void onFailure(Exception exception) {
        }

        protected void onSuccess() {
        }

        protected abstract SurveySender getSurveySender();

        @Override
        public void onClick(View v) {
            beforeStart();

            new AsyncTask<Void, Void, Exception>() {

                @Override
                protected Exception doInBackground(Void... params) {
                    try {
                        beforeSend();
                        getSurveySender().send();
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

package amai.org.conventions.events;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.stream.IntStream;

import sff.org.conventions.R;
import amai.org.conventions.model.FeedbackQuestion;
import amai.org.conventions.model.Survey;
import amai.org.conventions.utils.StateList;

public class FiveStarsAnswersViewBuilder {

    private static final int NUMBER_OF_STARS = 5;

    private Context context;
    private ColorStateList textColor;
    private Runnable onAnswerChanged = () -> {};

    public static FiveStarsAnswersViewBuilder withContext(Context context, ColorStateList textColor) {
        FiveStarsAnswersViewBuilder builder = new FiveStarsAnswersViewBuilder();
        builder.context = context;
        builder.textColor = textColor;
        return builder;
    }

    public FiveStarsAnswersViewBuilder onAnswerChange(Runnable onAnswerChanged) {
        this.onAnswerChanged = onAnswerChanged;
        return this;
    }

    public View build(FeedbackQuestion question, Survey feedback) {
        LinearLayout starsLayout = new LinearLayout(context);
        starsLayout.setOrientation(LinearLayout.HORIZONTAL);
        IntStream
                .range(1, NUMBER_OF_STARS + 1) // so that the answers sent won't be zero-based
                .mapToObj(i -> {
                    ImageView image = new ImageView(context);
                    image.setImageResource(R.drawable.feedback_star_empty);
                    image.setTag(i);
                    int imageIconSize = context.getResources().getDimensionPixelOffset(R.dimen.feedback_five_stars_icon_size);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(imageIconSize, imageIconSize);
                    int marginSize = context.getResources().getDimensionPixelOffset(R.dimen.feedback_five_stars_answer_margin);
                    layoutParams.setMargins(marginSize, marginSize, 0, 0);
                    image.setLayoutParams(layoutParams);
                    image.setOnClickListener(view -> {
                        if (feedback.isSent()) {
                            return;
                        }
                        int index = (int) view.getTag();
                        if (question.getAnswer() != null && (int) question.getAnswer() == index) {
                            // Clicking the selected answer cancels it
                            question.setAnswer(null);
                            setSelectedStarsInLayout(0, starsLayout);
                        } else {
                            question.setAnswer(index);
                            setSelectedStarsInLayout(index, starsLayout);
                        }

                        onAnswerChanged.run();
                    });
                    return image;
                })
                .forEach(starsLayout::addView);

        if (question.getAnswer() != null) {
            setSelectedStarsInLayout((int) question.getAnswer(), starsLayout);
        } else {
            setSelectedStarsInLayout(0, starsLayout);
        }

        return starsLayout;
    }

    private void setSelectedStarsInLayout(int selectedStarIndex, LinearLayout starsLayout) {
        IntStream
                .range(0, NUMBER_OF_STARS)
                .forEach(i -> {
                    ImageView image = (ImageView) starsLayout.getChildAt(i);

                    StateList answerState = new StateList(R.attr.state_feedback_answer, R.attr.state_feedback_answer_type_five_stars);
                    StateList selectedAnswerState = answerState.clone().add(R.attr.state_feedback_answer_selected);

                    int answerColor = answerState.getColor(textColor);
                    int selectedAnswerColor = selectedAnswerState.getColor(textColor);

                    image.setColorFilter(i <= selectedStarIndex - 1 ? selectedAnswerColor : answerColor, PorterDuff.Mode.SRC_IN);
                    image.setImageResource(i <= selectedStarIndex - 1 ? R.drawable.feedback_star_full : R.drawable.feedback_star_empty);
                });
    }
}

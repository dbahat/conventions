package amai.org.conventions.updates;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import amai.org.conventions.R;
import amai.org.conventions.model.Dates;
import amai.org.conventions.model.Update;

public class UpdateViewHolder extends RecyclerView.ViewHolder {
    private TextView updateText;
    private TextView updateTime;
    private TextView updateDay;

    public UpdateViewHolder(View itemView) {
        super(itemView);

        updateText = (TextView) itemView.findViewById(R.id.update_text);
        updateTime = (TextView) itemView.findViewById(R.id.update_time);
        updateDay = (TextView) itemView.findViewById(R.id.update_day);
    }

    public void setContent(Update update) {
        updateText.setText(update.getText());
        updateTime.setText(Dates.formatHoursAndMinutes(update.getDate()));
        updateDay.setText(Dates.formatDateWithoutTime(update.getDate()));
    }
}

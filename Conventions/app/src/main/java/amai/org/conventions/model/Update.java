package amai.org.conventions.model;

import java.util.Date;

public class Update {
    private String text;
    private Date date;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Update withText(String text) {
        setText(text);
        return this;
    }

    public Update withDate(Date date) {
        setDate(date);
        return this;
    }
}

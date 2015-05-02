package amai.org.conventions.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Dates {
    public static Date now() {
        // TODO this is a mock for testing purpose. Change to new Date() when it's the real app.
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:ss");
        try {
            return dateFormat.parse("05.03.2015 14:47");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}

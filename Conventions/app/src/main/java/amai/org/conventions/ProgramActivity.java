package amai.org.conventions;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import amai.org.conventions.R;

public class ProgramActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program);

        Toolbar toolbar = (Toolbar) findViewById(R.id.program_toolbar);
        toolbar.initialize();
        toolbar.configureMiddleSpinner(R.array.program_pages);
    }
}

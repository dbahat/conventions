package amai.org.conventions.updates;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.Update;
import amai.org.conventions.navigation.NavigationActivity;

public class UpdatesActivity extends NavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentInContentContainer(R.layout.activity_updates);

        setToolbarTitle(getResources().getString(R.string.updates));

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.updates_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Update> updates = new ArrayList<>(Convention.getInstance().getUpdates());
        Collections.sort(updates, new Comparator<Update>() {
            @Override
            public int compare(Update lhs, Update rhs) {
                // Sort the updates so the latest message would appear first.
                return rhs.getDate().compareTo(lhs.getDate());
            }
        });
        recyclerView.setAdapter(new UpdatesAdapter(updates));
    }
}

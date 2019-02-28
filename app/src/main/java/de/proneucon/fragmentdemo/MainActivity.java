package de.proneucon.fragmentdemo;

import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //INNERE KLASSEN:***********************************************
    public static class AuswahlFragment extends ListFragment {

    }

    public static class DetailFragment extends Fragment {

    }



}

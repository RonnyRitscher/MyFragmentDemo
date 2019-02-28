package de.proneucon.fragmentdemo;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //LOG-TAG
    private static final String TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //INNERE KLASSEN:**************************************************
    //
    public static class AuswahlFragment extends ListFragment {
        // durch den extends kann gleich der Adapter gesetzt werden

        //zustand der App speichern
        public static final String STR_ZULETZT_SELEKTIERT = "zuletztSelektiert";    //- KeyValue
        boolean imLandscape;   //
        int zuletztSelektiert = 0;

        //--------------------------------------------------
        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            //ADAPTER
            setListAdapter(new ArrayAdapter<>(
                    getActivity() , //
                    android.R.layout.simple_list_item_activated_1 ,     // -> um auch einen Eintrag auswählen zu können
                    new String[] {"eins" , "zwei" , "drei"}
            ));

            //VIEW
            View detailsFrame = getActivity().findViewById(R.id.details);
                // finden wir in der Activity

            imLandscape = detailsFrame!=null && detailsFrame.getVisibility()==View.VISIBLE ;
                // detailsFrame!=null -> prüft ob leer
                // detailsFrame.getVisibility()==View.VISIBLE  -> prüft ob sichtbar

            if(savedInstanceState!=null){
                //übergibt (wenn vorhanden) den aktuelle Selektion
                zuletztSelektiert = savedInstanceState.getInt(STR_ZULETZT_SELEKTIERT , 0);
            }

            //Damit der ChoiceMode auch in beiden Modes angezeigt wird
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE); //AuswahlModus

            if(imLandscape){
                detailsAnzeigen(zuletztSelektiert);        //Methode dA in der MainActivity
            }
        }

        //--------------------------------------------------
        //SAVE-ON-INSTANCE-STATE
        @Override
        public void onSaveInstanceState(@NonNull Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putInt(STR_ZULETZT_SELEKTIERT , zuletztSelektiert);
        }

        //--------------------------------------------------
        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            detailsAnzeigen(position);      //beim drücken wird das ELEMENT-X angezeigt
        }

        //--------------------------------------------------
        //soll es direkt (als weiterleitung zum Frame) angezeigt werden oder im 2 spalten-Modus
        private void detailsAnzeigen(int zuletztSelektiert) {
            this.zuletztSelektiert = zuletztSelektiert;

            //TODO zuletztSelektiert
            Log.d(TAG, "detailsAnzeigen: zuletztSelektiert: " + zuletztSelektiert);

            if(imLandscape){
                getListView().setItemChecked(zuletztSelektiert , true); // setItemChecked -> setzt den eintrag auf true
                getListView().post( () -> getListView().setItemChecked( zuletztSelektiert , true));

                //Detailsfragment soll gebaut werden -> über FragmentManager und findFragment*
                DetailFragment details = (DetailFragment) getFragmentManager().findFragmentById(R.id.details);

                if(details==null || details.getIndex()!=zuletztSelektiert ){ //Was, wenn wir im Landscape-Modus sind?:
                    //Wenn es nicht den Einstellungen entspricht, soll es neu erstellt werden
                    details = DetailFragment.newInstance(zuletztSelektiert);

                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    //ersetze ein vorhandenes durch ein anderes...
                    fragmentTransaction.replace(R.id.details , details);
                    //übergang-> wie soll der WEchsel wahrgenommen/ausgeführt werden (SZENENSCHNITT)
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    //ausführen
                    fragmentTransaction.commit();
                }
            }else{ //Was wenn wir im Protrait-Modus sind?:

                Intent intent = new Intent();
                intent.setClass(getActivity() , DetailsActivity.class);     //!!! in ein Fragment können wir kein intent schicken !
                intent.putExtra(DetailFragment.INDEX , zuletztSelektiert);  //INDEX in der DetailsFragment
                startActivity(intent);

            }
        }
    }
    //**************************************************
    // VERWENDET im LANDSCAPE-MODUS
    public static class DetailFragment extends Fragment {

        public static final String INDEX = "index";
        //--------------------------------
        public static DetailFragment newInstance(int indexZuletztSelektiert) {
            //Bauen eines DetailFragments:
            DetailFragment fragment = new DetailFragment(); //erzeugen des DetailFragment

            //TODO indexZuletztSelektiert
            Log.d(TAG, "newInstance: indexZuletztSelektiert: "+ indexZuletztSelektiert);

            //erstelle und gebe den Bundle mit:
            Bundle args = new Bundle();                     //erzeugt Bundle
            args.putInt(INDEX , indexZuletztSelektiert);    //gibt den index rein
            fragment.setArguments(args);                    //setzen der übergebenen argumente
            return fragment;                                // zurückgeben des DetailFragments
        }
        //--------------------------------
        public int getIndex(){      //besorge den Index
            return getArguments().getInt(INDEX , 0);
        }

        //--------------------------------
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            //bauen und eine ScrollView programmatisch zusammen
            ScrollView scrollView = null;

            //prügen ob der container da ist
            if(container!=null){
                scrollView = new ScrollView(getActivity());     //wie unsere Activity, bzw wird geholt

                TextView textView = new TextView(getActivity());
                //füge die TV der SC hinzu
                scrollView.addView(textView);
                // Text anzeigen
                textView.setText("Element #" + (getIndex() + 1) + " ist sichtbar"); //+1 , da der Index bei 0 begint
            }
            //Rückgabe der jetzt erzeugten ScrollView
            return scrollView;
        }
    }

    //**************************************************
    public static class DetailsActivity extends AppCompatActivity{
        //TODO DetailsActivity
        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            //BERÜCKSICHTIGE die orientierung  -> wenn wir das Handy drehen
            //prüfe ob im landscape-Modus
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                finish();                                   //Activity soll beendet werden
                return;                                     //Methode verlassen wenn es nicht so ist
            }

            if(savedInstanceState==null){
                DetailFragment detailFragment = new DetailFragment();

                detailFragment.setArguments( getIntent().getExtras() );     //setzt die übergebenen Argumente in das detailFragment

                getSupportFragmentManager()
                        .beginTransaction()
                        .add( android.R.id.content , detailFragment )
                        .commit();

            }
        }
    }



}

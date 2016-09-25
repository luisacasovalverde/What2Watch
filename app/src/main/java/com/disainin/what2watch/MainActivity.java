package com.disainin.what2watch;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements MainFragment.OnFragmentInteractionListener {

    private Toolbar toolbar;

//    private static final String TAG_SEARCH_FRAGMENT = "fragment_search_task";
//    private SearchFragment fragmentSearchTask;
//    FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.general_home));

        setNavigationActions();


//        fm = getSupportFragmentManager();
//        mTaskFragment = (SearchFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);
//
//        // If the Fragment is non-null, then it is currently being
//        // retained across a configuration change.
//        if (mTaskFragment == null) {
//            mTaskFragment = new SearchFragment();
//            fm.beginTransaction().add(R.id.search_content, mTaskFragment, TAG_TASK_FRAGMENT).commit();
//        }


//        loadViews();
//        loadActions();
    }


    private void setNavigationActions() {
        /*navigation_view = (NavigationView) findViewById(R.id.navigation_view);

        navigation_view.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        MainFragment fragment = new MainFragment();

                        switch (menuItem.getItemId()) {
                            case R.id.nav_item_home:
                                fragment.setOption(R.id.nav_item_home);
                                break;
                            case R.id.nav_item_ajustes:
                                fragment.setOption(R.id.nav_item_ajustes);
//                                Toast.makeText(getApplicationContext(), "AJUSTES", Toast.LENGTH_SHORT).show();
                                break;
                        }

                        ft.replace(R.id.fragment_content, fragment).commit();
                        menuItem.setChecked(true);
                        getSupportActionBar().setTitle(menuItem.getTitle());

                        drawerLayout.closeDrawer(GravityCompat.START);

                        return true;
                    }

                }

        );*/
    }


    public void loadViews() {
//        Typeface fontawesome = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
//        Typeface font_roboto_thin = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

//    public void hideSoftKeyboard() {
//        if (getCurrentFocus() != null) {
//            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
//        }
//    }
//
//    public void loadActions() {
//        btn_accion.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getJSON(input_pelicula.getText() + "");
//            }
//        });
//
//        btn_mic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                new BusquedaVoz(getApplicationContext(), txt_queryvoice, input_pelicula);
//            }
//        });
//
//        layout_base.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                    if (input_pelicula.isFocused()) {
//                        input_pelicula.clearFocus();
//                        hideSoftKeyboard();
//                    }
//                }
//                return false;
//            }
//        });
//    }
//
//    public void getJSON(String query) {
//        new AdapterAPI(this, 0, query, vistas);
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_actionbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                startActivity(new Intent(this, SearchActivity.class));
                this.overridePendingTransition(R.animator.pull_right, R.animator.push_left);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

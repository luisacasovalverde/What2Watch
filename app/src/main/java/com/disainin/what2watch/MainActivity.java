package com.disainin.what2watch;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements MainFragment.OnFragmentInteractionListener {

    //    private RelativeLayout layout_base;
//    private Button btn_accion, btn_mic;
    private TextView txt_resultado, txt_queryvoice, nav_header_text;
    //    private EditText input_pelicula;
//    public static TextView[] vistas;


    private DrawerLayout drawerLayout;
    private NavigationView navigation_view;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.home));


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.bv_reconociendo, R.string.bv_reconociendo);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        setNavigationActions();


//        cargarViews();
//        loadActions();
    }


    private void setNavigationActions() {
        navigation_view = (NavigationView) findViewById(R.id.navigation_view);

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

                });
    }


    public void cargarViews() {
//        Typeface fontawesome = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
        Typeface font_roboto_thin = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");

//        nav_header_text = (TextView) nav_header.findViewById(R.id.nav_header_text);
//        nav_header_text.setText("hola");


//        btn_accion = (Button) findViewById(R.id.btn_accion);
//        btn_accion.setTypeface(fontawesome);
//
//        txt_resultado = (TextView) findViewById(R.id.txt_resultado);
//
//        txt_queryvoice = (TextView) findViewById(R.id.txt_queryvoice);
//
//        input_pelicula = (EditText) findViewById(R.id.input_pelicula);
//
//        btn_mic = (Button) findViewById(R.id.btnSpeak);
//        btn_mic.setTypeface(fontawesome);
//
//        vistas = new TextView[]{
//                txt_resultado,
//                input_pelicula
//        };
//
//
//        layout_base = (RelativeLayout) findViewById(R.id.layout_base);
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
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                startActivity(new Intent(this, BuscarActivity.class));
                this.overridePendingTransition(R.animator.pull_right, R.animator.push_left);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

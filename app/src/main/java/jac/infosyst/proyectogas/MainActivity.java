package jac.infosyst.proyectogas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import jac.infosyst.proyectogas.FragmentDrawer;

import jac.infosyst.proyectogas.fragments.PedidosFragment;
import jac.infosyst.proyectogas.fragments.OperadorFragment;
import jac.infosyst.proyectogas.utils.Sessions;

public class MainActivity extends AppCompatActivity
     //   implements NavigationView.OnNavigationItemSelectedListener {
        implements FragmentDrawer.FragmentDrawerListener{


    private static String TAG = MainActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;

   // Bundle bundle;
    PedidosFragment pedidoObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


*/

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);

        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        // display the first navigation drawer view on app launch
        displayView(0);


       // bundle = new Bundle();
       // pedidoObj = new PedidosFragment();
      //  pedidoObj.setArguments(bundle);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

/*
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_pedidos) {
            // Handle the camera action

        } else if (id == R.id.nav_operador) {

        } else if (id == R.id.nav_mapa) {
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_ajustes) {

        } else if (id == R.id.nav_prealizados) {

        } else if (id == R.id.nav_cerrar) {


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

*/

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);


        switch (position) {
            case 0:
/*
                Bundle bundle = new Bundle();
                bundle.putString("tipoPedidos", "From Activity");
                PedidosFragment fragobj = new PedidosFragment();
                fragobj.setArguments(bundle);
*/


                //strSess.setSesnameTitle("Pedidos");
               // bundle.putString("tipoPedidos", "Pedidos");
                title = getString(R.string.title_pedidos);
                //Toast.makeText(this, "asignando..." +  strSess.getSesnameTitle(), Toast.LENGTH_SHORT).show();

                fragment = new PedidosFragment();
                break;
/*
            case 1:
                fragment = new OperadorFragment();
                title = getString(R.string.title_operador);
                break;
            case 101:
                fragment = new DetallePedidoFragment();Result
                title = "Datos Cliente";
                break;
*/

            case 1:
                fragment = new OperadorFragment();
                title = getString(R.string.title_operador);
                break;

            case 2:
                fragment = new MapsActivity();
                title = getString(R.string.title_mapa);
                break;
            case 3:
                Intent i = new Intent(MainActivity.this, Configuracion.class);
                startActivity(i);
                ((Activity) MainActivity.this).overridePendingTransition(0,0);

                break;

            case 4:
               // strSess.setSesnameTitle("Pedidos Realizados");
                title = getString(R.string.title_pedidosrealizados);

                fragment = new PedidosFragment();
                break;

            case 5:
                Intent i2 = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i2);
                ((Activity) MainActivity.this).overridePendingTransition(0,0);

                break;

            default:
                break;




        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);




        }
    }




}

package jac.infosyst.proyectogas;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jac.infosyst.proyectogas.R;
import  jac.infosyst.proyectogas.adaptadores.NavigationDrawerAdapter;
import jac.infosyst.proyectogas.modelo.Imagen;
import jac.infosyst.proyectogas.modelo.NavDrawerItem;
import jac.infosyst.proyectogas.modelo.ObjetoRes;
import jac.infosyst.proyectogas.modelo.UsuarioInfo;
import jac.infosyst.proyectogas.utils.SQLiteDBHelper;
import jac.infosyst.proyectogas.utils.ServicioUsuario;
import jac.infosyst.proyectogas.utils.Sessions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class FragmentDrawer extends Fragment {

    private static final int DATABASE_VERSION = 1;
    protected static final String DATABASE_NAME = "proyectoGas";
    private static String TAG = FragmentDrawer.class.getSimpleName();
    private RecyclerView recyclerView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private NavigationDrawerAdapter adapter;
    private static String[] titles = null;
    private static int icons[] = null;
    private FragmentDrawerListener drawerListener;
    private View containerView;
    private ImageView imageViewPerfil;

    static String strUsuarioRol;

    private String BASEURL = "";
    Sessions objSessions;
    Bitmap decodedByte;
    String strIP = "";
    String strtoken = "";
    String archivo = "";

    private static SQLiteDBHelper sqLiteDBHelper = null;
    private static String DB_NAME = "proyectogas11.db";
    private static int DB_VERSION = 1;

    public FragmentDrawer() {
    }

    public void setDrawerListener (FragmentDrawerListener listener){
        this.drawerListener = listener;
    }
        public static List<NavDrawerItem> getData () {
        List<NavDrawerItem> data = new ArrayList<>();
        // preparing navigation drawer items
        for (int i = 0; i < titles.length; i++) {
            NavDrawerItem navItem = new NavDrawerItem();
                navItem.setTitle(titles[i]);
                navItem.setimgMenu(icons[i]);
            data.add(navItem);
        }

            if(strUsuarioRol.equals("Operador")) {
                data.remove(3);
            }
            return data;
    }

        @Override
        public void onCreate (Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            // drawer labels
            titles = getActivity().getResources().getStringArray(R.array.nav_drawer_labels);
            icons = getActivity().getResources().getIntArray(R.array.nav_drawer_icons);
            strUsuarioRol = ((Sessions)getActivity().getApplication()).getsesUsuarioRol();
        }

        @Override
        public View onCreateView (LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState){

            final View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

            if(UsuarioInfo.getFoto() == null) {
                Sessions strSess = new Sessions();
                sqLiteDBHelper = new SQLiteDBHelper(getActivity(), DATABASE_NAME, null, DATABASE_VERSION);
                final SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();

                String sql = "SELECT * FROM config WHERE id = 1 ORDER BY id DESC limit 1";

                final int recordCount = db.rawQuery(sql, null).getCount();

                final Cursor record = db.rawQuery(sql, null);

                if (record.moveToFirst()) {
                    strIP = record.getString(record.getColumnIndex("ip"));
                }

                sqLiteDBHelper = new SQLiteDBHelper(getActivity(), DATABASE_NAME, null, DATABASE_VERSION);

                final SQLiteDatabase db3 = sqLiteDBHelper.getWritableDatabase();

                String sql3 = "SELECT * FROM usuario ORDER BY id DESC limit 1";

                final int recordCount3 = db.rawQuery(sql3, null).getCount();
                //  Toast.makeText(getActivity(), "CONTADOR PEDIDOS: " + recordCount3, Toast.LENGTH_LONG).show();

                SQLiteDatabase dbConn3 = sqLiteDBHelper.getWritableDatabase();

                Cursor cursor3 = dbConn3.rawQuery(sql3, null);

                if (cursor3.moveToFirst()) {
                    strtoken = cursor3.getString(cursor3.getColumnIndex("token"));
                }

            BASEURL = strIP + "glpservices/webresources/glpservices/";
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASEURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            final ServicioUsuario service = retrofit.create(ServicioUsuario.class);

            Call call = service.Foto(UsuarioInfo.getOid(), "2", strtoken);
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    if (response.isSuccessful()) {
                        ObjetoRes resObj = (ObjetoRes) response.body();

                        if (resObj.geterror().equals("false")) {
                         List<Imagen> arrayListImagen = Arrays.asList(resObj.getImagen());
                            archivo = arrayListImagen.get(0).getArchivo();
                            decodedByte = decodeBase64(archivo);
                            UsuarioInfo uss = new UsuarioInfo();
                            uss.setFoto(decodedByte);
                            imageViewPerfil = (ImageView) layout.findViewById(R.id.profile_image) ;
                            imageViewPerfil.setImageBitmap(UsuarioInfo.getFoto());


                        }

                    }
                }
                @Override
                public void onFailure(Call call, Throwable t) {
                }
            });
        } else{
            imageViewPerfil = (ImageView) layout.findViewById(R.id.profile_image) ;
            imageViewPerfil.setImageBitmap(UsuarioInfo.getFoto());
        }
            TextView textViewInterno = (TextView) layout.findViewById(R.id.Nom_Operador);
            textViewInterno.setText(UsuarioInfo.getNombre());

        recyclerView = (RecyclerView) layout.findViewById(R.id.drawerList);
        adapter = new NavigationDrawerAdapter(getActivity(), getData());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                drawerListener.onDrawerItemSelected(view, position);
                mDrawerLayout.closeDrawer(containerView);
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));

        return layout;
    }

        public void setUp ( int fragmentId, DrawerLayout drawerLayout,final Toolbar toolbar){
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                toolbar.setAlpha(1 - slideOffset / 2);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }

        public static interface ClickListener {
            public void onClick(View view, int position);
            public void onLongClick(View view, int position);
        }

        static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

            private GestureDetector gestureDetector;
            private ClickListener clickListener;

            public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
                this.clickListener = clickListener;
                gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }

                    @Override
                    public void onLongPress(MotionEvent e) {
                        View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                        if (child != null && clickListener != null) {
                            clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                        }
                    }
                });
            }

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                    clickListener.onClick(child, rv.getChildPosition(child));
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        }

        public interface FragmentDrawerListener {
            public void onDrawerItemSelected(View view, int position);
        }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input.getBytes(), Base64.DEFAULT);
        BitmapFactory.Options options;

        try {
            options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length, options);
            return bitmap;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }
}

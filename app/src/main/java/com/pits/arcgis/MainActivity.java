package com.pits.arcgis;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.esri.android.map.MapOptions;
import com.esri.android.map.MapView;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.tasks.na.NAFeaturesAsFeature;
import com.esri.core.tasks.na.RouteParameters;
import com.esri.core.tasks.na.RouteResult;
import com.esri.core.tasks.na.RouteTask;
import com.esri.core.tasks.na.StopGraphic;

public class MainActivity extends AppCompatActivity {

    private MapView mMapView;

    // The basemap switching menu items.
    MenuItem mStreetsMenuItem;
    MenuItem mTopoMenuItem;
    MenuItem mGrayMenuItem;
    MenuItem mOceansMenuItem;

    // Create MapOptions for each type of basemap.
    final MapOptions mTopoBasemap = new MapOptions(MapOptions.MapType.TOPO);
    final MapOptions mStreetsBasemap = new MapOptions(MapOptions.MapType.STREETS);
    final MapOptions mGrayBasemap = new MapOptions(MapOptions.MapType.GRAY);
    final MapOptions mOceansBasemap = new MapOptions(MapOptions.MapType.OCEANS);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.enableWrapAround(true);
        final String routeTaskURL = "http://utility.arcgis.com/usrsvcs/servers/17d6ec37320149b49e6adf1045678e3a/rest/services/World/Route/NAServer/Route_World";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    RouteTask routeTask = RouteTask.createOnlineRouteTask(routeTaskURL, null);
                    RouteParameters routeParameters = routeTask.retrieveDefaultRouteTaskParameters();
                    routeParameters.setImpedanceAttributeName("Length");
                    NAFeaturesAsFeature naFeatures = new NAFeaturesAsFeature();
                    SimpleMarkerSymbol sms = new SimpleMarkerSymbol(
                            Color.RED, 5, SimpleMarkerSymbol.STYLE.CIRCLE);
                    com.esri.core.geometry.Point startPoint = new com.esri.core.geometry.Point();
                    startPoint.setXY(48.859101, 2.292229);
                    com.esri.core.geometry.Point stopPoint = new com.esri.core.geometry.Point();
                    stopPoint.setXY(48.860589, 2.290870);

                    Graphic graphic1 = new Graphic(startPoint, sms);
                    Graphic graphic2 = new Graphic(stopPoint, sms);

                    StopGraphic startPnt = new StopGraphic(graphic1);
                    StopGraphic stopPnt = new StopGraphic(graphic2);
                    naFeatures.setFeatures(new Graphic[] {startPnt, stopPnt});
                    routeParameters.setStops(naFeatures);
                    RouteResult mResults = routeTask.solve(routeParameters);
                    System.out.println(mResults.getStops());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.unpause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // Get the basemap switching menu items.
        mStreetsMenuItem = menu.getItem(0);
        mTopoMenuItem = menu.getItem(1);
        mGrayMenuItem = menu.getItem(2);
        mOceansMenuItem = menu.getItem(3);

        // Also set the topo basemap menu item to be checked, as this is the default.
        mTopoMenuItem.setChecked(true);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle menu item selection.
        switch (item.getItemId()) {
            case R.id.World_Street_Map:
                mMapView.setMapOptions(mStreetsBasemap);
                mStreetsMenuItem.setChecked(true);
                return true;
            case R.id.World_Topo:
                mMapView.setMapOptions(mTopoBasemap);
                mTopoMenuItem.setChecked(true);
                return true;
            case R.id.Gray:
                mMapView.setMapOptions(mGrayBasemap);
                mGrayMenuItem.setChecked(true);
                return true;
            case R.id.Ocean_Basemap:
                mMapView.setMapOptions(mOceansBasemap);
                mOceansMenuItem.setChecked(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

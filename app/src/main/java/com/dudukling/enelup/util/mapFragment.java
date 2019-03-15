package com.dudukling.enelup.util;

import android.os.Bundle;

import com.dudukling.enelup.dao.lpDAO;
import com.dudukling.enelup.ligProvFormActivity;
import com.dudukling.enelup.model.lpModel;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class mapFragment extends SupportMapFragment implements OnMapReadyCallback {

    private static ligProvFormActivity activity;
    private static double[] latitude = {0};
    private static double[] longitude = {0};
    private lpModel lp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (ligProvFormActivity) getActivity();
        getMapAsync(this);
    }

//    private static void getGPSFromInput(){
////        TextInputLayout editTextGPSLatitude = activity.findViewById(R.id.editTextGPSLatitude);
////        String StringGPSLatitude = editTextGPSLatitude.getEditText().getText().toString();
////
////        TextInputLayout editTextGPSLongitude = activity.findViewById(R.id.editTextGPSLongitude);
////        String StringGPSLongitude = editTextGPSLongitude.getEditText().getText().toString();
//
////        if(!StringGPSLatitude.equals("") && !StringGPSLongitude.equals("")){
////            try{
////                latitude[0] = Double.parseDouble(StringGPSLatitude);
////                longitude[0] = Double.parseDouble(StringGPSLongitude);
////            }catch(NumberFormatException ignored){}
////        }
//    }

    public void onMapReady(GoogleMap googleMap) {

        lp = mapsController.lp;
        String lat = lp.getLatitude();
        String lng = lp.getLongitude();
        latitude[0] = Double.parseDouble(lat);
        longitude[0] = Double.parseDouble(lng);


        if(latitude[0] !=0 && longitude[0] !=0){
            LatLng posicao = new LatLng(latitude[0], longitude[0]);

            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(posicao, 14);
            googleMap.moveCamera(update);

            MarkerOptions marker = new MarkerOptions();
            marker.position(posicao);
            marker.title(lp.getCliente());
            marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            marker.snippet(lp.getTipoOrdem());
            googleMap.addMarker(marker);
        }else{
            // ENEL: -22.502650, -43.171077
            latitude[0] = -22.502650;
            longitude[0] = -43.171077;

            LatLng posicao = new LatLng(latitude[0], longitude[0]);
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(posicao, 11);
            googleMap.moveCamera(update);

//            MarkerOptions marker = new MarkerOptions();
//            marker.position(posicao);
//            marker.title(lp.getCliente());
//            marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
//            marker.snippet("Sem informações de GPS");
//            googleMap.addMarker(marker);
        }


        showAllOtherMarkers(googleMap);
    }

    private void showAllOtherMarkers(GoogleMap googleMap) {
        lpDAO dao = new lpDAO(activity);
        List<lpModel> lpList = dao.getGPSList(lp.getId());
        dao.close();

        for(int i=0; i<lpList.size(); i++) {
            String lat2 = lpList.get(i).getLatitude();
            String lng2 = lpList.get(i).getLongitude();
            String ordem = lpList.get(i).getOrdem();
            String etapa = lpList.get(i).getEtapa();

            double[] latitude2 = {0};
            double[] longitude2 = {0};
            latitude2[0] = Double.parseDouble(lat2);
            longitude2[0] = Double.parseDouble(lng2);

            if(latitude2[0]!=0 && longitude2[0]!=0){
                LatLng posicao2 = new LatLng(latitude2[0], longitude2[0]);

                MarkerOptions marker2 = new MarkerOptions();
                marker2.position(posicao2);
                marker2.title(lp.getCliente());
                marker2.snippet(lp.getTipoOrdem());

                switch(etapa.substring(0, 3)){
                    case "CAC":
                    case "ANA":
                        marker2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        break;
                    case "INP":
                    case "EXE":
                        marker2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                        break;

                }

                googleMap.addMarker(marker2);
            }
        }
    }
}
package com.dudukling.enelz.util;

import android.os.Bundle;

import com.dudukling.enelz.ligProvFormActivity;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class mapFragment extends SupportMapFragment implements OnMapReadyCallback {

    private static ligProvFormActivity activity;
    private static double[] latitude = {0};
    private static double[] longitude = {0};


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = (ligProvFormActivity) getActivity();
        getMapAsync(this);
    }

    private static void getGPSFromInput(){
//        TextInputLayout editTextGPSLatitude = activity.findViewById(R.id.editTextGPSLatitude);
//        String StringGPSLatitude = editTextGPSLatitude.getEditText().getText().toString();
//
//        TextInputLayout editTextGPSLongitude = activity.findViewById(R.id.editTextGPSLongitude);
//        String StringGPSLongitude = editTextGPSLongitude.getEditText().getText().toString();

//        if(!StringGPSLatitude.equals("") && !StringGPSLongitude.equals("")){
//            try{
//                latitude[0] = Double.parseDouble(StringGPSLatitude);
//                longitude[0] = Double.parseDouble(StringGPSLongitude);
//            }catch(NumberFormatException ignored){}
//        }
    }

    public void onMapReady(GoogleMap googleMap) {
//        getGPSFromInput();

        // ENEL: -22.502650, -43.171077
        latitude[0] = -22.502650;
        longitude[0] = -43.171077;

//        Toast.makeText(activity, "POSICAO: "+latitude[0]+","+longitude[0], Toast.LENGTH_LONG).show();

        if(latitude[0] !=0 && longitude[0] !=0){
            LatLng posicao = new LatLng(latitude[0], longitude[0]);
            //if(posicao != null){
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(posicao, 18);
            googleMap.moveCamera(update);

            MarkerOptions marker = new MarkerOptions();
            marker.position(posicao);
            marker.title("You are here!");
            //marker.snippet("oi");
            googleMap.addMarker(marker);
            //}
        }
    }
}
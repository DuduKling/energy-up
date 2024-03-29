package com.dudukling.energyup.ligacaoes_provisorias.util;

import android.annotation.SuppressLint;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.dudukling.energyup.R;
import com.dudukling.energyup.ligacaoes_provisorias.ligProvFormActivity;
import com.dudukling.energyup.model.ligProvModel;

public class ligProvMapsController {
    private static ligProvFormActivity activity;
    public static ligProvModel lp;

    public ligProvMapsController(ligProvFormActivity activity) {
        ligProvMapsController.activity = activity;
    }

//    public void getCurrentPlace(double latitude, double longitude) throws IOException {
//        Geocoder geocoder;
//        List<Address> addresses;
//        geocoder = new Geocoder(activity, Locale.getDefault());
//
//        addresses = geocoder.getFromLocation(latitude, longitude, 1);
//
//        //String address = addresses.get(0).getAddressLine(0);
//        //String street = addresses.get(0).getThoroughfare();
//        String neighborhood = addresses.get(0).getSubLocality();
//        String city = addresses.get(0).getLocality();
//        String state = addresses.get(0).getAdminArea();
//        String country = addresses.get(0).getCountryName();
//        //String postalCode = addresses.get(0).getPostalCode();
//        //String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
//
//        //TextView textViewTestMapInfo = activity.findViewById(R.id.textViewTestMapInfo);
//        //textViewTestMapInfo.setText("Endereço: "+address+" | Cidade: "+city+" | Estado: "+ state+ " | País: "+country+" | CEP: "+postalCode+" | Outro: "+knownName);
//
////        TextInputLayout textInputCountry = activity.findViewById(R.id.editTextGeoCountry);
////        EditText editTextCountry = textInputCountry.getEditText();
////        TextInputLayout textInputState = activity.findViewById(R.id.editTextGeoState);
////        EditText editTextState = textInputState.getEditText();
////        TextInputLayout textInputCity = activity.findViewById(R.id.editTextGeoCity);
////        EditText editTextCity = textInputCity.getEditText();
////        TextInputLayout textInputNeighborhood = activity.findViewById(R.id.editTextGeoNeighborhood);
////        EditText editTextNeighborhood = textInputNeighborhood.getEditText();
//
//        //TextInputLayout textInputStreet = activity.findViewById(R.id.editTextGeoStreet);
//        //EditText editTextStreet = textInputStreet.getEditText();
//        //TextInputLayout textInputNumber = activity.findViewById(R.id.editTextGeoNumber);
//        //EditText editTextNumber = textInputNumber.getEditText();
//
////        if (editTextCountry != null) {
////            editTextCountry.setText(country);
////        }
////        if (editTextState != null) {
////            editTextState.setText(state);
////        }
////        if (editTextCity != null) {
////            editTextCity.setText(city);
////        }
////        if (editTextNeighborhood != null) {
////            editTextNeighborhood.setText(neighborhood);
////        }
//
//        //editTextStreet.setText(street);
//        //editTextNumber.setText(knownName);
//    }

    public void startMaps(ligProvModel lp) {
        ligProvMapsController.lp = lp;

        fixScrollForMaps();

        FragmentManager fragManager = activity.getSupportFragmentManager();
        FragmentTransaction tx = fragManager.beginTransaction();
        tx.replace(R.id.mapFrame, new ligProvMapFragment());
        //tx.commit();
        tx.commitAllowingStateLoss();
    }

    @SuppressLint("ClickableViewAccessibility")
    private static void fixScrollForMaps() {
        final ScrollView mainScrollView = activity.findViewById(R.id.main_scrollview);
        ImageView transparentImageView = activity.findViewById(R.id.transparent_image);

        transparentImageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        mainScrollView.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        mainScrollView.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        mainScrollView.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });
    }

}

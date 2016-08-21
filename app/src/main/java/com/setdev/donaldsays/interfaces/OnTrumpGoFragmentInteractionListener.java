package com.setdev.donaldsays.interfaces;

import android.os.Bundle;

/**
 * Created by oscarlafarga on 8/17/16.
 */
public interface OnTrumpGoFragmentInteractionListener {
    void hideSplashScreen();
    void onFragmentInteraction(String previousFragment, String upcomingFragment, Bundle data);
    void trackEvent(String eventName);
    void timeEvent(String eventName);

}
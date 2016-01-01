package s.chavan.finalapp;

import java.util.Observable;

/**
 * Created by Sachin on 5/15/2015.
 */
public class BroadcastObserver extends Observable{

    public void triggerObservers() {
    // Sets the changed flag for this Observable
        setChanged();

    // notify registered observer objects by calling the update method
        notifyObservers();
    }

}

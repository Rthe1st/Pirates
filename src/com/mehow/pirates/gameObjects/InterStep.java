package com.mehow.pirates.gameObjects;

import com.mehow.pirates.Cords;

/**
 * Created by User on 05/03/14.
 */
public class InterStep{

    public final Cords startCords;
    public final Cords endCords;

    public InterStep(Cords tStartCords, Cords tEndCords){
        startCords = tStartCords;
        endCords = tEndCords;
    }
}

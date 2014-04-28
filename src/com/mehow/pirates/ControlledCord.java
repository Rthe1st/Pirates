package com.mehow.pirates;

/**
 * Created by User on 02/02/14.
 */

//Designed purley for sematics/safeguard against screwups
    //Helps clarify logic where a co-ordiante should never dirctly have it value changed

//------------------
//this was mainly desgined to prevent selectedCords and touchedCords getting screwed up in gameLogic
//never got used though
//also this implements the idea pretty shittily
//-----------------

public class ControlledCord {

    private Cords cord;

    public ControlledCord(Cords tCord){
        cord = tCord;
    }

    private Cords get(){
        if(cord != null){
            return cord;
        }else{
            throw new NullPointerException("controlled cord was requested whilst null "+cordInfo());
        }
    }
    public void release(){
        if(cord != null){
            cord = null;
        }else{
            throw new IllegalStateException("Attempted to null controlled cord when already null"+cordInfo());
        }
    }
    public void set(Cords tCord){
        cord = tCord;//should deep copy?
    }
    //try to avoid this function. This class was surpossed to be used a safe guard when state of the cord should be known
    public boolean isSet(){
        return cord == null;
    }
    private String cordInfo(){
        return "Cord value, x: "+cord.x+" y: "+cord.y;
    }
}

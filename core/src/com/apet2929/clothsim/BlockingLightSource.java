package com.apet2929.clothsim;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class BlockingLightSource {
    public LightSource lightSource;
    public LightMask mask;
    public BlockingLightSource(int numRays, float x, float y){
        Vector3 pos = new Vector3(x,y, 0.1f);
        lightSource = new LightSource(pos.x, pos.y);
        mask = new LightMask(numRays, pos.x, pos.y);
    }
}

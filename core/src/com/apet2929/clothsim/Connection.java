package com.apet2929.clothsim;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import java.io.Serializable;


public class Connection implements Serializable {
    private static final float EQUILIBRIUM_DIST = 75;
    private static final float SPRING_CONST = 2f;
    private final Node nodeA, nodeB;

    public Connection(Node nodeA, Node nodeB) {
        this.nodeA = nodeA;
        this.nodeB = nodeB;
    }

    public void update(){
        Vector2 r = new Vector2(nodeA.position.x - nodeB.position.x, nodeA.position.y - nodeB.position.y);
        float dist = r.len() - EQUILIBRIUM_DIST;

        if(dist == 0) r.x += 0.000001; // avoid normalize vector of length 0
        Vector2 dir = r.nor();
        Vector2 fAB = new Vector2(dist * dir.x * -SPRING_CONST, dist * dir.y * -SPRING_CONST);
        Vector2 fBA = new Vector2(dist * -dir.x * -SPRING_CONST, dist * -dir.y * -SPRING_CONST);
        nodeA.applyForce(fAB);
        nodeB.applyForce(fBA);
    }

    public void render(ShapeRenderer sr){
        sr.line(nodeA.position, nodeB.position);
    }

    public boolean hasNode(Node node){
        return node == nodeA || node == nodeB;
    }
}

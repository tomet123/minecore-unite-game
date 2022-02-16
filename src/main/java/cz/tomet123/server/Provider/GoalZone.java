package cz.tomet123.server.Provider;

import net.minestom.server.coordinate.Point;

public class GoalZone {

    int width;
    int radius;
    Point center;
    int points;
    boolean finite;


    public GoalZone(int width, int radius, Point center, int points, boolean finite) {
        this.width = width;
        this.radius = radius;
        this.center = center;
        this.points = points;
        this.finite = finite;


    }


    public void placeGoalZone(){

    }
}

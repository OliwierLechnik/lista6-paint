package org.example.paint;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PolygonShape implements Serializable {

    private static final long serialVersionUID = 2L;
    private static final double upscaleFactor = 1.1;
    private static final double downscaleFactor = 0.909;

    private double[] x;
    private double[] y;
    private int count;
    private double r,g,b;
    private double centerx;
    private double centery;

    /**
     * initialise polygon with list of corner points and colour
     * @param x
     * @param y
     * @param colour
     */
    private PolygonShape(List<Double> x, List<Double> y, Color colour){

        this.x = x.stream().mapToDouble(d -> d).toArray();
        this.y = y.stream().mapToDouble(d -> d).toArray();
        this.count = y.size();
        this.r = colour.getRed();
        this.g = colour.getGreen();
        this.b = colour.getBlue();

        this.updateCenter();

    }

    /**
     * calculate new center of mass
     * to use after every transform
     */
    private void updateCenter(){

        this.centerx = 0;
        this.centery = 0;

        for(int i = 0; i < count; i++){
            this.centerx += this.x[i];
            this.centery += this.y[i];
        }

        this.centerx /= count;
        this.centery /= count;
    }

    /**
     * creates a new rectangle
     * @param origin vector2d of the start drag point
     * @param size vector2d of the size of the shape (can be negative)
     * @param colour
     * @return desired shape
     */
    public static PolygonShape Rectangle(Pair<Double,Double> origin, Pair<Double,Double> size, Color colour){

        ArrayList<Double> x = new ArrayList<Double>();
        ArrayList<Double> y = new ArrayList<Double>();

        x.add(origin.getKey());
        y.add(origin.getValue());

        x.add(origin.getKey()+size.getKey());
        y.add(origin.getValue());

        x.add(origin.getKey()+size.getKey());
        y.add(origin.getValue()+size.getValue());

        x.add(origin.getKey());
        y.add(origin.getValue()+size.getValue());

        return new PolygonShape(x,y, colour);
    }

    /**
     * creates a new triangle
     * @param origin vector2d of the start drag point
     * @param size vector2d of the size of the shape (can be negative)
     * @param colour
     * @return desired shape
     */
    public static PolygonShape Triangle(Pair<Double,Double> origin, Pair<Double,Double> size, Color colour){

        ArrayList<Double> x = new ArrayList<Double>();
        ArrayList<Double> y = new ArrayList<Double>();

        if(size.getValue() >= 0){
            x.add(origin.getKey() + size.getKey()/2);
            y.add(origin.getValue());

            x.add(origin.getKey()+size.getKey());
            y.add(origin.getValue()+ size.getValue());

            x.add(origin.getKey());
            y.add(origin.getValue()+size.getValue());
        }else{
            x.add(origin.getKey());
            y.add(origin.getValue());

            x.add(origin.getKey()+size.getKey());
            y.add(origin.getValue());

            x.add(origin.getKey()+size.getKey()/2.0);
            y.add(origin.getValue()+size.getValue());
        }




        return new PolygonShape(x,y, colour);
    }

    /**
     * creates a new oval
     * @param origin vector2d of the start drag point
     * @param size vector2d of the size of the shape (can be negative)
     * @param colour
     * @return desired shape
     */
    public static PolygonShape Oval(Pair<Double,Double> origin, Pair<Double,Double> size, Color colour){

        ArrayList<Double> x = new ArrayList<Double>();
        ArrayList<Double> y = new ArrayList<Double>();

        for(int i = 0; i < 90; i++){
            x.add(size.getKey()*Math.cos(6.283*i/90.0)/2.0 + origin.getKey() + size.getKey()/2.0);
            y.add(size.getValue()*Math.sin(6.283*i/90.0)/2.0 + origin.getValue() + size.getValue()/2.0);
        }

        return new PolygonShape(x,y, colour);
    }

    /**
     * draws the shape on a GraphicsContext
     * @param surface where to draw the sahape
     */
    public void draw(GraphicsContext surface){

        Color c = (Color) surface.getFill();

        surface.setFill(Color.color(r,g,b));
        surface.fillPolygon(this.x, this.y, this.count);

        surface.setFill(c);

    }


    /**
     * determine the desired rotation by original and current mouse position
     * @param ox coordinate of begin drag point
     * @param oy coordinate of begin drag point
     * @param x coordinate of current mouse position
     * @param y coordinate of current mouse position
     */
    public void rotate(double ox, double oy, double x, double y){

        double angle = Math.atan2(centerx - x,centery - y);

        double oangle = Math.atan2(centerx - ox, centery - oy);

        double dangle = oangle - angle;

        double[] ogx = this.x.clone();
        double[] ogy = this.y.clone();

        for(int i = 0; i<count; i++){
            this.x[i] = (ogx[i]-centerx)*Math.cos(dangle)-(ogy[i]-centery)*Math.sin(dangle) + centerx;
            this.y[i] = (ogy[i]-centery)*Math.cos(dangle)+(ogx[i]-centerx)*Math.sin(dangle) + centery;
        }

    }

    /**
     * move the shape by given value
     * @param dx horizontal offset
     * @param dy vertical offset
     */
    public void move(double dx, double dy){
        for(int i = 0; i < count; i++){
            this.x[i]+=dx;
            this.y[i]+=dy;
        }
        this.updateCenter();
    }

    /**
     * determines whether line given by 2 (x,y) points intersects ox axis at positive x
     * @param ax
     * @param ay
     * @param bx
     * @param by
     * @return
     */
    private static boolean intersectPositive(double ax, double ay, double bx, double by){
        if(ay*by > 0){
            return false;
        }

        if(ay*by == 0){
            return true;
        }

        if(ax==bx){
            return (ax >= 0);
        }

        if(ay==by){
            return ay == 0 && (ax >= 0 || bx >= 0) && !(ax >= 0 && bx >= 0);
        }

        return 0 < bx - by*(ax-bx)/(ay-by);
    }

    /**
     * determines whether the (x,y) point is inside the shape
     * @param x
     * @param y
     * @return
     */
    public boolean contains(double x, double y){

        int sum = 0;

        for(int i = 0; i < count; i++){
            if(intersectPositive(
                    this.x[i]-x,
                    this.y[i]-y,
                    this.x[(i+1)%count]-x,
                    this.y[(i+1)%count]-y
                )){
                sum+=1;
            }
        }

        return sum%2==1;
    }

    /**
     * scales the shape by given factor
     * and ensures that the center is in the same coordinates
     * @param factor
     */
    private void scale(double factor){
        for(int i = 0; i<count; i++){
            this.x[i]*=factor;
            this.y[i]*=factor;
        }

        double newcenterx = 0;
        double newcentery = 0;

        for(int i = 0; i < count; i++){
            newcenterx += this.x[i];
            newcentery += this.y[i];
        }

        newcenterx /= count;
        newcentery /= count;

        for(int i = 0; i<count; i++){
            this.x[i]+= centerx - newcenterx;
            this.y[i]+= centery - newcentery;
        }
    }

    /**
     * self-explanatory
     * @param colour
     */
    public void setColour(Color colour){
        this.r = colour.getRed();
        this.g = colour.getGreen();
        this.b = colour.getBlue();
    }

    /**
     * upscale the shape by predetermined factor
     */
    public void upscale(){
        this.scale(upscaleFactor);
    }

    /**
     * downscale the shape by predetermined factor
     */
    public void downscale(){
        this.scale(downscaleFactor);
    }

}

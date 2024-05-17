package org.example.paint;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ColorPicker;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Pair;

import javax.swing.*;
import java.io.File;


public class EventController {

    private int focused = -1;
    private double xBegin;
    private double yBegin;

    private Type action = Type.NONE;

    private static final Color colour = Color.BLACK;

    @FXML
    public Canvas surface;

    @FXML
    public ColorPicker colourfx;


    /**
     * Summons file picker and saves shapes to file
     */
    public void save(){
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(surface.getScene().getWindow());
        try {
            Shapes.getInstance().save(file);
        }catch (Exception e){
            System.out.println("Something went wrong QwQ " + e.getMessage());
        }
    }

    /**
     * Summons file picker and loads shapes from file
     */
    public void load(){
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(surface.getScene().getWindow());
        try {
            Shapes.getInstance().load(file);
        }catch (Exception e){
            System.out.println("Something went wrong QwQ " + e.getMessage());
        }
    }

    /**
     * Summons dialog box with info about program
     */
    public void info(){
        new Thread(() -> {
            JOptionPane.showMessageDialog(null,
                    "App:Pain(t)\nAuthor: Oliwier Lechnik\nUsage:\n\tChoose a shape from 'Tools'\n\tDraw the shape by left click dragging\n\tScroll to rescale shape\n\tRight click to change colour\n\tmiddle click drag to rotate the shape",
                    "About",
                    JOptionPane.INFORMATION_MESSAGE);
        }).start();

    }

    public void setTriangle(){
        action = Type.TRIANGLE;
        focused = -1;
    }

    public void setRectangle(){
        action = Type.RECTANGLE;
        focused = -1;
    }

    public void setOval(){
        action = Type.OVAL;
        focused = -1;
    }

    public void setEdit(){
        action = Type.NONE;
        focused = -1;
    }

    /**
     * focuses most top shape under a pointer
     * and summons color picker dialog box
     * that allows to change focused shape color
     * @param event
     */
    public  void onRClick(javafx.scene.input.MouseEvent event){
        if(!event.getButton().equals(MouseButton.SECONDARY)){
            return;
        }
        for(int i = Shapes.getInstance().getSize()-1; i>=0; i--){
            if(Shapes.getInstance().get(i).contains(event.getX(), event.getY())){
                focused = i;
                new Thread(() -> {
                    java.awt.Color c = JColorChooser.showDialog(null, "Choose a color", java.awt.Color.BLACK);
                    if (c != null) {
                        javafx.scene.paint.Color c2 = javafx.scene.paint.Color.rgb(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha() / 255.0);
                        if (focused != -1) {
                            Shapes.getInstance().get(focused).setColour(c2);
                        }
                    }
                }).start();

                return;
            }
        }

    }

    /**
     * focus the most top shape under a pointer
     * or begin drawing a given shape
     * @param event
     */
    public void onDragBegin(javafx.scene.input.MouseEvent event){
        xBegin = event.getX();
        yBegin = event.getY();
        if(action == Type.NONE){
            for(int i = Shapes.getInstance().getSize()-1; i>=0; i--){
                if(Shapes.getInstance().get(i).contains(event.getX(), event.getY())){
                    focused = i;
                    return;
                }
            }
        } else if (action == Type.RECTANGLE) {
            Shapes.getInstance().add(PolygonShape.Rectangle(new Pair<Double,Double>(xBegin,yBegin), new Pair<Double,Double>(0.0,0.0), colour));
            focused = Shapes.getInstance().getSize()-1;
        }else if (action == Type.TRIANGLE) {
            Shapes.getInstance().add(PolygonShape.Triangle(new Pair<Double,Double>(xBegin,yBegin), new Pair<Double,Double>(0.0,0.0), colour));
            focused = Shapes.getInstance().getSize()-1;
        }else if (action == Type.OVAL) {
            Shapes.getInstance().add(PolygonShape.Oval(new Pair<Double,Double>(xBegin,yBegin), new Pair<Double,Double>(0.0,0.0), colour));
            focused = Shapes.getInstance().getSize()-1;
        }
    }


    /**
     * determine transformation and continue the transformation
     * or continue modyfying the shape
     * @param event
     */
    public void onDrag(javafx.scene.input.MouseEvent event){
        if(focused != -1){
            if(event.getButton().equals(MouseButton.PRIMARY) && action == Type.NONE){
                Shapes.getInstance().get(focused).move(event.getX()-xBegin, event.getY()-yBegin);
                xBegin = event.getX();
                yBegin = event.getY();
            }
            if(event.getButton().equals(MouseButton.MIDDLE) && action == Type.NONE){
                Shapes.getInstance().get(focused).rotate(xBegin, yBegin, event.getX(), event.getY());
                xBegin = event.getX();
                yBegin = event.getY();
            }
            if(action == Type.RECTANGLE){
                Shapes.getInstance().set(focused,PolygonShape.Rectangle(new Pair<Double,Double>(xBegin,yBegin), new Pair<Double,Double>(event.getX()-xBegin,event.getY()-yBegin), colour));
            }
            if(action == Type.TRIANGLE){
                Shapes.getInstance().set(focused,PolygonShape.Triangle(new Pair<Double,Double>(xBegin,yBegin), new Pair<Double,Double>(event.getX()-xBegin,event.getY()-yBegin), colour));
            }
            if(action == Type.OVAL){

                Shapes.getInstance().set(focused,PolygonShape.Oval(new Pair<Double,Double>(xBegin,yBegin), new Pair<Double,Double>(event.getX()-xBegin,event.getY()-yBegin), colour));
            }
        }
    }

    /**
     * clear the action
     */
    public void onDragEnd(){
        action = Type.NONE;
        focused = -1;
    }

    /**
     * scale focused shape
     * @param event
     */
    public void onScroll(ScrollEvent event){
        if(focused != -1){
            if(event.getDeltaY() > 0){
                Shapes.getInstance().get(focused).upscale();
            }else if(event.getDeltaY() < 0){
                Shapes.getInstance().get(focused).downscale();
            }
        }
    }
}
package org.example.paint;

import java.io.*;
import java.util.ArrayList;

public class Shapes implements Serializable {
    private static Shapes instance;
    private ArrayList<PolygonShape> shapes;


    private Shapes(){
        this.shapes = new ArrayList<PolygonShape>();
    }

    /**
     * @return instance of a singleton containing all shapes
     */
    public static Shapes getInstance(){
        if(instance == null){
            instance = new Shapes();
        }
        return instance;
    }

    /**
     * append given shape to the end of the list
     * @param shape
     */
    public void add(PolygonShape shape){
        this.shapes.add(shape);
    }

    /**
     * save all the shapes to given file
     * @param file
     * @throws IOException when saving is not possible, for example by read only filesystem
     */
    public void save(File file) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(instance);
    }

    /**
     * load the shapes from given file
     * @param file
     * @throws IOException when loading is not possible, for example by corrupted file
     */
    public void load(File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        try{
            instance = (Shapes) objectInputStream.readObject();

        }catch (Exception e){
            System.out.println("Corrupted file");
            instance = new Shapes();
        }

    }

    public int getSize(){
        return this.shapes.size();
    }

    /**
     * return shape at give index, if idx is out of range, return null
     * @param idx
     * @return
     */
    public PolygonShape get(int idx){

        try {
            return this.shapes.get(idx);
        }catch (Exception e){
            System.out.println(e.getMessage() + " " + idx + " / " + getSize());
            return null;
        }
    }

    /**
     * set shape at given index, if the idx is out of range, do nothing
     * @param idx
     * @param shape
     */
    public void set(int idx, PolygonShape shape){

        try {
            this.shapes.set(idx, shape);
        }catch (Exception e){
            System.out.println(e.getMessage() + " " + idx + " / " + getSize());
        }
    }

}

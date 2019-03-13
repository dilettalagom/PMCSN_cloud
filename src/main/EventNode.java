package main;

public class EventNode {

    private double temp;
    private int type;

    public EventNode(){

    }

    public EventNode(double t, int x){
        this.temp = t;
        this.type =x;

    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

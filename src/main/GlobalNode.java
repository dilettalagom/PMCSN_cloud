package main;

public class GlobalNode {

    private double complete_time_cloud;
    private double complete_time_cloudlet;
    private double complete_time_system;

    private double area_cloudlet_taskA;
    private double area_cloud_taskB;

    private double area_cloudlet_taskB;
    private double area_cloud_taskA;

    private int working_cloudlet_taskA;
    private int working_cloudlet_taskB;

    private int working_cloud_taskA;
    private int working_cloud_taskB;

    private double complete_time_taskA;
    private double complete_time_taskB;



    private int processed_cloudlet_taskA, processed_cloudlet_taskB; // numero di job processati dal cloudlet per tipo
    private int processed_cloud_taskA, processed_cloud_taskB;        // numero di job processati dal cloud per tipo


    public GlobalNode(double time_cloudlet, double time_cloud, int ncl, int nc){
        this.complete_time_cloudlet = time_cloudlet;
        this.complete_time_cloud = time_cloud;
        this.complete_time_system = time_cloudlet + time_cloud;
       // this.working_server_cloudlet = ncl;
       // this.working_server_cloud = nc;
        this.processed_cloudlet_taskA = 0;
        this.processed_cloudlet_taskB = 0;
        this.processed_cloud_taskA = 0;
        this.processed_cloud_taskB = 0;
        this.complete_time_taskA = 0;
        this.complete_time_taskB = 0;
    }

    public int getWorking_cloudlet_taskA() {
        return working_cloudlet_taskA;
    }

    public void setWorking_cloudlet_taskA(int working_cloudlet_taskA) {
        this.working_cloudlet_taskA = working_cloudlet_taskA;
    }

    public int getWorking_cloudlet_taskB() {
        return working_cloudlet_taskB;
    }

    public void setWorking_cloudlet_taskB(int working_cloudlet_taskB) {
        this.working_cloudlet_taskB = working_cloudlet_taskB;
    }

    public int getWorking_cloud_taskA() {
        return working_cloud_taskA;
    }

    public void setWorking_cloud_taskA(int working_cloud_taskA) {
        this.working_cloud_taskA = working_cloud_taskA;
    }

    public int getWorking_cloud_taskB() {
        return working_cloud_taskB;
    }

    public void setWorking_cloud_taskB(int working_cloud_taskB) {
        this.working_cloud_taskB = working_cloud_taskB;
    }

    public double getArea_cloudlet_taskA() {
        return area_cloudlet_taskA;
    }

    public void setArea_cloudlet_taskA(double area_cloudlet_taskA) {
        this.area_cloudlet_taskA = area_cloudlet_taskA;
    }

    public double getArea_cloud_taskB() {
        return area_cloud_taskB;
    }

    public void setArea_cloud_taskB(double area_cloud_taskB) {
        this.area_cloud_taskB = area_cloud_taskB;
    }

    public double getArea_cloudlet_taskB() {
        return area_cloudlet_taskB;
    }

    public void setArea_cloudlet_taskB(double area_cloudlet_taskB) {
        this.area_cloudlet_taskB = area_cloudlet_taskB;
    }

    public double getArea_cloud_taskA() {
        return area_cloud_taskA;
    }

    public void setArea_cloud_taskA(double area_cloud_taskA) {
        this.area_cloud_taskA = area_cloud_taskA;
    }


    public double getComplete_time_taskA() {
        return complete_time_taskA;
    }

    public void setComplete_time_taskA(double complete_time_taskA) {
        this.complete_time_taskA = complete_time_taskA;
    }

    public double getComplete_time_taskB() {
        return complete_time_taskB;
    }

    public void setComplete_time_taskB(double complete_time_taskB) {
        this.complete_time_taskB = complete_time_taskB;
    }

    public double getComplete_time_system() {
        return complete_time_system;
    }

    public void setComplete_time_system(double complete_time_system) {
        this.complete_time_system = complete_time_system;
    }

    public double getComplete_time_cloudlet() {
        return complete_time_cloudlet;
    }

    public void setComplete_time_cloudlet(double complete_time_cloudlet) {
        this.complete_time_cloudlet = complete_time_cloudlet;
    }

    public double getComplete_time_cloud() {
        return complete_time_cloud;
    }

    public void setComplete_time_cloud(double complete_time_cloud) {
        this.complete_time_cloud = complete_time_cloud;
    }


    public int getProcessed_cloudlet_taskA() {
        return processed_cloudlet_taskA;
    }

    public void setProcessed_cloudlet_taskA(int processed_cloudlet_taskA) {
        this.processed_cloudlet_taskA = processed_cloudlet_taskA;
    }

    public int getProcessed_cloudlet_taskB() {
        return processed_cloudlet_taskB;
    }

    public void setProcessed_cloudlet_taskB(int processed_cloudlet_taskB) {
        this.processed_cloudlet_taskB = processed_cloudlet_taskB;
    }

    public int getProcessed_cloud_taskA() {
        return processed_cloud_taskA;
    }

    public void setProcessed_cloud_taskA(int processed_cloud_taskA) {
        this.processed_cloud_taskA = processed_cloud_taskA;
    }

    public int getProcessed_cloud_taskB() {
        return processed_cloud_taskB;
    }

    public void setProcessed_cloud_taskB(int processed_cloud_taskB) {
        this.processed_cloud_taskB = processed_cloud_taskB;
    }

}

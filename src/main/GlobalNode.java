package main;

public class GlobalNode {

    private double complete_time_cloud;
    private double complete_time_cloudlet;
    private double complete_time_system;

    private double complete_time_cloudlet_task1;
    private double complete_time_cloud_task1;

    private double complete_time_cloudlet_task2;
    private double complete_time_cloud_task2;

    private double complete_time_task1;
    private double complete_time_task2;

    private int working_server_cloudlet;
    private int working_server_cloud;

    private int working_server_task1;
    private int working_server_task2;


    private int cloudlet_number1, cloudlet_number2; // numero di job processati dal cloudlet per tipo
    private int cloud_number1, cloud_number2;        // numero di job processati dal cloud per tipo


    public GlobalNode(double time_cloudlet, double time_cloud, int ncl, int nc){
        this.complete_time_cloudlet = time_cloudlet;
        this.complete_time_cloud = time_cloud;
        this.complete_time_system = time_cloudlet + time_cloud;
        this.working_server_cloudlet = ncl;
        this.working_server_cloud = nc;
        this.cloudlet_number1 = 0;
        this.cloudlet_number2 = 0;
        this.cloud_number1 = 0;
        this.cloud_number2 = 0;
        this.complete_time_task1 = 0;
        this.complete_time_task2 = 0;
    }


    public int getWorking_server_task1() {
        return working_server_task1;
    }

    public double getComplete_time_cloudlet_task1() {
        return complete_time_cloudlet_task1;
    }

    public void setComplete_time_cloudlet_task1(double complete_time_cloudlet_task1) {
        this.complete_time_cloudlet_task1 = complete_time_cloudlet_task1;
    }

    public double getComplete_time_cloud_task1() {
        return complete_time_cloud_task1;
    }

    public void setComplete_time_cloud_task1(double complete_time_cloud_task1) {
        this.complete_time_cloud_task1 = complete_time_cloud_task1;
    }

    public double getComplete_time_cloudlet_task2() {
        return complete_time_cloudlet_task2;
    }

    public void setComplete_time_cloudlet_task2(double complete_time_cloudlet_task2) {
        this.complete_time_cloudlet_task2 = complete_time_cloudlet_task2;
    }

    public double getComplete_time_cloud_task2() {
        return complete_time_cloud_task2;
    }

    public void setComplete_time_cloud_task2(double complete_time_cloud_task2) {
        this.complete_time_cloud_task2 = complete_time_cloud_task2;
    }

    public void setWorking_server_task1(int working_server_task1) {
        this.working_server_task1 = working_server_task1;
    }

    public int getWorking_server_task2() {
        return working_server_task2;
    }

    public void setWorking_server_task2(int working_server_task2) {
        this.working_server_task2 = working_server_task2;
    }

    public double getComplete_time_task1() {
        return complete_time_task1;
    }

    public void setComplete_time_task1(double complete_time_task1) {
        this.complete_time_task1 = complete_time_task1;
    }

    public double getComplete_time_task2() {
        return complete_time_task2;
    }

    public void setComplete_time_task2(double complete_time_task2) {
        this.complete_time_task2 = complete_time_task2;
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


    public int getWorking_server_cloudlet() {
        return working_server_cloudlet;
    }

    public void setWorking_server_cloudlet(int working_server_cloudlet) {
        this.working_server_cloudlet = working_server_cloudlet;
    }

    public int getWorking_server_cloud() {
        return working_server_cloud;
    }

    public void setWorking_server_cloud(int working_server_cloud) {
        this.working_server_cloud = working_server_cloud;
    }

    public int getCloudlet_number1() {
        return cloudlet_number1;
    }

    public void setCloudlet_number1(int cloudlet_number1) {
        this.cloudlet_number1 = cloudlet_number1;
    }

    public int getCloudlet_number2() {
        return cloudlet_number2;
    }

    public void setCloudlet_number2(int cloudlet_number2) {
        this.cloudlet_number2 = cloudlet_number2;
    }

    public int getCloud_number1() {
        return cloud_number1;
    }

    public void setCloud_number1(int cloud_number1) {
        this.cloud_number1 = cloud_number1;
    }

    public int getCloud_number2() {
        return cloud_number2;
    }

    public void setCloud_number2(int cloud_number2) {
        this.cloud_number2 = cloud_number2;
    }

}

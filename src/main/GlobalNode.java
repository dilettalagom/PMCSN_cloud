package main;

public class GlobalNode {

    private double complete_time_cloud;
    private double complete_time_cloudlet;

    private int cloudlet_number;
    private int cloud_number;

    public GlobalNode(double time_cloudlet, double time_cloud, int ncl, int nc){
        this.complete_time_cloudlet = time_cloudlet;
        this.complete_time_cloud = time_cloud;
        this.cloudlet_number = ncl;
        this.cloud_number = nc;
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


    public int getCloudlet_number() {
        return cloudlet_number;
    }

    public void setCloudlet_number(int cloudlet_number) {
        this.cloudlet_number = cloudlet_number;
    }

    public int getCloud_number() {
        return cloud_number;
    }

    public void setCloud_number(int cloud_number) {
        this.cloud_number = cloud_number;
    }



}

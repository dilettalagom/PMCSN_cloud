package restructed.StruttureDiSistema;

public class GlobalNode {

    private double complete_time_cloud = 0;
    private double complete_time_cloudlet = 0;
    private double complete_time_system = 0;

    private double complete_time_task1 = 0;
    private double complete_time_task2 = 0;

    public GlobalNode(double time_cloudlet, double time_cloud) {
        this.complete_time_cloudlet = time_cloudlet;
        this.complete_time_cloud = time_cloud;
        this.complete_time_system = time_cloudlet + time_cloud;
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

}

package restructed.StruttureDiSistema;

public class Cloudlet_server {

    private double service = 0.0;
    private int processed_task1 = 0;
    private int processed_task2 = 0;

    public Cloudlet_server() {

    }

    public double getService() {
        return service;
    }

    public void setService(double service) {
        this.service = service;
    }

    public int getProcessed_task1() {
        return processed_task1;
    }

    public void setProcessed_task1(int processed_task1) {
        this.processed_task1 = processed_task1;
    }

    public int getProcessed_task2() {
        return processed_task2;
    }

    public void setProcessed_task2(int processed_task2) {
        this.processed_task2 = processed_task2;
    }
}

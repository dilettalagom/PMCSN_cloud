package StruttureDiSistema;

public class Server {

    private double total_service = 0.0;
    private int processed_task1 = 0;
    private int processed_task2 = 0;
    private double last_service =0.0;

    public Server() {

    }

    public double getLast_service() {
        return last_service;
    }

    public void setLast_service(double last_service) {
        this.last_service = last_service;
    }

    public double getTotal_service() {
        return total_service;
    }

    public void setTotal_service(double total_service) {
        this.total_service = total_service;
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

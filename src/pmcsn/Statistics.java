package pmcsn;

import StruttureDiSistema.Cloud;
import StruttureDiSistema.Cloudlet;
import StruttureDiSistema.GlobalNode;
import StruttureDiSistema.SystemClock;

import java.time.Clock;
import java.util.ArrayList;

public class Statistics {

    private ArrayList<ArrayList<Double>> estimateTempi;
    private ArrayList<ArrayList<Double>> estimatePacchetti;
    private ArrayList<ArrayList<Double>> estimateThroughput;

    public Statistics(){
        this.estimateTempi = initArrayList();
        this.estimatePacchetti = initArrayList();
        this.estimateThroughput = initArrayList();
    }

    private ArrayList<ArrayList<Double>> initArrayList() {
        ArrayList<ArrayList<Double>>array= new ArrayList<>();
        for (int i=0; i<9; i++){
            array.add(new ArrayList<>());
        }
        return array;
    }

    public ArrayList<ArrayList<Double>> getEstimateTempi() {
        return estimateTempi;
    }

    public void setEstimateTempi(ArrayList<ArrayList<Double>> estimateTempi) {
        this.estimateTempi = estimateTempi;
    }

    public ArrayList<ArrayList<Double>> getEstimatePacchetti() {
        return estimatePacchetti;
    }

    public void setEstimatePacchetti(ArrayList<ArrayList<Double>> estimatePacchetti) {
        this.estimatePacchetti = estimatePacchetti;
    }

    public ArrayList<ArrayList<Double>> getEstimateThroughput() {
        return estimateThroughput;
    }

    public void setEstimateThroughput(ArrayList<ArrayList<Double>> estimateThroughput) {
        this.estimateThroughput = estimateThroughput;
    }

    public void saveTempiValues(GlobalNode global_node, Cloudlet cloudlet, Cloud cloud) {

        this.estimateTempi.get(0).add(global_node.getComplete_time_cloudlet() / (cloudlet.getProcessed_task1() + cloudlet.getProcessed_task2()) );
        this.estimateTempi.get(1).add(cloudlet.getArea_task1() / cloudlet.getProcessed_task1());
        this.estimateTempi.get(2).add(cloudlet.getArea_task2() / cloudlet.getProcessed_task2());

        this.estimateTempi.get(3).add(global_node.getComplete_time_cloud() / (cloud.getProcessed_task1() + cloud.getProcessed_task2()) );
        this.estimateTempi.get(4).add(cloud.getArea_task1() / cloud.getProcessed_task1());
        this.estimateTempi.get(5).add(cloud.getArea_task2() / cloud.getProcessed_task2());

        this.estimateTempi.get(6).add(global_node.getComplete_time_system() / global_node.getTotalTask());
        this.estimateTempi.get(7).add(global_node.getComplete_time_task1() / (cloudlet.getProcessed_task1() + cloud.getProcessed_task1()));
        this.estimateTempi.get(8).add(global_node.getComplete_time_task2() / (cloudlet.getProcessed_task2() + cloud.getProcessed_task2()));

    }

    public void savePacchettiValues(GlobalNode global_node, Cloudlet cloudlet, Cloud cloud, SystemClock clock) {

        this.estimatePacchetti.get(0).add( global_node.getComplete_time_cloudlet()/clock.getCurrent() );
        this.estimatePacchetti.get(1).add( cloudlet.getArea_task1()/clock.getCurrent() );
        this.estimatePacchetti.get(2).add( cloudlet.getArea_task2()/clock.getCurrent() );

        this.estimatePacchetti.get(3).add( global_node.getComplete_time_cloud()/clock.getCurrent() );
        this.estimatePacchetti.get(4).add( cloud.getArea_task1()/clock.getCurrent() );
        this.estimatePacchetti.get(5).add( cloud.getArea_task2()/clock.getCurrent() );

        this.estimatePacchetti.get(6).add( global_node.getComplete_time_system()/clock.getCurrent() );
        this.estimatePacchetti.get(7).add( global_node.getComplete_time_task1()/clock.getCurrent() );
        this.estimatePacchetti.get(8).add( global_node.getComplete_time_task2()/clock.getCurrent() );

    }

    public void saveThroughput(GlobalNode global_node, Cloudlet cloudlet, Cloud cloud, SystemClock clock) {
        this.estimateThroughput.get(0).add( (cloudlet.getProcessed_task1() + cloudlet.getProcessed_task2()) / clock.getCurrent() );
        this.estimateThroughput.get(1).add( cloudlet.getProcessed_task1() / clock.getCurrent() );
        this.estimateThroughput.get(2).add( cloudlet.getProcessed_task2() / clock.getCurrent() );

        this.estimateThroughput.get(3).add( (cloud.getProcessed_task1() + cloud.getProcessed_task2()) / clock.getCurrent() );
        this.estimateThroughput.get(4).add( cloud.getProcessed_task1() / clock.getCurrent() );
        this.estimateThroughput.get(5).add( cloud.getProcessed_task2() / clock.getCurrent() );

        this.estimateThroughput.get(6).add( global_node.getTotalTask() / clock.getCurrent() );
        this.estimateThroughput.get(7).add( (cloudlet.getProcessed_task1() + cloud.getProcessed_task1()) / clock.getCurrent() );
        this.estimateThroughput.get(8).add( (cloudlet.getProcessed_task2() + cloud.getProcessed_task2()) / clock.getCurrent() );

    }
}

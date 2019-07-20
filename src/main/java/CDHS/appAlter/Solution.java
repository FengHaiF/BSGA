package CDHS.appAlter;

import CDHS.MyGA.Chromosome;
import CDHS.domain.OilStation;
import CDHS.domain.Operation;
import CDHS.domain.Seat;
import CDHS.persistence.Importer;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solution {
    private Chromosome chromosome;
    private Importer importer;
    private List<Operation> operationList = new ArrayList<>();
    private List<List<Integer>> geneList;
    private List<Integer> genes;
    private List<Integer> orderGenes;
    private Map<Integer,Operation> operationMap = new HashMap<>();
    private Map<Long,List<Operation>> operationPlaneMap = new HashMap<>();
    private Map<Long,Double> seatEndTimeMap = new HashMap<>();
    private Map<String,Double> stationEndTimeMap = new HashMap<>();
    private Map<Long,List<Operation>> seatOperationMap = new HashMap<>();

    private double makespan;

    public Solution(Chromosome chromosome,Importer importer) {
        this.chromosome = chromosome;
        this.genes = chromosome.getGenes();
        this.importer = importer;
        this.geneList = chromosome.getGeneList();
        this.orderGenes = chromosome.getOrderGenes();
    }

    public Map<Integer, Operation> getOperationMap() {
        return operationMap;
    }

    public double getMakespan() {
        return makespan;
    }

    private void chromosomeToOperation(){
        for (int i = 0; i < importer.getNumOfPlane(); i++) {
            Seat seatLast = new Seat();
            seatLast.setSeatId(Setting.INITIAL_TABLE[i]);

            Operation operation = new Operation(i, i, seatLast, 0,0,0,0);
            operationList.add(operation);
            operationMap.put(operation.getOperationId(),operation);
        }
        int z = 0;
        for (int i = 0; i < geneList.size()-Setting.NUM_OF_NEXT; i++) {
            for (int j = 0; j < geneList.get(i).size(); j++) {
                int gene;
//                if (i==geneList.size()-Setting.NUM_OF_NEXT)
//                    gene = orderGenes.get(j);
//                else
                    gene = geneList.get(i).get(j);
                Operation operation = new Operation();
                if (i==0){
                    Seat seat = importer.getOilSeatMap().get(gene);
                    operation.setOperation(z+importer.getNumOfPlane(), j, seat, importer.getOilDuration(j,seat.getStationPosition()), 0, 0, i+1);
                }if (i==1){
                    Seat seat = importer.getDySeatMap().get(gene);
                    operation.setOperation(z+importer.getNumOfPlane(), j, seat, importer.getDyDuration(), 0, 0, i+1);
                }
//                if (i==geneList.size()-Setting.NUM_OF_NEXT){
//                    Seat seat = importer.getOrderMap().get(gene);
//                    operation.setOperation(z, j, seat, 0,0,0,i);
//                    operationList.add(operation);
//                }
                operationList.add(operation);
                int order = orderGenes.get(z);
                operationMap.put(order+importer.getNumOfPlane(),operation);
                z++;
            }
        }
        int size = operationMap.size();
        for (int i = 0; i < importer.getNumOfPlane(); i++) {
            Seat seatLast = new Seat();
            chromosome.getBfGenes().get(i);
//            seatLast.setSeatId(Setting.TAKEOFF_TABLE[i]);
            seatLast.setSeatId(chromosome.getBfGenes().get(i));

            Operation operation = new Operation(size+i, i, seatLast, 0,0,0,geneList.size()-Setting.NUM_OF_NEXT+1);
            operationList.add(operation);
            operationMap.put(operation.getOperationId(),operation);
            operationPlaneMap.put((long) i, new ArrayList<>());
        }

        for (int i = 0; i < operationMap.size(); i++) {
            Operation operation = operationMap.get(i);
            long planeId = operation.getPlaneId();
            operationPlaneMap.get(planeId).add(operation);
        }

        for (Seat seat : importer.getSeatList()) {
            seatEndTimeMap.put(seat.getSeatId(),0.0);
            seatOperationMap.put(seat.getSeatId(),new ArrayList<>());
        }

        for (OilStation oilStation : importer.getOilStationList()) {
            stationEndTimeMap.put(oilStation.getPosition(),0.0);
        }

        for (int i = 0; i < operationPlaneMap.size(); i++) {
            List<Operation> operations = operationPlaneMap.get((long)i);
            for (int j = 0; j < operations.size(); j++) {
                Operation operation = operations.get(j);
                if (j == 0) {
                    operation.setNextOperation(operations.get(j + 1));
                }
                else if (j == operations.size()-1)
                    operation.setPreviousOperation(operations.get(j-1));
                else {
                    operation.setPreviousOperation(operations.get(j-1));
                    operation.setNextOperation(operations.get(j+1));
                }
            }
        }
    }

    public void calculateMakespan(){
        chromosomeToOperation();

        makespan = 0;

        List<List<Double>> distTable = Setting.DIST_TABLE;

        long previous;
        double startTime;

        for (int i = 0; i < operationMap.size(); i++) {
            Operation operation = operationMap.get(i);

            double lastOperationTime = 0;
            if (operation.getPreviousOperation() == null){
                previous = Setting.INITIAL_TABLE[(int)operation.getPlaneId()];
            }else {
                lastOperationTime = operation.getPreviousOperation().getEnd();
                previous = operation.getPreviousOperation().getSeatId();
            }

            operation.setPreviousSeatId(previous);
            operation.setDistTime((distTable.get((int)previous).get((int)operation.getSeatId())/Setting.QYC_SPEED));

            startTime = Math.max(lastOperationTime+operation.getDistTime(), seatEndTimeMap.get(operation.getSeatId()));
            if (operation.getOperationType() == 1) {//等油站
                Double stationFreeTime = stationEndTimeMap.get(operation.getStationPosition());
                if (startTime < stationFreeTime){
                    operation.setDuration(operation.getDuration()+stationFreeTime-startTime);
                    operation.setWaitOilStation(stationFreeTime-startTime);
                }
//                startTime = Math.max(startTime, stationEndTimeMap.get(operation.getStationPosition()));
            }
            operation.setStart(startTime);

            if(startTime - operation.getDistTime() - lastOperationTime > 0) {
                if(operation.getPreviousOperation()!=null){
                    Operation previousOperation = operation.getPreviousOperation();
                    double endRefresh = startTime - operation.getDistTime();
                    previousOperation.setWaitTime(startTime - operation.getDistTime()-lastOperationTime);

                    seatEndTimeMap.put(previousOperation.getSeatId(),endRefresh);
                }
            }

            //设置结束时间，时长duration
            double newEndTime = startTime + operation.getDuration();
            operation.setEnd(newEndTime);

            //更新
            if (operation.getOperationType()==1)
                stationEndTimeMap.replace(operation.getStationPosition(),newEndTime);
            //设置最后站位已经占用
            if (operation.getNextOperation()==null){
                seatEndTimeMap.replace(operation.getSeatId(),5000.0);
            }else {
                seatEndTimeMap.replace(operation.getSeatId(), newEndTime);
            }
            seatOperationMap.get(operation.getSeatId()).add(operation);
            makespan = Math.max(makespan,newEndTime);

        }


        //最后加上时间约束，不满足约束直接去除解
        if (isTimeConflict(importer))
            makespan += 5000;

        chromosome.setFitness(makespan);
    }

    //筛选时间冲突的解
    private boolean isTimeConflict(Importer importer){
        for (Seat seat : importer.getSeatList()){
            long seatId = seat.getSeatId();
            int size = seatOperationMap.get(seatId).size();
            for (int i = 0; i < size;i++) {
                Operation operation = seatOperationMap.get(seatId).get(i);
                for (int j = i+1; j < size; j++) {
                    Operation otherOperation = seatOperationMap.get(seatId).get(j);
                    if (!(operation.getStart() >= otherOperation.getEnd()+otherOperation.getWaitTime()
                            ||operation.getEnd()+operation.getWaitTime() <= otherOperation.getStart()))
                        return true;
                }
            }
        }
        return false;
    }

}
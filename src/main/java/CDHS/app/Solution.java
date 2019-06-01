package CDHS.app;

import CDHS.GA.AlleleF;
import CDHS.domain.*;
import CDHS.persistence.Importer;
import io.jenetics.AnyGene;
import io.jenetics.Chromosome;
import io.jenetics.Genotype;

import java.util.*;

public class Solution {
    private List<Operation> operationList = new ArrayList<>();
    private Map<Integer,Operation> operationMap = new HashMap<>();
    private Map<Long,List<Operation>> seatOperationMap = new HashMap<>();
    private Map<Long,Double> seatEndTimeMap = new HashMap<>();
    private Map<String,Map<Long,Double>> pipEndTimeMap = new HashMap<>();
    private Map<Long,List<Operation>> operationPlaneMap = new HashMap<>();
    private Map<Long,Operation> excuteMap = new HashMap<>();
    private int[][] orderTable = Setting.ORDER_TABLE;
    private double uselessOccupy;
    private double totalDist;
    private double makespan;
    /**
     * 配置文件
     */
    private static int numOfMatainance;
    static {
        numOfMatainance = Setting.NUM_OF_MATAINANCE;
    }
    /*
    private static int numOfMatainance;
    static {
        try {
            Properties properties = new Properties();
            ClassLoader classLoader = Solution.class.getClassLoader();
            URL resource = classLoader.getResource("setting.properties");
            String path = resource.getPath();
            properties.load(new FileReader(path));

            numOfMatainance = Integer.parseInt(String.valueOf(properties.get("numOfMatainance")));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    */

    public Solution() {
    }

    public List<Operation> getOperationList() {
        return operationList;
    }

    public double getUselessOccupy() {
        return uselessOccupy;
    }

    public double getTotalDist() {
        return totalDist;
    }

    public double getMakespan() {
        return makespan;
    }

    private void operationListInit(Genotype<AnyGene<AlleleF>> genotype, Importer importer){
        int numOfPlane = importer.getNumOfPlane();

        //初始化operation，最后添加了起飞布列站位的operation
        for (int j = 0; j < numOfPlane; j++) {
            for (int i = 0; i < numOfMatainance; i++) {
                Chromosome<AnyGene<AlleleF>> chromosome = genotype.getChromosome(i);
                Seat seat = (Seat) chromosome.getGene(j).getAllele();
                //加油挂弹operation分别设置
                if (i==0){
                    Operation operation = new Operation(i + j * (numOfMatainance+1), j, seat, importer.getOilDuration(j,seat.getStationPosition()),0,0,i);
                    operationList.add(operation);
                }
                else{
                    Operation operation = new Operation(i + j * (numOfMatainance+1), j, seat, importer.getDyDuration(),0,0,i);
                    operationList.add(operation);
                }
            }
            Seat seatLast = new Seat();
            seatLast.setSeatId(Setting.TAKEOFF_TABLE[j]);
            Operation operation = new Operation(numOfMatainance + j * (numOfMatainance+1), j, seatLast, 0,0,0,numOfMatainance);
            operationList.add(operation);
        }

        for (int i = 0; i < numOfPlane; i++) {
            operationPlaneMap.put((long) i, new ArrayList<>());
        }

        for (Operation operation : operationList) {
            long planeId = operation.getPlaneId();
            operationPlaneMap.get(planeId).add(operation);
            operationMap.put(operation.getOperationId(),operation);
        }

        for (Seat seat : importer.getSeatList()) {
            seatEndTimeMap.put(seat.getSeatId(),0.0);
            seatOperationMap.put(seat.getSeatId(),new ArrayList<>());
        }

        for (OilStation oilStation : importer.getOilStationList()) {
            pipEndTimeMap.put(oilStation.getPosition(),new HashMap<>());
        }
        for (Seat seat : importer.getOilSeatList()) {
            pipEndTimeMap.get(seat.getStationPosition()).put(seat.getOilPipId(),0.0);
        }

        //operation链表初始化，找前后关系，如果保障计划有变要改
        Chromosome<AnyGene<AlleleF>> orderChromosome = genotype.getChromosome(numOfMatainance);
        for (int i = 0; i < importer.getNumOfPlane(); i++) {
            Order order = (Order) orderChromosome.getGene(i).getAllele();

            List<Operation> operations = operationPlaneMap.get((long) i);

            int[] orderArray = orderTable[(int)order.getOrderId()];

            for (int j = 0; j < orderArray.length; j++) {
                //按照order的顺序取出operation
                Operation operation = operations.get(orderArray[j]);
                if (j == 0) {
                    operation.setNextOperation(operations.get(orderArray[j + 1]));
                    excuteMap.put((long)i,operation);
                }
                else if (j == orderArray.length-1)
                    operation.setPreviousOperation(operations.get(orderArray[j-1]));
                else {
                    operation.setPreviousOperation(operations.get(orderArray[j-1]));
                    operation.setNextOperation(operations.get(orderArray[j+1]));
                }

            }
        }
    }

    //选择时间最小的开始执行
    private long chooseExcute(){
        long witch = -1;
        double start = -1;

        for (Long i : excuteMap.keySet()) {
            Operation operation = excuteMap.get(i);
            if (start == -1){
                start = operation.getStart();
                witch = i;
                continue;
            }
            if (operation.getStart() < start) {
                witch = i;
                start = operation.getStart();
            }
        }
        return witch;
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
                    if (!(operation.getStart() >= otherOperation.getEnd()||operation.getEnd() <= otherOperation.getStart()))
                        return true;
                }
            }
        }
        return false;
    }

    //适应度计算
    public double calculateMakespan(Genotype<AnyGene<AlleleF>> genotype, Importer importer){
        //初始化
        operationListInit(genotype,importer);

        makespan = 0;
        totalDist = 0;
        uselessOccupy = 0;

        List<List<Double>> distTable = Setting.DIST_TABLE;

        long previous;
        double startTime;
        long which = chooseExcute();

        while (which != -1){
            //按照时间顺序取出operation
            Operation operation = excuteMap.get(which);

            double lastOperationTime = operation.getStart();

            //设置第一个的previous为自己
            if (operation.getPreviousOperation() == null){
                previous = operation.getSeatId();
            }else {
                lastOperationTime = operation.getPreviousOperation().getEnd();
                previous = operation.getPreviousOperation().getSeatId();
            }

            //设置拖行时间
            operation.setPreviousSeatId(previous);
            operation.setDistTime(distTable.get((int)previous).get((int)operation.getSeatId()));

            //设置开始时间，根据前一个operation的时间、站位结束时间、管道占用结束时间来确定
            startTime = Math.max(lastOperationTime+operation.getDistTime(), seatEndTimeMap.get(operation.getSeatId()));
            if (operation.getOperationType() == 0)
                startTime = Math.max(startTime, pipEndTimeMap.get(operation.getStationPosition()).get(operation.getPipId()));

            operation.setStart(startTime);

            //设置前一个等待，并更新站位时间
            if(startTime - operation.getDistTime() - lastOperationTime != 0) {
                if(operation.getPreviousOperation()!=null){
                    Operation previousOperation = operation.getPreviousOperation();
                    double endRefresh = startTime - operation.getDistTime();
                    previousOperation.setWaitTime(endRefresh-lastOperationTime);
                    previousOperation.setEnd(endRefresh);
                    seatEndTimeMap.put(previousOperation.getSeatId(),endRefresh);
                }
            }

            //设置结束时间，时长duration
            double newEndTime = startTime + operation.getDuration();
            operation.setEnd(newEndTime);

            //计算中间的空白时间
            double tempTime = operation.getStart() - operation.getDistTime() - lastOperationTime;
            if ( tempTime > 0)
                uselessOccupy += tempTime;

            //更新
            if (operation.getOperationType()==0)
                pipEndTimeMap.get(operation.getStationPosition()).replace(operation.getPipId(),newEndTime);
            totalDist += operation.getDistTime();
            //设置最后站位已经占用
            if (operation.getNextOperation()==null){
                seatEndTimeMap.replace(operation.getSeatId(),5000.0);
            }else {
                seatEndTimeMap.replace(operation.getSeatId(), newEndTime);
            }
            makespan = Math.max(makespan,newEndTime);

            //替换下一个执行的operation
            if (operation.getNextOperation()!=null) {
                operation.getNextOperation().setStart(newEndTime);
                excuteMap.replace(operation.getPlaneId(), operation.getNextOperation());
            }
            else
                excuteMap.remove(operation.getPlaneId());

            //更新选择常数
            which = chooseExcute();
            seatOperationMap.get(operation.getSeatId()).add(operation);
        }

        //最后加上时间约束，不满足约束直接去除解
        if (isTimeConflict(importer))
            makespan += 5000;

//            System.out.println(operationList);
        return makespan;
    }
}

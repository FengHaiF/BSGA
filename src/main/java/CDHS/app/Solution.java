package CDHS.app;

import CDHS.GA.AlleleF;
import CDHS.domain.OilStation;
import CDHS.domain.Operation;
import CDHS.domain.Order;
import CDHS.domain.Seat;
import CDHS.persistence.Importer;
import io.jenetics.AnyGene;
import io.jenetics.Chromosome;
import io.jenetics.Genotype;

import java.util.*;

public class Solution {
    private List<Operation> operationList = new ArrayList<>();
    private Map<Integer,Operation> operationMap = new HashMap<>();
    private Map<Long,Double> seatEndTimeMap = new HashMap<>();
    private Map<String,Map<Long,Double>> pipEndTimeMap = new HashMap<>();
    private Map<Long,List<Operation>> operationPlaneMap = new HashMap<>();
    private int[][] orderTable = Setting.ORDER_TABLE;
    private double uselessOccupy;

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

    private void operationListInit(Genotype<AnyGene<AlleleF>> genotype, Importer importer){
        int numOfPlane = importer.getNumOfPlane();
        for (int i = 0; i < numOfMatainance; i++) {
            Chromosome<AnyGene<AlleleF>> chromosome = genotype.getChromosome(i);
            for (int j = 0; j < numOfPlane; j++) {
                Seat seat = (Seat) chromosome.getGene(j).getAllele();
                //加油挂弹operation分别设置
                if (i==0){
                    Operation operation = new Operation(i + j * numOfMatainance, j, seat, importer.getOilDuration(j,seat.getStationPosition()),0,0,i);
                    operationList.add(operation);
                }
                else{
                    Operation operation = new Operation(i + j * numOfMatainance, j, seat, importer.getDyDuration(),0,0,i);
                    operationList.add(operation);
                }

            }
        }

        for (int i = 0; i < numOfPlane; i++) {
            operationPlaneMap.put((long) i, new ArrayList<>());
        }

        for (Operation operation : operationList) {
            long planeId = operation.getPlaneId();
            operationPlaneMap.get(planeId).add(operation);
        }

        for (Operation operation : operationList) {
            operationMap.put(operation.getOperationId(),operation);
        }

        for (Seat seat : importer.getSeatList()) {
            seatEndTimeMap.put(seat.getSeatId(),0.0);
        }

        //初始化油管时间
        for (OilStation oilStation : importer.getOilStationList()) {
            pipEndTimeMap.put(oilStation.getPosition(),new HashMap<>());
        }
        for (Seat seat : importer.getOilSeatList()) {
            pipEndTimeMap.get(seat.getStationPosition()).put(seat.getOilPipId(),0.0);
        }
    }

    public double calculateMakespan(Genotype<AnyGene<AlleleF>> genotype, Importer importer){
        operationListInit(genotype,importer);
        Chromosome<AnyGene<AlleleF>> orderChromosome = genotype.getChromosome(numOfMatainance);
        double makespan = 0;
        uselessOccupy = 0;
        List<List<Double>> distTable = Setting.DIST_TABLE;

        for (int i = 0; i < importer.getNumOfPlane(); i++) {
            Order order = (Order) orderChromosome.getGene(i).getAllele();

            List<Operation> operations = operationPlaneMap.get((long) i);

            int[] orderArray = orderTable[(int)order.getOrderId()];

            double lastOperationTime = 0;

            long previous = -1;

            for (int j = 0; j < orderArray.length; j++) {

                //按照order的顺序取出operation
                Operation operation = operations.get(orderArray[j]);
                //设置第一个的previous为自己
                if (previous == -1){
                    previous = operation.getSeatId();
                }
                //设置拖行时间
                operation.setPreviousSeatId(previous);
                operation.setDistTime(distTable.get((int)previous).get((int)operation.getSeatId()));

                //设置开始时间，根据前一个operation的时间、站位结束时间、管道占用结束时间来确定
                Double startTime = Math.max(lastOperationTime,seatEndTimeMap.get(operation.getSeatId()));
                if (operation.getOperationType()==0)
                    startTime = Math.max(startTime,pipEndTimeMap.get(operation.getStationPosition()).get(operation.getPipId()));

                //设置拖行时间，如果和前一个operation之间不足则延长时间
                if (startTime-lastOperationTime < operation.getDistTime())
                        startTime += operation.getDistTime();
                operation.setStart(startTime);

                //设置结束时间，时长duration
                double newEndTime = startTime + operation.getDuration();
                operation.setEnd(newEndTime);

                //计算中间的空白时间,第一个不参与计算
                if(lastOperationTime != 0){
                    double tempTime = operation.getStart() - operation.getDistTime() - lastOperationTime;
                    if ( tempTime > 0)
                        uselessOccupy += tempTime;
                }


                //更新
                if (operation.getOperationType()==0)
                    pipEndTimeMap.get(operation.getStationPosition()).replace(operation.getPipId(),newEndTime);
                seatEndTimeMap.replace(operation.getSeatId(),newEndTime);
                lastOperationTime = newEndTime;
                makespan = Math.max(makespan,newEndTime);
                previous = operation.getSeatId();
            }
        }
//        operationList.stream().filter(operation -> operation.getOperationType() == 0).forEach(operation -> System.out.println(operation));
//        System.out.println(operationList);
        return makespan;
    }
}

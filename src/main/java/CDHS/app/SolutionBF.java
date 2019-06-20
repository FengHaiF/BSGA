package CDHS.app;

import CDHS.GA.AlleleF;
import CDHS.domain.Operation;
import CDHS.domain.Seat;
import CDHS.persistence.Importer;
import io.jenetics.AnyGene;
import io.jenetics.Chromosome;
import io.jenetics.Genotype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SolutionBF {
    private Map<Long,Boolean> zbSeatStateMap = new HashMap<>();
    private List<Operation> operationList = new ArrayList<>();
    private Map<Long,Double> zbSeatEndTimeMap = new HashMap<>();
    private Map<Long,Double> tsqEndTimeMap = new HashMap<>();
    private Map<Long,Double> slideEndTimeMap = new HashMap<>();

    private double makespan;

    public List<Operation> getOperationList() {
        return operationList;
    }

    public double getMakespan() {
        return makespan;
    }

    private void operationListInit(Genotype<AnyGene<AlleleF>> genotype, Importer importer){
        int numOfPlane = importer.getNumOfPlane();
        int numOfTsq = importer.getTsqList().size();
        List<Seat> zbList = importer.getZbList();

        for (Seat seat : zbList) {
            zbSeatStateMap.put(seat.getSeatId(),true);

        }

        for (Seat seat : importer.getTsqList()) {
            zbSeatEndTimeMap.put(seat.getSeatId(),0.0);
            tsqEndTimeMap.put(seat.getSeatId(),0.0);
            slideEndTimeMap.put(seat.getSeatId(),0.0);
        }

        Chromosome<AnyGene<AlleleF>> chromosomeSeat = genotype.getChromosome(0);
        Chromosome<AnyGene<AlleleF>> chromosomeTsq = genotype.getChromosome(1);
        for (int i = 0; i < numOfPlane; i++) {
            if (i < numOfTsq){
                Operation operationZB = new Operation(i,i,importer.getTsqList().get(i),Setting.ZB_DURATION,0,Setting.ZB_DURATION,1);
                operationZB.setColdTime(Setting.COLD_DURATION);
                operationList.add(operationZB);
            }else {
                Seat seat = (Seat) chromosomeSeat.getGene(i-numOfTsq).getAllele();
                Seat tsq = (Seat) chromosomeTsq.getGene(i-numOfTsq).getAllele();
                Operation operationDrag = new Operation(2*i-2,i,seat,0,0,0,0);
                Operation operationZB = new Operation(2*i-1,i,tsq,Setting.ZB_DURATION,0,0,1);
                operationDrag.setNextOperation(operationZB);
                operationZB.setColdTime(Setting.COLD_DURATION);
                operationZB.setPreviousOperation(operationDrag);

                operationList.add(operationDrag);
                operationList.add(operationZB);
            }
        }
    }

    public double calculateMakespan(Genotype<AnyGene<AlleleF>> genotype, Importer importer){

        operationListInit(genotype,importer);

        long previous;

        List<List<Double>> distTable = Setting.DIST_TABLE;

        for (Operation operation : operationList) {
            //设置第一个的previous为自己
            if (operation.getPreviousOperation() == null)
                previous = operation.getSeatId();
            else
                previous = operation.getPreviousOperation().getSeatId();

            operation.setPreviousSeatId(previous);
            operation.setDistTime((distTable.get((int)previous).get((int)operation.getSeatId())/Setting.QYC_SPEED));

            if (operation.getOperationType()==0){
                Double endTime = zbSeatEndTimeMap.get(operation.getSeatId());
//                operation.setStart(endTime);
            }else {
                if (operation.getPreviousOperation() != null){
                    Double startTime = slideEndTimeMap.get(operation.getSeatId());
                    operation.setStart(startTime);

                    operation.setEnd(startTime+operation.getDuration());

                    slideEndTimeMap.replace(operation.getSeatId(),operation.getEnd()+operation.getColdTime());
                    operation.getPreviousOperation().setEnd(startTime);
                    zbSeatEndTimeMap.replace(operation.getPreviousOperation().getSeatId(),startTime);
                }
            }
        }

        System.out.println(operationList);
        makespan = 100.0;
        return 1.0;
    }
}

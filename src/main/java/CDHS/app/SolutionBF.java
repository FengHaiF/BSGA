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

    public double calculateMakespan(Genotype<AnyGene<AlleleF>> genotype, Importer importer){
        int numOfPlane = importer.getNumOfPlane();
        int numOfTsq = importer.getTsqList().size();
        List<Seat> zbList = importer.getZbList();

        for (Seat seat : zbList) {
            zbSeatStateMap.put(seat.getSeatId(),true);
        }

        Chromosome<AnyGene<AlleleF>> chromosomeSeat = genotype.getChromosome(0);
        Chromosome<AnyGene<AlleleF>> chromosomeTsq = genotype.getChromosome(1);
        for (int i = 0; i < numOfPlane; i++) {
            if (i < numOfTsq){
                Operation operationZB = new Operation(i,i,importer.getTsqList().get(i),Setting.ZB_DURATION,0,Setting.ZB_DURATION,1);
                operationZB.setColdTime(Setting.COLD_DURATION);
                operationList.add(operationZB);
            }else {
                Seat seat = (Seat) chromosomeSeat.getGene(i).getAllele();
                Seat tsq = (Seat) chromosomeTsq.getGene(i).getAllele();
                Operation operationDrag = new Operation(i+(i-2)*2-1,i,seat,0,0,0,0);
                Operation operationZB = new Operation(i+(i-2)*2,i,tsq,Setting.ZB_DURATION,0,0,1);
                operationZB.setColdTime(Setting.COLD_DURATION);
                operationList.add(operationDrag);
                operationList.add(operationZB);
            }
        }

        System.out.println(operationList);
        return 1.0;
    }
}

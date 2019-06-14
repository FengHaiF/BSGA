package CDHS.app;

import CDHS.GA.AlleleF;
import CDHS.domain.Seat;
import CDHS.persistence.Importer;
import io.jenetics.AnyGene;
import io.jenetics.Genotype;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SolutionBF {
    private Map<Long,Boolean> zbSeatStateMap = new HashMap<>();


    public double calculateMakespan(Genotype<AnyGene<AlleleF>> genotype, Importer importer){
        int numOfPlane = importer.getNumOfPlane();
        List<Seat> zbList = importer.getZbList();
        for (Seat seat : zbList) {
            zbSeatStateMap.put(seat.getSeatId(),true);
        }



        for (int i = 0; i < numOfPlane; i++) {

        }

        return 1.0;
    }
}

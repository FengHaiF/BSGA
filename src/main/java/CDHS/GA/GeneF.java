package CDHS.GA;

import CDHS.domain.Order;
import CDHS.domain.Seat;
import io.jenetics.util.ISeq;
import io.jenetics.util.RandomRegistry;

import java.util.List;


public class GeneF {

    public static AlleleF generateAllele(ISeq<AlleleF> alleles){
        int index= RandomRegistry.getRandom().nextInt(alleles.length());
        return alleles.get(index);
    }

    public static AlleleF generateAllele(List<Seat> seat){
        return generateAllele(ISeq.of(seat));
    }

    public static AlleleF generateAllele1(List<Order> orders){
        return generateAllele(ISeq.of(orders));
    }
}

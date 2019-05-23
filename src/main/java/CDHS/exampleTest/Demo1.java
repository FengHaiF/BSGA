package CDHS.exampleTest;

import CDHS.GA.AlleleF;
import CDHS.app.Setting;
import io.jenetics.*;
import io.jenetics.engine.Codecs;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.Problem;
import io.jenetics.ext.moea.UFTournamentSelector;
import io.jenetics.ext.moea.Vec;
import io.jenetics.util.DoubleRange;

public class Demo1 {
    public static void main(String[] args) {
//        Problem<double [ ] , DoubleGene, Vec<double[]>> problem =
//                Problem.of(v -> Vec.of(v[0]*Math.cos(v[1])+1, v[0]*Math.sin(v[1])+1),
//                        Codecs.ofVector(
//                                DoubleRange.of(0,1),DoubleRange.of(0,2*Math.PI)
//                        )
//                );
        Problem<double[], DoubleGene, Vec<double[]>> problem =
                Problem.of(v -> Vec.of(v[0]+1, v[0]+5,v[0]),
                        Codecs.ofVector(DoubleRange.of(1,2))
                );

        Engine<DoubleGene,Vec<double[]>> engine =
                Engine.builder(problem)
                        .offspringSelector(new TournamentSelector<>(4))
                        .survivorsSelector(UFTournamentSelector.ofVec())
                        .optimize(Optimize.MAXIMUM)
                        .build();

        EvolutionResult<DoubleGene, Vec<double[]>> collect = engine
                .stream()
                .limit(Setting.LIMIT_GENERATION)
                .collect(EvolutionResult.toBestEvolutionResult());

        int i = 0 ;
        for (Phenotype<DoubleGene, Vec<double[]>> doubleGeneVecPhenotype : collect.getPopulation()) {
            System.out.println(i + ":");
            i++;
            System.out.println(doubleGeneVecPhenotype);
        }
        System.out.println(collect.getBestPhenotype());
    }
}

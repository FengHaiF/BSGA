package CDHS.app;

import CDHS.GA.AlleleF;
import CDHS.GA.GeneF;
import CDHS.persistence.Importer;
import CDHS.showframe.ShowFrame;
import io.jenetics.*;
import io.jenetics.engine.*;
import io.jenetics.ext.moea.NSGA2Selector;
import io.jenetics.ext.moea.Vec;

import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("all")
public class Main {
    static List<AnyChromosome<AlleleF>> chromosomes = new ArrayList<>();
    static Importer importer = new Importer();

    public static void main(String[] args) {
        long start= System.currentTimeMillis();

        initialChromosomes();
        Genotype<AnyGene<AlleleF>> genotype = Genotype.of(chromosomes);
        System.out.println(genotype);

        /**
         * solver
         */
        SingleObjSolver(genotype);
//        MultiObjSolver(genotype);

        System.out.println(System.currentTimeMillis()-start);



    }

    public static void initialChromosomes(){
        AnyChromosome<AlleleF> chromosome1 =
                AnyChromosome.of(
                        ()->GeneF.generateAllele(importer.getOilSeatList()),
                        importer.getPlaneList().size()
                );

        System.out.println(chromosome1);
        chromosomes.add(chromosome1);

        AnyChromosome<AlleleF> chromosome2 =
                AnyChromosome.of(
                        ()->GeneF.generateAllele(importer.getDySeatList()),
                        importer.getPlaneList().size()
                );

        System.out.println(chromosome2);
        chromosomes.add(chromosome2);

        AnyChromosome<AlleleF> chromosome3 =
                AnyChromosome.of(
                        ()-> GeneF.generateAllele1(importer.getOrderList()),
                        importer.getPlaneList().size()
                );
        System.out.println(chromosome3);
        chromosomes.add(chromosome3);
    }

    public static void MultiObjSolver(Genotype<AnyGene<AlleleF>> genotype){
        Engine<AnyGene<AlleleF>, Vec<Float[]>> engine = Engine
                .builder(gt -> Vec.of(fitnessAndOccupy(gt,importer)),genotype)
                .populationSize(Setting.POPULATION_SIZE)
                .offspringSelector(new TournamentSelector<>(4))
                .survivorsSelector(NSGA2Selector.ofVec())
                .optimize(Optimize.MINIMUM)
                .build();

        EvolutionResult<AnyGene<AlleleF>, Vec<Float[]>> collect = engine
                .stream()
                .limit(Setting.LIMIT_GENERATION)
                .peek(Main::printM)
                .collect(EvolutionResult.toBestEvolutionResult());

        new ShowFrame().MOGA_ShowFrame(collect,importer);
    }

    public static void SingleObjSolver(Genotype<AnyGene<AlleleF>> genotype){
        Engine<AnyGene<AlleleF>, Float> engine = Engine
                .builder(gt -> Main.fitnessS(gt,importer),genotype)
                .populationSize(Setting.POPULATION_SIZE)
                .optimize(Optimize.MINIMUM)
                .build();
        EvolutionResult<AnyGene<AlleleF>, Float> result = engine
                .stream()
                .limit(Setting.LIMIT_GENERATION)
                .peek(Main::printS)
                .collect(EvolutionResult.toBestEvolutionResult());

        new ShowFrame().SOGA_ShowFrame(result,importer);
    }

    private static void printS(final EvolutionResult<AnyGene<AlleleF>, Float> result) {
        for (Phenotype<AnyGene<AlleleF>, Float> phenotype:
        result.getPopulation()) {
            System.out.println(result.getGeneration()+":"+phenotype);
        }
        System.out.println(result.getGeneration()+" best:"+result.getBestPhenotype());
    }

    private static void printM(EvolutionResult<AnyGene<AlleleF>, Vec<Float[]>> result) {
        for (Phenotype<AnyGene<AlleleF>, Vec<Float[]>> phenotype:
                result.getPopulation()) {
            System.out.println(result.getGeneration()+":"+phenotype);
        }
        System.out.println(result.getGeneration()+" best:"+result.getBestPhenotype());
    }

    public static float fitnessS(Genotype<AnyGene<AlleleF>> genotype, Importer importer) {
        Solution  solution= new Solution();

        double makespan = solution.calculateMakespan(genotype, importer);

//        System.out.println(solution.getOperationList());
        return (float)makespan;
    }

    public static Float[] fitnessAndOccupy(Genotype<AnyGene<AlleleF>> genotype, Importer importer) {
        Solution  solution= new Solution();

        Float makespan = (float)solution.calculateMakespan(genotype, importer);
        Float uselessOccupy = (float)solution.getUselessOccupy();
        Float[] floats ={makespan,uselessOccupy};

//        System.out.println(solution.getOperationList());
        return floats;
    }

}

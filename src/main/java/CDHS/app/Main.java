package CDHS.app;

import CDHS.GA.AlleleF;
import CDHS.GA.GeneF;
import CDHS.domain.Order;
import CDHS.domain.Seat;
import CDHS.persistence.Importer;
import CDHS.showframe.ShowFrame;
import io.jenetics.*;
import io.jenetics.engine.*;
import io.jenetics.ext.moea.MOEA;
import io.jenetics.ext.moea.NSGA2Selector;
import io.jenetics.ext.moea.Vec;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;

import java.util.*;
import java.util.function.Predicate;


@SuppressWarnings("all")
public class Main {
    static List<AnyChromosome<AlleleF>> chromosomesBZ = new ArrayList<>();
    static List<AnyChromosome<AlleleF>> chromosomesBF= new ArrayList<>();
    static Importer importer = new Importer();

    public static void main(String[] args) {
        startApp();
    }

    public static void startApp(){
        long start= System.currentTimeMillis();

        initialChromosomes();
        Genotype<AnyGene<AlleleF>> genotypeBZ = Genotype.of(chromosomesBZ);
        Genotype<AnyGene<AlleleF>> genotypeBF = Genotype.of(chromosomesBF);
        System.out.println(genotypeBZ);
        System.out.println(genotypeBF);
        /**
         * solver BF
         */
        Engine<AnyGene<AlleleF>, Float> engine = Engine
                .builder(gt -> Main.fitnessSingleBF(gt,importer),genotypeBF)
//                .genotypeValidator(validator)
                .populationSize(Setting.POPULATION_SIZE)
                .optimize(Optimize.MINIMUM)
                .build();
        Phenotype<AnyGene<AlleleF>, Float> result = engine
                .stream()
                .limit(Setting.LIMIT_GENERATION)
                .peek(Main::printS)
                .collect(EvolutionResult.toBestPhenotype());
//        new ShowFrame().SOGA_ShowFrame(result,importer);
        /**
         * solver BZ
         */
//        SingleObjSolverBZ(genotype);
        MultiObjSolverBZ(genotypeBZ);

        System.out.println(System.currentTimeMillis()-start);
    }

    //初始化
    public static void initialChromosomes(){
        AnyChromosome<AlleleF> chromosome1 =
                AnyChromosome.of(
                        ()->GeneF.generateAllele(importer.getOilSeatList()),
                        importer.getPlaneList().size()
                );

        System.out.println(chromosome1);
        chromosomesBZ.add(chromosome1);

        AnyChromosome<AlleleF> chromosome2 =
                AnyChromosome.of(
                        ()->GeneF.generateAllele(importer.getDySeatList()),
                        importer.getPlaneList().size()
                );

        System.out.println(chromosome2);
        chromosomesBZ.add(chromosome2);

        AnyChromosome<AlleleF> chromosome3 =
                AnyChromosome.of(
                        ()-> GeneF.generateAllele1(importer.getOrderList()),
                        importer.getPlaneList().size()
                );

        System.out.println(chromosome3);
        chromosomesBZ.add(chromosome3);

        AnyChromosome<AlleleF> chromosome4 =
                AnyChromosome.of(
                        ()-> GeneF.generateAllele(importer.getBfList()),
                        importer.getPlaneList().size()-importer.getTsqList().size()
                );

        System.out.println(chromosome4);
        chromosomesBF.add(chromosome4);

        AnyChromosome<AlleleF> chromosome5 =
                AnyChromosome.of(
                        ()-> GeneF.generateAllele(importer.getTsqList()),
                        importer.getPlaneList().size()-importer.getTsqList().size()
                );

        System.out.println(chromosome5);
        chromosomesBF.add(chromosome5);
    }

    //保障计划构建
    public static void MultiObjSolverBZ(Genotype<AnyGene<AlleleF>> genotype){
        Engine<AnyGene<AlleleF>, Vec<Float[]>> engine = Engine
                .builder(gt -> Vec.of(multiObjectBZ(gt,importer)),genotype)
                .genotypeValidator(validator)
                .populationSize(Setting.POPULATION_SIZE)
                .alterers(new MultiPointCrossover<>(0.5,2),new Mutator<>(0.15))
                .offspringSelector(new TournamentSelector<>(4))
                .survivorsSelector(NSGA2Selector.ofVec())
//                .survivorsSelector(UFTournamentSelector.ofVec())
                .optimize(Optimize.MINIMUM)
                .build();

        //求帕累托集
        ISeq<Phenotype<AnyGene<AlleleF>, Vec<Float[]>>> collect = engine
                .stream()
                .limit(Setting.LIMIT_GENERATION)
                .peek(Main::printM)
//                .collect(EvolutionResult.toBestPhenotype());
                .collect(MOEA.toParetoSet(IntRange.of(20,30)));

        Phenotype<AnyGene<AlleleF>, Vec<Float[]>> bestPhenotype = collect.get(getSeqOfBestPhenotye(collect));

        System.out.println("--- ParetoSet --- ");
        for (Phenotype<AnyGene<AlleleF>, Vec<Float[]>> anyGeneVecPhenotype : collect) {
            System.out.println(anyGeneVecPhenotype);
        }

        new ShowFrame().MOGA_ShowFrame(bestPhenotype,importer);
    }

    public static void SingleObjSolverBZ(Genotype<AnyGene<AlleleF>> genotype){
        Engine<AnyGene<AlleleF>, Float> engine = Engine
                .builder(gt -> Main.fitnessSingleBZ(gt,importer),genotype)
                .genotypeValidator(validator)
                .populationSize(Setting.POPULATION_SIZE)
                .optimize(Optimize.MINIMUM)
                .build();
        Phenotype<AnyGene<AlleleF>, Float> result = engine
                .stream()
                .limit(Setting.LIMIT_GENERATION)
                .peek(Main::printS)
                .collect(EvolutionResult.toBestPhenotype());

        new ShowFrame().SOGA_ShowFrame(result,importer);
    }

    public static Predicate<? super Genotype<AnyGene<AlleleF>>> validator = gt -> {
        // Implement advanced Genotype check .
        Set<Long> set = new HashSet<>();
        Chromosome<AnyGene<AlleleF>> orderChromosome = gt.getChromosome(Setting.NUM_OF_MATAINANCE);
        for (int i = 0; i < importer.getNumOfPlane(); i++) {
            Order order = (Order) orderChromosome.getGene(i).getAllele();
            int[] orderArray = Setting.ORDER_TABLE[(int) order.getOrderId()];
            Chromosome<AnyGene<AlleleF>> chromosome = gt.getChromosome(orderArray[0]);
            Seat seat = (Seat) chromosome.getGene(i).getAllele();
            if(set.contains(seat.getSeatId())){
                return false;
            }
            else {
                set.add(seat.getSeatId());
            }
        }

        //过滤不知道有没有用
//        for (int i = 0; i < importer.getNumOfPlane(); i++) {
//            Order order = (Order) orderChromosome.getGene(i).getAllele();
//            int[] orderArray = Setting.ORDER_TABLE[(int) order.getOrderId()];
//            Chromosome<AnyGene<AlleleF>> chromosome = gt.getChromosome(orderArray[0]);
//            Seat seat = (Seat) chromosome.getGene(i).getAllele();
//            if(seat.getSeatId()!=Setting.INITIAL_TABLE[i]){
//                return false;
//            }
//        }

        //做一层过滤，有点用
        if (fitnessSingleBZ(gt,importer) > 5000.0)
            return false;

        return true;
        };

    private static int getSeqOfBestPhenotye(ISeq<Phenotype<AnyGene<AlleleF>, Vec<Float[]>>> collect){
        Phenotype<AnyGene<AlleleF>, Vec<Float[]>> firstGeneVecPhenotype = collect.get(0);
        Float makespan = firstGeneVecPhenotype.getFitness().data()[0];
        Float dist = firstGeneVecPhenotype.getFitness().data()[1];

        int which = 0;
        for (int i = 1; i < collect.size(); i++) {
            Phenotype<AnyGene<AlleleF>, Vec<Float[]>> anyGeneVecPhenotype = collect.get(i);
            Float newMakespan = anyGeneVecPhenotype.getFitness().data()[0];
            if (makespan > newMakespan){
                makespan = newMakespan;
                which = i;
            }
            else if ( makespan == newMakespan ){
                Float newDist = anyGeneVecPhenotype.getFitness().data()[1];
                if ( dist > newDist){
                    which = i;
                }
            }
        }
        return which;
    }

    public static float fitnessSingleBZ(Genotype<AnyGene<AlleleF>> genotype, Importer importer) {
        Solution  solution= new Solution();

        double makespan = solution.calculateMakespan(genotype, importer);

//        System.out.println(solution.getOperationList());
        return (float)makespan;
    }

    public static Float[] multiObjectBZ(Genotype<AnyGene<AlleleF>> genotype, Importer importer) {
        Solution  solution= new Solution();

        Float makespan = (float)solution.calculateMakespan(genotype, importer);
        Float uselessOccupy = (float)solution.getUselessOccupy();
        Float totalDist = (float)solution.getTotalDist();
//        makespan+=uselessOccupy;
        Float[] floats ={makespan,totalDist};
//        System.out.println(solution.getOperationList());
//        System.out.println(uselessOccupy);
        return floats;
    }

    //布放计划的构建
    public static float fitnessSingleBF(Genotype<AnyGene<AlleleF>> genotype, Importer importer) {
        SolutionBF  solutionBF = new SolutionBF();
//
        double makespan = solutionBF.calculateMakespan(genotype, importer);

//        System.out.println(solution.getOperationList());
        return (float)makespan;
    }
    //print
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
//        Vec<Float[]> fitness = result.getPopulation().get(0).getFitness();
        System.out.println(result.getGeneration()+" best:"+result.getBestPhenotype());
        System.out.println(result.getGeneration()+"fitness"+result.getBestFitness());
    }

}

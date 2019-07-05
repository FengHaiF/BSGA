package CDHS.MyGA;

import CDHS.appAlter.Setting;
import CDHS.domain.Seat;
import CDHS.persistence.Importer;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@SuppressWarnings("all")
public class GeneEngine {
    private Importer importer;
    private Random random = new Random();
    private Map<Integer,Chromosome> chromosomeMap = new HashMap<>();
    private List<Chromosome> population = new ArrayList<>();
    private HashMap<Integer,Double> rouletteMap = new HashMap<>();
    int numOfPlane;
    int numOfMatainance;

    /**
     * Gene Tools
     */
    private void rouletteInit(){
        double total_fitness = 0;
        for (Chromosome chromosome : population) {
            total_fitness+=chromosome.getFitness();
        }
        for (int i = 0; i < population.size(); i++) {
            double each = total_fitness / population.get(i).getFitness();
            rouletteMap.put(i,each);
        }
        total_fitness = 0;
        for (int i = 0; i < rouletteMap.size(); i++) {
            total_fitness += rouletteMap.get(i);
        }
        double each = 0;
        for (int i = 0; i < population.size(); i++) {
            each += rouletteMap.get(i)/total_fitness;
            rouletteMap.put(i,each);
        }
    }

    private Chromosome roulette(List<Chromosome> population){
        double randomNum = random.nextDouble();
        for (int i = 0; i < rouletteMap.size(); i++) {
            if (randomNum < rouletteMap.get(i)){
                return population.get(i);
            }
        }
        return null;
    }

    private Chromosome roulette(List<Chromosome> population,int id){
        //重复20次
        while (true){
            double randomNum = random.nextDouble();
            for (int i = 0; i < rouletteMap.size(); i++) {
                if (randomNum < rouletteMap.get(i)){
                    if (population.get(i).getId() == id){
                        break;
                    }else
                        return population.get(i);
                }
            }
        }
    }

    private boolean isTheSameGene(int gene,List<Integer> w){
        for (int i = 0; i < w.size(); i++) {
            if (gene == w.get(i))
                return true;
        }
        return false;
    }

    private int getAllele(int matainance){
        Map<Integer, List<Seat>> matainanceSeatMap = importer.getMatainanceSeatMap();
        List<Seat> seats = matainanceSeatMap.get(matainance);
        int randomNum = random.nextInt(seats.size());
        return seats.get(randomNum).get_id();
    }

    private int getAllele(int matainance,List<Integer> elseList){
        Set<Integer> elseSet = new HashSet<>();
        for (Integer integer : elseList) {
            elseSet.add(integer);
        }
        Map<Integer, List<Seat>> matainanceSeatMap = importer.getMatainanceSeatMap();
        List<Seat> seats = matainanceSeatMap.get(matainance);
        int randomNum = random.nextInt(seats.size());
        int id = seats.get(randomNum).get_id();
        while (elseSet.contains(id)){
            randomNum = random.nextInt(seats.size());
            id = seats.get(randomNum).get_id();
        }
        return id;
    }

    private List<Integer> getBfSeatList(){
        Map<Integer, Seat> bfSeatMap = importer.getBfSeatMap();
        ArrayList<Integer> integerList = new ArrayList<Integer>(bfSeatMap.keySet());
        List<Integer> bflist = new ArrayList<>();
        for (int i = 0; i < numOfPlane; i++) {
            int randomNum = random.nextInt(integerList.size());
            bflist.add(integerList.get(randomNum));
            integerList.remove(randomNum);
        }
        return bflist;
    }

    private void sortPopulation(List<Chromosome> population){
        Collections.sort(population, new Comparator<Chromosome>() {
            @Override
            public int compare(Chromosome o1, Chromosome o2) {
                return new Double(o1.getFitness()).compareTo(new Double(o2.getFitness()));
            }
        });
    }

    private void initPopulation(){
        numOfPlane = importer.getNumOfPlane();
        numOfMatainance = Setting.NUM_OF_MATAINANCE + Setting.NUM_OF_NEXT;
        for (int i = 0; i < Setting.POPULATION_SIZE; i++) {
            Chromosome chromosome = new Chromosome();
            List<Integer> bfSeatList = getBfSeatList();
            for (int j = 0; j < numOfPlane; j++) {
                List<Seat> oilSeatList = importer.getOilSeatList();
                List<Seat> dySeatList = importer.getDySeatList();
                List<Seat> orderList = importer.getOrderList();
                List<Seat> bfList = importer.getBfList();
                List<Seat> tsqList = importer.getTsqList();

                chromosome.setId(i);
                chromosome.getGenes().add(oilSeatList.get(random.nextInt(oilSeatList.size())).get_id());
                chromosome.getGenes().add(dySeatList.get(random.nextInt(dySeatList.size())).get_id());
                chromosome.getGenes().add(orderList.get(random.nextInt(orderList.size())).get_id());
                chromosome.getGenes().add(bfSeatList.get(j));
                chromosome.getGenes().add(tsqList.get(random.nextInt(tsqList.size())).get_id());
            }
            chromosome.initGeneList(numOfPlane,numOfMatainance);
            chromosome.initFitness();
            population.add(chromosome);
            sortPopulation(population);
//            chromosomeMap.put(chromosome.getId(),chromosome);
        }
    }

    /**
     * Gene Engine
     */

    public void engineBegin(){
        initPopulation();//初始化种群
        for (int i = 0; i < Setting.LIMIT_GENERATION; i++) {
            rouletteInit();//初始化轮盘赌
            System.out.println("generation "+ i );
            for (int mateTime = 0; mateTime < Setting.POPULATION_SIZE; mateTime++) {
                crossoverAndMutation();
            }
            dieOut();
            System.out.println(population);
        }
    }

    private void dieOut() {
        if (population.size() > Setting.POPULATION_SIZE){
            sortPopulation(population);
            for (int i = (population.size()-1); i >= Setting.POPULATION_SIZE; i--) {
                population.remove(i);
            }
        }
        for (int i = 0; i < population.size(); i++) {
            population.get(i).setId(i);
        }
    }

    private void crossoverAndMutation(){
        Chromosome chromosomeF = roulette(population);
        Chromosome chromosomeM = roulette(population, chromosomeF.getId());
        List<Integer> C1 = new ArrayList<>();
        List<Integer> C2 = new ArrayList<>();

        crossoverBf(C1,C2,chromosomeF,chromosomeM,3);
        muntationBf(C1,C2,Setting.MUTATION_RATE);

        List<Integer> genesF = chromosomeF.getGenes();
        List<Integer> genesM = chromosomeM.getGenes();
        Chromosome chromosomeC1 = new Chromosome();
        Chromosome chromosomeC2 = new Chromosome();
        //单点交叉
//        crossoverSinglePoint(genesF,genesM,chromosomeC1,chromosomeC2);
        //两点交叉
        crossoverDoublePoint(genesF,genesM,chromosomeC1,chromosomeC2);
        mutation(Setting.MUTATION_RATE,chromosomeC1);
        mutation(Setting.MUTATION_RATE,chromosomeC2);

        chromosomeC1.getGeneList().get(3).clear();
        chromosomeC1.getGeneList().get(3).addAll(C1);
        chromosomeC1.initGenes(numOfPlane,numOfMatainance);
        chromosomeC2.getGeneList().get(3).clear();
        chromosomeC2.getGeneList().get(3).addAll(C2);
        chromosomeC2.initGenes(numOfPlane,numOfMatainance);

        chromosomeC1.initFitness();
        chromosomeC2.initFitness();

//        System.out.println("F :"+chromosomeF);
//        System.out.println("C1:"+chromosomeC1);
//        System.out.println("M :"+chromosomeM);
//        System.out.println("C2:"+chromosomeC2);

        population.add(chromosomeC1);
        population.add(chromosomeC2);
    }

    private void muntationBf(List<Integer> C1,List<Integer> C2,double mutationRate){
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < numOfPlane; j++) {
                if (random.nextDouble() < mutationRate) {
                    List<Integer> elseList = new ArrayList<>();
                    for (int k = 0; k < C1.size(); k++) {
                        if (j != k)
                            elseList.add(C1.get(k));
                    }
                    C1.set(j, getAllele(3,elseList));
//                        System.out.println("mutation:" + "the " + j + " matainance" + "the " + k + " plane" + chromosome.getGeneList().get(j).get(k));
                }
                if (random.nextDouble() < mutationRate) {
                    List<Integer> elseList = new ArrayList<>();
                    for (int k = 0; k < C2.size(); k++) {
                        if (j != k)
                            elseList.add(C2.get(k));
                    }
                    C2.set(j, getAllele(3,elseList));
//                        System.out.println("mutation:" + "the " + j + " matainance" + "the " + k + " plane" + chromosome.getGeneList().get(j).get(k));
                }
            }
        }
    }

    private void mutation(double mutationRate,Chromosome chromosome) {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < chromosome.getGeneList().size(); j++) {
                for (int k = 0; k < chromosome.getGeneList().get(j).size(); k++) {
                    if (random.nextDouble() < mutationRate) {
                        chromosome.getGeneList().get(j).set(k, getAllele(j));
//                        System.out.println("mutation:" + "the " + j + " matainance" + "the " + k + " plane" + chromosome.getGeneList().get(j).get(k));
                    }
                }
            }
            chromosome.initGenes(numOfPlane,numOfMatainance);
        }
    }

    private void crossoverBf(List<Integer> C1,List<Integer> C2,Chromosome chromosomeF, Chromosome chromosomeM,int bfNum) {
        List<Integer> geneListF = chromosomeF.getGeneList().get(bfNum);
        List<Integer> geneListM = chromosomeM.getGeneList().get(bfNum);

        List<Integer> f = new ArrayList<>();
        List<Integer> _f = new ArrayList<>();
        List<Integer> m = new ArrayList<>();
        List<Integer> _m = new ArrayList<>();

        int crossPoint1 = random.nextInt(geneListF.size());
        int crossPoint2 = random.nextInt(geneListF.size());

        if (crossPoint1 > crossPoint2){
            int temp = crossPoint1;
            crossPoint1 = crossPoint2;
            crossPoint2 = temp;
        }

        for (int i = 0; i < geneListF.size(); i++) {
            _f.add(geneListF.get(i));
            _m.add(geneListM.get(i));
        }

        for (int i = crossPoint1; i >= crossPoint1 && i <= crossPoint2; i++) {
                f.add(geneListF.get(i));
                m.add(geneListM.get(i));
        }

        _m.removeAll(f);
        _f.removeAll(m);

        for (int i = 0; i < geneListF.size(); i++) {
            if (i < crossPoint1){
                C1.add(_m.get(i));
                C2.add(_f.get(i));
            }else if (i > crossPoint2){
                int num = i - 1 - crossPoint2 + crossPoint1;
                C1.add(_m.get(num));
                C2.add(_f.get(num));
            }else {
                C1.add(f.get(i-crossPoint1));
                C2.add(m.get(i-crossPoint1));
            }
        }
    }

    private void crossoverSinglePoint(List<Integer> genesF,List<Integer> genesM,Chromosome chromosomeC1,Chromosome chromosomeC2){

        int crossPoint = random.nextInt(genesF.size());
        for (int i = 0; i < crossPoint; i++) {
            chromosomeC1.getGenes().add(genesF.get(i));
            chromosomeC2.getGenes().add(genesM.get(i));
        }
        for (int i = crossPoint; i < genesF.size(); i++) {
            chromosomeC1.getGenes().add(genesM.get(i));
            chromosomeC2.getGenes().add(genesF.get(i));
        }
        chromosomeC1.initGeneList(numOfPlane,numOfMatainance);
        chromosomeC2.initGeneList(numOfPlane,numOfMatainance);
    }

    private void crossoverDoublePoint(List<Integer> genesF,List<Integer> genesM,Chromosome chromosomeC1,Chromosome chromosomeC2){
        int crossPoint1 = random.nextInt(genesF.size());
        int crossPoint2 = random.nextInt(genesF.size());
        if (crossPoint1 > crossPoint2){
            int temp = crossPoint1;
            crossPoint1 = crossPoint2;
            crossPoint2 = temp;
        }
        for (int i = 0; i < crossPoint1; i++) {
            chromosomeC1.getGenes().add(genesF.get(i));
            chromosomeC2.getGenes().add(genesM.get(i));
        }
        for (int i = crossPoint1; i < crossPoint2; i++) {
            chromosomeC1.getGenes().add(genesM.get(i));
            chromosomeC2.getGenes().add(genesF.get(i));
        }
        for (int i = crossPoint2; i < genesF.size(); i++) {
            chromosomeC1.getGenes().add(genesF.get(i));
            chromosomeC2.getGenes().add(genesM.get(i));
        }
        chromosomeC1.initGeneList(numOfPlane,numOfMatainance);
        chromosomeC2.initGeneList(numOfPlane,numOfMatainance);
    }



    public GeneEngine(Importer importer) {
        this.importer = importer;
    }
}

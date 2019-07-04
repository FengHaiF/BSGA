package CDHS.MyGA;

import CDHS.appAlter.Setting;
import CDHS.domain.PlaneJZJ;
import CDHS.domain.Seat;
import CDHS.persistence.Importer;

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

    public void engineBegin(){
        engineInit();
        for (int i = 0; i < 2; i++) {
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
    }

    private void engineInit(){
        initPopulation();//初始化种群
        rouletteInit();//初始化轮盘赌
    }

    private void initPopulation(){
        numOfPlane = importer.getNumOfPlane();
        numOfMatainance = Setting.NUM_OF_MATAINANCE + Setting.NUM_OF_NEXT;
        for (int i = 0; i < Setting.POPULATION_SIZE; i++) {
            Chromosome chromosome = new Chromosome();
            for (PlaneJZJ planeJZJ : importer.getPlaneList()) {
                List<Seat> oilSeatList = importer.getOilSeatList();
                List<Seat> dySeatList = importer.getDySeatList();
                List<Seat> orderList = importer.getOrderList();
                List<Seat> bfList = importer.getBfList();
                List<Seat> tsqList = importer.getTsqList();

                chromosome.setId(i);
                chromosome.getGenes().add(oilSeatList.get(random.nextInt(oilSeatList.size())).get_id());
                chromosome.getGenes().add(dySeatList.get(random.nextInt(dySeatList.size())).get_id());
                chromosome.getGenes().add(orderList.get(random.nextInt(orderList.size())).get_id());
                chromosome.getGenes().add(bfList.get(random.nextInt(bfList.size())).get_id());
                chromosome.getGenes().add(tsqList.get(random.nextInt(tsqList.size())).get_id());
            }
            chromosome.init(numOfPlane,numOfMatainance);
            population.add(chromosome);
            chromosomeMap.put(chromosome.getId(),chromosome);
        }
    }

    private void sortPopulation(List<Chromosome> population){
        Collections.sort(population, new Comparator<Chromosome>() {
            @Override
            public int compare(Chromosome o1, Chromosome o2) {
                return new Double(o1.getFitness()).compareTo(new Double(o2.getFitness()));
            }
        });
    }

    private void crossoverAndMutation(){
        Chromosome chromosomeF = roulette(population);
        Chromosome chromosomeM = roulette(population, chromosomeF.getId());
        List<Integer> genesF = chromosomeF.getGenes();
        List<Integer> genesM = chromosomeM.getGenes();
        Chromosome chromosomeC1 = new Chromosome();
        Chromosome chromosomeC2 = new Chromosome();
        //单点交叉
//        crossoverSinglePoint(genesF,genesM,chromosomeC1,chromosomeC2);
        //两点交叉
        crossoverDoublePoint(genesF,genesM,chromosomeC1,chromosomeC2);
        mutation(0.15,chromosomeC1);
        mutation(0.15,chromosomeC2);
//        System.out.println("F :"+chromosomeF);
//        System.out.println("C1:"+chromosomeC1);
//        System.out.println("M :"+chromosomeM);
//        System.out.println("C2:"+chromosomeC2);
        population.add(chromosomeC1);
        population.add(chromosomeC2);
    }

    private void mutation(double mutationRate,Chromosome chromosome) {
        for (int i = 0; i < 2; i++) {
            if(random.nextDouble() < mutationRate){
//                System.out.println("mutation:");
                for (int j = 0; j < chromosome.getGeneList().size(); j++) {
                    if (random.nextDouble() < mutationRate){
//                        System.out.println("the " + j + " matainance");
                        for (int k = 0; k < chromosome.getGeneList().get(j).size(); k++) {
                            if (random.nextDouble() < mutationRate){
//                                System.out.println("the " + k + " plane");
                                chromosome.getGeneList().get(j).set(k,getAllele(j));
                            }
                        }
                    }
                }
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
        chromosomeC1.init(numOfPlane,numOfMatainance);
        chromosomeC2.init(numOfPlane,numOfMatainance);
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
        chromosomeC1.init(numOfPlane,numOfMatainance);
        chromosomeC2.init(numOfPlane,numOfMatainance);
    }

    private void rouletteInit(){
        sortPopulation(population);
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
        double randomNum = random.nextDouble();
        for (int i = 0; i < rouletteMap.size(); i++) {
            if (randomNum < rouletteMap.get(i)){
                if (population.get(i).getId() == id){
                    randomNum = random.nextDouble();
                    i--;
                }else
                    return population.get(i);
            }
        }
        return null;
    }

    private int getAllele(int matainance){
        Map<Integer, List<Seat>> matainanceSeatMap = importer.getMatainanceSeatMap();
        List<Seat> seats = matainanceSeatMap.get(matainance);
        int randomNum = random.nextInt(seats.size());
        return seats.get(randomNum).get_id();
    }

    public GeneEngine(Importer importer) {
        this.importer = importer;
    }
}

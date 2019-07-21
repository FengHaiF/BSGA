package CDHS.MyGA;

import CDHS.appAlter.Setting;
import CDHS.domain.Seat;
import CDHS.persistence.Importer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@SuppressWarnings("all")
public class GeneEngine {
    //配置初始化到importer
    private Importer importer;
    private Random random = new Random();
    //种群
    public List<Chromosome> population = new ArrayList<>();
    //轮盘赌的概率存放
    private HashMap<Integer,Double> rouletteMap = new HashMap<>();
    //飞机数
    int numOfPlane;
    //保障数
    int numOfMatainance;

    /**
     * Gene Tools
     */
    //轮盘赌初始化
    private void rouletteInit(){
        double total_fitness = 0;
        //每个个体的适应度，
        for (int i = 0; i < population.size(); i++) {
            double each = 1 / population.get(i).getFitness();
            rouletteMap.put(i,each);
        }
        for (int i = 0; i < rouletteMap.size(); i++) {
            total_fitness += rouletteMap.get(i);
        }
        //每个个体所占的比例（和为1）
        double each = 0;
        for (int i = 0; i < population.size(); i++) {
            each += rouletteMap.get(i)/total_fitness;
            rouletteMap.put(i,each);
        }
    }

    //轮盘赌产生一个随机个体
    private Chromosome roulette(List<Chromosome> population){
        double randomNum = random.nextDouble();
        for (int i = 0; i < rouletteMap.size(); i++) {
            if (randomNum < rouletteMap.get(i)){
                return population.get(i);
            }
        }
        return null;
    }

    //产生的个体不重复
    private Chromosome roulette(List<Chromosome> population,int id){
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

    //判断是否是重复基因
    private boolean isTheRepeatGene(int gene, List<Integer> w){
        for (int i = 0; i < w.size(); i++) {
            if (gene == w.get(i))
                return true;
        }
        return false;
    }

    //根据保障的次序来选择等位基因
    private int getAllele(int matainance){
        Map<Integer, List<Seat>> matainanceSeatMap = importer.getMatainanceSeatMap();
        List<Seat> seats = matainanceSeatMap.get(matainance);
        int randomNum = random.nextInt(seats.size());
        return seats.get(randomNum).get_id();
    }

    //选在布放站位是不允许重复
    private int getAllele(List<Integer> seatsList,List<Integer> elseList){
        Set<Integer> elseSet = new HashSet<>();
        for (Integer integer : elseList) {
            elseSet.add(integer);
        }

        int randomNum = random.nextInt(seatsList.size());
        int id = seatsList.get(randomNum);
        while (elseSet.contains(id)){
            randomNum = random.nextInt(seatsList.size());
            id = seatsList.get(randomNum);
        }
        return id;
    }

    //返回随机生成布放站位
    private List<Integer> getNotRepeatSeatList(Set<Integer> set,int num){
        ArrayList<Integer> integerList = new ArrayList<Integer>(set);
        List<Integer> bflist = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            int randomNum = random.nextInt(integerList.size());
            bflist.add(integerList.get(randomNum));
            integerList.remove(randomNum);
        }
        return bflist;
    }

    //将种群排序
    private void sortPopulation(List<Chromosome> population){
        Collections.sort(population, new Comparator<Chromosome>() {
            @Override
            public int compare(Chromosome o1, Chromosome o2) {
                return new Double(o1.getFitness()).compareTo(new Double(o2.getFitness()));
            }
        });
    }


    /**
     * Gene Engine
     */
    //入口
    public void engineBegin() throws IOException {
        File file = new File("E:\\cdhsData\\data.txt");
        FileWriter fw = null;
        fw = new FileWriter(file);
        file.createNewFile();

        //初始化种群
        initPopulation();
        for (int i = 0; i < Setting.LIMIT_GENERATION; i++) {
            //初始化轮盘赌
            rouletteInit();
            System.out.println("generation "+ i );
            //交配产生三倍的种群
            for (int mateTime = 0; mateTime < Setting.POPULATION_SIZE; mateTime++) {
                crossoverAndMutation();
            }
            dieOut();

            double fitness = population.get(0).getFitness();
            fw.write((i+1)+","+fitness+"\r\n");
            fw.flush();
            System.out.println(population);
        }

        fw.close();
    }

    //初始化种群
    private void initPopulation(){
        numOfPlane = importer.getNumOfPlane();
        numOfMatainance = Setting.NUM_OF_MATAINANCE + Setting.NUM_OF_NEXT;
        int numOfOperation = Setting.NUM_OF_MATAINANCE * numOfPlane;//加一是最后的布放站位
        for (int i = 0; i < Setting.POPULATION_SIZE; i++) {
            Chromosome chromosome = new Chromosome();
            //先获取随机布放站位
            List<Integer> bfSeatList = getNotRepeatSeatList(importer.getBfSeatMap().keySet(),numOfPlane);
            List<Integer> orderSeatList = getNotRepeatSeatList(importer.getOrderMap().keySet(),numOfOperation);
            for (int j = 0; j < numOfPlane; j++) {
                List<Seat> oilSeatList = importer.getOilSeatList();
                List<Seat> dySeatList = importer.getDySeatList();
                List<Seat> tsqList = importer.getTsqList();

                chromosome.setId(i);
                chromosome.getGenes().add(oilSeatList.get(random.nextInt(oilSeatList.size())).get_id());
                chromosome.getGenes().add(dySeatList.get(random.nextInt(dySeatList.size())).get_id());
//                chromosome.getGenes().add(orderList.get(random.nextInt(orderList.size())).get_id());
                chromosome.getBfGenes().add(bfSeatList.get(j));
                chromosome.getGenes().add(tsqList.get(random.nextInt(tsqList.size())).get_id());
            }
            for (int k = 0; k < numOfOperation; k++) {
                chromosome.getOrderGenes().add(orderSeatList.get(k));
            }
            //做初始化添加排序
            chromosome.initGeneList(numOfPlane,numOfMatainance);
            chromosome.initFitness(importer);

            population.add(chromosome);
        }
        sortPopulation(population);
    }

    //淘汰垃圾
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

    //交叉变异
    private void crossoverAndMutation(){
        //轮盘赌随机取父母个体，保证父母不一样
        Chromosome chromosomeF = roulette(population);
        Chromosome chromosomeM = roulette(population, chromosomeF.getId());

        //布放站位的交叉变异先执行,C1和C2存储结果
//        List<Integer> C1 = new ArrayList<>();
//        List<Integer> C2 = new ArrayList<>();
//        crossoverBf(C1,C2,chromosomeF,chromosomeM,Setting.BF_NUM);
//        muntationBf(C1,C2,Setting.MUTATION_RATE);

        List<Integer> genesF = chromosomeF.getGenes();
        List<Integer> genesM = chromosomeM.getGenes();
        List<Integer> bfGenesF = chromosomeF.getBfGenes();
        List<Integer> bfGenesM = chromosomeM.getBfGenes();
        List<Integer> orderGenesF = chromosomeF.getOrderGenes();
        List<Integer> orderGenesM = chromosomeM.getOrderGenes();
        //child1和child2
        Chromosome chromosomeC1 = new Chromosome();
        Chromosome chromosomeC2 = new Chromosome();
        //单点交叉
//        crossoverSinglePoint(genesF,genesM,chromosomeC1,chromosomeC2);
        //两点交叉
        crossoverDoublePoint(genesF,genesM,chromosomeC1,chromosomeC2);
        crossoverNotRepeat(bfGenesF,bfGenesM,chromosomeC1.getBfGenes(),chromosomeC2.getBfGenes());
        crossoverNotRepeat(orderGenesF,orderGenesM,chromosomeC1.getOrderGenes(),chromosomeC2.getOrderGenes());
        //两个子代的变异
        mutation(Setting.MUTATION_RATE,chromosomeC1);
        mutation(Setting.MUTATION_RATE,chromosomeC2);
        mutationNotRepeat(chromosomeC1.getBfGenes(),0.003);
        mutationNotRepeat(chromosomeC2.getBfGenes(),0.003);
        mutationExchange(chromosomeC1.getOrderGenes(),0.003);
        mutationExchange(chromosomeC2.getOrderGenes(),0.003);
        //将布放站位交叉变异的值放入，并初始化
//        chromosomeC1.getGeneList().get(Setting.BF_NUM).clear();
//        chromosomeC1.getGeneList().get(Setting.BF_NUM).addAll(C1);
//        chromosomeC1.initGenes(numOfPlane,numOfMatainance);
//        chromosomeC2.getGeneList().get(Setting.BF_NUM).clear();
//        chromosomeC2.getGeneList().get(Setting.BF_NUM).addAll(C2);
//        chromosomeC2.initGenes(numOfPlane,numOfMatainance);

        //重新计算适应度
        chromosomeC1.initFitness(importer);
        chromosomeC2.initFitness(importer);

//        System.out.println("F :"+chromosomeF);
//        System.out.println("C1:"+chromosomeC1);
//        System.out.println("M :"+chromosomeM);
//        System.out.println("C2:"+chromosomeC2);

        //加入种群
        population.add(chromosomeC1);
        population.add(chromosomeC2);
    }

    //普通变异
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

    //不重复变异
    private void mutationNotRepeat(List<Integer> child,double mutationRate){
        //这样写不好，一定会生成其他站位，可以设计自己交换两个基因的位置
            for (int j = 0; j < child.size(); j++) {
                if (random.nextDouble() < mutationRate) {
                    //为了保证不重复
                    List<Integer> elseList = new ArrayList<>();
                    for (int k = 0; k < child.size(); k++) {
                        if (j != k)
                            elseList.add(child.get(k));
                    }
                    child.set(j, getAllele(child, elseList));
//                        System.out.println("mutation:" + "the " + j + " matainance" + "the " + k + " plane" + chromosome.getGeneList().get(j).get(k));
                }
            }
    }

    //不重复变异
    private void mutationExchange(List<Integer> child,double mutationRate){
        for (int i = 0; i < child.size(); i++) {
            if (random.nextDouble() < mutationRate){
                int mutationGene = random.nextInt(child.size());
                int changeGene = random.nextInt(child.size());
                Integer temp = child.get(mutationGene);
                child.set(mutationGene,child.get(changeGene));
                child.set(changeGene,temp);
            }
        }
    }

    //不重复的交叉
    private void crossoverNotRepeat(List<Integer> genesF,List<Integer> genesM,List<Integer> chromosomeC1,List<Integer> chromosomeC2) {
//        List<Integer> geneListF = chromosomeF.getGeneList().get(bfNum);
//        List<Integer> geneListM = chromosomeM.getGeneList().get(bfNum);

        //存储交叉点
        List<Integer> f = new ArrayList<>();
        List<Integer> m = new ArrayList<>();
        //复制geneListF&M
        List<Integer> _f = new ArrayList<>();
        List<Integer> _m = new ArrayList<>();

        //交叉点
        int crossPoint1 = random.nextInt(genesF.size());
        int crossPoint2 = random.nextInt(genesF.size());
        if (crossPoint1 > crossPoint2){
            int temp = crossPoint1;
            crossPoint1 = crossPoint2;
            crossPoint2 = temp;
        }

        //复制
        for (int i = 0; i < genesF.size(); i++) {
            _f.add(genesF.get(i));
            _m.add(genesM.get(i));
        }

        //储存中间段
        for (int i = crossPoint1; i >= crossPoint1 && i <= crossPoint2; i++) {
            f.add(genesF.get(i));
            m.add(genesM.get(i));
        }

        //去掉中间段，防止重复
        _m.removeAll(f);
        _f.removeAll(m);


        //将剩下基因的分到其他位置
        for (int i = 0; i < genesF.size(); i++) {
            if (i < crossPoint1){
                chromosomeC1.add(_m.get(i));
                chromosomeC2.add(_f.get(i));
            }else if (i > crossPoint2){
                int num = i - 1 - crossPoint2 + crossPoint1;
                chromosomeC1.add(_m.get(num));
                chromosomeC2.add(_f.get(num));
            }else {
                chromosomeC1.add(f.get(i-crossPoint1));
                chromosomeC2.add(m.get(i-crossPoint1));
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
        //交叉换位置
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
        //初始化
        chromosomeC1.initGeneList(numOfPlane,numOfMatainance);
        chromosomeC2.initGeneList(numOfPlane,numOfMatainance);
    }



    public GeneEngine(Importer importer) {
        this.importer = importer;
    }
}

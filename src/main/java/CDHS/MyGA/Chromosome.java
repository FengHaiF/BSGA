package CDHS.MyGA;


import CDHS.appAlter.Solution;
import CDHS.persistence.Importer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Chromosome {
    private int id;
    private double fitness;
    //将所有基因放入一个list中
    private List<Integer> genes = new ArrayList<>();
    //将gene里的基因按照保障种类分类
    private List<List<Integer>> geneList = new ArrayList<>();

    private List<Integer> orderGenes = new ArrayList<>();
    private List<Integer> bfGenes = new ArrayList<>();

    public Chromosome() {

    }

    public List<Integer> getBfGenes() {
        return bfGenes;
    }

    public void setBfGenes(List<Integer> bfGenes) {
        this.bfGenes = bfGenes;
    }

    public List<Integer> getOrderGenes() {
        return orderGenes;
    }

    public void setOrderGenes(List<Integer> orderGenes) {
        this.orderGenes = orderGenes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public List<Integer> getGenes() {
        return genes;
    }

    public void setGenes(List<Integer> genes) {
        this.genes = genes;
    }

    public List<List<Integer>> getGeneList() {
        return geneList;
    }

    public void setGeneList(List<List<Integer>> geneList) {
        this.geneList = geneList;
    }

    //将Genes转换成GeneList
    public void initGeneList(int planesize, int numOfMatainance){
        if (genes!=null){
            for (int i = 0; i < numOfMatainance; i++) {
                List<Integer> geneM = new ArrayList<>();
                for (int j = 0; j < planesize; j++) {
                    geneM.add(genes.get(j*numOfMatainance+i));
                }
                geneList.add(geneM);
            }
        }
    }

    //将GeneList转换成Genes
    public void initGenes(int planesize, int numOfMatainance){
        for (int i = 0; i < planesize; i++) {
            for (int j = 0; j < numOfMatainance; j++) {
                genes.set(i*numOfMatainance+j,geneList.get(j).get(i));
            }
        }
    }

    //适应度计算（还没写完）
    public void initFitness(Importer importer){
        Solution solution = new Solution(this, importer);
        solution.calculateMakespan();
    }

    @Override
    public String
    toString() {
        return "Chromosome{" +
                "id=" + id +
                ", fitness=" + fitness +
                ", genes=" + genes +
                ", bf=" + bfGenes +
                ", orders=" + orderGenes +
                '}'+'\n';
    }
}

package CDHS.MyGA;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Chromosome {
    private int id;
    private double fitness;
    private List<Integer> genes = new ArrayList<>();
    private List<List<Integer>> geneList = new ArrayList<>();

    public Chromosome() {
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

//    public void init(int planesize, int numOfMatainance){
//        initGeneList(planesize,numOfMatainance);
//        initFitness();
//    }

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

    public void initGenes(int planesize, int numOfMatainance){
        for (int i = 0; i < planesize; i++) {
            for (int j = 0; j < numOfMatainance; j++) {
                genes.set(i*numOfMatainance+j,geneList.get(j).get(i));
            }
        }
    }
    public void initFitness(){
        fitness = 0;
        for (Integer gene : genes) {
            fitness+=gene;
        }
    }

    @Override
    public String
    toString() {
        return "Chromosome{" +
                "id=" + id +
                ", fitness=" + fitness +
                ", genes=" + genes +
                '}'+'\n';
    }
}

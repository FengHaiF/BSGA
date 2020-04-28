package CDHS.appAlter;

import CDHS.MyGA.Chromosome;
import CDHS.MyGA.GeneEngine;
import CDHS.persistence.Importer;
import CDHS.showframe.ShowFrame;

import java.io.IOException;
import java.util.List;

public class Main {
    static Importer importer = new Importer();
    private Solution solution;
    //private double makespan;

    public static void main(String[] args) {
        long start= System.currentTimeMillis();
        //先构建遗传算法的初始解

        GeneEngine geneEngine = new GeneEngine(importer);
        try {
            geneEngine.engineBegin();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Chromosome> population = geneEngine.population;

        Chromosome chromosome = population.get(0);

        new ShowFrame().showFrame(chromosome,importer);

        System.out.println(System.currentTimeMillis()-start);



    }


}

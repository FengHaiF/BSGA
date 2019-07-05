package CDHS.appAlter;

import CDHS.MyGA.GeneEngine;
import CDHS.persistence.Importer;

public class Main {
    static Importer importer = new Importer();

    public static void main(String[] args) {
        long start= System.currentTimeMillis();
        //先构建遗传算法的初始解

        GeneEngine geneEngine = new GeneEngine(importer);
        geneEngine.engineBegin();
//        List<List<String>> population = geneEngine.getPopulation();

        System.out.println(System.currentTimeMillis()-start);
    }

}

package CDHS.showframe;


import CDHS.MyGA.Chromosome;
import CDHS.appAlter.Solution;
import CDHS.persistence.Importer;

import javax.swing.*;

@SuppressWarnings("all")
public class ShowFrame {

    public void showFrame(Chromosome chromosome, Importer importer) {

//        Phenotype<AnyGene<AlleleF>, Vec<Float[]>> phenotype=result.getBestPhenotype();
//        System.out.println("show: G="+result.getGeneration()+" "+result.getBestPhenotype());
        Solution solution = new Solution(chromosome, importer);
        solution.calculateMakespan();
        JFrame jf = new JFrame();
        Gantt gantt = new Gantt(solution,importer);

        jf.setBounds(300, 50, 1000, 800);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.add(gantt);
        jf.setVisible(true);
    }

    public static void main(String[] args) {

    }
}

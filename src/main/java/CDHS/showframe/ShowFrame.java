package CDHS.showframe;

import CDHS.GA.AlleleF;
import CDHS.app.Solution;
import CDHS.app.SolutionBF;
import CDHS.persistence.Importer;
import io.jenetics.AnyGene;
import io.jenetics.DoubleGene;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.ext.moea.Vec;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("all")
public class ShowFrame {

    public void BF_ShowFrame(Phenotype<AnyGene<AlleleF>, Float> phenotype, Importer importer) {
        System.out.println("show: G="+phenotype.getGeneration()+" "+phenotype);

        SolutionBF solution = new SolutionBF();
        solution.calculateMakespan(phenotype.getGenotype(),importer);

        JFrame jf = new JFrame();
        GanttBF gantt = new GanttBF(solution,importer);

        jf.setBounds(300, 50, 1000, 800);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.add(gantt);
        jf.setVisible(true);
    }

    public void SOGA_ShowFrame(Phenotype<AnyGene<AlleleF>, Float> phenotype, Importer importer) {
        System.out.println("show: G="+phenotype.getGeneration()+" "+phenotype);

        Solution solution = new Solution();
        solution.calculateMakespan(phenotype.getGenotype(),importer);

        JFrame jf = new JFrame();
        Gantt gantt = new Gantt(solution,importer);

        jf.setBounds(300, 50, 1000, 800);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.add(gantt);
        jf.setVisible(true);
    }

    public void MOGA_ShowFrame(Phenotype<AnyGene<AlleleF>, Vec<Float[]>> phenotype, Importer importer) {

//        Phenotype<AnyGene<AlleleF>, Vec<Float[]>> phenotype=result.getBestPhenotype();
//        System.out.println("show: G="+result.getGeneration()+" "+result.getBestPhenotype());
        System.out.println("show: G="+phenotype.getGeneration()+" "+phenotype+phenotype.getFitness());

        Solution solution = new Solution();
        solution.calculateMakespan(phenotype.getGenotype(),importer);
//        System.out.println(solution.getOperationList());

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

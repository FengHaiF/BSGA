package CDHS.showframe;

import CDHS.GA.AlleleF;
import CDHS.app.Solution;
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

    public void SOGA_ShowFrame(EvolutionResult<AnyGene<AlleleF>, Float> result, Importer importer) {

        Phenotype<AnyGene<AlleleF>,Float> phenotype=result.getBestPhenotype();
        Solution solution = new Solution();
        solution.calculateMakespan(phenotype.getGenotype(),importer);

        JFrame jf = new JFrame();
        Gantt gantt = new Gantt(solution,importer);

        jf.setBounds(300, 50, 1000, 800);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.add(gantt);
        jf.setVisible(true);
    }

    public void MOGA_ShowFrame(EvolutionResult<AnyGene<AlleleF>, Vec<Float[]>> result, Importer importer) {

        Phenotype<AnyGene<AlleleF>, Vec<Float[]>> phenotype=result.getBestPhenotype();
        Solution solution = new Solution();
        solution.calculateMakespan(phenotype.getGenotype(),importer);

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

package CDHS.controller;

import CDHS.GA.AlleleF;
import CDHS.app.Main;
import io.jenetics.AnyGene;
import io.jenetics.Chromosome;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.ext.moea.Vec;
import io.jenetics.util.ISeq;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
public class controller {
    @RequestMapping("/hello")
    @ResponseBody
    public Map<String,String> hello(){
//        Main.startApp();
//        Main::main();
        Map<String,String> data1 = new HashMap<>();
        data1.put("name","zhangsan");
        data1.put("age","23");
        return data1;
    }
}

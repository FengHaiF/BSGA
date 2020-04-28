package CDHS.showframe;


import CDHS.MyGA.Chromosome;
import CDHS.appAlter.Setting;
import CDHS.appAlter.Solution;
import CDHS.domain.Operation;
import CDHS.persistence.Importer;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings("all")
public class ShowFrame {

    public void showFrame(Chromosome chromosome, Importer importer) {

//        Phenotype<AnyGene<AlleleF>, Vec<Float[]>> phenotype=result.getBestPhenotype();
//        System.out.println("show: G="+result.getGeneration()+" "+result.getBestPhenotype());
        Solution solution = new Solution(chromosome, importer);
        solution.calculateMakespan();
        JFrame jf = new JFrame();
        Gantt gantt = new Gantt(solution,importer);
        jsonOutput(solution);

        jf.setBounds(300, 50, 1000, 800);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.add(gantt);
        jf.setVisible(true);
    }

    public static void main(String[] args) {

    }
    public void jsonOutput(Solution solution) {

        double makespan = solution.getMakespan();
        JSONObject jsonObject;
        JSONArray solutionArray = new JSONArray();
        System.out.println(solution.getOperationList());

        for (int i = 0; i < solution.getOperationPlaneMap().size(); i++) {
            List<Operation> operations = solution.getOperationPlaneMap().get((long)i);
            jsonObject = new JSONObject();
            List<JSONObject> record= new ArrayList<>();
//            jsonObject.put("planeId",String.valueOf(importer.PLANE_ID.get(i)));
            jsonObject.put("planeId",i);
            for (int j = 0; j < operations.size(); j++) {
                SimpleDateFormat ft = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
                JSONObject jsonObjectOperation = new JSONObject();
                Operation operation = operations.get(j);
                jsonObjectOperation.put("operationId",operation.getOperationId());
//                jsonObjectOperation.put("planeId",String.valueOf(importer.PLANE_ID.get((int)operation.getPlaneId())));
                jsonObjectOperation.put("operationType",operation.getOperationType());
                jsonObjectOperation.put("seatId",String.valueOf(operation.getSeatId()));
                jsonObjectOperation.put("duration",operation.getDuration());

                Date date = Setting.DATE;
                jsonObjectOperation.put("startTime",ft.format((date.getTime()-makespan*60*1000+operation.getStart()*60*1000)));
                jsonObjectOperation.put("endTime",ft.format((date.getTime()-makespan*60*1000+operation.getEnd()*60*1000)));
                jsonObjectOperation.put("waitTime",operation.getWaitTime());
                jsonObjectOperation.put("distTime",operation.getDistTime());
                /*if (operation.getOperationType()==0){
                    String s = operation.getSeat().getStationPosition().split("S")[1];
                    jsonObjectOperation.put("stationId",Integer.parseInt(s));
                }else
                    jsonObjectOperation.put("stationId",null);*/
                jsonObjectOperation.put("stationId",operation.getSeat().getStationPosition());

                switch (operation.getOperationType()){
                    case 1 :
                        jsonObject.put("oil_station_operation",jsonObjectOperation);
                        break;
                    case 2 :
                        jsonObject.put("armmo_operation",jsonObjectOperation);
                        break;
                    case 3 :
                        jsonObject.put("wait_fly_operation",jsonObjectOperation);
                        break;
                }
            }
            record.add(jsonObject);
            solutionArray.addAll(record);
        }


        try {
            FileWriter fileWriter = new FileWriter(new File( ShowFrame.class.getClassLoader().getResource("data").getPath(),"solved.json"));
            //FileWriter fileWriter = new FileWriter(new File(Setting.root,"solved.json"));
            //FileWriter fileWriter = new FileWriter(new File("C:\\Users\\DLQ\\Desktop\\BSGA\\target\\classes\\data\\solved.json"));

            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.write(JSON.toJSONString(solutionArray, SerializerFeature.DisableCircularReferenceDetect));
            printWriter.println();
            fileWriter.close();
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

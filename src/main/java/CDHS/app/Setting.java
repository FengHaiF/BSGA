package CDHS.app;

import CDHS.persistence.Importer;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Setting {
    public static int LIMIT_GENERATION;
    public static int NUM_OF_MATAINANCE;
    public static int POPULATION_SIZE;
    public static double QYC_SPEED = 90;
    public static double ZB_DURATION = 10;
    public static double COLD_DURATION = 2;


    public static List<List<Double>> DIST_TABLE = new ArrayList<>();

    public static String DIST_PATH = Setting.class.getClassLoader().getResource("data/dist.xlsx").getPath();

    public static int[][] ORDER_TABLE = {
            {0,1,2},
            {1,0,2}
    };

    public static long[] TAKEOFF_TABLE = {14,15,0,1,2,3,4,5,6,7,8,9,10,11};
    public static long[] INITIAL_TABLE = {14,3,2,15,6,5,7,1,4,7,8,9,10,11};

    static {
        set(SetMode.SET_MODE_01);
    }

    public static void set(SetMode mode){
        switch (mode){
            case SET_MODE_01:
                setDistTable(DIST_TABLE,DIST_PATH);
                POPULATION_SIZE = 20;
                LIMIT_GENERATION  = 5000;
                NUM_OF_MATAINANCE = 2;
                break;
            case SET_MODE_02:
                setDistTable(DIST_TABLE,DIST_PATH);
                POPULATION_SIZE = 30;
                LIMIT_GENERATION  = 10000;
                NUM_OF_MATAINANCE = 2;
                break;
            case SET_MODE_03:
                break;
        }
    }

    public static void setDistTable(List<List<Double>> table,String distPath){
        try {
            File file = new File(distPath);
            Workbook wb = null;
            wb = new XSSFWorkbook(file);
            Sheet sheet = wb.getSheetAt(0);
            int fistRowIndex = sheet.getFirstRowNum();
            int lastRowIndex = sheet.getLastRowNum();

            Row row = sheet.getRow(fistRowIndex);
//            for (Cell cell : row) {
//
//            }
            fistRowIndex += 1;
            for(int rIndex = fistRowIndex; rIndex <= lastRowIndex; rIndex++) {
                row = sheet.getRow(rIndex);
                int firstCellIndex = row.getFirstCellNum();
                int lastCellIndex = row.getLastCellNum();
                List<Double> line = new ArrayList<>();
                for(int cIndex = firstCellIndex + 1; cIndex < lastCellIndex; cIndex++) {
                    Double curValue =  row.getCell(cIndex).getNumericCellValue();
                    line.add(curValue);
                }
                table.add(line);
            }
//            System.out.println(table);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
    }
}

enum SetMode{
    SET_MODE_01,//fcfs 无添加buffer
    SET_MODE_02,//fcfs 添加buffer
    SET_MODE_03
}

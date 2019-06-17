package CDHS.persistence;

import CDHS.app.Setting;
import CDHS.domain.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import com.fasterxml.jackson.databind.util.BeanUtil;
import org.apache.commons.beanutils.BeanUtils;

public class Importer {
    private List<PlaneJZJ> planeList;
    private List<Seat> seatList;
    private List<Seat> oilSeatList;
    private List<Seat> dySeatList;
    private List<OilStation> oilStationList;
    private List<Order> orderList;
    private List<Seat> bfList;
    private List<Seat> zbList;
    private List<Seat> tsqList;

    private Map<Long,PlaneJZJ> planeJZJMap;
    private Map<Long,Seat> oilSeatMap;
    private Map<Long,Seat> dySeatMap;
    private Map<Long,Order> orderMap;
    private Map<String,OilStation> oilStationMap;


    private static String seatPath;
    private static String planePath;
    private static String stationPath;
    private static String orderPath;

    private static String jsonPath;

    static {
        seatPath = Importer.class.getClassLoader().getResource("data/seats.xml").getPath();
        planePath = Importer.class.getClassLoader().getResource("data/planes.xml").getPath();
        stationPath = Importer.class.getClassLoader().getResource("data/stations.xml").getPath();
        orderPath = Importer.class.getClassLoader().getResource("data/orders.xml").getPath();
        jsonPath = Importer.class.getClassLoader().getResource("data/initmess.json").getPath();
    }

    public Importer() {
        init();
    }

    private void init(){
        oilSeatList = new ArrayList<>();
        dySeatList = new ArrayList<>();
        oilStationList = new ArrayList<>();
        orderList = new ArrayList<>();
        bfList = new ArrayList<>();
        zbList = new ArrayList<>();
        tsqList = new ArrayList<>();

        planeJZJMap = new HashMap<>();
        oilSeatMap = new HashMap<>();
        dySeatMap = new HashMap<>();
        oilStationMap = new HashMap<>();
        orderMap = new HashMap<>();

        File file = new File(jsonPath);
        try {
            String s = new JSONReader(new FileReader(file)).readString();
            JSONObject jsonObject = JSONObject.parseObject(s);
            JSONArray jsonArrayPlane = jsonObject.getJSONArray("plane");
            planeList = JSONArray.parseArray(jsonArrayPlane.toString(),PlaneJZJ.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


//        planeList = new XmlIO<PlaneJZJ>(planePath).xml2Object();
        seatList = new XmlIO<Seat>(seatPath).xml2Object();
        oilStationList = new XmlIO<OilStation>(stationPath).xml2Object();
        orderList = new XmlIO<Order>(orderPath).xml2Object();



        for (OilStation oilStation : oilStationList)
            oilStationMap.put(oilStation.getPosition(),oilStation);

        for (Seat seat : seatList) {
            if (seat.isOilFlag()){
                for (String s : seat.getStationList()) {
                    Seat seatOil = new Seat();
                    try {
                        BeanUtils.copyProperties(seatOil,seat);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    //不实用pip
                    seatOil.setOilPipId(0L);
                    seatOil.setStationPosition(s);
                    oilSeatList.add(seatOil);
                }
            }
            if (seat.isDyFlag()){
                dySeatList.add(seat);
            }
            switch (seat.getSeatType()){
                case 0:
                    bfList.add(seat);
                    break;
                case 1:
                    tsqList.add(seat);
                    break;
                case 2:
                    zbList.add(seat);
                    break;
            }
        }

        for (PlaneJZJ planeJZJ : planeList)
            planeJZJMap.put(planeJZJ.getPlaneId(),planeJZJ);
        for (Seat seat : oilSeatList)
            oilSeatMap.put(seat.getSeatId(),seat);
        for (Seat seat : dySeatList)
            dySeatMap.put(seat.getSeatId(),seat);
        for (Order order : orderList)
            orderMap.put(order.getOrderId(),order);

        for (int i = 0; i < planeList.size(); i++) {
            Setting.INITIAL_TABLE[i] = planeList.get(i).getInitialPosition();
        }


        System.out.println(oilSeatList);
        System.out.println(dySeatList);
        System.out.println(planeList);
        System.out.println(oilStationList);
        System.out.println(orderList);

    }

    public int getOilstationPipNum(String station){
        return oilStationMap.get(station).getOilPipNum();
    }

    public double getDyDuration(){
        return 10;
    }

    public double getOilDuration(long planeId,String seatPosition){
        double duration = 0;
        for (OilStation oilStation : oilStationList) {
            if (oilStation.getPosition().equals(seatPosition)){
                duration = planeJZJMap.get(planeId).getOilNeed() / oilStation.getSpeed();
            }
        }
        return duration;
    }

    public int getNumOfPlane(){
        return planeList.size();
    }

    public List<Seat> getBfList() {
        return bfList;
    }

    public void setBfList(List<Seat> bfList) {
        this.bfList = bfList;
    }

    public List<Seat> getZbList() {
        return zbList;
    }

    public void setZbList(List<Seat> zbList) {
        this.zbList = zbList;
    }

    public List<Seat> getTsqList() {
        return tsqList;
    }

    public void setTsqList(List<Seat> tsqList) {
        this.tsqList = tsqList;
    }

    public List<PlaneJZJ> getPlaneList() {
        return planeList;
    }

    public void setPlaneList(List<PlaneJZJ> planeList) {
        this.planeList = planeList;
    }

    public List<Seat> getSeatList() {
        return seatList;
    }

    public void setSeatList(List<Seat> seatList) {
        this.seatList = seatList;
    }

    public List<Seat> getOilSeatList() {
        return oilSeatList;
    }

    public void setOilSeatList(List<Seat> oilSeatList) {
        this.oilSeatList = oilSeatList;
    }

    public List<Seat> getDySeatList() {
        return dySeatList;
    }

    public void setDySeatList(List<Seat> dySeatList) {
        this.dySeatList = dySeatList;
    }

    public List<OilStation> getOilStationList() {
        return oilStationList;
    }

    public void setOilStationList(List<OilStation> oilStationList) {
        this.oilStationList = oilStationList;
    }

    public List<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
    }

    public static String getSeatPath() {
        return seatPath;
    }

    public static void setSeatPath(String seatPath) {
        Importer.seatPath = seatPath;
    }

    public static String getPlanePath() {
        return planePath;
    }

    public static void setPlanePath(String planePath) {
        Importer.planePath = planePath;
    }

    public static String getStationPath() {
        return stationPath;
    }

    public static void setStationPath(String stationPath) {
        Importer.stationPath = stationPath;
    }

    public static String getOrderPath() {
        return orderPath;
    }

    public static void setOrderPath(String orderPath) {
        Importer.orderPath = orderPath;
    }


    public static void main(String[] args) {
        new Importer().init();
        System.out.println("over");
    }
}

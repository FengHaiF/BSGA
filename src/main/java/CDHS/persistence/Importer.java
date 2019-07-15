package CDHS.persistence;

import CDHS.appAlter.Setting;
import CDHS.domain.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import org.apache.commons.beanutils.BeanUtils;

@SuppressWarnings("all")
public class Importer {
//    public  List<Long> PLANE_ID = new ArrayList<>();
    private List<PlaneJZJ> planeList;
    private List<Seat> seatList;
    private List<Seat> oilSeatList;
    private List<Seat> dySeatList;
    private List<OilStation> oilStationList;
    private List<Seat> orderList;
    private List<Seat> bfList;
    private List<Seat> zbList;
    private List<Seat> tsqList;

    private Map<Long,PlaneJZJ> planeJZJMap;
    private Map<Integer,List<Seat>> matainanceSeatMap;
    private Map<Integer,Seat> oilSeatMap;
    private Map<Integer,Seat> dySeatMap;
    private Map<Integer,Seat> orderMap;

    private Map<Integer,Seat> bfSeatMap;
    private Map<Integer,Seat> tsqSeatMap;

    private Map<String,OilStation> oilStationMap;


    private static String seatPath;
    private static String planePath;
    private static String stationPath;
//    private static String orderPath;

    private static String jsonPath;

    static {
        seatPath = Importer.class.getClassLoader().getResource("data/seats.xml").getPath();
        planePath = Importer.class.getClassLoader().getResource("data/planes.xml").getPath();
        stationPath = Importer.class.getClassLoader().getResource("data/stations.xml").getPath();
//        orderPath = Importer.class.getClassLoader().getResource("data/orders.xml").getPath();
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

        matainanceSeatMap = new HashMap<>();
        bfSeatMap = new HashMap<>();
        tsqSeatMap = new HashMap<>();

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
            JSONArray oil_station = jsonObject.getJSONArray("oil_station");

            //遍历油站
            for (int i = 0; i < oil_station.size(); i++) {
                JSONArray serve_parking = oil_station.getJSONObject(0).getJSONArray("serve_parking");
                Double speed = oil_station.getJSONObject(0).getDouble("speed");
                Long stationId = oil_station.getJSONObject(0).getLong("stationId");
            }

            for (int i = 0; i < Setting.NUM_OF_MATAINANCE * getNumOfPlane(); i++) {
                Seat seat = new Seat();
                seat.set_id(i);
                orderList.add(seat);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


//        planeList = new XmlIO<PlaneJZJ>(planePath).xml2Object();
        seatList = new XmlIO<Seat>(seatPath).xml2Object();
        oilStationList = new XmlIO<OilStation>(stationPath).xml2Object();
//        orderList = new XmlIO<Seat>(orderPath).xml2Object();

        //设置开始时间
        if (planeList!=null){
            String landTime = planeList.get(0).getTakeoffTime();
            SimpleDateFormat ft = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            try {
                Date parse = ft.parse(landTime);
                Setting.DATE.setTime(parse.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

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
        for (int i = 0; i < oilSeatList.size(); i++) {
            Seat seat = oilSeatList.get(i);
            seat.set_id(i);
            oilSeatMap.put(seat.get_id(),seat);
        }
        for (int i = 0; i < dySeatList.size(); i++) {
            Seat seat = dySeatList.get(i);
            seat.set_id(i);
            dySeatMap.put(seat.get_id(),seat);
        }
        for (int i = 0; i < orderList.size(); i++) {
            Seat order = orderList.get(i);
            order.set_id(i);
            orderMap.put(order.get_id(),order);
        }
        for (int i = 0; i < bfList.size(); i++) {
            Seat seat = bfList.get(i);
            seat.set_id(i);
            bfSeatMap.put(seat.get_id(),seat);
        }
        for (int i = 0; i < tsqList.size(); i++) {
            Seat seat = tsqList.get(i);
            seat.set_id(i);
            tsqSeatMap.put(seat.get_id(),seat);
        }

        for (int i = 0; i < planeList.size(); i++) {
            Setting.INITIAL_TABLE[i] = planeList.get(i).getInitialPosition();
        }

        for (int i = 0; i < Setting.NUM_OF_MATAINANCE + Setting.NUM_OF_NEXT; i++) {
            switch (i){
                case 0:
                    matainanceSeatMap.put(i,oilSeatList);
                    break;
                case 1:
                    matainanceSeatMap.put(i,dySeatList);
                    break;
                case 2:
                    matainanceSeatMap.put(i, bfList);
                    break;
                case 3:
                    matainanceSeatMap.put(i,tsqList);
                    break;
            }
        }

        System.out.println(oilSeatList);
        System.out.println(dySeatList);
        System.out.println(planeList);
        System.out.println(orderList);
        System.out.println(oilStationList);


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

    public Map<Integer, Seat> getOilSeatMap() {
        return oilSeatMap;
    }

    public Map<Integer, Seat> getDySeatMap() {
        return dySeatMap;
    }

    public Map<Integer, Seat> getTsqSeatMap() {
        return tsqSeatMap;
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

    public List<Seat> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<Seat> orderList) {
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

    public Map<Integer, List<Seat>> getMatainanceSeatMap() {
        return matainanceSeatMap;
    }

    public void setMatainanceSeatMap(Map<Integer, List<Seat>> matainanceSeatMap) {
        this.matainanceSeatMap = matainanceSeatMap;
    }

    public Map<Integer, Seat> getBfSeatMap() {
        return bfSeatMap;
    }

    public Map<Integer, Seat> getOrderMap() {
        return orderMap;
    }

    public static void main(String[] args) {
        new Importer().init();
        System.out.println("over");
    }
}

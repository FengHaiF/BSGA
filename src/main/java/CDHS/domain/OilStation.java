package CDHS.domain;

public class OilStation {
    private long stationId;
    private int oilPipNum;
    private String position;
    private double speed;
    private double stationEndTime;

    public OilStation() {
    }

    public OilStation(long stationId, int oilPipNum, String position, int speed, double stationEndTime) {
        this.stationId = stationId;
        this.oilPipNum = oilPipNum;
        this.position = position;
        this.speed = speed;
        this.stationEndTime = stationEndTime;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public long getStationId() {
        return stationId;
    }

    public void setStationId(long stationId) {
        this.stationId = stationId;
    }

    public int getOilPipNum() {
        return oilPipNum;
    }

    public void setOilPipNum(int oilPipNum) {
        this.oilPipNum = oilPipNum;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public double getStationEndTime() {
        return stationEndTime;
    }

    public void setStationEndTime(double stationEndTime) {
        this.stationEndTime = stationEndTime;
    }

    @Override
    public String toString() {
        return  "Station-" + stationId;
    }
}

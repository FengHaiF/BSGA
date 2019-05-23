package CDHS.absplanner;

import CDHS.GA.AlleleF;

public abstract class AbstractSeat extends AlleleF {
    protected int seatType;
    protected long seatId;
    protected String seatSize;
    protected boolean oilFlag;
    protected boolean dyFlag;
    protected String stationPosition;

    public AbstractSeat() {
    }

    public AbstractSeat(int _id, int _type, String _name) {
        super(_id, _type, _name);
    }

    public AbstractSeat(int _id, int _type, String _name, int seatType, long seatId, String seatSize, boolean oilFlag, boolean dyFlag, String stationPosition) {
        super(_id, _type, _name);
        this.seatType = seatType;
        this.seatId = seatId;
        this.seatSize = seatSize;
        this.oilFlag = oilFlag;
        this.dyFlag = dyFlag;
        this.stationPosition = stationPosition;
    }

    public int getSeatType() {
        return seatType;
    }

    public void setSeatType(int seatType) {
        this.seatType = seatType;
    }

    public long getSeatId() {
        return seatId;
    }

    public void setSeatId(long seatId) {
        this.seatId = seatId;
    }

    public String getSeatSize() {
        return seatSize;
    }

    public void setSeatSize(String seatSize) {
        this.seatSize = seatSize;
    }

    public boolean isOilFlag() {
        return oilFlag;
    }

    public void setOilFlag(boolean oilFlag) {
        this.oilFlag = oilFlag;
    }

    public boolean isDyFlag() {
        return dyFlag;
    }

    public void setDyFlag(boolean dyFlag) {
        this.dyFlag = dyFlag;
    }

    public String getStationPosition() {
        return stationPosition;
    }

    public void setStationPosition(String stationPosition) {
        this.stationPosition = stationPosition;
    }

    @Override
    public String toString() {
        return "Seat-" + seatId ;
    }
}

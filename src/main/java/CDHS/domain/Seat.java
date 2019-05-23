package CDHS.domain;

import CDHS.absplanner.AbstractSeat;

public class Seat extends AbstractSeat {
    private Long oilPipId;

    @Override
    public String toString() {
        if (oilPipId == null){
            return "Seat-" + seatId;
        }else {
            return "Seat-" + seatId + " " + this.getStationPosition() +"-"+ oilPipId;
        }
    }

    public Seat() {
    }

    public Seat(int _id, int _type, String _name) {
        super(_id, _type, _name);
    }

    public Seat(int _id, int _type, String _name, int seatType, long seatId, String seatSize, boolean oilFlag, boolean dyFlag, String stationPosition, Long oilPipId) {
        super(_id, _type, _name, seatType, seatId, seatSize, oilFlag, dyFlag, stationPosition);
        this.oilPipId = oilPipId;
    }

    public Long getOilPipId() {
        return oilPipId;
    }

    public void setOilPipId(Long oilPipId) {
        this.oilPipId = oilPipId;
    }
}


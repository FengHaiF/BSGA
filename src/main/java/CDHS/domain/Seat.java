package CDHS.domain;

import CDHS.absplanner.AbstractSeat;

import java.util.List;

public class Seat extends AbstractSeat {
    private Long oilPipId;

    @Override
    public String toString() {
        if (oilPipId == null){
            return "Seat-" + seatId;
        }else {
            return "Seat-" + seatId + " " + this.getStationPosition();
        }
    }

    public Seat() {
    }

    public Seat(int seatType, long seatId, String seatSize, boolean oilFlag, boolean dyFlag, List<String> stationList, String stationPosition, Long oilPipId) {
        super(seatType, seatId, seatSize, oilFlag, dyFlag, stationList, stationPosition);
        this.oilPipId = oilPipId;
    }

    public Long getOilPipId() {
        return oilPipId;
    }

    public void setOilPipId(Long oilPipId) {
        this.oilPipId = oilPipId;
    }
}


package CDHS.domain;


import CDHS.absplanner.AbstractPlane;

public class PlaneJZJ extends AbstractPlane {
    private Seat oilSeat;
    private Seat dySeat;
    private OilStation oilStation;
    private Order order;


    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Seat getOilSeat() {
        return oilSeat;
    }

    public void setOilSeat(Seat oilSeat) {
        this.oilSeat = oilSeat;
    }

    public Seat getDySeat() {
        return dySeat;
    }

    public void setDySeat(Seat dySeat) {
        this.dySeat = dySeat;
    }

    public OilStation getOilStation() {
        return oilStation;
    }

    public void setOilStation(OilStation oilStation) {
        this.oilStation = oilStation;
    }

    public PlaneJZJ() {
    }

    public PlaneJZJ(long planeId, String task, String planeType, String planeSize, double oilMax, double oilQuantity, int ammunitionMax, int ammunitionQuantity, Seat oilSeat, Seat dySeat, OilStation oilStation,long initialPosition) {
        super(planeId, task, planeType, planeSize, oilMax, oilQuantity, ammunitionMax, ammunitionQuantity,initialPosition);
        this.oilSeat = oilSeat;
        this.dySeat = dySeat;
        this.oilStation = oilStation;
    }

    public long getPlaneOrderId(){
        if (order == null)
            return -1;
        return order.getOrderId();
    }

    public long getPlaneOilSeatId(){
        if (oilSeat == null)
            return -1;
        return oilSeat.getSeatId();
    }

    public long getPlaneDySeatId(){
        if (oilSeat == null)
            return -1;
        return dySeat.getSeatId();
    }

    public long getOilStationId(){
        if (oilStation == null)
            return -1;
        return oilStation.getStationId();
    }

    public String getOilStationPosition(){
        if (oilStation == null)
            return null;
        return oilStation.getPosition();
    }

    public String getOilSeatPosition(){
        if (oilSeat == null)
            return null;
        return oilSeat.getStationPosition();
    }

    public double getOilNeed(){
        return (oilMax - oilQuantity);
    }

}

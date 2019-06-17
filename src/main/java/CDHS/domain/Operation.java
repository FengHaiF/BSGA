package CDHS.domain;



public class Operation {
    private int operationId;    //什么保障操作
    private long planeId;   //哪个飞机
    private Seat seat;
    private double duration;    //持续时间
    private double start;
    private double end;    //开始结束时间
    private double waitTime;
    private double coldTime;
    private int operationType;
    private long previousSeatId;
    private double distTime;
    private Operation previousOperation;
    private Operation nextOperation;

    public Operation() {
    }

    public Operation(int operationId, long planeId, Seat seat, double duration, double start, double end, int operationType) {
        this.operationId = operationId;
        this.planeId = planeId;
        this.seat = seat;
        this.duration = duration;
        this.start = start;
        this.end = end;
        this.operationType = operationType;
        this.waitTime = 0;
        this.coldTime = 0;
    }

    public String getStationPosition(){
        return seat.getStationPosition();
    }

    public long getPipId(){
        return seat.getOilPipId();
    }

    public long getSeatId(){
        return seat.getSeatId();
    }

    public int getOperationId() {
        return operationId;
    }

    public void setOperationId(int operationId) {
        this.operationId = operationId;
    }

    public long getPlaneId() {
        return planeId;
    }

    public void setPlaneId(long planeId) {
        this.planeId = planeId;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double getStart() {
        return start;
    }

    public void setStart(double start) {
        this.start = start;
    }

    public double getEnd() {
        return end;
    }

    public void setEnd(double end) {
        this.end = end;
    }

    public int getOperationType() {
        return operationType;
    }

    public void setOperationType(int operationType) {
        this.operationType = operationType;
    }

    public long getPreviousSeatId() {
        return previousSeatId;
    }

    public void setPreviousSeatId(long previousSeatId) {
        this.previousSeatId = previousSeatId;
    }

    public double getDistTime() {
        return distTime;
    }

    public void setDistTime(double distTime) {
        this.distTime = distTime;
    }

    public Operation getPreviousOperation() {
        return previousOperation;
    }

    public void setPreviousOperation(Operation previousOperation) {
        this.previousOperation = previousOperation;
    }

    public Operation getNextOperation() {
        return nextOperation;
    }

    public void setNextOperation(Operation nextOperation) {
        this.nextOperation = nextOperation;
    }

    public double getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(double waitTime) {
        this.waitTime = waitTime;
    }

    public double getColdTime() {
        return coldTime;
    }

    public void setColdTime(double coldTime) {
        this.coldTime = coldTime;
    }

    @Override
    public String toString() {
        return "Operation{" +
                "operationId=" + operationId +
                ", planeId=" + planeId +
                ", seat=" + seat +
                ", duration=" + duration +
                ", start=" + start +
                ", end=" + end +
                ", operationType=" + operationType +
                ", previousSeatId=" + previousSeatId +
                ", distTime=" + distTime +
                '}'+'\n';
    }
}

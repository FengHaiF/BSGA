package CDHS.domain;

public class TimeBlock {
    protected double startTime;
    protected double endTime;
    protected Integer operationBelongTo;

    public TimeBlock(double startTime, double endTime, Integer operationBelongTo) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.operationBelongTo = operationBelongTo;
    }

    public Integer getOperationBelongTo() {
        return operationBelongTo;
    }

    public void setOperationBelongTo(Integer operationBelongTo) {
        this.operationBelongTo = operationBelongTo;
    }

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public double getEndTime() {
        return endTime;
    }

    public void setEndTime(double endTime) {
        this.endTime = endTime;
    }
}

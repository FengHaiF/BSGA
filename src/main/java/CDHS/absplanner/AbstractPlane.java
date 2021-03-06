package CDHS.absplanner;

public abstract class AbstractPlane {
    protected long planeId;
    protected String task;
    protected String planeType;
    protected String planeSize;
    protected double oilMax;
    protected double oilQuantity;
    protected int ammunitionMax;
    protected int ammunitionQuantity;
    protected long initialPosition;
    protected String takeoffTime;
    protected String landTime;

    public AbstractPlane() {
    }

    public AbstractPlane(long planeId, String task, String planeType, String planeSize, double oilMax, double oilQuantity, int ammunitionMax, int ammunitionQuantity, long initialPosition) {
        this.planeId = planeId;
        this.task = task;
        this.planeType = planeType;
        this.planeSize = planeSize;
        this.oilMax = oilMax;
        this.oilQuantity = oilQuantity;
        this.ammunitionMax = ammunitionMax;
        this.ammunitionQuantity = ammunitionQuantity;
        this.initialPosition = initialPosition;
    }

    public long getPlaneId() {
        return planeId;
    }

    public void setPlaneId(long planeId) {
        this.planeId = planeId;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getPlaneType() {
        return planeType;
    }

    public void setPlaneType(String planeType) {
        this.planeType = planeType;
    }

    public String getPlaneSize() {
        return planeSize;
    }

    public void setPlaneSize(String planeSize) {
        this.planeSize = planeSize;
    }

    public int getAmmunitionMax() {
        return ammunitionMax;
    }

    public void setAmmunitionMax(int ammunitionMax) {
        this.ammunitionMax = ammunitionMax;
    }

    public double getOilMax() {
        return oilMax;
    }

    public void setOilMax(double oilMax) {
        this.oilMax = oilMax;
    }

    public double getOilQuantity() {
        return oilQuantity;
    }

    public void setOilQuantity(double oilQuantity) {
        this.oilQuantity = oilQuantity;
    }

    public int getAmmunitionQuantity() {
        return ammunitionQuantity;
    }

    public void setAmmunitionQuantity(int ammunitionQuantity) {
        this.ammunitionQuantity = ammunitionQuantity;
    }

    public long getInitialPosition() {
        return initialPosition;
    }

    public void setInitialPosition(long initialPosition) {
        this.initialPosition = initialPosition;
    }

    public String getTakeoffTime() {
        return takeoffTime;
    }

    public void setTakeoffTime(String takeoffTime) {
        this.takeoffTime = takeoffTime;
    }

    public String getLandTime() {
        return landTime;
    }

    public void setLandTime(String landTime) {
        this.landTime = landTime;
    }

    @Override
    public String toString() {
        return  planeType + " - " + planeId;
    }
}

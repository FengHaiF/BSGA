package CDHS.persistence;

import CDHS.domain.OilStation;
import CDHS.domain.Order;
import CDHS.domain.PlaneJZJ;
import CDHS.domain.Seat;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.Dom4JDriver;


public class XmlIO<T> {
    String path;
    String fileName;

    public XmlIO() {
    }

    public XmlIO(String path, String fileName) {
        this.path = path;
        this.fileName = fileName;
    }

    public XmlIO(String path){
        File file = new File(path);
        String filePath = file.getParent();
        String fileName = file.getName();
        this.path = filePath;
        this.fileName = fileName;
    }

    public List<T> xml2Object(File xmlFile) {
        XStream xStream = new XStream(new Dom4JDriver());
        XStream.setupDefaultSecurity(xStream);
        xStream.allowTypes(new Class[]{List.class, Order.class, OilStation.class, Seat.class, PlaneJZJ.class, HashSet.class, String.class, HashMap.class, String.class});
        List<T> origin = (List<T>) xStream.fromXML(xmlFile);
        return origin;
    }

    public List<T> xml2Object(){
        return xml2Object(new File(path, fileName));
    }

}

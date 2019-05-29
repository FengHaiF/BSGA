package CDHS.persistence;

import CDHS.domain.Seat;
import com.thoughtworks.xstream.XStream;

import java.util.ArrayList;
import java.util.List;

public class ToXml {
    public static void main(String[] args) {
        Seat seat = new Seat();
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        seat.setStationList(list);

        XStream xStream = new XStream();

        String s = xStream.toXML(seat);
        System.out.println(s);
    }
}

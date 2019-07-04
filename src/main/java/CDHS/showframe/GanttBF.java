//package CDHS.showframe;
//
//import CDHS.domain.Operation;
//import CDHS.persistence.Importer;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.font.FontRenderContext;
//import java.awt.geom.AffineTransform;
//import java.util.List;
//
//
//@SuppressWarnings("all")
//public class GanttBF extends JPanel {
//    private int starttime = 8 * 60;
//    private int endtime = 11 * 60;
//    private int blank;
//    private int numOfJZJ;
//    private int numOfZW;
//    private float hJZJ;
//    private float wZW;
//    private SolutionBF solutionBF;
//    private Point lt;
//    private Point lb;
//    private Point rt;
//    private Point rb;
//
//    public GanttBF(SolutionBF solution, Importer importer) {
//        this.solutionBF = solution;
//        numOfJZJ = importer.getNumOfPlane();
////        numOfZW = importer.ge;
//        init();
//    }
//    @Override
//    public void paint(Graphics g) {
//        super.paint(g);
//        Graphics2D g2d = (Graphics2D) g;
//
//        drawItem(g2d);
//        drawAxis(g2d);
//    }
//
//    private void init() {
//        this.starttime = 0 * 60;
//        this.endtime = 4 * 60;
//        this.blank = 20;
//        blank = 20;
//        hJZJ = 20f;
//        wZW = 10f;
//        lt = new Point(100, 70);
//        lb = new Point(lt.x, (int) (lt.y + numOfJZJ * (hJZJ + 10)));
//        rb = new Point((int) (lb.x + (endtime - starttime) * wZW), lb.y);
//        rt = new Point(rb.x, lt.y);
//    }
//
//    public void drawItem(Graphics2D g) {
//        List<Operation> result = solutionBF.getOperationList();
//        for (Operation operation : result) {
//            String name = getZWName(operation);
//            double time = operation.getDuration();
//            int x = (int) (lt.x+operation.getStart()*wZW);
//            int y = (int) (lt.y +operation.getPlaneId() * (hJZJ + 10) + hJZJ / 2 + (hJZJ + 10) / 2);
//            int width = (int) (operation.getDuration()*wZW);
//            int heigth = (int) hJZJ;
//            double trantime=operation.getDistTime();
//            int width_tran = (int) (trantime*wZW);
//
//            //画gantt图
//            g.drawRect(x, y-heigth, width, heigth);
//            //  name+=":"+oex.o.get_duration();
//            int strx = x + width / 2 - getStringWidth(name, g.getFont()) / 2;
//            int stry = y - heigth / 2 + getStringHeight(name, g.getFont()) / 2;
//            if (operation.getOperationType()==2)
//                g.drawString(name,lt.x + (int)solutionBF.getMakespan()*wZW+5, stry);
//            else
//                g.drawString(name, strx, stry);
//
//            //粉色为等待
//            if(operation.getPreviousOperation()==null&&operation.getStart()!=0) {
//                int waitWidth = (int) (operation.getStart() * wZW);
//                g.setColor(Color.pink);
//                g.fillRect(lt.x , y-heigth, waitWidth, heigth);
//            }
//            if (operation.getWaitTime()!=0){
//                int waitWidth = (int) (operation.getWaitTime() * wZW);
//                g.setColor(Color.pink);
//                g.fillRect(x + width + 1, y-heigth, waitWidth-1, heigth+1);
//            }
//            if (operation.getOperationType()==1){
//                int waitWidth = (int) (operation.getColdTime() * wZW);
//                g.setColor(Color.CYAN);
//                g.fillRect(x + width + 1, y-heigth, waitWidth-1, heigth+1);
//            }
//
//            g.setColor(Color.lightGray);
//            g.fillRect(x-width_tran +1, y-heigth, width_tran, heigth+1);
//            g.setColor(Color.BLACK);
//            //距离显示
////            g.drawString(String.valueOf(trantime),x, y-heigth);
//        }
//        //画最后makespan线
//        int x = (int)(lt.x + solutionBF.getMakespan()*wZW);
////        System.out.println("Print" + solution.getMakespan());
//        g.drawLine(x, lb.y,x, lt.y);
//        g.drawString(String.valueOf(solutionBF.getMakespan()), x, lb.y + 15);
//    }
//
//    private void drawAxis(Graphics2D g) {
//        setBackground(Color.white);
//        g.setColor(Color.BLACK);
//        int strx,stry;
//        String strname;
//
//        g.drawLine(lb.x, lb.y, lt.x, lt.y);
//        for (int i = 0; i < numOfJZJ; i++) {
//            int x = lt.x;
//            int y = (int) (lt.y + i * (hJZJ + 10) + (hJZJ + 10) / 2);
//            g.drawLine(x, y, x + 2, y);
//            String name = "F" + (i);
//            g.drawString(name, x - 25, y+5);
////            String init = "M" + INIT_TABLE[i];
////            g.drawString(init, x - 25, y+5);
//        }
//        strname="Aircraft";
//        strx=50;
//        stry=100;
//
////        g.rotate(90,getStringWidth(strname,g.getFont())/2,getStringHeight(strname,g.getFont())/2);
//        g.drawString(strname,50,50);
////        g.rotate(-90,getStringWidth(strname,g.getFont())/2,getStringHeight(strname,g.getFont())/2);
//
//        g.drawLine(lb.x, lb.y, rb.x, rb.y);
//        for (int i = 0; i <= endtime - starttime; i += blank) {
//            int x = (int) (lb.x + i * wZW);
//            int y = lb.y;
//            g.drawLine(x, y, x, y - 5);
//            String name = i+"";
//            g.drawString(name, x - 10, y + 15);
//        }
//        strname="Time";
//        strx=lb.x+(rb.x-lb.x)/2-getStringWidth(strname,g.getFont())/2;
//        stry=lb.y+30;
//        g.drawString(strname,strx,stry);
//    }
//
//    private static AffineTransform atf = new AffineTransform();
//    private static FontRenderContext frc = new FontRenderContext(atf, true,
//            true);
//
//    public static int getStringHeight(String str, Font font) {
//        if (str == null || str.isEmpty() || font == null) {
//            return 0;
//        }
//        return (int) font.getStringBounds(str, frc).getHeight();
//    }
//
//    public static int getStringWidth(String str, Font font) {
//        if (str == null || str.isEmpty() || font == null) {
//            return 0;
//        }
//        return (int) font.getStringBounds(str, frc).getWidth();
//    }
//
//    public static String getZWName(Operation operation) {
//        String name = null;
//        if (operation.getSeat().getOilPipId() == null)
//            name= "M"+(operation.getSeatId());
//        else
//            name= "M"+(operation.getSeatId())+"-"+operation.getSeat().getStationPosition();
//        return name;
//    }
//}

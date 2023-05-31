import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.Scanner;

public class ProductionPossibilityCurve extends JPanel {

    private List<Point> points;
    private String xAxisLabel;
    private String yAxisLabel;
    private static int con;
    private static int cap;
    private Point dragPoint;

    private JLabel coordinatesLabel;

    private JSpinner maxCon;
    private JSpinner maxCap;

    private JButton addCoor;

    public ProductionPossibilityCurve(String xAxisLabel, String yAxisLabel) {
        points = new ArrayList<>();
        this.xAxisLabel = xAxisLabel;
        this.yAxisLabel = yAxisLabel;

        coordinatesLabel = new JLabel("");
        add(coordinatesLabel);

        // Button
        addCoor = new JButton("Add coordinates");
        addCoor.setBounds(10, 130, 150, 40);
        add(addCoor);

        // Spinners
        SpinnerNumberModel maxC = new SpinnerNumberModel(50, 0, 250, 1);
        maxCon = new JSpinner(maxC);
        con = (int) maxC.getValue();
        maxCon.setBounds(10, 200, 150, 40);
        maxCon.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                con = (int) maxCon.getValue();
                repaint();
            }
        });

        SpinnerNumberModel maxCa = new SpinnerNumberModel(50, 0, 250, 1);
        maxCap = new JSpinner(maxCa);
        cap = (int) maxCa.getValue();
        maxCap.setBounds(10, 270, 150, 40);
        maxCap.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                cap = (int) maxCap.getValue();
                repaint();
            }
        });

        add(maxCon);
        add(maxCap);

        // Set the layout manager for the panel
        setLayout(new BorderLayout());

        // Click Points
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point clickedPoint = getClickedPoint(e.getX(), e.getY());
                if (clickedPoint != null) {
                    handlePointClick(clickedPoint);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                dragPoint = getClickedPoint(e.getX(), e.getY());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                dragPoint = null;
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragPoint != null) {
                    int width = getWidth();
                    int height = getHeight();
                    int xCenter = width / 2;
                    int yCenter = height / 2;

                    dragPoint.x = e.getX() - xCenter;
                    dragPoint.y = yCenter - e.getY();

                    // Update the coordinates label
                    coordinatesLabel.setText("Coordinates: (" + dragPoint.x + ", " + dragPoint.y + ")");

                    // Call repaint to update the component
                    repaint();
                }
            }
        });
    }


    //Adds points to list
    public void addPoint(int x, int y) {
        points.add(new Point(x, y));
    }
    private Point getClickedPoint(int x, int y) {
        int width = getWidth();
        int height = getHeight();
        int xCenter = width / 2;
        int yCenter = height / 2;

        for (Point point : points) {
            int pointX = xCenter + point.x;
            int pointY = yCenter - point.y;

            if (Math.abs(pointX - x) <= 5 && Math.abs(pointY - y) <= 5) {
                return point;
            }
        }

        return null;
    }
    private void handlePointClick(Point clickedPoint) {
        int x = clickedPoint.x;
        int y = clickedPoint.y;
        int width = getWidth();
        int height = getHeight();
        int xCenter = width / 2;
        int yCenter = height / 2;
        double radius = Math.sqrt(x*x+y*y);
        String message = "";
        if ((x - xCenter) * (x - xCenter) +
                (y - yCenter) * (y - yCenter) > radius * radius) {
            message = "Below the curve, might be in a recession";
        }else if ((x - xCenter) * (x - xCenter) +
                (y - yCenter) * (y - yCenter) == radius * radius){
            message = "On the curve, normal production" ;
        }else{
            message = "Above the curve, technological advancement";
        }
        if((x==cap)&&(y==0)||(y==con)&&(x==0)){
            message = "On the curve, normal production";
        }

        JOptionPane.showMessageDialog(this, "Clicked point: (" + x + ", " + y + ") " + message);

    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Color gray = new Color(175,175,175);
        g.setColor(gray);
        g.fillRect(0, 0, getWidth(), getHeight());

        //label jspinner
        g.drawString("Change maxY",20,150);
        g.drawString("Change maxX",20,150);


        int width = getWidth();
        int height = getHeight();
        int xCenter = width / 2;
        int yCenter = height / 2;

        // Draw the vertical line
        g.setColor(Color.BLACK);
        g.drawLine(xCenter, 0, xCenter, height / 2);

        // Draw the horizontal line
        g.drawLine(xCenter, height / 2, width, height / 2);

        // Draw the x and y axis labels
        g.setColor(Color.BLACK);
        g.drawString(xAxisLabel, width - 150, height / 2 + 30);
        g.drawString(yAxisLabel, xCenter -160, 50);

        // Plot the user-provided points
        Color red = new Color(189,34,34);
        g.setColor(red);
        for (Point point : points) {
            int x = xCenter + point.x;
            int y = yCenter - point.y;
            g.fillOval(x - 2, y - 2, 10, 10);
        }

        // Draw the arc
        Color blue = new Color(85,122,171);
        g.setColor(blue);
        int arcStartX = xCenter - cap;
        int arcStartY = yCenter - con;
        int arcWidth = cap * 2;
        int arcHeight = con * 2;

        g.drawArc(arcStartX, arcStartY, arcWidth, arcHeight, 0, 90);
    }
  /*  public void setAddCoor(){
        //get coordinates
         ProductionPossibilityCurve graphPanel = new ProductionPossibilityCurve(xAxisLabel, yAxisLabel);
        while (true) {

            String input = JOptionPane.showInputDialog("Enter coordinates (x, y) separated by spaces (or press enter to finish): ");
            if (input.isEmpty()) {
                break;
            }

            String[] parts = input.split(" ");
            if (parts.length != 2) {
                System.out.println("Invalid input! Please enter coordinates in the format 'x y'.");
                continue;
            }
            try {
                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);
                graphPanel.addPoint(x, y);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter integers for the coordinates.");
                continue;
            }
        }
    }*/


    public static void main(String[] args) {
        JFrame frame = new JFrame("Production Possibility Curve");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400,400);
        Scanner scanner = new Scanner(System.in);
        //Label axis
        //coordinates only show within 250?
        String xAxisLabel= JOptionPane.showInputDialog("Label x:");
        String yAxisLabel= JOptionPane.showInputDialog("Label y:");
        ProductionPossibilityCurve graphPanel = new ProductionPossibilityCurve(xAxisLabel, yAxisLabel);
        //get coordinates
         while (true) {
            String input = JOptionPane.showInputDialog("Enter coordinates (x, y) separated by spaces (or press enter to finish): ");
            if (input.isEmpty()) {
                break;
            }

            String[] parts = input.split(" ");
            if (parts.length != 2) {
                System.out.println("Invalid input! Please enter coordinates in the format 'x y'.");
                continue;
            }
            try {
                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);
                graphPanel.addPoint(x, y);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter integers for the coordinates.");
                continue;
            }
        }

        frame.add(graphPanel);

        frame.setVisible(true);
        //scanner.close();
    }
}

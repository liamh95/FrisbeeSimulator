/**
 * Takes data from the .csv file and graphs it
 * @author Art Kalb
 * @author Liam Hardiman
 */
import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.io.*;

class FrisbeeGraph extends JFrame{
	String fileName;
	
    public FrisbeeGraph(String fileName, boolean b){//takes file where graph method stored flight data
        if(b==false)//if it tries to graph something silly
        	System.out.println("Input was bad: can't graph it!");
        else{
        	super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.fileName = fileName;
        	setSize(20*25+100,6*25+100);
        	setTitle("FRC Team 254 2013 Frisbee Display");
			this.setVisible(true);
        }
        
    }
    @Override
	/**
	 * Displays the contents of the window
	 */
    public void paint(Graphics g) {
        super.paint(g);
		try{
			File f = new File(fileName);
			Scanner scan = new Scanner(f);
			
			int ylen = (int)this.getSize().getHeight();
			int xlen = (int)this.getSize().getWidth();
			
			//Sets up variables for display and reading
			double x0,y0,w,x1,y1;
			String[] parts = scan.nextLine().split(",");
			x0 = Double.parseDouble(parts[0]);
			w = Double.parseDouble(parts[2]);
			y0 = Double.parseDouble(parts[1]);
			//goes through the file
			while(scan.hasNextLine()){
				parts = scan.nextLine().split(",");
				
				//Draws the wall
				g.setColor(Color.RED);
				drawLine( g,25*w + 50,ylen-25*y0 - 25,25*w + 50,ylen - 25 );
				
				//Draws the path
				x1 = Double.parseDouble(parts[0]);
				y1 = Double.parseDouble(parts[1]);
				g.setColor(Color.BLUE);
				drawLine(g,25*x0 + 50,(ylen-25*y0) - 25,25*x1 + 50,(ylen-25*y1) - 25);
				
				//Iterates the points
				x0 = x1;
				y0 = y1;
			}
			
			//Draws gridlines 1 every meter
			g.setColor(Color.GRAY);
			for(int i =0, j =ylen; i<xlen || j>ylen;i+=25, j-=25){
				g.drawLine(i,0,i,ylen);
				g.drawLine(0,j,xlen,j);
			}

            //Axes
            g.setColor(Color.BLACK);
            drawLine(g, 50, 0, 50, ylen);//X axis
            drawLine(g, 0, ylen - 25, xlen, ylen - 25);//Y axis
            
            //Graph lables
            g.setColor(Color.RED);
            g.drawString("X(m)", xlen/2, ylen-10);
            g.drawString("Y\n(m)", 15, ylen/2);

            //1 meter markers
            g.setColor(Color.BLACK);
            g.drawString("1", 75, ylen - 10);
            g.drawString("1", 40, ylen - 50);

            //5 meter markers
            for(int i=175, j=150; i<xlen || j<ylen; i+=125, j+= 125){
                String s = "" + (i/25 - 2);
                g.setColor(Color.BLACK);
                g.drawString(s, i, ylen - 10);
                s = "" + (j/25 - 1);
                g.setColor(Color.BLACK);
                g.drawString(s, 40, ylen - j);
            }

		}catch(Exception e){
			System.out.println(fileName+"no exist");
		}
    }
	private void drawLine(Graphics g,double a,double b, double c,double d){
		g.drawLine((int)a,(int)b,(int)c,(int)d);
	}
}
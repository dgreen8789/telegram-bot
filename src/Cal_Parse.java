
import java.util.Scanner;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author cori
 */
public class Cal_Parse {
    public String Input;
    public String Month;
    public String Day;
    public String Year;
    public String Hour;
    public String Minute;

    public Cal_Parse(String in){
        Input = in;
    }

    public void parse(){
        Scanner s = new Scanner(Input);
        String time;
        Month = s.next();
        //System.out.println(Month);
        Scanner s3 = new Scanner(s.next()).useDelimiter("\\s*,\\s*");
        Day=s3.next();
        //System.out.println(Day);
        Year=s.next();
        //System.out.println(Year);
        time=s.next();
        Scanner s2 = new Scanner(time).useDelimiter("\\s*:\\s*");
        Hour = s2.next();
        //System.out.println(Hour);
        Minute = s2.next();
        //System.out.println(Minute);
        
    }
    
    @Override
    public String toString() {
        return "Input = "+Input+"\n"+"Output = "+"\n"+"Month = "+Month+"\n"+"Day = "+Day+"\n"+"Year = "+Year+"\n"+"Hour = "+Hour+"\n"+"Minute = "+Minute+"\n";
    }
    
    
    
}

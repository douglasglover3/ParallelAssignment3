import java.util.*;

class SensorThread extends Thread {

    int[] temperatures = new int[6];
    long startTime;
    long timePassed = 0;
    Random r = new Random();
    synchronized public void run()
    {

        System.out.println("Sensor " + this.threadId() + " is online.");

        while(timePassed < 24 * 60)
        {
            timePassed = System.currentTimeMillis() - startTime;
            if(timePassed % 10 == 0)
            {
                int temperature = r.nextInt(170) - 100;
                temperatures[(int)(timePassed % 60 / 10 )] = temperature;
            }
        }
    }
}

public class ProblemTwo extends Thread {

    public static void main(String[] args)
    {
        
        //Number of rovers
        int m = 4;
        
        SensorThread sensors[] = new SensorThread[m];
        long startTime = System.currentTimeMillis();
        int[][] temperatureData = new int[m][6];

        for (int i = 0; i < m; i++) {
            sensors[i] = new SensorThread();
            sensors[i].startTime = startTime;
            sensors[i].temperatures = temperatureData[i];
            sensors[i].start();
        }
        long timePassed = 0;
        int currentHour = 0;
        while(timePassed <= 24 * 60)
        {
            timePassed = System.currentTimeMillis() - startTime;
            
            if(timePassed / 60 > currentHour)
            {
                currentHour++;
                int[][] data = temperatureData.clone();
                int greatestDiff = 0;
                int interval = -1;
                int[] highest = new int[5];
                int[] lowest = new int[5];
                
                for(int i = 0; i < 6; i++)
                {   
                    int min = 200;
                    int max = 0;
                    for(int j = 0; j < m; j++)
                    {
                        if(data[j][i] < min)
                            min = data[j][i];
                        if(data[j][i] > max)
                            max = data[j][i];
                    }
                    if (max - min > greatestDiff)
                    {
                        interval = i;
                        greatestDiff = max - min;
                    }
                }
                for (int i = 0; i < 5; i++)
                {
                    int min = 200;
                    int minj = -1;
                    int mink = -1;
                    int max = 0;
                    int maxj = -1;
                    int maxk = -1;
                    for(int j = 0; j < m; j++)
                    {
                        for (int k = 0; k < 6; k++)
                        {
                            if(data[j][k] < min)
                            {
                                min = data[j][k];
                                minj = j;
                                mink = k;
                            }
                            if(data[j][k] > max && data[j][k] != 200)
                            {
                                max = data[j][k];
                                maxj = j;
                                maxk = k;
                            }
                        }
                    }
                    highest[i] = max;
                    lowest[i] = min;
                    data[minj][mink] = 200;
                    data[maxj][maxk] = 200;
                }
                System.out.println("\nHour " + currentHour);
                System.out.println("Highest temps: " +  Arrays.toString(highest));
                System.out.println("Lowest temps: " +  Arrays.toString(lowest));
                System.out.println("Greatest difference: " +  greatestDiff + " at " + (interval * 10) + " to " + (interval * 10 + 10) + " minutes");
            }
        }
    }
}
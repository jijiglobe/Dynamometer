import java.util.*;
import java.io.*;


public class analyzer{
    private static int cols = 4;
    private static ArrayList<double[]> readCSV(String filename){
	int row = 0;
	int col = 0;
	ArrayList<double[]> csv = new ArrayList<double[]>(0);
	try(BufferedReader reader = new BufferedReader(new FileReader(filename))){
	    String line;
	    reader.readLine();
	    while((line = reader.readLine())!= null){
		String[] stringRow = line.split(","); 
		double[] currentRow = new double[cols];
		for(int i = 0; i < stringRow.length; i++){
		    currentRow[i] = Double.parseDouble(stringRow[i]);
		}
		csv.add(currentRow);
	    }
	}
	catch(IOException e){
	    System.out.println(e);
	}
	return csv;
    }

    public static void update(ArrayList<double[]> dataArray){
	int totalEdges = 0;
	for(double[] row : dataArray){
	    totalEdges += row[1];
	    row[2] = totalEdges;
	    row[3] = 2*Math.PI*totalEdges/4096;
	}
    }
    public static void printArray(ArrayList<double[]> dataArray){
	for(double[] row : dataArray){
	    for(double data : row){
		System.out.printf("%f",data);
		System.out.printf(",");
	    }
	    System.out.printf("\n");
	}
    }	
    public static void main(String[] args){
	ArrayList<double[]> data = readCSV("model.csv");
	update(data);
	printArray(data);
    }
}

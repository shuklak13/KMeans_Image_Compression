//to run this program, use the following line
//  java Kmeans (input image).jpg (k value) (output image).jpg

//Example:
//  java KMeans Portrait.jpg 12 CompressedPortrait.jpg

//for reading files
import java.io.*;
import javax.imageio.*;

//for color extraction and image functions
import java.awt.*;
import java.awt.image.*;

//for determining initial points
import java.util.Random;

//for array equality
import java.util.Arrays;

public class KMeans {
    public static void main(String [] args){

		try{
		    BufferedImage originalImage = ImageIO.read(new File(args[0]));
		    int k=Integer.parseInt(args[1]);
		    BufferedImage kmeansJpg = kmeans_helper(originalImage,k);
		    ImageIO.write(kmeansJpg, "jpg", new File(args[2]));     
		}catch(IOException e){
		    System.out.println("Please run the program from the command line with th following format\njava Kmeans (input image).jpg (k value) (output image).jpg");
		}	
    }

    private static void printArray(int[] arr){
    	for(int a: arr)
    		System.out.println(a);
    }

    // private static void testKMeans(){
    // 	Random rand = new Random();

    // 	int[] rgb = new int[5];
    // 	System.out.println("Points");
    // 	for(int i=0; i<rgb.length; i++){
    // 		rgb[i] = (1+rand.nextInt(10)) *-1;
    // 		System.out.println(rgb[i]);
    // 	}
    // 	int k = 2;

    //     int[] clusterCentroids = new int[k];		//centroid of each cluster
    //     int[] rgbClusters = new int[rgb.length];	//cluster each rgb value belongs to
        
    //     System.out.println("Initial centroids:");
    //     //assigning default centroids randomly
    //     for(int i=0; i<k; i++){
    //         clusterCentroids[i] = rgb[rand.nextInt(rgb.length)];
    //         System.out.println(clusterCentroids[i]);
    //     }

    //     for(int i=0; i<1000; i++){
    //     	System.out.println("Iteration " + i);
    //     	int[] rgbClustersNew = findClosestClusterOfEachPoint(rgb, clusterCentroids);
    //     	System.out.println("RGB Clusters");
    //     	printArray(rgbClustersNew);
    //     	if(i>0 && Arrays.equals(rgbClusters, rgbClustersNew)){	//if convergance reached, exit
    //     		System.out.println("Reached converagnace at " + i + " iterations");
    //     		break;
    //     	}
    //     	else
    //     		rgbClusters = rgbClustersNew;
    //     	clusterCentroids = recalculateCentroids(rgb, rgbClusters, clusterCentroids);
    //     	System.out.println("Cluster Centroids");
    //     	printArray(clusterCentroids);
    //     }

    //     System.out.println("New Centroids:");        
    // 	printArray(clusterCentroids);
    //     System.out.println("Closest clusters of each point:");
    //     printArray(rgbClusters);
    // }

    private static void testColorConversions(int rgb){
    	//rgb before conversion
    	System.out.println(rgb);
    	int[] trueColor = convertToTrueColor(rgb);
    	//rgb after converting and back again
    	System.out.println(convertColorToARGB(trueColor[0],trueColor[1],trueColor[2],trueColor[3]));
    }

    // Your k-means code goes here
    // Update the array rgb by assigning each entry in the rgb array to its cluster center
    private static void kmeans(int[] rgb, int k){

    	//testColorConversions(rgb[0]);

        Random rand = new Random();

        int[] clusterCentroids = new int[k];		//centroid of each cluster
        int[] rgbClusters = new int[rgb.length];	//cluster each rgb value belongs to
        
        //assigning default centroids randomly
        for(int i=0; i<k; i++){
            clusterCentroids[i] = rgb[rand.nextInt(rgb.length)];
        }

        //iterative k-means algorithm
        for(int i=0; i<1000; i++){
        	int[] rgbClustersNew = findClosestClusterOfEachPoint(rgb, clusterCentroids);
        	if(i>0 && Arrays.equals(rgbClusters, rgbClustersNew))	//if convergance reached, exit
        		break;
        	else
        		rgbClusters = rgbClustersNew;
        	clusterCentroids = recalculateCentroids(rgb, rgbClusters, clusterCentroids);
        }

        //result of k-means
        for(int i=0; i<rgb.length; i++){
        	rgb[i] = clusterCentroids[rgbClusters[i]];
        }
    }

    //returns 4-tuple {alpha, red, blue, green} of integers
    //For information on how this works refer here:
    	//http://stackoverflow.com/questions/6001211/format-of-type-int-rgb-and-type-int-argb
    private static int[] convertToTrueColor(int rgb){
    	int alpha = (rgb >> 24) & 0xFF;
		int red =   (rgb >> 16) & 0xFF;
		int green = (rgb >>  8) & 0xFF;
		int blue =  (rgb      ) & 0xFF;
    	int[] color = {alpha, red, green, blue};
    	return color;
    }

    private static int convertColorToARGB(int alpha, int red, int green, int blue){
    	int rgb = (alpha << 24) + (red << 16) + (green << 8) + (blue);
    	return rgb;
    }

    private static int findDistance(int rgb1, int rgb2){
    	int[] color1 = convertToTrueColor(rgb1);
    	int[] color2 = convertToTrueColor(rgb2);

    	int distance = (color1[0]-color2[0]) + (color1[1]-color2[1]) + (color1[2]-color2[2]) + (color1[3]-color2[3]);
		distance = distance * distance;
    	return distance;
    }

    private static int findClosestCluster(int point, int[] centroids){
    	int closestCluster = 0;
    	int closestDistance = findDistance(point, centroids[0]);
    	for(int i=1; i<centroids.length; i++){
    		int distance = findDistance(point, centroids[i]);
    		if(distance<closestDistance){
    			closestCluster = i;
    			closestDistance = distance;
    		}
    	}
    	return closestCluster;
    }

    private static int[] findClosestClusterOfEachPoint(int[] points, int[] centroids){
    	int[] pointClusters = new int[points.length];
    	for(int i=0; i<points.length; i++){
    		pointClusters[i] = findClosestCluster(points[i], centroids);
    	}
    	return pointClusters;
    }

    private static int[] recalculateCentroids(int[] points, int[] pointClusters, int[] clusterCentroids){

    	//the sum of the argb values of all the points that are part of a cluster
    	int[] alphaSum = new int[clusterCentroids.length];
    	int[] redSum = new int[clusterCentroids.length];
    	int[] greenSum = new int[clusterCentroids.length];
    	int[] blueSum = new int[clusterCentroids.length];
    	
    	//the number of points that are clustered at a centroid
    	int[] clusterN = new int[clusterCentroids.length];
    	
    	//fill up clusterSum and clusterN
    	for(int i=0; i<pointClusters.length; i++){
    		int cluster = pointClusters[i];
    		int[] pointColor = convertToTrueColor(points[i]);
    		alphaSum[cluster] += pointColor[0];
    		redSum[cluster] += pointColor[1];
    		greenSum[cluster] += pointColor[2];
    		blueSum[cluster] += pointColor[3];
    		clusterN[cluster]++;
    	}

    	//recalculate centroid by taking the mean
    	for(int cluster=0; cluster<clusterCentroids.length; cluster++){
    		//if cluster has no points, cannot calculate new position
    		if(clusterN[cluster] != 0){
	    		//double newCentroid = clusterSum[i] / clusterN[i];
	    		int newAlpha = (int) (alphaSum[cluster] / clusterN[cluster]);
	    		int newRed = (int) (redSum[cluster] / clusterN[cluster]);
	    		int newGreen = (int) (greenSum[cluster] / clusterN[cluster]);
	    		int newBlue = (int) (blueSum[cluster] / clusterN[cluster]);
	    		
	    		clusterCentroids[cluster] = convertColorToARGB(newAlpha, newRed, newGreen, newBlue);
    		}
    	}

    	return clusterCentroids;
    }
    
    private static BufferedImage kmeans_helper(BufferedImage uncompressedImage, int k){
        int height=uncompressedImage.getHeight();
		int width=uncompressedImage.getWidth();

        //initialize the compressed image
		BufferedImage compressedImage = new BufferedImage(width,height,uncompressedImage.getType());
		Graphics2D g2d = compressedImage.createGraphics();  //for rendering
		g2d.drawImage(uncompressedImage, 0, 0, width, height, null);

		//extract the rgb values from the original image
		int[] rgb=new int[width*height];  //1D array of ARGB integers
		int i=0;  //position in the rgb array
		for(int x=0;x<width;x++){
		    for(int y=0;y<height;y++){
			rgb[i++]=compressedImage.getRGB(x,y);
		    }
		}

		//Compress rgb values (array) using kmeans algorithm
		kmeans(rgb, k);

		//Write compressed rgb values to new image
		i=0;
		for(int x=0;x<width;x++){
		    for(int y=0;y<height;y++){
			compressedImage.setRGB(x,y,rgb[i++]);
		    }
		}
		return compressedImage;
    }

}

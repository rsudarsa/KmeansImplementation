/**
 * Created by ramya on 2/4/17.
 *//*** Author :Vibhav Gogate
 The University of Texas at Dallas
 *****/


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import javax.imageio.ImageIO;


public class KMeans {
    public static void main(String [] args){
        if (args.length < 3){
            System.out.println("Usage: Kmeans <input-image> <k> <output-image>");
            return;
        }
        try{
            File originalImageFile = new File(args[0]);
            System.out.println("Before Compression : "+ originalImageFile.length());
            BufferedImage originalImage = ImageIO.read(originalImageFile);
            int k=Integer.parseInt(args[1]);

            BufferedImage kmeansJpg = kmeans_helper(originalImage,k);
            File compressedImageFile = new File(args[2]);
            ImageIO.write(kmeansJpg, "jpg", compressedImageFile);
            System.out.println("After Compression : "+compressedImageFile.length());
            float Accuracy = originalImageFile.length() - compressedImageFile.length();
            System.out.println("Accuracy = "+Accuracy/originalImageFile.length());

        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }

    private static BufferedImage kmeans_helper(BufferedImage originalImage, int k){
        int w=originalImage.getWidth();
        int h=originalImage.getHeight();
        BufferedImage kmeansImage = new BufferedImage(w,h,originalImage.getType());
        Graphics2D g = kmeansImage.createGraphics();
        g.drawImage(originalImage, 0, 0, w,h , null);
        // Read rgb values from the image
        int[] rgb=new int[w*h];
        int count=0;
        for(int i=0;i<w;i++){
            for(int j=0;j<h;j++){
                rgb[count]=kmeansImage.getRGB(i,j);

                count = count + 1;

            }
        }

        kmeans(rgb,k);


        count=0;
        for(int i=0;i<w;i++){
            for(int j=0;j<h;j++){
                kmeansImage.setRGB(i,j,rgb[count++]);

            }
        }
        return kmeansImage;
    }

    private static void kmeans(int[] rgb, int k){


    int K[] = new int[k];
        for (int i = 0; i < K.length; i++) {
            int randK = getRandom(rgb);
            if(!Arrays.asList(K).contains(randK))
                K[i] = randK;
            else
                i--;
        }
        int[] pixelAssignedToCluster = new int[rgb.length];
        int[] numPixelsAssginedtoCluster = new int[k];
        int[] redSumCluster = new int[k];
        int[] greenSumCluster = new int[k];
        int[] blueSumCluster = new int[k];

        int iterator = 1;
        int maxIterations = 1000;

        while (iterator<maxIterations){

            for (int i = 0; i < K.length ; i++) {
                numPixelsAssginedtoCluster[i] = 0;
                redSumCluster[i] = 0;
                greenSumCluster[i] = 0;
                blueSumCluster[i] = 0;
            }

            for (int i = 0; i < rgb.length; i++)    {

                int clusterNumber  = getClusterFromDistance(rgb[i],K);
                pixelAssignedToCluster[i] = clusterNumber;
                numPixelsAssginedtoCluster[clusterNumber]++;
                Color rgbColour = new Color(rgb[i]);
                redSumCluster[clusterNumber] = redSumCluster[clusterNumber]+rgbColour.getRed();
                greenSumCluster[clusterNumber] = greenSumCluster[clusterNumber]+rgbColour.getGreen();
                blueSumCluster[clusterNumber] = blueSumCluster[clusterNumber]+rgbColour.getBlue();
            }
            for (int i = 0; i < K.length; i++) {

                int redAvg = (int) ((double) redSumCluster[i] / (double) numPixelsAssginedtoCluster[i]);
                int greenAvg = (int) ((double) greenSumCluster[i] / (double) numPixelsAssginedtoCluster[i]);
                int blueAvg = (int) ((double) blueSumCluster[i] / (double) numPixelsAssginedtoCluster[i]);
                K[i] = (((redAvg & 0x000000FF) << 16) | ((greenAvg & 0x000000FF) << 8) |((blueAvg & 0x000000FF) << 0));
            }

            iterator = iterator + 1;
        }

        for (int i = 0; i < rgb.length; i++) {
            rgb[i] = K[pixelAssignedToCluster[i]];
        }

        System.out.println("Clustering image converged.");
        for (int i = 0; i < K.length; i++) {
            System.out.println("Final k mean " + i + ": " + K[i]);
        }


    }
    public static int getClusterFromDistance(int rgb, int[] K){
           double minDist = Double.MAX_VALUE;
           Color rgbColor = new Color(rgb);
           int indexToReturn = 0;
        for (int i = 0; i < K.length ; i++) {
            Color KColor = new Color(K[i]);
            int redDist = rgbColor.getRed() - KColor.getRed();
            int greenDist = rgbColor.getGreen() - KColor.getGreen();
            int blueDist = rgbColor.getBlue() - KColor.getBlue();
            double dist = Math.sqrt(redDist * redDist + greenDist * greenDist + blueDist * blueDist);
            if(dist< minDist){
                minDist = dist;
                indexToReturn = i;
            }

        }
        return indexToReturn;


    }
    public static int getRandom(int[] array) {
        int rnd = new Random().nextInt(array.length - 0) + 0;
        return array[rnd];
    }


}



package photoFilter;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Scanner;

import org.apache.commons.io.FilenameUtils;

public class PhotoFilter {

    //Filename
    private static String fileName;
    //cut off point
    private static int cutOffPoint;


    public static void main(String[] args) throws IOException {

        takeDataFromUser();

        //Loading images from folder
        File dir = new File(fileName);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {

                BufferedImage image = ImageIO.read(new File(fileName+"/"+child.getName()));
                int width = image.getWidth();
                int height = image.getHeight();

                int luminance = getLuminance(image, width, height);

                if (luminance >= cutOffPoint) {
                    renameFile(luminance, child, "dark_");
                } else {
                    renameFile(luminance, child, "bright_");
                }
            }
        }
        System.out.println("Succeed");
    }

    private static void takeDataFromUser() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Type name of the folder with photos");
        fileName = scan.nextLine();
        System.out.println("Set cutOffPoint (suggested: 75)");
        cutOffPoint = scan.nextInt();
        scan.nextLine();
    }

    private static void renameFile(int luminance, File child, String hue_) throws IOException {
        String basename = FilenameUtils.getBaseName(child.getName());
        String extension = FilenameUtils.getExtension(child.getName());
        File newName = new File(basename + "_" + hue_ + luminance + "." + extension);
        copyFile(child,new File("out"+ "/" + newName));
    }

    private static int getLuminance(BufferedImage image, int width, int height) {
        float luminance = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int color = image.getRGB(x, y);
                int red = (color >>> 16) & 0xFF;
                int green = (color >>> 8) & 0xFF;
                int blue = (color) & 0xFF;
                luminance += ((red * 0.2126f + green * 0.7152f + blue * 0.0722f) / 255);
                luminance += ((red * 0.299f + green * 0.587f + blue * 0.114f) / 255);
            }
        }
        luminance = (luminance / (width * height));
        luminance = (2 - luminance) / 2 * 100;
        return Math.round(luminance);
    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if(!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        }
        finally {
            if(source != null) {
                source.close();
            }
            if(destination != null) {
                destination.close();
            }
        }
    }
}


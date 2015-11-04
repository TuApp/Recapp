package com.unal.tuapp.recapp.backend;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

/**
 * Created by yeisondavid on 07/10/2015.
 */
public class UtilDB {
    public static byte[] getImageByteOfPath()
    {
        try {
            File file = new File("*/res/CyT.jpg");

            if ( file.exists())
            {
                return new byte[]{1};

            }
            else
            {

                return new byte[]{0};
            }
            /*BufferedImage bufferedImage = ImageIO.read(file);
            WritableRaster raster = bufferedImage .getRaster();
            DataBufferByte data   = (DataBufferByte) raster.getDataBuffer();
            return ( data.getData() );*/
        }catch(Exception e)
        {
            return new byte[]{1, 1 ,1};
        }
    }
}

/*
 * Copyright (c) 2009 Carmen Alvarez. All Rights Reserved.
 *
 */
package ca.rmen.nounours;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import ca.rmen.nounours.data.Feature;
import ca.rmen.nounours.data.Image;
import ca.rmen.nounours.data.ImageFeature;

/**
 * Provides utility methods used by the Nounours application.
 * 
 * @author Carmen Alvarez
 * 
 */
public class Util {

    private static HttpClient threadSafehttpClient;

    /**
     * Returns a long property specified in the given properties, or the
     * defaultValue if it is not specified in the properties.
     * 
     * @param properties
     * @param key
     *            the name of the property to retrieve
     * @param defaultValue
     *            the value to use, if the property is not specified in the
     *            properties
     * @return the value of the given property in the properties, if it is
     *         specified. Otherwise returns the defaultValue.
     */
    public static long getLongProperty(Properties properties, String key, long defaultValue) {
        Object value = properties.get(key);
        if (value == null)
            return defaultValue;
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException ex) {
            return defaultValue;
        }

    }

    /**
     * Returns a float property specified in the given properties, or the
     * defaultValue if it is not specified in the properties.
     * 
     * @param properties
     * @param key
     *            the name of the property to retrieve
     * @param defaultValue
     *            the value to use, if the property is not specified in the
     *            properties
     * @return the value of the given property in the properties, if it is
     *         specified. Otherwise returns the defaultValue.
     */
    public static float getFloatProperty(Properties properties, String key, float defaultValue) {
        Object value = properties.get(key);
        if (value == null)
            return defaultValue;
        try {
            return Float.parseFloat(value.toString());
        } catch (NumberFormatException ex) {
            return defaultValue;
        }

    }

    /**
     * Find the feature in the given image which is closest to the given point.
     * 
     * @param image
     * @param x
     * @param y
     * @return
     */
    public static Feature getClosestFeature(Image image, int x, int y) {
        Feature result = null;
        int minDistance = Integer.MAX_VALUE;
        for (Feature feature : image.getFeatures()) {
            int distance = getDistance(image, feature.getId(), x, y);
            if (distance < minDistance) {
                minDistance = distance;
                result = feature;
            }
        }
        return result;
    }

    public static int[] translate(int deviceX, int deviceY, int deviceWidth, int deviceHeight, int imageWidth,
            int imageHeight) {
        float heightRatio = (float) deviceHeight / imageHeight;
        float widthRatio = (float) deviceWidth / imageWidth;
        float ratioToUse = heightRatio > widthRatio ? widthRatio : heightRatio;
        int offsetX = 0;
        int offsetY = 0;
        if (heightRatio > widthRatio) {
            offsetY = (int) ((deviceHeight - ratioToUse * imageHeight) / 2);
        } else {
            offsetX = (int) ((deviceWidth - ratioToUse * imageWidth) / 2);
        }
        int translatedX = (int) ((deviceX - offsetX) / ratioToUse);
        int translatedY = (int) ((deviceY - offsetY) / ratioToUse);
        // System.out.println("device click: " + deviceX + "," + deviceY);
        // System.out.println("Offset: " + offsetX + "," + offsetY);
        return new int[] { translatedX, translatedY };
    }

    /**
     * Find the best image to display next, assuming we are displaying the
     * current image, and the given feature should be moved to the new location.
     * This looks at each image (including the given image) to which a
     * transition is possible, if the given feature moves, and selects the image
     * where the feature is closest to the given location.
     * 
     * @param image
     *            a given image (usually the one being displayed currently)
     * @param featureId
     *            the selected feature (what the user clicked on)
     * @param x
     *            the new x-location for the given feature
     * @param y
     *            the new y-location for the given feature.
     * @return
     */
    public static Image getAdjacentImage(Image image, String featureId, int x, int y) {
        Image result = image;
        int minDistance = getDistance(image, featureId, x, y);
        for (Image adjImage : image.getAdjacentImages(featureId)) {
            int distance = getDistance(adjImage, featureId, x, y);
            if (distance < minDistance) {
                minDistance = distance;
                result = adjImage;
            }
        }
        return result;
    }

    /**
     * Get the distance in pixels between the given point and the location of
     * the given feature in the given image.
     * 
     * @param image
     * @param featureId
     *            a feature in the given image, which is located somewhere other
     *            than (x,y).
     * @param x
     *            the x-location of the mouse/touch
     * @param y
     *            the y-location of the mouse/touch
     * @return
     */
    public static int getDistance(Image image, String featureId, int x, int y) {
        ImageFeature featureImage = image.getImageFeature(featureId);
        if (featureImage == null) {
            System.out.println("Feature " + featureId + " is not in image " + image);
            return Integer.MAX_VALUE;
        }
        int distance = getDistance(featureImage.getX(), featureImage.getY(), x, y);
        return distance;
    }

    /**
     * Get the distance between two points.
     * 
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static int getDistance(int x1, int y1, int x2, int y2) {
        return (int) Math.sqrt(Math.pow(x1 - x2, 2.0) + Math.pow(y2 - y1, 2.0));
    }

    /**
     * 
     * @param pointX
     * @param pointY
     * @param squareX
     * @param squareY
     * @param squareWidth
     * @param squareHeight
     * @return true if the point is within the square.
     */
    public static boolean pointIsInSquare(int pointX, int pointY, int squareX, int squareY, int squareWidth,
            int squareHeight) {
        if (pointX >= squareX && pointX <= (squareX + squareWidth) && pointY >= squareY
                && pointY <= (squareY + squareHeight))
            return true;
        return false;
    }

    /**
     * 
     * @param v1
     * @param v2
     * @return true if the velocity v1 is faster than the velocity v2, and in
     *         the same direction.
     */
    public static boolean isFaster(float v1, float v2) {
        if (v2 <= 0 && v1 <= v2)
            return true;
        if (v2 >= 0 && v1 >= v2)
            return true;
        return false;
    }

    /**
     * Downloads the given remote image to the given local location.
     * 
     * @param remoteFileLocation
     * @param localFileLocation
     * @throws IllegalStateException
     * @throws IOException
     */
    public static boolean downloadFile(URI remoteFileLocation, File localFileLocation) throws IllegalStateException,
            IOException {
        return downloadFile(remoteFileLocation, localFileLocation, 3);
    }

    /**
     * Downloads the given remote image to the given local location.
     * 
     * @param remoteFileLocation
     * @param localFileLocation
     * @throws IllegalStateException
     * @throws IOException
     */
    public static boolean downloadFile(URI remoteFileLocation, File localFileLocation, int retries)
            throws IllegalStateException, IOException {

        try {
            HttpClient httpClient = getHttpClient();
            HttpGet httpGet = new HttpGet(remoteFileLocation);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            int code = httpResponse.getStatusLine().getStatusCode();
            if (code != HttpStatus.SC_OK) {
                System.out.println("Error code " + code + " trying to download " + remoteFileLocation + " to "
                        + localFileLocation);
                return false;
            }
            HttpEntity httpEntity = httpResponse.getEntity();
            InputStream inputStream = httpEntity.getContent();
            File tempFile = new File(localFileLocation.getAbsolutePath() + ".tmp");
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            final byte[] buffer = new byte[1500];
            for (int read = inputStream.read(buffer); read != -1; read = inputStream.read(buffer)) {
                outputStream.write(buffer, 0, read);
                outputStream.flush();
            }
            outputStream.close();
            tempFile.renameTo(localFileLocation);
            return true;
        } catch (Exception e) {
            System.out.println("Error downloading " + remoteFileLocation + " to " + localFileLocation + ": " + e + ". "
                    + retries + " retries left");
            if (retries > 0)
                return downloadFile(remoteFileLocation, localFileLocation, retries - 1);
            return false;
        }

    }

    public static String getRemoteFileContents(URI remoteFileLocation, int retries) {
        try {

            HttpClient httpClient = getHttpClient();
            HttpGet httpGet = new HttpGet(remoteFileLocation);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            InputStream inputStream = httpEntity.getContent();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            final byte[] buffer = new byte[1500];
            for (int read = inputStream.read(buffer); read != -1; read = inputStream.read(buffer)) {
                outputStream.write(buffer, 0, read);
                outputStream.flush();
            }
            outputStream.close();
            return outputStream.toString();
        } catch (Exception e) {
            if (retries > 0)
                return getRemoteFileContents(remoteFileLocation, retries - 1);
            return null;
        }
    }

    /**
     * Copied from WorldTour
     */
    private static HttpClient getHttpClient() {
        if (threadSafehttpClient == null) {
            final SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

            final HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 8000);
            HttpConnectionParams.setSoTimeout(httpParams, 8000);

            final ClientConnectionManager clientConnectionManager = new ThreadSafeClientConnManager(httpParams,
                    schemeRegistry);
            threadSafehttpClient = new DefaultHttpClient(clientConnectionManager, httpParams);
        }
        return threadSafehttpClient;
    }

}

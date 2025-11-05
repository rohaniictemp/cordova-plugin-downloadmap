package cordova.plugin.map;

import org.apache.cordova.CordovaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import java.io.FileOutputStream;
import java.io.OutputStream;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

/**
 * This class echoes a string called from JavaScript.
 */
public class offlinemap extends CordovaPlugin {

    public offlinemap() {
    }

    private static String WMS_BASE_URL = "";
    private static String LAYER_NAME = "";
    private static String CRS = "";
    private static int TILE_SIZE ;
    private static int MIN_ZOOM ;
    private static int MAX_ZOOM ;



//     public void initializeDownload( double[] rEQUEST_BBOX,CallbackContext callbackContext)  {
        
//         int totalTiles = 0;
//         int downloadedTiles = 0;
//         for (int zoom = MIN_ZOOM; zoom <= MAX_ZOOM; zoom++) {
//         int numTiles = 1 << zoom; // 2^zoom
//         for (int x = 0; x < numTiles; x++) {
//             for (int y = 0; y < numTiles; y++) {
//                 double[] bbox = getTileBBox(x, y, zoom);
//                 if (intersects(bbox, rEQUEST_BBOX)) {
//                     totalTiles++;
//                 }
//             }
//         }
//       }
//         //  for (int zoom = MIN_ZOOM; zoom <= MAX_ZOOM; zoom++) {
//         //     int numTiles = 1 << zoom; // 2^zoom
//         //     for (int x = 0; x < numTiles; x++) {
//         //         for (int y = 0; y < numTiles; y++) {
//         //             double[] bbox = getTileBBox(x, y, zoom);
//         //             if (intersects(bbox, rEQUEST_BBOX)) {
//         //                 String wmsUrl = buildWmsUrl(bbox);
//         //                 downloadTile(wmsUrl, zoom, x, y);
//         //             }
//         //         }
//         //     }
//         // }
//          for (int zoom = MIN_ZOOM; zoom <= MAX_ZOOM; zoom++) {
//         int numTiles = 1 << zoom;
//         for (int x = 0; x < numTiles; x++) {
//             for (int y = 0; y < numTiles; y++) {
//                 double[] bbox = getTileBBox(x, y, zoom);
//                 if (intersects(bbox, rEQUEST_BBOX)) {
//                     String wmsUrl = buildWmsUrl(bbox);
//                     try {
//                         downloadTile(wmsUrl, zoom, x, y);
//                         downloadedTiles++;

//                         // Calculate % complete
//                         int progress = (int) ((downloadedTiles / (float) totalTiles) * 100);

//                         // Send progress update to JS
//                         PluginResult update = new PluginResult(
//                                 PluginResult.Status.OK,
//                                 "Progress: " + progress + "%"
//                         );
//                         update.setKeepCallback(true);
//                         callbackContext.sendPluginResult(update);

//                     } catch (Exception e) {
//                         PluginResult err = new PluginResult(
//                                 PluginResult.Status.ERROR,
//                                 "Tile download failed: " + e.getMessage()
//                         );
//                         err.setKeepCallback(true); // keep callback alive even if one tile fails
//                         callbackContext.sendPluginResult(err);
//                     }
//                 }
//             }
//         }

//     }

// }

public void initializeDownload(double[] rEQUEST_BBOX, CallbackContext callbackContext) {
    int totalTiles = 0;
    int downloadedTiles = 0;

    // Count total tiles
    for (int zoom = MIN_ZOOM; zoom <= MAX_ZOOM; zoom++) {
        int numTiles = 1 << zoom;
        for (int x = 0; x < numTiles; x++) {
            for (int y = 0; y < numTiles; y++) {
                double[] bbox = getTileBBox(x, y, zoom);
                if (intersects(bbox, rEQUEST_BBOX)) {
                    totalTiles++;
                }
            }
            
        }
        PluginResult update = new PluginResult(
                                PluginResult.Status.OK,
                                "Progress: " + totalTiles
                        );
            update.setKeepCallback(true); // keep callback alive
        callbackContext.sendPluginResult(update);
}

    // Now download
    for (int zoom = MIN_ZOOM; zoom <= MAX_ZOOM; zoom++) {
        int numTiles = 1 << zoom;
        for (int x = 0; x < numTiles; x++) {
            for (int y = 0; y < numTiles; y++) {
                double[] bbox = getTileBBox(x, y, zoom);
                if (intersects(bbox, rEQUEST_BBOX)) {
                    String wmsUrl = buildWmsUrl(bbox);
                    try {
                        downloadTile(wmsUrl, zoom, x, y);
                        downloadedTiles++;

                    } catch (Exception e) {
                        PluginResult err = new PluginResult(
                                PluginResult.Status.ERROR,
                                "Tile download failed: " + e.getMessage()
                        );
                        err.setKeepCallback(true);
                        callbackContext.sendPluginResult(err);
                    }
                }
            }

            int progress = (int) ((downloadedTiles / (float) totalTiles) * 100);

                        // Log it for debugging

                        // Send progress update
                        PluginResult update = new PluginResult(
                                PluginResult.Status.OK,
                                "Progress: " + progress + "%"
                        );
                        update.setKeepCallback(true); // keep callback alive
                        callbackContext.sendPluginResult(update);
        }
    }
}



        

    

    
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("setConfig")) {
            JSONObject config = args.getJSONObject(0);
            WMS_BASE_URL = config.optString("baseUrl", WMS_BASE_URL);
            LAYER_NAME   = config.optString("layerName", LAYER_NAME);
            CRS          = config.optString("crs", CRS);
            TILE_SIZE    = config.optInt("tileSize", TILE_SIZE);
            MIN_ZOOM     = config.optInt("minZoom", MIN_ZOOM);
            MAX_ZOOM     = config.optInt("maxZoom", MAX_ZOOM);
            callbackContext.success("Configuration updated successfully");
        return true;
        }else if(action.equals("initializeDownload")){
            JSONArray bboxArray = args.getJSONArray(0);
            double[] rEQUEST_BBOX = new double[bboxArray.length()];
            for (int i = 0; i < bboxArray.length(); i++) {
                rEQUEST_BBOX[i] = bboxArray.getDouble(i);
                System.out.println(rEQUEST_BBOX.toString());
            }
            

            cordova.getThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {


                            initializeDownload(rEQUEST_BBOX,callbackContext);
                            PluginResult done = new PluginResult(
                                    PluginResult.Status.OK,
                                    "Download Completed"
                            );
                            done.setKeepCallback(false);
                            callbackContext.sendPluginResult(done);

                            // Notify JS success
                        } catch (Exception e) {
                            callbackContext.error("Error starting download: " + e.getMessage());
                        }


                    }


                });         
             return true;
        }
        return false;
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    private static String buildWmsUrl(double[] bbox) {
        return String.format(
            "%sSERVICE=WMS&VERSION=1.3.0&REQUEST=GetMap&LAYERS=%s&STYLES=&CRS=%s&BBOX=%.6f,%.6f,%.6f,%.6f&WIDTH=%d&HEIGHT=%d&FORMAT=image/png",
            WMS_BASE_URL, LAYER_NAME, CRS, bbox[0], bbox[1], bbox[2], bbox[3], TILE_SIZE, TILE_SIZE
        );
    }

    private static void downloadTile(String wmsUrl, int zoom, int x, int y) {
        File outputFile = new File(String.format("/sdcard/Android/data/com.iic.naavic/tiles/%d/%d/%d.png", zoom, x, y));
        outputFile.getParentFile().mkdirs();

 
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(wmsUrl).openConnection();
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
 
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (InputStream in = conn.getInputStream();
                     FileOutputStream out = new FileOutputStream(outputFile)) {
                    Bitmap bitmap = BitmapFactory.decodeStream(in);
                    if (bitmap != null) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    } 
                }
            } else {
            }
        } catch (Exception e) {
        }
    }

    // Calculate the BBOX for a tile in EPSG:3857
    private static double[] getTileBBox(int x, int y, int zoom) {
        double initialResolution = 2 * Math.PI * 6378137 / TILE_SIZE;
        double originShift = 2 * Math.PI * 6378137 / 2.0;
        double resolution = initialResolution / (1 << zoom);

        double minX = x * TILE_SIZE * resolution - originShift;
        double maxY = originShift - y * TILE_SIZE * resolution;
        double maxX = (x + 1) * TILE_SIZE * resolution - originShift;
        double minY = originShift - (y + 1) * TILE_SIZE * resolution;

        return new double[] { minX, minY, maxX, maxY };
    }

    // Check if two bounding boxes intersect
    private static boolean intersects(double[] bbox1, double[] bbox2) {
        double minX1 = bbox1[0], minY1 = bbox1[1], maxX1 = bbox1[2], maxY1 = bbox1[3];
        double minX2 = bbox2[0], minY2 = bbox2[1], maxX2 = bbox2[2], maxY2 = bbox2[3];
        return maxX1 > minX2 && maxX2 > minX1 && maxY1 > minY2 && maxY2 > minY1;
    }
}

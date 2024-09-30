package com.celebal.route.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.celebal.route.exceptions.BadRequestException;
import com.celebal.route.factory.RouteFactory;
import com.celebal.route.request.RouteGenerationRequest;
import com.celebal.route.response.ResponseWrapper;
import com.celebal.route.utils.CommonUtils;
import com.celebal.route.wrapper.ResponseEntityWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.internal.chartpart.Annotation;
import org.knowm.xchart.style.markers.SeriesMarkers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class RouteService {

    private final AmazonS3 amazonS3;

    @Qualifier("optAlgorithm")
    private final RouteFactory routeFactory;

    public ResponseEntity<ResponseWrapper> findOptimizedRouteAndGenerateChart(RouteGenerationRequest routeGenerationRequest) {
        log.info("starting optimize route");
        List<Stop> stops = new ArrayList<>();
        try {
             stops = getStopsFromS3(routeGenerationRequest.getBucketName(),routeGenerationRequest.getFileName());
        } catch (IOException e){
            throw new BadRequestException("Not able to find file : " + routeGenerationRequest.getFileName());
        }
        ResponseEntity<ResponseWrapper> optimizeRouteResponse =routeFactory.optimizeRoute(stops);

        ResponseWrapper responseWrapper = optimizeRouteResponse.getBody();
        OptimizedRouteOutput optimizedRouteOutput = CommonUtils.extractObject( responseWrapper.getData(),OptimizedRouteOutput.class);

        List<Integer> coordinates = optimizedRouteOutput.getStops();
        Map<Integer, Stop> stopMap = new HashMap<>();
        for (Stop stop : stops) {
            stopMap.put(stop.getStopNo(), stop);
        }

        double[] xData =  new double[coordinates.size()];
        double[] yData =  new double[coordinates.size()];
        xData[0] = 0;
        yData[0] = 0;
        for(int i=1;i<coordinates.size();i++) {
            Stop s = stopMap.get(coordinates.get(i));
            if(s!=null) {
                xData[i] = s.getX();
                yData[i] = s.getY();
            }

        }

        XYChart chart = new XYChart(800, 600);
        chart.setTitle("Most efficient route for the shortest total distance = "+String.format("%.2f", optimizedRouteOutput.getOptimizedDistance()));
        chart.setXAxisTitle("X Coordinate");
        chart.setYAxisTitle("Y Coordinate");

        // Add stops as points
        XYSeries stopsSeries = chart.addSeries("Stops", xData, yData);
        stopsSeries.setMarker(SeriesMarkers.TRIANGLE_UP);
        stopsSeries.setMarkerColor(Color.BLUE);

        // Draw connecting route
        XYSeries routeSeries = chart.addSeries("Route", xData, yData);
        routeSeries.setMarker(SeriesMarkers.NONE);
        routeSeries.setLineColor(Color.GREEN);
        routeSeries.setLineWidth(2);

        for (int i = 0; i < xData.length-1; i++) {
            final int index = i; // Make a final variable for lambda expressions
            Annotation annotation = new Annotation() {
                @Override
                public Rectangle2D getBounds() {
                    // Define the bounds for the annotation to avoid overlap with other elements
                    return new Rectangle2D.Double(0, 0, 800, 600); // Set chart dimensions
                }

                @Override
                public void paint(Graphics2D g) {
                    // Set font and color for the annotation text
                    g.setFont(new Font("Arial", Font.PLAIN, 11));
                    g.setColor(Color.BLACK);

                    // Calculate chart boundaries and dimensions
                    double xMin = Arrays.stream(xData).min().getAsDouble();
                    double xMax = Arrays.stream(xData).max().getAsDouble();
                    double yMin = Arrays.stream(yData).min().getAsDouble();
                    double yMax = Arrays.stream(yData).max().getAsDouble();

                    // Get chart dimensions and define padding
                    double chartWidth = chart.getWidth();
                    double chartHeight = chart.getHeight();
                    double paddingLeft = 50;
                    double paddingRight = 150;
                    double paddingTop = 50;
                    double paddingBottom = 50;

                    // Define actual drawable chart area (chartWidth and chartHeight minus padding)
                    double drawableWidth = chartWidth - paddingLeft - paddingRight;
                    double drawableHeight = chartHeight - paddingTop - paddingBottom;

                    // Calculate the range of the x and y axes
                    double xRange = xMax - xMin;
                    double yRange = yMax - yMin;

                    // Convert chart coordinates (data) to screen coordinates
                    double xScreen = paddingLeft + ((xData[index] - xMin) / xRange) * drawableWidth;
                    double yScreen = paddingTop + (1 - (yData[index] - yMin) / yRange) * drawableHeight; // Inverted y-axis for screen

                    // Define a small offset to display the label near the point
                    int labelOffsetX = 2;
                    int labelOffsetY = 0;
                    String label = String.format("Stop %d", coordinates.get(index));
                    // Draw the annotation text slightly offset from the point
                    if(index ==0){
                        label="Depot";
                    }else if(index==1){
                        label+="\n FirstStop";
                    }else if(index == xData.length-2){
                        label+="\n LastStop";
                    }

                    g.drawString(label, (int) (xScreen + labelOffsetX), (int) (yScreen + labelOffsetY)); // Apply label offset
                }
            };

            // Add the annotation to the chart
            chart.addAnnotation(annotation);
        }

        // Save the Chart as a PNG file
        try {
            log.info("saving file");

            String[] s = routeGenerationRequest.getFileName().split("\\.");
            String filePath = "/mnt/cele/"+s[0]+".png";
            String csvFilePath = "/mnt/cele/"+s[0]+".csv";

            BitmapEncoder.saveBitmap(chart, filePath, BitmapEncoder.BitmapFormat.PNG);
            log.info("Chart saved as PNG file.");

            generateAndUploadCsv(optimizedRouteOutput.getFormattedRoute(),csvFilePath,routeGenerationRequest.getBucketName(),routeGenerationRequest.getFileName());
            log.info("Uploading file");
            uploadFileToS3(routeGenerationRequest.getBucketName(),filePath,routeGenerationRequest.getFileName());

        } catch (Exception e) {
            e.printStackTrace();
            log.info("an error occurred ", e);
        }
        return ResponseEntityWrapper.successResponseBuilder("Route generated successfully");

    }

    private void generateAndUploadCsv(String formattedRoute,  String csvFilePath, String bucketName,String filename) {

        File csvFile = new File(csvFilePath);
        String[] s= filename.split("\\.");
        String vehicleId =s[0];
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
            writer.write(formattedRoute);
            log.info("CSV file created successfully at: " + csvFile.getAbsolutePath());
        } catch (IOException e) {
            log.info("Error occurred while writing to the CSV file.");
        }

        if (csvFile.exists()) {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,"graphs/"+vehicleId+"-route.csv",csvFile );
            amazonS3.putObject(putObjectRequest);
            log.info("File uploaded to S3 successfully!");
        } else {
            log.info("File not found!");
        }
    }

    public void uploadFileToS3(String bucketName,String filePath, String filename) throws IOException {

        File file = new File(filePath);
        String vehicleId =filename.split("\\.")[0];


        // Check if the file exists
        if (file.exists()) {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,"graphs/"+vehicleId+"-route.png", file);
            amazonS3.putObject(putObjectRequest);
            log.info("File uploaded to S3 successfully!");
        } else {
            log.info("File not found!");
        }
    }


    public List<Stop> getStopsFromS3(String bucketName, String fileName) throws IOException {
        log.info("Fetching file from S3 : "+ fileName);
        S3Object s3Object = amazonS3.getObject(new GetObjectRequest(bucketName, fileName));

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(s3Object.getObjectContent()))) {
            return reader.lines()
                    .skip(1)
                    .map(this::parseLineToStop)
                    .collect(Collectors.toList());
        }
    }

    private Stop parseLineToStop(String line) {
        String[] fields = line.split(",");
        return new Stop(Integer.parseInt(fields[0]), Integer.parseInt(fields[1]), Integer.parseInt(fields[2]));
    }
}

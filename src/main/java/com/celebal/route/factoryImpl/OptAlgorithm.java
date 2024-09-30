package com.celebal.route.factoryImpl;

import com.celebal.route.factory.RouteFactory;
import com.celebal.route.response.ResponseWrapper;
import com.celebal.route.service.OptimizedRouteOutput;
import com.celebal.route.service.Stop;
import com.celebal.route.wrapper.ResponseEntityWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Component
public class OptAlgorithm  implements RouteFactory {

    private  List<Stop> stops;
    private  double[][] distanceMatrix;
    // Solve the TSP using Nearest Neighbor and 2-opt
    @Override
    public ResponseEntity<ResponseWrapper> optimizeRoute(List<Stop> stops) {
        this.stops = new ArrayList<>(stops);
        this.stops.add(0, new Stop(0, 0, 0)); // Add depot as the first stop
        this.distanceMatrix = new double[this.stops.size()][this.stops.size()];
        initializeDistanceMatrix();

        List<Integer> initialRoute = nearestNeighbor();
        List<Integer> optimizedRoute = twoOptOptimization(initialRoute);
        double optimizedDistance = calculateRouteDistance(optimizedRoute);
        String formattedRoute = printRouteDetails(optimizedDistance, optimizedRoute);
        OptimizedRouteOutput optimizedRouteOutput = new OptimizedRouteOutput();
        optimizedRouteOutput.setOptimizedDistance(optimizedDistance);
        optimizedRouteOutput.setStops(optimizedRoute);
        optimizedRouteOutput.setFormattedRoute(formattedRoute);
        return ResponseEntityWrapper.successResponseBuilder("Optimized route successfully",optimizedRouteOutput);
    }

    // Initialize the distance matrix based on the list of stops and depot (0, 0)
    private void initializeDistanceMatrix() {
        int n = stops.size();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                distanceMatrix[i][j] = calculateDistance(stops.get(i), stops.get(j));
            }
        }
    }

    // Calculate Euclidean distance between two stops
    private double calculateDistance(Stop stop1, Stop stop2) {
        return Math.sqrt(Math.pow(stop2.getX() - stop1.getX(), 2) + Math.pow(stop2.getY() - stop1.getY(), 2));
    }

    // Nearest Neighbor algorithm to find an initial solution
    public List<Integer> nearestNeighbor() {
        int n = stops.size();
        boolean[] visited = new boolean[n];
        List<Integer> route = new ArrayList<>();
        route.add(0); // Start from depot
        visited[0] = true;
        int current = 0;

        for (int i = 1; i < n; i++) {
            int nearest = -1;
            double nearestDist = Double.MAX_VALUE;
            for (int j = 1; j < n; j++) {
                if (!visited[j] && distanceMatrix[current][j] < nearestDist) {
                    nearestDist = distanceMatrix[current][j];
                    nearest = j;
                }
            }
            route.add(nearest);
            visited[nearest] = true;
            current = nearest;
        }

        route.add(0); // Return to depot
        return route;
    }

    // Calculate the total distance of the given route
    public double calculateRouteDistance(List<Integer> route) {
        double totalDistance = 0.0;
        for (int i = 0; i < route.size() - 1; i++) {
            totalDistance += distanceMatrix[route.get(i)][route.get(i + 1)];
        }
        return totalDistance;
    }

    // 2-opt algorithm to optimize the route
    public List<Integer> twoOptOptimization(List<Integer> route) {
        boolean improvement = true;
        int n = route.size();
        while (improvement) {
            improvement = false;
            for (int i = 1; i < n - 2; i++) {
                for (int j = i + 1; j < n - 1; j++) {
                    if (twoOptSwap(route, i, j)) {
                        improvement = true;
                    }
                }
            }
        }
        return route;
    }

    // Perform 2-opt swap and return true if there's an improvement
    private boolean twoOptSwap(List<Integer> route, int i, int j) {
        if (i >= j) return false;

        // Calculate distances before the swap
        double originalDistance = distanceMatrix[route.get(i - 1)][route.get(i)] + distanceMatrix[route.get(j)][route.get(j + 1)];
        double newDistance = distanceMatrix[route.get(i - 1)][route.get(j)] + distanceMatrix[route.get(i)][route.get(j + 1)];

        // Swap the elements and check if the new distance is better
        if (newDistance < originalDistance) {
            // Reverse the segment between i and j
            while (i < j) {
                int temp = route.get(i);
                route.set(i, route.get(j));
                route.set(j, temp);
                i++;
                j--;
            }
            return true;
        }
        return false;
    }


    // Print the detailed route and total distance in the required format
    public String printRouteDetails(double totalDistance, List<Integer> route) {
        StringBuilder routeStr = new StringBuilder();
        for (int i = 0; i < route.size(); i++) {
            routeStr.append(route.get(i));
            if (i < route.size() - 1) {
                routeStr.append("-");
            }
        }
        String formattedOutput = String.format("TotalDistance: %.2f, Route: %s", totalDistance, route);

        log.info("TotalDistance: %.2f, Route: %s\n", totalDistance, routeStr.toString());
        return formattedOutput;
    }
}

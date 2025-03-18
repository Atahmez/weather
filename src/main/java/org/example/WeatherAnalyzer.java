package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * ## Weather Data Analyzer
 *
 * This program reads weather data from a CSV file and provides various analytics
 * such as average temperature, days with high temperatures, and rainy days count.
 *
 * ```java
 * var analyzer = new WeatherAnalyzer("weatherdata.csv");
 * System.out.println(analyzer.averageTemperature("08"));
 * System.out.println(analyzer.countRainyDays());
 * ```
 */
public interface WeatherAnalyzer {

    /**
     * Represents a weather record.
     * @param date          The date of the record.
     * @param temperature   The temperature in Celsius.
     * @param humidity      The humidity percentage.
     * @param precipitation The precipitation in millimeters.
     */
    record WeatherRecord(String date, double temperature, int humidity, double precipitation) {}

    static List<WeatherRecord> loadData(String filePath) throws IOException {
        return Files.lines(Path.of(filePath))
                .skip(1)
                .map(line -> line.split(","))
                .map(parts -> new WeatherRecord(parts[0], Double.parseDouble(parts[1]),
                        Integer.parseInt(parts[2]), Double.parseDouble(parts[3])))
                .collect(Collectors.toList());
    }

    static double averageTemperature(String month) throws IOException {
        return loadData("weatherdata.csv").stream()
                .filter(record -> record.date().substring(5, 7).equals(month))
                .mapToDouble(WeatherRecord::temperature)
                .average()
                .orElse(Double.NaN);
    }

    static long countDaysAboveTemperature(double threshold) throws IOException {
        return loadData("weatherdata.csv").stream()
                .filter(record -> record.temperature() > threshold)
                .count();
    }

    static long countRainyDays() throws IOException {
        return loadData("weatherdata.csv").stream()
                .filter(record -> record.precipitation() > 0)
                .count();
    }

    static String categorizeTemperature(double temperature) {
        return switch ((int) temperature / 10) {
            case 0, 1 -> "Cold";
            case 2, 3 -> "Warm";
            case 4, 5 -> "Hot";
            default -> "Extreme";
        };
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Average August Temperature: " + averageTemperature("08"));
        System.out.println("Days above 30°C: " + countDaysAboveTemperature(30));
        System.out.println("Number of rainy days: " + countRainyDays());
        System.out.println("Temperature Category for 35°C: " + categorizeTemperature(35));
    }
}

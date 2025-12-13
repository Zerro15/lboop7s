package com.example.lab5.manual.service;

import com.example.lab5.manual.dao.PointDAO;
import com.example.lab5.manual.dto.PointDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Optional;

public class PointService {
    private static final Logger logger = LoggerFactory.getLogger(PointService.class);
    private final PointDAO pointDAO;

    public PointService() {
        this.pointDAO = new PointDAO();
    }

    public Optional<PointDTO> getPointById(Long id) {
        return pointDAO.findById(id);
    }

    public List<PointDTO> getPointsByFunctionId(Long functionId) {
        return pointDAO.findByFunctionId(functionId);
    }

    public PointDTO findMaxYPoint(Long functionId) {
        return pointDAO.findByFunctionId(functionId).stream()
                .max((a, b) -> Double.compare(a.getYValue(), b.getYValue()))
                .orElse(null);
    }

    public PointDTO findMinYPoint(Long functionId) {
        return pointDAO.findByFunctionId(functionId).stream()
                .min((a, b) -> Double.compare(a.getYValue(), b.getYValue()))
                .orElse(null);
    }

    public PointStatistics getPointStatistics(Long functionId) {
        List<PointDTO> points = pointDAO.findByFunctionId(functionId);
        if (points.isEmpty()) {
            return null;
        }

        DoubleSummaryStatistics xStats = points.stream().mapToDouble(PointDTO::getXValue).summaryStatistics();
        DoubleSummaryStatistics yStats = points.stream().mapToDouble(PointDTO::getYValue).summaryStatistics();

        return new PointStatistics(points.size(),
                xStats.getMin(), xStats.getMax(), xStats.getAverage(),
                yStats.getMin(), yStats.getMax(), yStats.getAverage());
    }

    public int generateFunctionPoints(Long functionId,
                                      String functionType,
                                      int pointsCount,
                                      double start,
                                      double end) {
        List<PointDTO> generated = new ArrayList<>();
        double step = (end - start) / Math.max(pointsCount - 1, 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = start + step * i;
            double y = calculate(functionType, x);
            PointDTO point = new PointDTO();
            point.setFunctionId(functionId);
            point.setXValue(x);
            point.setYValue(y);
            generated.add(point);
        }
        return pointDAO.createPoints(generated);
    }

    private double calculate(String type, double x) {
        String normalized = type == null ? "identity" : type.toLowerCase();
        return switch (normalized) {
            case "sqr", "quadratic" -> x * x;
            case "sin" -> Math.sin(x);
            case "cos" -> Math.cos(x);
            case "linear" -> x;
            default -> x; // тождественная функция по умолчанию
        };
    }

    public Long createPoint(Long functionId, Double x, Double y) {
        PointDTO dto = new PointDTO();
        dto.setFunctionId(functionId);
        dto.setXValue(x);
        dto.setYValue(y);
        return pointDAO.createPoint(dto);
    }

    public boolean updatePoint(Long id, Long functionId, Double x, Double y) {
        PointDTO dto = new PointDTO();
        dto.setId(id);
        dto.setFunctionId(functionId);
        dto.setXValue(x);
        dto.setYValue(y);
        return pointDAO.updatePoint(dto);
    }

    public boolean deletePoint(Long id) {
        return pointDAO.deletePoint(id);
    }

    public record PointStatistics(int totalPoints,
                                  double minX,
                                  double maxX,
                                  double avgX,
                                  double minY,
                                  double maxY,
                                  double avgY) { }
}

package com.example.lab5.manual.service;

import com.example.lab5.manual.dao.FunctionDAO;
import com.example.lab5.manual.dao.PointDAO;
import com.example.lab5.manual.dto.FunctionDTO;
import com.example.lab5.manual.dto.PointDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Optional;

public class FunctionService {
    private static final Logger logger = LoggerFactory.getLogger(FunctionService.class);
    private final FunctionDAO functionDAO;
    private final PointDAO pointDAO;

    public FunctionService() {
        this.functionDAO = new FunctionDAO();
        this.pointDAO = new PointDAO();
    }

    public List<FunctionDTO> getAllFunctions() {
        return functionDAO.findAll();
    }

    public List<FunctionDTO> getFunctionsByUserId(Long userId) {
        return functionDAO.findByUserId(userId);
    }

    public List<FunctionDTO> getFunctionsByName(String name) {
        return functionDAO.findByName(name);
    }

    public Optional<FunctionDTO> getFunctionById(Long id) {
        return functionDAO.findById(id);
    }

    public Long createFunction(Long userId, String name, String signature) {
        FunctionDTO dto = new FunctionDTO();
        dto.setUserId(userId);
        dto.setName(name);
        dto.setSignature(signature);
        return functionDAO.createFunction(dto);
    }

    public boolean updateFunction(Long id, Long userId, String name, String signature) {
        FunctionDTO dto = new FunctionDTO();
        dto.setId(id);
        dto.setUserId(userId);
        dto.setName(name);
        dto.setSignature(signature);
        return functionDAO.updateFunction(dto);
    }

    public boolean deleteFunction(Long id) {
        // удаляем точки функции перед удалением самой функции
        pointDAO.deleteByFunctionId(id);
        return functionDAO.deleteFunction(id);
    }

    public FunctionStatistics getFunctionStatistics(Long functionId) {
        List<PointDTO> points = pointDAO.findByFunctionId(functionId);
        if (points.isEmpty()) {
            return null;
        }

        DoubleSummaryStatistics yStats = points.stream()
                .mapToDouble(p -> p.getYValue() == null ? 0.0 : p.getYValue())
                .summaryStatistics();
        DoubleSummaryStatistics xStats = points.stream()
                .mapToDouble(p -> p.getXValue() == null ? 0.0 : p.getXValue())
                .summaryStatistics();

        return new FunctionStatistics(
                points.size(),
                xStats.getMin(),
                xStats.getMax(),
                yStats.getMin(),
                yStats.getMax(),
                yStats.getAverage()
        );
    }

    public record FunctionStatistics(int pointsCount,
                                     double minX,
                                     double maxX,
                                     double minY,
                                     double maxY,
                                     double averageY) { }
}

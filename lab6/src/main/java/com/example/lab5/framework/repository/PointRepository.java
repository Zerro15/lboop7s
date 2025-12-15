package com.example.lab5.framework.repository;

import com.example.lab5.framework.entity.Point;
import com.example.lab5.framework.entity.Function;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {
    List<Point> findByFunction(Function function);

    @Query("SELECT p FROM Point p WHERE p.function.id = :functionId")
    List<Point> findByFunctionId(@Param("functionId") Long functionId);

    // ДОБАВЛЕННЫЙ МЕТОД - получение точек функции, отсортированных по X
    @Query("SELECT p FROM Point p WHERE p.function.id = :functionId ORDER BY p.xValue ASC")
    List<Point> findByFunctionIdOrderByXValueAsc(@Param("functionId") Long functionId);

    // УДАЛИТЕ ЭТУ СТРОКУ - она вызывает ошибку:
    // List<Point> findByFunctionIdOrderByXValue(Long functionId);
    // Вместо этого используйте правильное имя поля (xValue с маленькой буквы):
    List<Point> findByFunctionIdOrderByxValueAsc(Long functionId);  // xValue с маленькой x

    @Query("SELECT p FROM Point p WHERE p.function.user.login = :userLogin")
    List<Point> findByUserLogin(@Param("userLogin") String userLogin);

    @Query("SELECT p FROM Point p WHERE p.xValue BETWEEN :minX AND :maxX")
    List<Point> findByXValueBetween(@Param("minX") Double minX, @Param("maxX") Double maxX);

    @Query("SELECT p FROM Point p WHERE p.yValue BETWEEN :minY AND :maxY")
    List<Point> findByYValueBetween(@Param("minY") Double minY, @Param("maxY") Double maxY);

    @Query("SELECT p FROM Point p WHERE p.function.id = :functionId AND p.xValue BETWEEN :minX AND :maxX")
    List<Point> findByFunctionIdAndXValueBetween(@Param("functionId") Long functionId,
                                                 @Param("minX") Double minX,
                                                 @Param("maxX") Double maxX);

    @Query("SELECT p FROM Point p WHERE p.function.id = :functionId AND p.yValue BETWEEN :minY AND :maxY")
    List<Point> findByFunctionIdAndYValueBetween(@Param("functionId") Long functionId,
                                                 @Param("minY") Double minY,
                                                 @Param("maxY") Double maxY);

    // Поиск точки по точному значению X
    @Query("SELECT p FROM Point p WHERE p.function.id = :functionId AND ABS(p.xValue - :xValue) < 1e-10")
    List<Point> findByFunctionIdAndXValue(@Param("functionId") Long functionId, @Param("xValue") Double xValue);

    // Получение точек с сортировкой по Y
    @Query("SELECT p FROM Point p WHERE p.function.id = :functionId ORDER BY p.yValue ASC")
    List<Point> findByFunctionIdOrderByYValueAsc(@Param("functionId") Long functionId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Point p WHERE p.function.id = :functionId")
    void deleteByFunctionId(@Param("functionId") Long functionId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Point p WHERE p.function.id = :functionId AND p.xValue < :xThreshold")
    void deleteByFunctionIdAndXValueLessThan(@Param("functionId") Long functionId,
                                             @Param("xThreshold") Double xThreshold);

    @Modifying
    @Transactional
    @Query("DELETE FROM Point p WHERE p.function IN (SELECT f FROM Function f WHERE f.user.login = :userLogin)")
    void deleteByUserLogin(@Param("userLogin") String userLogin);

    // Метод для подсчета количества точек у функции
    @Query("SELECT COUNT(p) FROM Point p WHERE p.function.id = :functionId")
    Long countByFunctionId(@Param("functionId") Long functionId);

    // Метод для получения минимального и максимального X у функции
    @Query("SELECT MIN(p.xValue), MAX(p.xValue) FROM Point p WHERE p.function.id = :functionId")
    Object[] findMinMaxXByFunctionId(@Param("functionId") Long functionId);

    // Метод для получения минимального и максимального Y у функции
    @Query("SELECT MIN(p.yValue), MAX(p.yValue) FROM Point p WHERE p.function.id = :functionId")
    Object[] findMinMaxYByFunctionId(@Param("functionId") Long functionId);
}
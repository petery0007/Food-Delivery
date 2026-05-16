package com.xtu.mapper;

import com.xtu.pojo.Order;
import com.xtu.pojo.OrderItemEntity;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {

    //订单
    @Insert("INSERT INTO orders (user_id, receiver, phone, address, delivery_type, payment_type, remark, goods_total, delivery_fee, total_amount, status, create_time) " +
            "VALUES (#{userId}, #{receiver}, #{phone}, #{address}, #{deliveryType}, #{paymentType}, #{remark}, #{goodsTotal}, #{deliveryFee}, #{totalAmount}, #{status}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertOrder(Order order);

    //订单项
    @Insert("INSERT INTO order_items (order_id, product_id, product_name, price, quantity, specification, image_url, subtotal) " +
            "VALUES (#{orderId}, #{productId}, #{productName}, #{price}, #{quantity}, #{specification}, #{imageUrl}, #{subtotal})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertOrderItem(OrderItemEntity orderItem);

    //获取订单
    @Select("SELECT * FROM orders WHERE id = #{id}")
    Order getOrderById(Integer id);

    //获取订单
    @Select("SELECT * FROM orders WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Order> getOrdersByUserId(Integer userId);

    //获取订单项
    @Select("SELECT * FROM order_items WHERE order_id = #{orderId}")
    List<OrderItemEntity> getOrderItemsByOrderId(Integer orderId);

    //更新订单
    @Update("UPDATE orders SET status = #{status} WHERE id = #{id}")
    int updateOrderStatus(Integer id, Integer status);

    //完成订单
    @Update("UPDATE orders SET finish_time = #{finishTime}, status = #{status} WHERE id = #{id}")
    int finishOrder(Integer id, Integer status, LocalDateTime finishTime);

    //分配配送
    @Update("UPDATE orders SET delivery_staff = #{deliveryStaff}, status = #{status} WHERE id = #{id}")
    int assignDelivery(Integer id, String deliveryStaff, Integer status);

    //分页获取订单
    @Select("SELECT * FROM orders WHERE user_id = #{userId} ORDER BY create_time DESC LIMIT #{offset}, #{pageSize}")
    List<Order> getOrdersByUserIdPage(Integer userId, Integer offset, Integer pageSize);

    //获取订单总数
    @Select("SELECT COUNT(*) FROM orders WHERE user_id = #{userId}")
    Integer countOrdersByUserId(Integer userId);

    //取消订单
    @Update("UPDATE orders SET status = #{status}, finish_time = #{finishTime} WHERE id = #{id}")
    int cancelOrder(Integer id, Integer status, LocalDateTime finishTime);

    //删除订单
    @Delete("DELETE FROM order_items WHERE order_id = #{orderId}")
    int deleteOrderItems(Integer orderId);

    @Delete("DELETE FROM orders WHERE id = #{id}")
    int deleteOrder(Integer id);

    //配送员获取所有订单
    @Select("SELECT * FROM orders WHERE delivery_staff = #{deliveryId} ORDER BY create_time DESC LIMIT #{offset}, #{pageSize}")
    List<Order> getOrdersByDeliveryIdPage(String deliveryId, Integer offset, Integer pageSize);

    @Select("SELECT COUNT(*) FROM orders WHERE delivery_staff = #{deliveryId}")
    Integer countOrdersByDeliveryId(String deliveryId);

    @Select("SELECT * FROM orders WHERE delivery_staff = #{deliveryId} AND status = #{status} ORDER BY create_time DESC LIMIT #{offset}, #{pageSize}")
    List<Order> getOrdersByDeliveryIdAndStatusPage(String deliveryId, Integer status, Integer offset, Integer pageSize);

    @Select("SELECT COUNT(*) FROM orders WHERE delivery_staff = #{deliveryId} AND status = #{status}")
    Integer countOrdersByDeliveryIdAndStatus(String deliveryId, Integer status);

    @Update("UPDATE orders SET status = #{status}, finish_time = #{finishTime} WHERE id = #{id}")
    int updateOrderStatusAndFinishTime(Integer id, Integer status, LocalDateTime finishTime);


    // 分页查询订单（支持多条件筛选）
    @Select("<script>" +
            "SELECT * FROM orders " +
            "<where>" +
            "  <if test='status != null'> AND status = #{status} </if>" +
            "  <if test='receiver != null and receiver != \"\"'> AND receiver LIKE CONCAT('%', #{receiver}, '%') </if>" +
            "  <if test='deliveryStaff != null and deliveryStaff != \"\"'> AND delivery_staff LIKE CONCAT('%', #{deliveryStaff}, '%') </if>" +
            "</where>" +
            "ORDER BY create_time DESC " +
            "LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<Order> getAllOrdersPage(Integer offset, Integer pageSize, Integer status, String receiver, String deliveryStaff);

    // 统计订单总数（支持多条件筛选）
    @Select("<script>" +
            "SELECT COUNT(*) FROM orders " +
            "<where>" +
            "  <if test='status != null'> AND status = #{status} </if>" +
            "  <if test='receiver != null and receiver != \"\"'> AND receiver LIKE CONCAT('%', #{receiver}, '%') </if>" +
            "  <if test='deliveryStaff != null and deliveryStaff != \"\"'> AND delivery_staff LIKE CONCAT('%', #{deliveryStaff}, '%') </if>" +
            "</where>" +
            "</script>")
    Integer countAllOrders(Integer status, String receiver, String deliveryStaff);

}

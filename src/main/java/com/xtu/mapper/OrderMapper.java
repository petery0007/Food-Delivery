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
    @Update("UPDATE orders SET peisong_id = #{peisongId}, status = #{status} WHERE id = #{id}")
    int assignDelivery(Integer id, String peisongId, Integer status);

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
}

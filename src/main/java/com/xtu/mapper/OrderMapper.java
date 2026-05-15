package com.xtu.mapper;

import com.xtu.pojo.Order;
import com.xtu.pojo.OrderItemEntity;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {

    @Insert("INSERT INTO orders (user_id, receiver, phone, address, delivery_type, payment_type, remark, goods_total, delivery_fee, total_amount, status, create_time) " +
            "VALUES (#{userId}, #{receiver}, #{phone}, #{address}, #{deliveryType}, #{paymentType}, #{remark}, #{goodsTotal}, #{deliveryFee}, #{totalAmount}, #{status}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertOrder(Order order);

    @Insert("INSERT INTO order_items (order_id, product_id, product_name, price, quantity, specification, image_url, subtotal) " +
            "VALUES (#{orderId}, #{productId}, #{productName}, #{price}, #{quantity}, #{specification}, #{imageUrl}, #{subtotal})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertOrderItem(OrderItemEntity orderItem);

    @Select("SELECT * FROM orders WHERE id = #{id}")
    Order getOrderById(Integer id);

    @Select("SELECT * FROM orders WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Order> getOrdersByUserId(Integer userId);

    @Select("SELECT * FROM order_items WHERE order_id = #{orderId}")
    List<OrderItemEntity> getOrderItemsByOrderId(Integer orderId);

    @Update("UPDATE orders SET status = #{status} WHERE id = #{id}")
    int updateOrderStatus(Integer id, Integer status);

    @Update("UPDATE orders SET finish_time = #{finishTime}, status = #{status} WHERE id = #{id}")
    int finishOrder(Integer id, Integer status, LocalDateTime finishTime);

    @Update("UPDATE orders SET peisong_id = #{peisongId}, status = #{status} WHERE id = #{id}")
    int assignDelivery(Integer id, String peisongId, Integer status);

    @Select("SELECT * FROM orders WHERE user_id = #{userId} ORDER BY create_time DESC LIMIT #{offset}, #{pageSize}")
    List<Order> getOrdersByUserIdPage(Integer userId, Integer offset, Integer pageSize);

    @Select("SELECT COUNT(*) FROM orders WHERE user_id = #{userId}")
    Integer countOrdersByUserId(Integer userId);
}

package com.infosystem.greenfooddelivery.Controller;

import com.infosystem.greenfooddelivery.utils.Result;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController // 代表此类所有方法都返回 JSON
@RequestMapping("/products") // 匹配前端 axios 里的 /products 前缀
public class ProductController {

    // 对应前端的 GET 请求: request.get('/products/list')
    @GetMapping("/list")
    public Result list(@RequestParam Map<String, Object> params) {

        // 1. 接收前端传来的参数
        String page = (String) params.get("page");
        String limit = (String) params.get("limit");
        String keywords = (String) params.get("keywords");

        System.out.println("收到前端请求！页码：" + page + ", 关键词：" + keywords);

        // 2. 调用 Service 层查询数据库...
        // (由于你刚搭建环境，数据库可能还没连上。为了验证前后端通信，我们先造点【假数据】在后端)

        List<Map<String, Object>> mockList = new ArrayList<>();

        Map<String, Object> item1 = new HashMap<>();
        item1.put("id", 101);
        item1.put("name", "后端传来的西红柿");
        item1.put("imageUrl", " https://via.placeholder.com/50/FF0000/FFFFFF?text=Tomato ");
        item1.put("specification", "新鲜蔬菜");
        item1.put("stock", 500);
        item1.put("price", 5.5);
        item1.put("clickCount", 88);
        item1.put("status", "上架");

        Map<String, Object> item2 = new HashMap<>();
        item2.put("id", 102);
        item2.put("name", "后端传来的有机小白菜");
        item2.put("imageUrl", " https://via.placeholder.com/50/00FF00/FFFFFF?text=Cabbage ");
        item2.put("specification", "有机蔬菜");
        item2.put("stock", 300);
        item2.put("price", 12.0);
        item2.put("clickCount", 120);
        item2.put("status", "上架");

        mockList.add(item1);
        mockList.add(item2);

        // 如果前端传了搜索词，我们可以简单模拟过滤一下
        if (keywords != null && !keywords.isEmpty()) {
            mockList.removeIf(item -> !item.get("name").toString().contains(keywords));
        }

        // 3. 构造前端需要的格式并返回
        Map<String, Object> data = new HashMap<>();
        data.put("list", mockList);     // 商品列表
        data.put("total", mockList.size()); // 总条数

        // 使用 R.ok().put() 将数据包装成 { code:200, message:"success", data: { list:[], total:2 } }
        return Result.ok().put("data", data);
    }
}

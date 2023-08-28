package com.order.ordermicroservices.service;

import com.order.ordermicroservices.config.WebClientConfig;
import com.order.ordermicroservices.dto.InventoryResponse;
import com.order.ordermicroservices.dto.OrderLineItemsDto;
import com.order.ordermicroservices.dto.OrderRequest;
import com.order.ordermicroservices.model.Order;
import com.order.ordermicroservices.model.OrderLineItems;
import com.order.ordermicroservices.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional //this will allow spring framework to create and commit the DB writes transactions
public class OrderService {

    private final OrderRepository orderRepository;
    private  final WebClient webClient;

    public void placeOrder(OrderRequest orderRequest){

        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                //map(orderLineItemsDto -> mapToDto(orderLineItemsDto));
                .map(this::mapToDto)
                .toList();
        order.setOrderLineItemsList(orderLineItems);

       List<String> skuCodes = order.getOrderLineItemsList()
                  .stream()
//                .map(orderLineItem -> orderLineItem.getSkuCode());
                  .map(OrderLineItems::getSkuCode).toList();

        //call inventory service and place order if product is in stock
        InventoryResponse[] inventoryResponseArray = webClient.get()
                .uri("http://localhost:8193/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        boolean allProductsInStock = Arrays.stream(inventoryResponseArray)
//                .allMatch(InventoryResponse -> InventoryResponse.isInStock())
                .allMatch(InventoryResponse::isInStock);

        if(allProductsInStock) {
            orderRepository.save(order);
        }else {
            throw new IllegalArgumentException("Product is not in Stock, try again later");
        }
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {

        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setId(orderLineItemsDto.getId());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}

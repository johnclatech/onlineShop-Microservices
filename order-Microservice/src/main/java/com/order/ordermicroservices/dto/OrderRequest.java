package com.order.ordermicroservices.dto;

import com.order.ordermicroservices.model.OrderLineItems;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {

    private List<OrderLineItemsDto> orderLineItemsDtoList;

}

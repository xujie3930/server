package com.szmsd.ec.shopify.domain.order;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @FileName Orders.java
 * @Description ----------功能描述---------
 * @Date 2021-04-13 13:48
 * @Author hyx
 * @Version 1.0
 */
@NoArgsConstructor
@Data
public class ShopifyOrders {
    private long id;
    private String admin_graphql_api_id;
    private int app_id;
    private String browser_ip;
    private boolean buyer_accepts_marketing;
    private String cancel_reason;
    private String cancelled_at;
    private String cart_token;
    private long checkout_id;
    private String checkout_token;
    private ClientDetails client_details;
    private String closed_at;
    private boolean confirmed;
    private String contact_email;
    private String created_at;
    private String currency;
    private String current_subtotal_price;
    private CurrentSubtotalPriceSet current_subtotal_price_set;
    private String current_total_discounts;
    private CurrentTotalDiscountsSet current_total_discounts_set;
    private String current_total_duties_set;
    private String current_total_price;
    private CurrentTotalPriceSet current_total_price_set;
    private String current_total_tax;
    private CurrentTotalTaxSet current_total_tax_set;
    private String customer_locale;
    private String device_id;
    private String email;
    private String financial_status;
    private String fulfillment_status;
    private String gateway;
    private String landing_site;
    private String landing_site_ref;
    private String location_id;
    private String name;
    private String note;
    private int number;
    private int order_number;
    private String order_status_url;
    private String original_total_duties_set;
    private String phone;
    private String presentment_currency;
    private String processed_at;
    private String processing_method;
    private String reference;
    private String referring_site;
    private String source_identifier;
    private String source_name;
    private String source_url;
    private String subtotal_price;
    private SubtotalPriceSet subtotal_price_set;
    private String tags;
    private boolean taxes_included;
    private boolean test;
    private String token;
    private String total_discounts;
    private TotalDiscountsSet total_discounts_set;
    private String total_line_items_price;
    private TotalLineItemsPriceSet total_line_items_price_set;
    private String total_outstanding;
    private String total_price;
    private TotalPriceSet total_price_set;
    private String total_price_usd;
    private TotalShippingPriceSet total_shipping_price_set;
    private String total_tax;
    private TotalTaxSet total_tax_set;
    private String total_tip_received;
    private double total_weight;
    private String updated_at;
    private String user_id;
    private BillingAddress billing_address;
    private Customer customer;
    private PaymentDetails payment_details;
    private ShopifyShippingAddress shipping_address;
    private List<Object> discount_codes;
    private List<Object> note_attributes;
    private List<String> payment_gateway_names;
    private List<Object> tax_lines;
    private List<Object> discount_applications;
    private List<Object> fulfillments;
    private List<LineItems> line_items;
    private List<Object> refunds;
    private List<ShippingLines> shipping_lines;
}
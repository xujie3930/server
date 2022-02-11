package com.szmsd.delivery.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * TrackingYee 路由信息
 */
@NoArgsConstructor
@Data
public class TrackingYeeTraceDto {


    /**
     * shipmentId : 5e4c1d4a49d2ad00121a2e0d
     * trackingNo : LX934733035NL
     * carrierCode : NLPost
     * orderNo : NLR200217ADLE1000549
     * platform : {"code":"Other","name":"","type":""}
     * shopInfo : {"id":"","name":""}
     * customFieldInfo : {"fieldOne":"","fieldTwo":"","fieldThree":""}
     * trackingStatus : Delivered
     * onlineTime : {"type":"NormalDate","dateTime":"2020-02-24 10:22:00","utcTime":null,"utcOfferset":null}
     * deliveryConfirmationTime : {"type":"NormalDate","dateTime":"2020-03-05 10:18:00","utcTime":null,"utcOfferset":null}
     * logisticsTrackingSections : [{"no":1,"sectionType":"InternationalPostalOrigin","carrierCode":"NLPost","trackingNos":["LX934733035NL"],"carrierStatus":"Normal","logisticsTracking":{"carrierCode":"NLPost","carrierService":"","otherTrackingNos":null,"multiplePackageTrackingInfo":{"isMultiplePackage":false,"masterTrackingNo":"","associatedTrackingNos":[]},"packageInfo":{"weight":{"value":0,"unit":"G"},"dimension":{"length":0,"width":0,"height":0,"lengthUnit":"CM"}},"associatedNumbers":[],"parcelMailingType":null,"onlineTime":{"type":"NormalDate","dateTime":"2020-02-24 10:22:00","utcTime":null,"utcOfferset":null},"deliveryConfirmationTime":{"type":"NormalDate","dateTime":"2020-03-05 10:18:00","utcTime":null,"utcOfferset":null},"trackingNo":"LX934733035NL","items":[{"actionCode":"PreNotified","no":1,"description":"The item is pre-advised to PostnL","trackingTime":{"type":"NormalDate","dateTime":"2020-02-24 10:22:00","utcTime":null,"utcOfferset":null},"location":{"display":"","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}},{"actionCode":"InTransit","no":2,"description":"The Item is at the shippers warehouse","trackingTime":{"type":"NormalDate","dateTime":"2020-02-26 04:39:00","utcTime":null,"utcOfferset":null},"location":{"display":"","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}},{"actionCode":"InTransit","no":3,"description":"The item is ready for shipment","trackingTime":{"type":"NormalDate","dateTime":"2020-02-26 04:42:00","utcTime":null,"utcOfferset":null},"location":{"display":"","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}},{"actionCode":"InTransit","no":4,"description":"The shipment is handed over in bulk, final acceptance of goods to be confirmed","trackingTime":{"type":"NormalDate","dateTime":"2020-02-28 12:17:00","utcTime":null,"utcOfferset":null},"location":{"display":"","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}},{"actionCode":"InTransit","no":5,"description":"Consignment received at the PostNL Acceptance Centre","trackingTime":{"type":"NormalDate","dateTime":"2020-02-29 13:47:00","utcTime":null,"utcOfferset":null},"location":{"display":"","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}},{"actionCode":"InTransit","no":6,"description":"The item is at the PostNL sorting center","trackingTime":{"type":"NormalDate","dateTime":"2020-03-01 13:45:00","utcTime":null,"utcOfferset":null},"location":{"display":"","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}},{"actionCode":"InTransit","no":7,"description":"The item is processed at the PostNL sorting center","trackingTime":{"type":"NormalDate","dateTime":"2020-03-01 13:45:00","utcTime":null,"utcOfferset":null},"location":{"display":"","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}},{"actionCode":"InTransit","no":8,"description":"The item is on transport to the country of destination","trackingTime":{"type":"NormalDate","dateTime":"2020-03-03 05:58:00","utcTime":null,"utcOfferset":null},"location":{"display":"","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}},{"actionCode":"InTransit","no":9,"description":"The consignment has arrived in the country of destination","trackingTime":{"type":"NormalDate","dateTime":"2020-03-04 08:20:00","utcTime":null,"utcOfferset":null},"location":{"display":"","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}},{"actionCode":"InTransit","no":10,"description":"The item has arrived in the country of destination","trackingTime":{"type":"NormalDate","dateTime":"2020-03-04 11:07:00","utcTime":null,"utcOfferset":null},"location":{"display":"","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}},{"actionCode":"InTransit","no":11,"description":"The item has been processed in the country of destination","trackingTime":{"type":"NormalDate","dateTime":"2020-03-04 20:47:00","utcTime":null,"utcOfferset":null},"location":{"display":"","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}},{"actionCode":"InTransit","no":12,"description":"The item is at the local sorting centre","trackingTime":{"type":"NormalDate","dateTime":"2020-03-05 08:56:00","utcTime":null,"utcOfferset":null},"location":{"display":"","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}},{"actionCode":"Delivered","no":13,"description":"The item has been delivered successfully","trackingTime":{"type":"NormalDate","dateTime":"2020-03-05 10:18:00","utcTime":null,"utcOfferset":null},"location":{"display":"","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}}],"senderAddress":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""},"destinationAddress":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"ES","alpha3Code":"ESP","enName":"Spain","cnName":"西班牙"},"postcode":""},"status":"Delivered"},"lastestTrackTime":"2020-03-05T10:52:25Z"},{"no":2,"sectionType":"InternationalPostalDestination","carrierCode":"ESPost","trackingNos":["LX934733035NL"],"carrierStatus":"Normal","logisticsTracking":{"carrierCode":"ESPost","carrierService":"","otherTrackingNos":null,"multiplePackageTrackingInfo":{"isMultiplePackage":false,"masterTrackingNo":"","associatedTrackingNos":[]},"packageInfo":{"weight":{"value":0,"unit":"G"},"dimension":{"length":0,"width":0,"height":0,"lengthUnit":"CM"}},"associatedNumbers":[],"parcelMailingType":null,"onlineTime":{"type":"NormalDate","dateTime":"2020-02-29 13:48:00","utcTime":null,"utcOfferset":null},"deliveryConfirmationTime":{"type":"NormalDate","dateTime":"2020-03-05 10:18:20","utcTime":null,"utcOfferset":null},"trackingNo":"LX934733035NL","items":[{"actionCode":"InTransit","no":1,"description":"Accepted,Your shipment has been received at the point of origin","trackingTime":{"type":"NormalDate","dateTime":"2020-02-29 13:48:00","utcTime":null,"utcOfferset":null},"location":{"display":"NLHAGA","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}},{"actionCode":"InTransit","no":2,"description":"Leaving the Exchange Office,The shipment has left the international logistics centre of origin and is en route to its destination","trackingTime":{"type":"NormalDate","dateTime":"2020-03-01 13:45:00","utcTime":null,"utcOfferset":null},"location":{"display":"NLHAGB","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}},{"actionCode":"InTransit","no":3,"description":"Arrival at the exchange office,The shipment has arrived at the international logistics centre of the destination for delivery","trackingTime":{"type":"NormalDate","dateTime":"2020-03-04 20:47:17","utcTime":null,"utcOfferset":null},"location":{"display":"ESMADA","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}},{"actionCode":"InTransit","no":4,"description":"Classified,Shipment has been classified in the Logistics Centre","trackingTime":{"type":"NormalDate","dateTime":"2020-03-04 20:47:17","utcTime":null,"utcOfferset":null},"location":{"display":"OFICINA CAMBIO AVION","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}},{"actionCode":"InTransit","no":5,"description":"Classified,Shipment has been classified in the Logistics Centre","trackingTime":{"type":"NormalDate","dateTime":"2020-03-05 06:08:24","utcTime":null,"utcOfferset":null},"location":{"display":"CTA MADRID","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}},{"actionCode":"InTransit","no":6,"description":"Registration in the delivery unit,Your shipment has arrived at the unit responsible for its delivery","trackingTime":{"type":"NormalDate","dateTime":"2020-03-05 08:56:08","utcTime":null,"utcOfferset":null},"location":{"display":"MADRID UR 22","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}},{"actionCode":"InTransit","no":7,"description":"In delivery,The shipment is on its way to be delivered","trackingTime":{"type":"NormalDate","dateTime":"2020-03-05 09:02:48","utcTime":null,"utcOfferset":null},"location":{"display":"MADRID UR 22","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}},{"actionCode":"Delivered","no":8,"description":"Delivered,Your shipment has been delivered to the recipient or authorised proxy","trackingTime":{"type":"NormalDate","dateTime":"2020-03-05 10:18:20","utcTime":null,"utcOfferset":null},"location":{"display":"MADRID UR 22","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}}],"senderAddress":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""},"destinationAddress":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""},"status":"Delivered"},"lastestTrackTime":"2020-03-05T10:52:25Z"}]
     */

    private String shipmentId;
    private String trackingNo;
    private String carrierCode;
    private String orderNo;
    private PlatformDto platform;
    private ShopInfoDto shopInfo;
    private CustomFieldInfoDto customFieldInfo;
    private String trackingStatus;
    private OnlineTimeDto onlineTime;
    private DeliveryConfirmationTimeDto deliveryConfirmationTime;
    private List<LogisticsTrackingSectionsDto> logisticsTrackingSections;

    @NoArgsConstructor
    @Data
    public static class PlatformDto {
        /**
         * code : Other
         * name :
         * type :
         */

        private String code;
        private String name;
        private String type;
    }

    @NoArgsConstructor
    @Data
    public static class ShopInfoDto {
        /**
         * id :
         * name :
         */

        private String id;
        private String name;
    }

    @NoArgsConstructor
    @Data
    public static class CustomFieldInfoDto {
        /**
         * fieldOne :
         * fieldTwo :
         * fieldThree :
         */

        private String fieldOne;
        private String fieldTwo;
        private String fieldThree;
    }

    @NoArgsConstructor
    @Data
    public static class OnlineTimeDto {
        /**
         * type : NormalDate
         * dateTime : 2020-02-24 10:22:00
         * utcTime : null
         * utcOfferset : null
         */

        private String type;
        private String dateTime;
        private Object utcTime;
        private Object utcOfferset;
    }

    @NoArgsConstructor
    @Data
    public static class DeliveryConfirmationTimeDto {
        /**
         * type : NormalDate
         * dateTime : 2020-03-05 10:18:00
         * utcTime : null
         * utcOfferset : null
         */

        private String type;
        private String dateTime;
        private Object utcTime;
        private Object utcOfferset;
    }

    @NoArgsConstructor
    @Data
    public static class LogisticsTrackingSectionsDto {
        /**
         * no : 1
         * sectionType : InternationalPostalOrigin
         * carrierCode : NLPost
         * trackingNos : ["LX934733035NL"]
         * carrierStatus : Normal
         * logisticsTracking : {"carrierCode":"NLPost","carrierService":"","otherTrackingNos":null,"multiplePackageTrackingInfo":{"isMultiplePackage":false,"masterTrackingNo":"","associatedTrackingNos":[]},"packageInfo":{"weight":{"value":0,"unit":"G"},"dimension":{"length":0,"width":0,"height":0,"lengthUnit":"CM"}},"associatedNumbers":[],"parcelMailingType":null,"onlineTime":{"type":"NormalDate","dateTime":"2020-02-24 10:22:00","utcTime":null,"utcOfferset":null},"deliveryConfirmationTime":{"type":"NormalDate","dateTime":"2020-03-05 10:18:00","utcTime":null,"utcOfferset":null},"trackingNo":"LX934733035NL","items":[{"actionCode":"PreNotified","no":1,"description":"The item is pre-advised to PostnL","trackingTime":{"type":"NormalDate","dateTime":"2020-02-24 10:22:00","utcTime":null,"utcOfferset":null},"location":{"display":"","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}},{"actionCode":"InTransit","no":2,"description":"The Item is at the shippers warehouse","trackingTime":{"type":"NormalDate","dateTime":"2020-02-26 04:39:00","utcTime":null,"utcOfferset":null},"location":{"display":"","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}},{"actionCode":"InTransit","no":3,"description":"The item is ready for shipment","trackingTime":{"type":"NormalDate","dateTime":"2020-02-26 04:42:00","utcTime":null,"utcOfferset":null},"location":{"display":"","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}},{"actionCode":"InTransit","no":4,"description":"The shipment is handed over in bulk, final acceptance of goods to be confirmed","trackingTime":{"type":"NormalDate","dateTime":"2020-02-28 12:17:00","utcTime":null,"utcOfferset":null},"location":{"display":"","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}},{"actionCode":"InTransit","no":5,"description":"Consignment received at the PostNL Acceptance Centre","trackingTime":{"type":"NormalDate","dateTime":"2020-02-29 13:47:00","utcTime":null,"utcOfferset":null},"location":{"display":"","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}},{"actionCode":"InTransit","no":6,"description":"The item is at the PostNL sorting center","trackingTime":{"type":"NormalDate","dateTime":"2020-03-01 13:45:00","utcTime":null,"utcOfferset":null},"location":{"display":"","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}},{"actionCode":"InTransit","no":7,"description":"The item is processed at the PostNL sorting center","trackingTime":{"type":"NormalDate","dateTime":"2020-03-01 13:45:00","utcTime":null,"utcOfferset":null},"location":{"display":"","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}},{"actionCode":"InTransit","no":8,"description":"The item is on transport to the country of destination","trackingTime":{"type":"NormalDate","dateTime":"2020-03-03 05:58:00","utcTime":null,"utcOfferset":null},"location":{"display":"","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}},{"actionCode":"InTransit","no":9,"description":"The consignment has arrived in the country of destination","trackingTime":{"type":"NormalDate","dateTime":"2020-03-04 08:20:00","utcTime":null,"utcOfferset":null},"location":{"display":"","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}},{"actionCode":"InTransit","no":10,"description":"The item has arrived in the country of destination","trackingTime":{"type":"NormalDate","dateTime":"2020-03-04 11:07:00","utcTime":null,"utcOfferset":null},"location":{"display":"","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}},{"actionCode":"InTransit","no":11,"description":"The item has been processed in the country of destination","trackingTime":{"type":"NormalDate","dateTime":"2020-03-04 20:47:00","utcTime":null,"utcOfferset":null},"location":{"display":"","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}},{"actionCode":"InTransit","no":12,"description":"The item is at the local sorting centre","trackingTime":{"type":"NormalDate","dateTime":"2020-03-05 08:56:00","utcTime":null,"utcOfferset":null},"location":{"display":"","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}},{"actionCode":"Delivered","no":13,"description":"The item has been delivered successfully","trackingTime":{"type":"NormalDate","dateTime":"2020-03-05 10:18:00","utcTime":null,"utcOfferset":null},"location":{"display":"","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}}],"senderAddress":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""},"destinationAddress":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"ES","alpha3Code":"ESP","enName":"Spain","cnName":"西班牙"},"postcode":""},"status":"Delivered"}
         * lastestTrackTime : 2020-03-05T10:52:25Z
         */

        private int no;
        private String sectionType;
        private String carrierCode;
        private String carrierStatus;
        private LogisticsTrackingDto logisticsTracking;
        private String lastestTrackTime;
        private List<String> trackingNos;
    }

    @NoArgsConstructor
    @Data
    public static class LogisticsTrackingDto {
        /**
         * carrierCode : NLPost
         * carrierService :
         * otherTrackingNos : null
         * multiplePackageTrackingInfo : {"isMultiplePackage":false,"masterTrackingNo":"","associatedTrackingNos":[]}
         * packageInfo : {"weight":{"value":0,"unit":"G"},"dimension":{"length":0,"width":0,"height":0,"lengthUnit":"CM"}}
         * associatedNumbers : []
         * parcelMailingType : null
         * onlineTime : {"type":"NormalDate","dateTime":"2020-02-24 10:22:00","utcTime":null,"utcOfferset":null}
         * deliveryConfirmationTime : {"type":"NormalDate","dateTime":"2020-03-05 10:18:00","utcTime":null,"utcOfferset":null}
         * trackingNo : LX934733035NL
         * items : [{"actionCode":"PreNotified","no":1,"description":"The item is pre-advised to PostnL","trackingTime":{"type":"NormalDate","dateTime":"2020-02-24 10:22:00","utcTime":null,"utcOfferset":null},"location":{"display":"","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}},{"actionCode":"InTransit","no":2,"description":"The Item is at the shippers warehouse","trackingTime":{"type":"NormalDate","dateTime":"2020-02-26 04:39:00","utcTime":null,"utcOfferset":null},"location":{"display":"","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}},{"actionCode":"InTransit","no":3,"description":"The item is ready for shipment","trackingTime":{"type":"NormalDate","dateTime":"2020-02-26 04:42:00","utcTime":null,"utcOfferset":null},"location":{"display":"","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}},{"actionCode":"InTransit","no":4,"description":"The shipment is handed over in bulk, final acceptance of goods to be confirmed","trackingTime":{"type":"NormalDate","dateTime":"2020-02-28 12:17:00","utcTime":null,"utcOfferset":null},"location":{"display":"","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}},{"actionCode":"InTransit","no":5,"description":"Consignment received at the PostNL Acceptance Centre","trackingTime":{"type":"NormalDate","dateTime":"2020-02-29 13:47:00","utcTime":null,"utcOfferset":null},"location":{"display":"","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}},{"actionCode":"InTransit","no":6,"description":"The item is at the PostNL sorting center","trackingTime":{"type":"NormalDate","dateTime":"2020-03-01 13:45:00","utcTime":null,"utcOfferset":null},"location":{"display":"","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}},{"actionCode":"InTransit","no":7,"description":"The item is processed at the PostNL sorting center","trackingTime":{"type":"NormalDate","dateTime":"2020-03-01 13:45:00","utcTime":null,"utcOfferset":null},"location":{"display":"","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}},{"actionCode":"InTransit","no":8,"description":"The item is on transport to the country of destination","trackingTime":{"type":"NormalDate","dateTime":"2020-03-03 05:58:00","utcTime":null,"utcOfferset":null},"location":{"display":"","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}},{"actionCode":"InTransit","no":9,"description":"The consignment has arrived in the country of destination","trackingTime":{"type":"NormalDate","dateTime":"2020-03-04 08:20:00","utcTime":null,"utcOfferset":null},"location":{"display":"","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}},{"actionCode":"InTransit","no":10,"description":"The item has arrived in the country of destination","trackingTime":{"type":"NormalDate","dateTime":"2020-03-04 11:07:00","utcTime":null,"utcOfferset":null},"location":{"display":"","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}},{"actionCode":"InTransit","no":11,"description":"The item has been processed in the country of destination","trackingTime":{"type":"NormalDate","dateTime":"2020-03-04 20:47:00","utcTime":null,"utcOfferset":null},"location":{"display":"","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}},{"actionCode":"InTransit","no":12,"description":"The item is at the local sorting centre","trackingTime":{"type":"NormalDate","dateTime":"2020-03-05 08:56:00","utcTime":null,"utcOfferset":null},"location":{"display":"","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}},{"actionCode":"Delivered","no":13,"description":"The item has been delivered successfully","trackingTime":{"type":"NormalDate","dateTime":"2020-03-05 10:18:00","utcTime":null,"utcOfferset":null},"location":{"display":"","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}}]
         * senderAddress : {"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}
         * destinationAddress : {"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"ES","alpha3Code":"ESP","enName":"Spain","cnName":"西班牙"},"postcode":""}
         * status : Delivered
         */

        private String carrierCode;
        private String carrierService;
        private Object otherTrackingNos;
        private MultiplePackageTrackingInfoDto multiplePackageTrackingInfo;
        private PackageInfoDto packageInfo;
        private Object parcelMailingType;
        private OnlineTimeDto onlineTime;
        private DeliveryConfirmationTimeDto deliveryConfirmationTime;
        private String trackingNo;
        private SenderAddressDto senderAddress;
        private DestinationAddressDto destinationAddress;
        private String status;
        private List<AssociatedNumbersDto> associatedNumbers;
        private List<ItemsDto> items;
    }

    @NoArgsConstructor
    @Data
    public static class MultiplePackageTrackingInfoDto {
        /**
         * isMultiplePackage : false
         * masterTrackingNo :
         * associatedTrackingNos : []
         */

        private boolean isMultiplePackage;
        private String masterTrackingNo;
        private List<?> associatedTrackingNos;
    }

    @NoArgsConstructor
    @Data
    public static class PackageInfoDto {
        /**
         * weight : {"value":0,"unit":"G"}
         * dimension : {"length":0,"width":0,"height":0,"lengthUnit":"CM"}
         */

        private WeightDto weight;
        private DimensionDto dimension;
    }

    @NoArgsConstructor
    @Data
    public static class WeightDto {
        /**
         * value : 0
         * unit : G
         */

        private int value;
        private String unit;
    }

    @NoArgsConstructor
    @Data
    public static class DimensionDto {
        /**
         * length : 0
         * width : 0
         * height : 0
         * lengthUnit : CM
         */

        private int length;
        private int width;
        private int height;
        private String lengthUnit;
    }

    @NoArgsConstructor
    @Data
    public static class SenderAddressDto {
        /**
         * street1 :
         * street2 :
         * street3 :
         * city :
         * province :
         * country : {"alpha2Code":"","alpha3Code":"","enName":"","cnName":""}
         * postcode :
         */

        private String street1;
        private String street2;
        private String street3;
        private String city;
        private String province;
        private CountryDto country;
        private String postcode;
    }

    @NoArgsConstructor
    @Data
    public static class CountryDto {
        /**
         * alpha2Code :
         * alpha3Code :
         * enName :
         * cnName :
         */

        private String alpha2Code;
        private String alpha3Code;
        private String enName;
        private String cnName;
    }

    @NoArgsConstructor
    @Data
    public static class DestinationAddressDto {
        /**
         * street1 :
         * street2 :
         * street3 :
         * city :
         * province :
         * country : {"alpha2Code":"ES","alpha3Code":"ESP","enName":"Spain","cnName":"西班牙"}
         * postcode :
         */

        private String street1;
        private String street2;
        private String street3;
        private String city;
        private String province;
        private CountryDto country;
        private String postcode;

    }

    @NoArgsConstructor
    @Data
    public static class ItemsDto {
        /**
         * actionCode : PreNotified
         * no : 1
         * description : The item is pre-advised to PostnL
         * trackingTime : {"type":"NormalDate","dateTime":"2020-02-24 10:22:00","utcTime":null,"utcOfferset":null}
         * location : {"display":"","address":{"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}}
         */

        private String actionCode;
        private int no;
        private String description;
        private TrackingTimeDto trackingTime;
        private LocationDto location;
    }


    @NoArgsConstructor
    @Data
    public static class TrackingTimeDto {
        /**
         * type : NormalDate
         * dateTime : 2020-02-24 10:22:00
         * utcTime : null
         * utcOfferset : null
         */

        private String type;
        private String dateTime;
        private Object utcTime;
        private Object utcOfferset;
    }

    @NoArgsConstructor
    @Data
    public static class LocationDto {
        /**
         * display :
         * address : {"street1":"","street2":"","street3":"","city":"","province":"","country":{"alpha2Code":"","alpha3Code":"","enName":"","cnName":""},"postcode":""}
         */

        private String display;
        private AddressDto address;

    }

    @NoArgsConstructor
    @Data
    public static class AddressDto {
        /**
         * street1 :
         * street2 :
         * street3 :
         * city :
         * province :
         * country : {"alpha2Code":"","alpha3Code":"","enName":"","cnName":""}
         * postcode :
         */

        private String street1;
        private String street2;
        private String street3;
        private String city;
        private String province;
        private CountryDto country;
        private String postcode;

    }

    @NoArgsConstructor
    @Data
    public static class AssociatedNumbersDto {
        /**
         * name : YunExpress Tracking Number
         * number : YT2133421272507218
         */
        private String name;
        private String number;
    }

}

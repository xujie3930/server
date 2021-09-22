package com.szmsd.doc.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.szmsd.bas.api.client.BasSubClientService;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import springfox.documentation.service.AllowableListValues;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ElementType.FIELD})
@Retention(RUNTIME)
@Documented
public @interface SwaggerDictionary {

    /**
     * 数据类型
     *
     * @return Type
     */
    DataType type() default DataType.DIC;

    /**
     * dic code
     *
     * @return String
     */
    String dicCode() default "";

    /**
     * key
     * <p>
     * value = subCode or subValue
     *
     * @return String
     */
    String dicKey() default "subCode";

    /**
     * JSON
     *
     * @return String
     */
    String json() default "";

    /**
     * key
     *
     * @return String
     */
    String jsonKey() default "key";

    /**
     * text
     *
     * @return String
     */
    String jsonText() default "text";

    /**
     * Value
     *
     * @return Value
     */
    Value[] values() default {};

    /**
     * 数据过滤
     *
     * @return DateFilter
     */
    Class<? extends DataFilter> filter() default DataFilter.class;

    /**
     * 数据列表
     */
    interface _AllowableListValues {

        _AllowableListValues VALUES = new _AllowableListValues() {
        };

        /**
         * 数据列表
         *
         * @return AllowableListValues
         */
        default AllowableListValues values(SwaggerDictionary swaggerDictionary, DataChannel dataChannel) {
            List<String> list = new ArrayList<>();
            List<DataFormat> dataFormatList = new ArrayList<>();
            DataFilter dataFilter = DataFilter.newIns(swaggerDictionary.filter());
            if (DataType.DIC.equals(swaggerDictionary.type())) {
                dataFormatList = dataChannel.list(swaggerDictionary, dataFilter);
            } else if (DataType.JSON.equals(swaggerDictionary.type())) {
                dataFormatList = dataChannel.list(swaggerDictionary, dataFilter);
            } else if (DataType.ANN.equals(swaggerDictionary.type())) {
                Value[] values = swaggerDictionary.values();
                for (Value value : values) {
                    DataFormat.DefDataFormat dataFormat = new DataFormat.DefDataFormat(value);
                    if (null != dataFilter) {
                        if (dataFilter.filter(dataFormat)) {
                            dataFormatList.add(dataFormat);
                        }
                    } else {
                        dataFormatList.add(dataFormat);
                    }
                }
            }
            if (null == dataFormatList || dataFormatList.isEmpty()) {
                return null;
            }
            if (null != dataFilter) {
                dataFormatList = dataFilter.filter(dataFormatList);
            }
            for (DataFormat dataFormat : dataFormatList) {
                list.add(dataFormat.toString());
            }
            return new AllowableListValues(list, "array");
        }
    }

    /**
     * 数据合并
     */
    interface DataMerge {

        DataMerge DATA_MERGE = new DataMerge() {
        };

        /**
         * merge
         *
         * @param dataFormat dataFormat
         * @return String
         */
        default String merge(DataFormat dataFormat) {
            return dataFormat.getKey() + ":" + dataFormat.getText();
        }
    }

    /**
     * 数据格式
     */
    interface DataFormat {

        /**
         * key
         *
         * @return String
         */
        String getKey();

        /**
         * text
         *
         * @return String
         */
        String getText();

        class DefDataFormat implements DataFormat {

            private final String key;
            private final String text;
            private final DataMerge dataMerge;

            public DefDataFormat(Value value) {
                this.key = value.key();
                this.text = value.text();
                this.dataMerge = DataMerge.DATA_MERGE;
            }

            public DefDataFormat(String key, String text) {
                this(key, text, DataMerge.DATA_MERGE);
            }

            public DefDataFormat(String key, String text, DataMerge dataMerge) {
                this.key = key;
                this.text = text;
                this.dataMerge = dataMerge;
            }

            @Override
            public String getKey() {
                return this.key;
            }

            @Override
            public String getText() {
                return this.text;
            }

            @Override
            public String toString() {
                return this.dataMerge.merge(this);
            }
        }
    }

    /**
     * 数据过滤
     */
    interface DataFilter {

        /**
         * filter
         *
         * @param dataFormat dataFormat
         * @return boolean
         */
        default boolean filter(DataFormat dataFormat) {
            return true;
        }

        /**
         * filter
         *
         * @param dataFormatList dataFormatList
         * @return DataFormat
         */
        List<DataFormat> filter(List<DataFormat> dataFormatList);

        static DataFilter newIns(Class<? extends DataFilter> filter) {
            DataFilter dataFilter = null;
            if (!DataFilter.class.equals(filter)) {
                try {
                    dataFilter = filter.getConstructor().newInstance();
                } catch (Exception e) {
                    // ignore
                }
            }
            return dataFilter;
        }

        class DefDataFilter implements DataFilter {

            @Override
            public List<DataFormat> filter(List<DataFormat> dataFormatList) {
                return dataFormatList;
            }
        }
    }

    interface DataChannel {

        List<DataFormat> list(SwaggerDictionary swaggerDictionary, DataFilter dataFilter);

        class DefDataChannel implements DataChannel {

            private final DataChannel dicDataChannel;

            public DefDataChannel(BasSubClientService basSubClientService) {
                this.dicDataChannel = new DicDataChannel(basSubClientService);
            }

            @Override
            public List<DataFormat> list(SwaggerDictionary swaggerDictionary, DataFilter dataFilter) {
                if (DataType.DIC.equals(swaggerDictionary.type())) {
                    return dicDataChannel.list(swaggerDictionary, dataFilter);
                } else if (DataType.JSON.equals(swaggerDictionary.type())) {
                    return JsonDataChannel.channel.list(swaggerDictionary, dataFilter);
                }
                return null;
            }
        }

        class DicDataChannel implements DataChannel {
            private final Logger logger = LoggerFactory.getLogger(DicDataChannel.class);
            private final BasSubClientService basSubClientService;
            private boolean isError;

            public DicDataChannel(BasSubClientService basSubClientService) {
                this.basSubClientService = basSubClientService;
                this.isError = false;
            }

            @Override
            public List<DataFormat> list(SwaggerDictionary swaggerDictionary, DataFilter dataFilter) {
                if (this.isError) {
                    return null;
                }
                if ("".equals(swaggerDictionary.dicCode().trim())) {
                    return null;
                }
                try {
                    Map<String, List<BasSubWrapperVO>> map = this.basSubClientService.getSub(swaggerDictionary.dicCode());
                    if (null != map) {
                        List<BasSubWrapperVO> list = map.get(swaggerDictionary.dicCode());
                        if (null != list && list.size() > 0) {
                            List<DataFormat> dataFormatList = new ArrayList<>();
                            for (BasSubWrapperVO vo : list) {
                                String key;
                                if ("subValue".equals(swaggerDictionary.dicKey())) {
                                    key = vo.getSubValue();
                                } else {
                                    key = vo.getSubCode();
                                }
                                DataFormat dataFormat = new DataFormat.DefDataFormat(key, vo.getSubName());
                                if (null != dataFilter) {
                                    if (dataFilter.filter(dataFormat)) {
                                        dataFormatList.add(dataFormat);
                                    }
                                } else {
                                    dataFormatList.add(dataFormat);
                                }
                            }
                            return dataFormatList;
                        }
                    }
                } catch (Exception e) {
                    this.logger.error(e.getMessage(), e);
                    this.isError = true;
                }
                return null;
            }
        }

        class JsonDataChannel implements DataChannel {

            public static DataChannel channel = new JsonDataChannel();

            @Override
            public List<DataFormat> list(SwaggerDictionary swaggerDictionary, DataFilter dataFilter) {
                String json = swaggerDictionary.json();
                if ("".equals(json.trim())) {
                    return null;
                }
                JSONArray jsonArray = JSONArray.parseArray(json);
                if (jsonArray.isEmpty()) {
                    return null;
                }
                List<DataFormat> dataFormatList = new ArrayList<>();
                for (Object o : jsonArray) {
                    JSONObject jsonObject = (JSONObject) o;
                    DataFormat dataFormat = new DataFormat.DefDataFormat(jsonObject.getString(swaggerDictionary.jsonKey()), jsonObject.getString(swaggerDictionary.jsonText()));
                    if (null != dataFilter) {
                        if (dataFilter.filter(dataFormat)) {
                            dataFormatList.add(dataFormat);
                        }
                    } else {
                        dataFormatList.add(dataFormat);
                    }
                }
                return dataFormatList;
            }
        }
    }

    @Target({ElementType.FIELD})
    @Retention(RUNTIME)
    @Documented
    @interface Value {

        /**
         * key
         *
         * @return String
         */
        String key();

        /**
         * text
         *
         * @return String
         */
        String text();
    }

    /**
     * 数据类型
     */
    enum DataType {

        /**
         * 数据字典
         */
        DIC,

        /**
         * JSON
         */
        JSON,

        /**
         * 注解赋值
         */
        ANN,
        ;
    }
}

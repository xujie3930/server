package com.szmsd.doc.api;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.google.common.base.Optional;
import com.szmsd.bas.api.client.BasSubClientService;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import springfox.documentation.schema.Annotations;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;
import springfox.documentation.swagger.schema.ApiModelProperties;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Component
public class SwaggerDictionaryPropertyPlugin implements ModelPropertyBuilderPlugin {

    @Autowired
    private BasSubClientService basSubClientService;

    @Override
    public void apply(ModelPropertyContext context) {
        Optional<ApiModelProperty> annotation = Optional.absent();
        if (context.getAnnotatedElement().isPresent()) {
            annotation = annotation.or(ApiModelProperties.findApiModePropertyAnnotation(context.getAnnotatedElement().get()));
        }
        BeanPropertyDefinition beanPropertyDefinition = context.getBeanPropertyDefinition().get();
        if (context.getBeanPropertyDefinition().isPresent()) {
            annotation = annotation.or(Annotations.findPropertyAnnotation(
                    beanPropertyDefinition,
                    ApiModelProperty.class));
        }
        SwaggerDictionary swaggerDictionary = beanPropertyDefinition.getField().getAnnotation(SwaggerDictionary.class);
        if (null != swaggerDictionary) {
            SwaggerDictionary.DataChannel dataChannel = new SwaggerDictionary.DataChannel.DefDataChannel(this.basSubClientService);
            AllowableListValues allowableListValues = SwaggerDictionary._AllowableListValues.VALUES.values(swaggerDictionary, dataChannel);
            Class<?> rawPrimaryType = context.getBeanPropertyDefinition().get().getRawPrimaryType();
            final ResolvedType resolvedType;
            if (List.class.equals(rawPrimaryType)) {
                resolvedType = context.getResolver().resolve(rawPrimaryType);
                String description = context.getBuilder().build().getDescription();
                StringJoiner sj = new StringJoiner(",");
                for (String value : allowableListValues.getValues()) {
                    sj.add(value);
                }
                description = description + ";可用值:" + sj.toString();
                context.getBuilder().description(description);
            } else {
                resolvedType = context.getResolver().resolve(String.class);
            }
            context.getBuilder().allowableValues(allowableListValues).type(resolvedType);
        } else {
            final Class<?> rawPrimaryType = beanPropertyDefinition.getRawPrimaryType();
            //过滤得到目标类型
            if (annotation.isPresent() && rawPrimaryType.isEnum()) {
                //获取CodedEnum的code值
                Enum<?>[] values = (Enum<?>[]) rawPrimaryType.getEnumConstants();
                final List<String> displayValues = Arrays.stream(values).map(Enum::toString).collect(Collectors.toList());
                final AllowableListValues allowableListValues = new AllowableListValues(displayValues, rawPrimaryType.getTypeName());
                //固定设置为int类型
                final ResolvedType resolvedType = context.getResolver().resolve(String.class);
                context.getBuilder().allowableValues(allowableListValues).type(resolvedType);
            }
        }
    }

    @Override
    public boolean supports(DocumentationType delimiter) {
        return true;
    }
}

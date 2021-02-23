package com.szmsd.common.core.utils;

/**
 * @author liyingfeng
 * @date 2020/9/11 14:02
 */
public class JAXBUtil {
    /*public static String toXml(Object obj,String encoding) {
        try {
            StringWriter sw = new StringWriter();
            JAXBContext context = JAXBCache.instance().getJAXBContext(obj.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE); // 格式化输出
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);// 是否省略xm头声明信息
            marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
            marshaller.setProperty("jaxb.encoding", "utf-8");

            marshaller.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper", new NamespacePrefixMapper() {
                @Override
                public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
                    if (NameSpaceConstant.AIR_IMAF_SUB.equals(namespaceUri))
                        return NameSpaceConstant.AIR_IMAF_SUB_PREFIX;
                    if (NameSpaceConstant.AIR_MAF_CM.equals(namespaceUri))
                        return NameSpaceConstant.AIR_MAF_CM_PREFIX;
                    if (NameSpaceConstant.CM.equals(namespaceUri))
                        return NameSpaceConstant.CM_PREFIX;
                    if (NameSpaceConstant.SAU.equals(namespaceUri))
                        return NameSpaceConstant.SAU_PREFIX;
                    if (NameSpaceConstant.XSI.equals(namespaceUri))
                        return NameSpaceConstant.XSI_PREFIX;
                    if (NameSpaceConstant.DECCM.equals(namespaceUri))
                        return NameSpaceConstant.DECCM_PREFIX;
                    if (NameSpaceConstant.DECSUB.equals(namespaceUri))
                        return NameSpaceConstant.DECSUB_PREFIX;
                    if (NameSpaceConstant.XSI_SCHE.equals(namespaceUri))
                        return NameSpaceConstant.XSI_SCHE_PREFIX;
                    return suggestion;
                }
            });
            marshaller.marshal(obj, sw);
            return sw.toString().replace("ns2:","").replace(":ns2","");
        } catch(JAXBException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }*/

   /* public static <T> T readString(Class<T> clazz, InputStream in) throws JAXBException {
        try {
            JAXBContext jc = JAXBContext.newInstance(clazz);
            Unmarshaller u = jc.createUnmarshaller();

//            StringReader reader = new StringReader(content);
//            SAXParserFactory sax = SAXParserFactory.newInstance();
//            sax.setNamespaceAware(false);
//            XMLReader xmlReader = sax.newSAXParser().getXMLReader();
//
//            Source source = new SAXSource(xmlReader, new InputSource(reader));

            return (T) u.unmarshal(in);
        } catch(JAXBException e) {
           throw e;
        }finally {
            if(null != in){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }*/

    /**
     * 生成xml文件
     * @param obj
     * @param out
     * @throws JAXBException
     */
    /*@SuppressWarnings("unchecked")
    public static void toXml(Object obj, OutputStream out) throws JAXBException {
        try {
            JAXBContext context = JAXBCache.instance().getJAXBContext(obj.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);// 是否省略xm头声明信息
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper", new NamespacePrefixMapper() {
                @Override
                public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
                    if (NameSpaceConstant.AIR_IMAF_SUB.equals(namespaceUri))
                        return NameSpaceConstant.AIR_IMAF_SUB_PREFIX;
                    if (NameSpaceConstant.AIR_MAF_CM.equals(namespaceUri))
                        return NameSpaceConstant.AIR_MAF_CM_PREFIX;
                    if (NameSpaceConstant.CM.equals(namespaceUri))
                        return NameSpaceConstant.CM_PREFIX;
                    if (NameSpaceConstant.SAU.equals(namespaceUri))
                        return NameSpaceConstant.SAU_PREFIX;
                    if (NameSpaceConstant.XSI.equals(namespaceUri))
                        return NameSpaceConstant.XSI_PREFIX;
                    if (NameSpaceConstant.DECCM.equals(namespaceUri))
                        return NameSpaceConstant.DECCM_PREFIX;
                    if (NameSpaceConstant.DECSUB.equals(namespaceUri))
                        return NameSpaceConstant.DECSUB_PREFIX;
                    if (NameSpaceConstant.XSI_SCHE.equals(namespaceUri))
                        return NameSpaceConstant.XSI_SCHE_PREFIX;
                    return suggestion;
                }
            });
            marshaller.marshal(obj, out);
        } catch (JAXBException e) {
            throw e;
        }
    }*/
    /**
     * xmlToBean
     * @param xml
     * @param c
     * @param <T>
     * @return
     */
    /*@SuppressWarnings("unchecked")
    public static <T> T toBean(String xml, Class<T> c,String encoding) {
        T t = null;
        try {
            JAXBContext context = JAXBContext.newInstance(c);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            t = (T) unmarshaller.unmarshal(new StringReader(xml));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }*/
}

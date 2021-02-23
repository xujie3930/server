package com.szmsd.common.core.enums;

import com.szmsd.common.core.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @FileName ExceptionCodeEnum.java
 * @Description 模块异常编码
 * @Date 2020-06-15 14:07
 * @Author Yan Hua
 * @Version 1.0
 */
@Slf4j
@Getter
@AllArgsConstructor
public enum ExceptionMessageEnum implements BaseEnum {

    //-----------------全局异常-------------------
    SUCCESS("SUCCESS", "成功", "SUCCESS", "النجاح"),
    FAIL("FAIL", "失败", "FAIL", "فشل"),
    CANNOTBENULL("CANNOTBENULL", "参数不能为空", "The parameter cannot be null", "البارامترات لا يمكن أن تكون فارغة"),
    RUNERROR("RUNERROR", "运行时异常", "Runtime exception", "غير طبيعي عند التشغيل"),
    REQUESTERROR("REQUESTERROR", "请求前异常", "request exception", "غير طبيعي قبل الطلب"),
    CASTINGFAIL("CASTINGFAIL","转换异常","casting error","استثناء التحويل"),
    UPLOAD_FAIL("UPLOAD_FAIL","上传失败!","upload fail!","التحميل فشل!"),
    IMPORT_FAIL("IMPORT_FAIL","导入失败!","import fail!","التحميل فشل!"),

    //--------------系统管理(SYSTEM)------------------------------
    EXPSYSTEM001("EXPSYSTEM001", "不允许操作超级管理员角色", "The super administrator role is not allowed to operate", "لا يسمح بتشغيل دور المسؤول السوبر"),
    EXPSYSTEM002("EXPSYSTEM002", "新增/修改参数 &1 失败，参数键名已存在", "Failed to add/modify parameter &1 The parameter key name already exists", "فشل البارامترات الإضافية و &1 مع وجود أسماء البارامترات"),
    EXPSYSTEM003("EXPSYSTEM003", "新增/修改菜单 &1 失败，菜单名称已存在", "Failed to add/modify menu &1, menu name already exists", "فشلت إضافة/تعديل القائمة &1 مع وجود اسم القائمة بالفعل"),
    EXPSYSTEM004("EXPSYSTEM004", "存在子菜单,不允许删除", "Submenu exists. Deletion is not allowed", "هناك قائمة فرعية، لا يسمح بحذفها"),
    EXPSYSTEM005("EXPSYSTEM005", "菜单已分配,不允许删除", "The menu has been assigned. Deletion is not allowed", "تم توزيع القائمة ولا يسمح بحذفها"),
    EXPSYSTEM006("EXPSYSTEM006", "修改个人信息异常，请联系管理员", "Modify personal information exception, please contact the administrator", "تعديل المعلومات الشخصية غير عادي، يرجى الاتصال بالمدير"),
    EXPSYSTEM007("EXPSYSTEM007", "修改密码失败，旧密码错误", "Password change failed. Old password is wrong", "فشل تعديل كلمة المرور، خطأ كلمة المرور القديمة"),
    EXPSYSTEM008("EXPSYSTEM008", "新密码不能与旧密码相同", "The new password cannot be the same as the old one", "لا يمكن أن تكون كلمة المرور الجديدة هي نفسها كلمة المرور القديمة"),
    EXPSYSTEM009("EXPSYSTEM009", "修改密码异常，请联系管理员", "Change password exception, please contact administrator", "تعديل كلمة المرور غير عادي، يرجى الاتصال بالمدير"),
    EXPSYSTEM010("EXPSYSTEM010", "新增/修改网点 &1 失败，网点名称已存在", "Failed to add/modify node &1, node name already exists", "إدخال/تعديل نقطة & &1 مع وجود اسم نقطة"),
    EXPSYSTEM011("EXPSYSTEM011", "修改网点 &1 失败，上级网点不能是自己", "Failed to modify branch &1, the superior branch cannot be itself", "فشل تعديل نقطة &1، لا يمكن أن تكون نقطة الأب نفسها"),
    EXPSYSTEM012("EXPSYSTEM012", "该网点包含未停用的子网点！", "This node contains undeactivated subnodes!", "يحتوي الموقع على نقاط فرعية غير متوقفة"),
    EXPSYSTEM013("EXPSYSTEM013", "存在下级网点,不允许删除！", "There are sub-branches and deletion is not allowed", "هناك نقاط أدنى ولا يسمح بحذفها"),

    EXPSYSTEM014("EXPSYSTEM014", "用户不存在", "The user does not exist", "المستخدم غير موجود"),
    EXPSYSTEM015("EXPSYSTEM015", "新增/修改用户 &1 失败，登录账号已存在", "Failed to add/modify user &1, login account already exists", "فشلت الإضافات/التعديلات المدخلة على شركة  &1 مع وجود حسابات تسجيل الدخول"),
    EXPSYSTEM016("EXPSYSTEM016", "新增/修改用户 &1 失败，手机号码已存在", "Failed to add/modify user &1, mobile phone number already exists", "新增/修改用户 &1 失败，手机号码已存在"),
    EXPSYSTEM017("EXPSYSTEM017", "新增/修改用户 &1 失败，电子邮箱已存在", "Failed to add/modify user &1, email address already exists", "新增/修改用户 &1 失败，电子邮箱已存在"),
    EXPSYSTEM018("EXPSYSTEM018", "原密码输入错误，请重试", "The original password is incorrect. Please try again", "خطأ إدخال كلمة المرور الأصلية، يرجى المحاولة مرة أخرى"),
    EXPSYSTEM019("EXPSYSTEM019", "登录异常，请重新登录", "Logon exception, please login again", "تسجيل الدخول غير عادي، يرجى إعادة تسجيل الدخول"),

    //-----------------基础资料(EXPBASIS)-------------------
    EXPBASIS001("EXPBASIS001", "编码不能为空", "code is not null", "编码不能为空"),
    EXPBASIS002("EXPBASIS002", "名称不能为空", "name is not null", "名称不能为空"),
    EXPBASIS004("EXPBASIS004", "客户资料信息不存在，客户编号：&1", "Customer info does not exist, customer code: &1", "معلومات العميل غير موجودة ، رمز العميل: & 1"),
    EXPBASIS005("EXPBASIS005", "客户员工信息异常", "cus user info error", "خطأ في معلومات المستخدم"),
    EXPBASIS006("EXPBASIS006","&1编码生成失败,请联系管理员重新设置编码格式!","&1 Code generation failed, please contact the administrator to reset the encoding format!","&1 فشل إنشاء الكود ، يرجى الاتصال بالمسؤول لإعادة تعيين تنسيق الترميز! - مستخدمو الإنترنت بالصينية التقليدية "),
    EXPBASIS007("EXPBASIS007","客户id必填","Customer ID is required","معرف العميل"),
    EXPBASIS008("EXPBASIS008","目的地必填","Destination is required"," الوجهة المطلوبة"),
    EXPBASIS009("EXPBASIS009","产品类型必填","Product type is required"," نوع المنتج"),
    EXPBASIS010("EXPBASIS010","费用类别必填","Expense category is required"," فئة الرسوم المطلوبة"),
    EXPBASIS011("EXPBASIS011","客户报价不存在","Customer quotation does not exist"," اقتباس العملاء لا توجد"),
    EXPBASIS012("EXPBASIS012","请维护可用的公式","Please maintain the available formula"," يرجى الحفاظ على الصيغة المتاحة "),
    EXPBASIS013("EXPBASIS013","客户编号不能为空or重复","Customer number cannot be empty or duplicate"," رقم العميل لا يمكن أن تكون فارغة أو مكررة"),

    //-----------------订单管理(EXPORDER)-------------------
    EXPORDER001("EXPORDER001", "编码不能为空", "编码不能为空", "编码不能为空"),
    EXPORDER002("EXPORDER002", "异常信息:&1 不存在! &2 请联系系统管理人员!", "Exception information: &1 does not exist! &2 Please contact the system administrator!", "معلومات الاستثناء: &2 غير موجود! &1 يرجى الاتصال بمسؤول النظام!"),
    EXPORDER026("EXPORDER026", "编码生成规则定制失败!", "Code generation rule customization failed!", "فشل تخصيص قاعدة إنشاء التعليمات البرمجية!"),
    EXPORDER027("EXPORDER027", "请勿重复提交表单!", "Code generation rule customization failed!", "فشل تخصيص قاعدة إنشاء التعليمات البرمجية!"),
    EXPORDER003("EXPORDER003", "修改订单失败", "Failed to modify order", "فشل تعديل النظام"),
    EXPORDER004("EXPORDER004", "请取消已经调度的订单", "Please cancel the scheduled order", " يرجى إلغاء النظام المحدد"),
    EXPORDER005("EXPORDER005", "请检查勾选订单状态=已结束的", "Please check that the order status = closed", "يرجى التحقق من حالة الطلب"),
    EXPORDER006("EXPORDER006", "二派失败", "Failure of the second school", "فشل الفصائل الثانية"),
    EXPORDER007("EXPORDER007", "请取消已经调度的订单", "Please cancel the scheduled order", " يرجى إلغاء النظام المحدد"),
    EXPORDER008("EXPORDER008", "请调度中心调派的订单", "Please dispatch the order from the dispatching center", " يرجى إرسال أوامر من مركز"),
    EXPORDER009("EXPORDER009", "请调派未处理的订单", "Please dispatch the outstanding order", " يرجى إرسال أوامر غير المجهزة "),
    EXPORDER010("EXPORDER010", "订单来源不能为空!", "Please dispatch the unprocessed order. The order source cannot be empty", " يرجى إرسال أوامر غير المجهزة المصدر لا يمكن أن تكون فارغة "),
    EXPORDER011("EXPORDER011", "找不到产品类型", "Product type not found", " لا يمكن العثور على نوع المنتج"),
    EXPORDER012("EXPORDER012", "生成订单编码失败，请找管理员维护", "Failed to generate order code, please contact administrator for maintenance", " توليد رمز النظام فشل ، يرجى العثور على مدير الصيانة"),
    EXPORDER013("EXPORDER013", "订单参数为空", "Order parameter is empty", "من أجل المعلمة فارغة"),
    EXPORDER014("EXPORDER014", "订单插入失败", "Order insertion failed", " من أجل إدراج فشلت"),
    EXPORDER015("EXPORDER015", "获取订单失败", "Failed to get order", "فشل الحصول على أمر"),
    EXPORDER016("EXPORDER016", "订单已存在", "Order already exists", "الطلب موجود"),
    EXPORDER017("EXPORDER017", "客户订单号存在", "Customer order number exists", " النظام العميل رقم موجود"),


    //-----------------单证管理(EXPWAYBILL)-------------------
    EXPWAYBILL001("EXPWAYBILL001", "运单号已存在", "编码不能为空", "رقم الشحن موجود بالفعل"),
    EXPWAYBILL002("EXPWAYBILL002", "运单号已被签收，请勿重复提交", "运单号已被签收，请勿重复提交", "رقم الشحن موجود بالفعل"),
    EXPWAYBILL005("EXPWAYBILL005", "新增运单失败", "Add waybill no failed", " فشل جديد "),
    EXPWAYBILL006("EXPWAYBILL006", "运单编号不存在", "该运单状态已被签收，不能登记问题件", "رقم الشحن موجود بالفعل"),
    EXPWAYBILL009("EXPWAYBILL009", "已完成的问题件不能被重复处理", "已完成的问题件不能被重复处理", "رقم الشحن موجود بالفعل"),
    EXPWAYBILL016("EXPWAYBILL016", "派件网点不是本网点，不能登记问题件", "派件网点不是本网点，不能登记问题件", "رقم الشحن موجود بالفعل"),
    EXPWAYBILL017("EXPWAYBILL017", "运单已签收，不能登记问题件", "派运单已签收，不能登记问题件", "رقم الشحن موجود بالفعل"),
    EXPWAYBILL018("EXPWAYBILL018", "写入数据不能为空", "写入数据不能为空", "رقم الشحن موجود بالفعل"),
    EXPWAYBILL019("EXPWAYBILL019", "运单号已存在", "运单号已存在", "رقم الشحن موجود بالفعل"),
    EXPWAYBILL020("EXPWAYBILL020", "查询结果为空", "查询结果为空", "رقم الشحن موجود بالفعل"),
    EXPWAYBILL037("EXPWAYBILL021", "数量已达上限，最大支持1000个", "数量已达上限，最大支持1000个", "رقم الشحن موجود بالفعل"),
    EXPWAYBILL040("EXPWAYBILL040", "快件需要拦截，请截留快件", "快件需要拦截，请截留快件", "رقم الشحن موجود بالفعل"),

    EXPWAYBILL003("EXPWAYBILL003", "重复扫描运单编号", "Repeat scan waybill no", "كرر رقم حافظة الشحن المسح الضوئي"),
    EXPWAYBILL004("EXPWAYBILL004", "扫描记录不存在", "The scan record does not exist", "سجلات المسح غير موجودة"),
    EXPWAYBILL011("EXPWAYBILL011", "该运单&1当前状态不允许执行此确认操作!", "The current status of the waybill &1 does not permit this acknowledgement operation", "لا تسمح الحالة الحالية لبوليصة الشحن &1 بعملية التأكيد هذه!"),
    EXPWAYBILL010("EXPWAYBILL010", "该运单&1不存在!", "The waybill: &1 does not exist", "بوليصة الشحن &1 غير موجودة - مستخدمو الإنترنت الصينيون التقليديون"),
    EXPWAYBILL012("EXPWAYBILL012", "该运单&1累计已收金额已超总运费!", "The total amount received of this Waybill has exceeded the total freight!", "فاتورة الشحن هذه &1 المبلغ التراكمي المستلم تجاوز إجمالي الشحن! - مستخدم الإنترنت الصيني التقليدي"),
    EXPWAYBILL013("EXPWAYBILL013", "该运单&1累计已收金额已超代货款!", "The accumulated amount of the waybill &1 has exceeded the payment for goods!", "فاتورة الشحن هذه &1 المبلغ المتراكم المستلم قد تجاوز الدفع مقابل البضائع!"),
    EXPWAYBILL014("EXPWAYBILL014", "运单&1不能重复审核!", "Waybill &1 cannot be reviewed again!", "لا يمكن مراجعة بوليصة الشحن و 1 بشكل متكرر!"),
    EXPWAYBILL015("EXPWAYBILL015", "运单&1当前状态不能反审!", "The current status of Waybill &1 cannot be reversed!", "لا يمكن مراجعة الحالة الحالية لبوليصة الشحن &1!"),
    EXPWAYBILL039("EXPWAYBILL039", "运单信息不存在或已生成!", "Waybill info does not exist or generated !", "معلومات بوليصة الشحن غير موجودة أو تم إنشاؤها!"),
    EXPWAYBILL021("EXPWAYBILL021", "&1订单不存在!", "&1 order does not exist!", "1& طلب غير موجود!"),
    EXPWAYBILL022("EXPWAYBILL022", "当前登录用户只能查询本网点数据!", "The current login user can only query the data of this network!", "يمكن للمستخدم الذي قام بتسجيل الدخول فقط الاستعلام عن بيانات هذا الموقع!"),
    EXPWAYBILL023("EXPWAYBILL023", "当前登录用户只能查询本中心及下属网点数据!", "The current login user can only query the data of the center and its subordinate branches!", "يمكن للمستخدم الذي قام بتسجيل الدخول فقط الاستعلام عن بيانات المركز والمنافذ التابعة له!"),
    EXPWAYBILL024("EXPWAYBILL024", "请通知管理员正确维护&1网点信息(类型)!", "Please inform the administrator to maintain the information (type) of & 1 branch correctly!", "يرجى إبلاغ المسؤول للحفاظ على &1 معلومات منفذ بشكل صحيح (النوع)!"),
    EXPWAYBILL025("EXPWAYBILL025", "寄件网点为空,请先分配寄件网点!", " The sending branch is empty, please assign the sending branch first!", "منفذ الشحن فارغ ، يرجى تخصيص منفذ الشحن أولاً!"),
    EXPWAYBILL026("EXPWAYBILL026","文件存储路径为空","The file storage path is empty","مسار تخزين الملف فارغ"),
    EXPWAYBILL029("EXPWAYBILL029", "&1已完成的问题件不允许重复操作", " &1 he waybill &1 has been registered", "&1 تم تسجيل بوليصة الشحن"),
    EXPWAYBILL030("EXPWAYBILL030", "单号不存在", "The order number does not exist", "رقم الشحن موجود بالفعل"),
    EXPWAYBILL031("EXPWAYBILL031", "该运单&1已被签收，请勿重复提交", "  该运单&1已被签收，请勿重复提交", "رقم الشحن موجود بالفعل"),
    EXPWAYBILL032("EXPWAYBILL032","申报失败","Declaration failure","فشل في التصريح"),
    EXPWAYBILL033("EXPWAYBILL033","脚本不存在&1","Command not exists:&1","البرنامج النصي غير موجود"),
    EXPWAYBILL034("EXPWAYBILL034","脚本执行失败&1","Command execution failed:&1","فشل تنفيذ البرنامج النصي"),
    EXPWAYBILL035("EXPWAYBILL035","操作返回报文失败","XML FILE FAILA","فشلت عملية إعادة الرسالة"),
    EXPWAYBILL036("EXPWAYBILL036","&1单清关数据已存在","&1 customs clearance data already exists","&1 بيانات التخليص الجمركي الفردية موجودة بالفعل"),
    EXPWAYBILL038("EXPWAYBILL038","&1该运单已登记","The waybill &1 has been registered","&1 تم تسجيل بوليصة الشحن"),
    EXPWAYBILL041("EXPWAYBILL041","货款表回写数据失败","Failed to write the payment table data","فشلت في إعادة كتابة بيان ثمن الشراء"),
    EXPWAYBILL042("EXPWAYBILL042","路由不能为空","The route cannot be null","الطريق لا يمكن أن يكون فارغا"),
    EXPWAYBILL043("EXPWAYBILL043","获取登录人信息失败","Failed to get logon information","فشل الحصول على معلومات صاحب الدخول"),
    EXPWAYBILL044("EXPWAYBILL044","请选择业务员","Please select a salesman","الرجاء تحديد مندوب مبيعات"),
    EXPWAYBILL045("EXPWAYBILL045","运单不属于当前业务员","The waybill does not belong to the current salesman","مذكرة الشحن لا تنتمي إلى البائع الحالي"),
    EXPWAYBILL046("EXPWAYBILL046","打印数量超出范围","The number of prints is out of range","كمية الطباعة خارج النطاق"),
    EXPWAYBILL047("EXPWAYBILL047","问题件不存在","The problem item does not exist","المشكلة ليست موجودة"),
    EXPWAYBILL048("EXPWAYBILL048", "运单&1重复扫描", "Waybill &1 repeat scan", "&1وثيقة الشحن تكرر المسح"),
    EXPWAYBILL049("EXPWAYBILL049", "文件移动失败", "File move failed", " فشل نقل الملف "),
    EXPWAYBILL050("EXPWAYBILL050", "没有需要重新生成报文的数据", "There is no data to regenerate the message", " لا توجد بيانات عن رسالة إعادة توليد "),
    EXPWAYBILL051("EXPWAYBILL051", "&1不是可审批的状态", " &1,it cannot be approved", "&1 لا يمكن الموافقة عليها"),
    EXPWAYBILL052("EXPWAYBILL052", "&1不是可调整客服的状态", " &1,It's not an adjustable state", "&1 ليس حالة قابلة للتعديل"),
    EXPWAYBILL053("EXPWAYBILL053", "&1不是可重新推送状态", " &1,It cannot push again", "&1 لا يمكن إعادة التوزيع"),

    EXPWAYBILL054("EXPWAYBILL054", "新增清关数据失败", "Failed to add customs clearance data", "فشل بيانات التخليص الجمركي الإضافية"),

    // 月结账单
    EXPMONTHLYBILL010("EXPMONTHLYBILL010", "帐单月份不能为空！", "Bill month cannot be null!", "لا يمكن أن يكون شهر الفاتورة فارغًا！"),// 月结账单
    EXPMONTHLYBILL011("EXPMONTHLYBILL011", "该运单已经生成对应的月结账单！", "The waybill already generated monthly bill！", "بوليصة الشحن تم إنشاؤها بالفعل فاتورة شهرية！"),
    EXPMONTHLYBILL012("EXPMONTHLYBILL012", "该运单不在当前账单，无法删除！", "The waybill not in current monthly bill, cannot delete！", "بوليصة الشحن ليست في الفاتورة الشهرية الحالية ، لا يمكن حذفها！"),
    EXPMONTHLYBILL013("EXPMONTHLYBILL013", "该运单付款方式不支持此操作！", "The waybill pay method not support current operation！", "طريقة دفع بوليصة الشحن لا تدعم العملية الحالية！"),
    EXPMONTHLYBILL014("EXPMONTHLYBILL014", "该运单不属于当前客户，无法进行操作！", "The waybill does not belong current cus, unable to operate！", "لا تنتمي بوليصة الشحن إلى العميل الحالي ، غير قادر على العمل！"),
    EXPMONTHLYBILL015("EXPMONTHLYBILL015", "&1产品类型的编码生成失败！", "&1 product type code generation failed!", "& 1 فشل إنشاء رمز نوع المنتجً！"),


    ;

    //-----------------财务管理(EXPFINANCE)-------------------


    //-----------------报表管理(EXPREPORT)-------------------


    //-----------------出库管理(EXPSYSTEM)-------------------


    //-----------------接口(EXPINTERFACE)-------------------


    //-----------------其他(EXPOTHER)-------------------

    private final String key;

    /**
     * 中文
     */
    private final String value;

    /**
     * 英文
     */
    private final String value_en;

    /**
     * 阿拉伯文
     */
    private final String value_ar;

    /**
     * 根据key获取去value
     *
     * @param key
     * @return
     */
    public static String getValueByCode(String key, String code) {
        for (ExceptionMessageEnum exceptionMessageEnum : ExceptionMessageEnum.values()) {
            if (key.equals(exceptionMessageEnum.getKey())) {
                code = StringUtils.isEmpty(code)?"zh":code;
                switch (code) {
                    case "zh":
                        return exceptionMessageEnum.getValue();
                    case "en":
                        return exceptionMessageEnum.getValue_en();
                    case "ar":
                        return exceptionMessageEnum.getValue_ar();
                    default:
                }
            }
        }
        return null;
    }

}


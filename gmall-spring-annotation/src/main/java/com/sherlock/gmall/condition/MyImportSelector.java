package com.sherlock.gmall.condition;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @Author xueshuai
 * @Description: TODO
 * @Date 2020/10/23
 **/
// 自定义逻辑返回需要导入的组件
public class MyImportSelector implements ImportSelector {
    /**
     * @param importingClassMetadata 当前标注@Import注解的类的所有注解信息
     * @return 返回值，就是到导入到容器中的组件全类名
     */
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        // 方法不要返回null值
        return new String[]{"com.sherlock.gmall.bean.Blue", "com.sherlock.gmall.bean.Yellow"};
    }
}

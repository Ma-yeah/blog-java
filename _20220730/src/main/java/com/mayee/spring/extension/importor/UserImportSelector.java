package com.mayee.spring.extension.importor;

import com.mayee.bean.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 当配置类实现了 ImportSelector 接口的时候，就会调用 selectImports 方法的实现，获取一批类的全限定名，最终这些类就会被注册到Spring容器中。
*/
@Slf4j
public class UserImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        log.info("调用 UserImportSelector 的 selectImports 方法获取一批类限定名");
        return new String[]{"com.mayee.bean.User"};
    }

    @Import(UserImportSelector.class)
    static class Application{
        public static void main(String[] args) {
            AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
            //将当前类注册到容器中
            applicationContext.register(Application.class);
            applicationContext.refresh();

            log.info("获取到的Bean为" + applicationContext.getBean(User.class));
        }
    }
}

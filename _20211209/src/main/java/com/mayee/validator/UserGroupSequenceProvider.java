package com.mayee.validator;

import com.mayee.model.User;
import com.mayee.validator.groups.ValidLearningGroup;
import com.mayee.validator.groups.ValidWorkingGroup;
import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @program: blog-example
 * @description:
 * @author: Bobby.Ma
 * @create: 2021-12-13 15:54
 **/
public class UserGroupSequenceProvider implements DefaultGroupSequenceProvider<User> {
    @Override
    public List<Class<?>> getValidationGroups(User user) {
        List<Class<?>> defaultGroupSequence = new ArrayList<>();
        defaultGroupSequence.add(User.class); // 这一步不能省,否则Default分组都不会执行了，会抛错的
        Integer age = Optional.ofNullable(user).map(User::getAge).orElse(null);
        if (Objects.nonNull(age)) { // 这块判空请务必要做
            if (age <= 22) {
                defaultGroupSequence.add(ValidLearningGroup.class);
            } else {
                defaultGroupSequence.add(ValidWorkingGroup.class);
            }
        }
        return defaultGroupSequence;
    }
}

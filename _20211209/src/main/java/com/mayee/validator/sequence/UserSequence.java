package com.mayee.validator.sequence;

import javax.validation.GroupSequence;

@GroupSequence({EmailSeq.class, PhoneSeq.class})
public interface UserSequence {
}

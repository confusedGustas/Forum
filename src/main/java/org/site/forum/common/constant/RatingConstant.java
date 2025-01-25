package org.site.forum.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RatingConstant {

    public static final Set<Integer> VALID_RATINGS = Set.of(-1, 0, 1);

}

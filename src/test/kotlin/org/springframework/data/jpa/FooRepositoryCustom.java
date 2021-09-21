package org.springframework.data.jpa;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import java.util.List;

public interface FooRepositoryCustom {
    default List<Person> findAll2(@Nullable Specification<Person> spec) {
        return findAll(spec);
    }

    List<Person> findAll(@Nullable Specification<Person> spec);
}

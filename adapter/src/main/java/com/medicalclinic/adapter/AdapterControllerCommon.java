package com.medicalclinic.adapter;

import enums.SortDirection;
import internalCommandsAndDtos.PageQuery;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class AdapterControllerCommon {
    public static Pageable toPageable(PageQuery query) {
        Sort sort = Sort.by(Sort.Direction.valueOf(query.direction().name()), query.sortBy());
        return PageRequest.of(query.page(), query.size(), sort);
    }
    public static PageQuery toPageQuerry(Pageable pageable) {
        Sort.Order order = pageable.getSort().stream().findFirst().orElse(Sort.Order.asc("id"));
        return new PageQuery(pageable.getPageNumber(), pageable.getPageSize(),
                order.getProperty(), SortDirection.valueOf(order.getDirection().name()));
    }
}

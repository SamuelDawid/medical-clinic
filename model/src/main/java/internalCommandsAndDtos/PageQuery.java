package internalCommandsAndDtos;

import enums.SortDirection;

public record PageQuery(
        int page,
        int size,
        String sortBy,
        SortDirection direction
) {
    public PageQuery{
        if (page < 0) throw new IllegalArgumentException("page must be >= 0");
        if (size < 1 || size > 100) throw new IllegalArgumentException("size must be 1..100");
    }
}

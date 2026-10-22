package internalCommandsAndDtos;

import java.util.List;
import java.util.function.Function;

public record PageResult<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages
) {
    public <R> PageResult<R> map(Function<T, R> mapper){
        return new PageResult<>(content.stream().map(mapper).toList()
                ,pageNumber,pageSize,totalElements,totalPages);
    }
}

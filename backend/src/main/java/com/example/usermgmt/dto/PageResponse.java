package com.example.usermgmt.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "PageResponse")
public class PageResponse<T> {

    @ArraySchema(schema = @Schema(description = "Items for the current page"))
    private List<T> items;

    @Schema(example = "0")
    private int page;

    @Schema(example = "20")
    private int size;

    @Schema(example = "100")
    private long totalItems;

    @Schema(example = "5")
    private int totalPages;

    @Schema(description = "Sort parameters applied", example = "updatedAt,desc")
    private List<String> sort;
}


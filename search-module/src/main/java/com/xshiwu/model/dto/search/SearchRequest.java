package com.xshiwu.model.dto.search;

import lombok.Data;

import java.io.Serializable;

@Data
public class SearchRequest implements Serializable {

    private String searchText;

    private Integer pageNum;

    private Integer pageSize;

    /**
     * <ul>
     *     <li>post</li>
     *     <li>picture</li>
     *     <li>user</li>
     * </ul>
     */
    private String type;

    private static final long serialVersionUID = 1L;

}

package com.xshiwu.model.vo;

import com.xshiwu.model.entity.Picture;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Picture> pictureList;

    private List<UserVO> userList;

    private List<PostVO> postList;

    private List<?> dataList;

    private String type;

    public SearchVO(List<Picture> pictureList, List<UserVO> userList, List<PostVO> postList) {
        this.pictureList = pictureList;
        this.userList = userList;
        this.postList = postList;
    }
}

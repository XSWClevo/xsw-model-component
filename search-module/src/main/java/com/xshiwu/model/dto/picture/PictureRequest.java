package com.xshiwu.model.dto.picture;

import com.xshiwu.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class PictureRequest extends PageRequest implements Serializable {

    private String searchText;

    private static final long serialVersionUID = 1L;

}


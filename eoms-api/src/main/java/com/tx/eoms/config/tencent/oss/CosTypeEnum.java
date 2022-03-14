package com.tx.eoms.config.tencent.oss;

import java.util.Objects;

public enum CosTypeEnum {

    ARCHIVE("archive"), AVATAR("avatar");

    private final String key;

    CosTypeEnum(String key) {
        this.key = key;
    }

    private String getKey() {
        return key;
    }

    public static CosTypeEnum findByKey(String key) {
        if (key != null) {
            for (CosTypeEnum type : CosTypeEnum.values()) {
                if (Objects.equals(key, type.getKey())) {
                    return type;
                }
            }
        }
        return null;
    }
}

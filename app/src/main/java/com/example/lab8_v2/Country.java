package com.example.lab8_v2;

import com.google.gson.annotations.SerializedName;

public class Country {
    @SerializedName("name")
    private Name name;
    
    @SerializedName("flags")
    private Flags flags;

    public String getName() {
        return name != null ? name.getCommon() : "";
    }

    public String getFlag() {
        return flags != null ? flags.getPng() : "";
    }

    public static class Name {
        @SerializedName("common")
        private String common;

        public String getCommon() {
            return common;
        }
    }

    public static class Flags {
        @SerializedName("png")
        private String png;

        public String getPng() {
            return png;
        }
    }
} 
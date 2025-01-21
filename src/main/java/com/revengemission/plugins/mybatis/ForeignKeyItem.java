package com.revengemission.plugins.mybatis;

public class ForeignKeyItem {
    private String fkName;
    private String fkTableName;
    private String fkColumnName;
    private String pkTableName;
    private String pkColumnName;

    public ForeignKeyItem() {
    }

    public ForeignKeyItem(String fkName, String fkTableName, String fkColumnName, String pkTableName, String pkColumnName) {
        this.fkName = fkName;
        this.fkTableName = fkTableName;
        this.fkColumnName = fkColumnName;
        this.pkTableName = pkTableName;
        this.pkColumnName = pkColumnName;
    }

    public String getFkName() {
        return fkName;
    }

    public void setFkName(String fkName) {
        this.fkName = fkName;
    }

    public String getFkTableName() {
        return fkTableName;
    }

    public void setFkTableName(String fkTableName) {
        this.fkTableName = fkTableName;
    }

    public String getFkColumnName() {
        return fkColumnName;
    }

    public void setFkColumnName(String fkColumnName) {
        this.fkColumnName = fkColumnName;
    }

    public String getPkTableName() {
        return pkTableName;
    }

    public void setPkTableName(String pkTableName) {
        this.pkTableName = pkTableName;
    }

    public String getPkColumnName() {
        return pkColumnName;
    }

    public void setPkColumnName(String pkColumnName) {
        this.pkColumnName = pkColumnName;
    }

    @Override
    public String toString() {
        return "ForeignKeyItem{" +
                "fkName='" + fkName + '\'' +
                ", fkTableName='" + fkTableName + '\'' +
                ", fkColumnName='" + fkColumnName + '\'' +
                ", pkTableName='" + pkTableName + '\'' +
                ", pkColumnName='" + pkColumnName + '\'' +
                '}';
    }
}

package com.qzh.eggcloud.model.query;

/**
 * @ClassName RoleQuery
 * @Author DiangD
 * @Date 2021/5/5
 * @Version 1.0
 * @Description
 **/
public class RoleQuery extends QueryBase {
    String roleName;
    String remark;

    public RoleQuery() {
    }

    public RoleQuery(String roleName, String remark) {
        this.roleName = roleName;
        this.remark = remark;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}

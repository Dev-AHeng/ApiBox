package my.zjh.model_sanhaiapi.model;

import com.google.gson.annotations.SerializedName;

/**
 * ICP备案查询响应数据模型
 *
 * @author AHeng
 * @date 2025/03/26 15:35
 */
public class ICPFilingResponse {

    // 状态（success/error）
    @SerializedName("status")
    private String status;
    
    // 查询时间
    @SerializedName("timestamp")
    private String timestamp;
    
    // ICP备案信息
    @SerializedName("data")
    private ICPData data;
    
    // 提示信息
    @SerializedName("tips")
    private String tips;
    
    // 错误信息说明（错误时返回）
    @SerializedName("message")
    private String message;
    
    // 状态消息（错误时返回）
    @SerializedName("msg")
    private String msg;
    
    /**
     * 内部ICP数据类
     */
    public static class ICPData {
        // 查询的域名
        @SerializedName("domain")
        private String domain;
        
        // 主办单位名称
        @SerializedName("organization")
        private String organization;
        
        // 主办单位性质
        @SerializedName("org_type")
        private String orgType;
        
        // 主体备案号
        @SerializedName("icp_number")
        private String icpNumber;
        
        // 网站备案号
        @SerializedName("website_icp")
        private String websiteIcp;
        
        // 网站名称
        @SerializedName("website_name")
        private String websiteName;
        
        // 审核通过时间
        @SerializedName("approval_date")
        private String approvalDate;
        
        // 信息更新时间
        @SerializedName("update_time")
        private String updateTime;

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public String getOrganization() {
            return organization;
        }

        public void setOrganization(String organization) {
            this.organization = organization;
        }

        public String getOrgType() {
            return orgType;
        }

        public void setOrgType(String orgType) {
            this.orgType = orgType;
        }

        public String getIcpNumber() {
            return icpNumber;
        }

        public void setIcpNumber(String icpNumber) {
            this.icpNumber = icpNumber;
        }

        public String getWebsiteIcp() {
            return websiteIcp;
        }

        public void setWebsiteIcp(String websiteIcp) {
            this.websiteIcp = websiteIcp;
        }

        public String getWebsiteName() {
            return websiteName;
        }

        public void setWebsiteName(String websiteName) {
            this.websiteName = websiteName;
        }

        public String getApprovalDate() {
            return approvalDate;
        }

        public void setApprovalDate(String approvalDate) {
            this.approvalDate = approvalDate;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public ICPData getData() {
        return data;
    }

    public void setData(ICPData data) {
        this.data = data;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    
    // 便捷方法获取域名
    public String getDomain() {
        return data != null ? data.getDomain() : null;
    }
    
    // 便捷方法获取主办单位
    public String getOrganization() {
        return data != null ? data.getOrganization() : null;
    }
    
    // 便捷方法获取主办单位性质
    public String getOrgType() {
        return data != null ? data.getOrgType() : null;
    }
    
    // 便捷方法获取主体备案号
    public String getIcpNumber() {
        return data != null ? data.getIcpNumber() : null;
    }
} 
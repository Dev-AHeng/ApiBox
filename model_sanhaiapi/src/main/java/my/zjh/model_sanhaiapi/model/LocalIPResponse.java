package my.zjh.model_sanhaiapi.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 本地IP查询响应数据模型
 *
 * @author AHeng
 * @date 2025/03/26
 */
public class LocalIPResponse {
    
    private String status;
    private String timestamp;
    
    private Network network;
    private Client client;
    private Server server;
    private Security security;
    private String tips;
    
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
    
    public Network getNetwork() {
        return network;
    }
    
    public void setNetwork(Network network) {
        this.network = network;
    }
    
    public Client getClient() {
        return client;
    }
    
    public void setClient(Client client) {
        this.client = client;
    }
    
    public Server getServer() {
        return server;
    }
    
    public void setServer(Server server) {
        this.server = server;
    }
    
    public Security getSecurity() {
        return security;
    }
    
    public void setSecurity(Security security) {
        this.security = security;
    }
    
    public String getTips() {
        return tips;
    }
    
    public void setTips(String tips) {
        this.tips = tips;
    }
    
    /**
     * 网络信息
     */
    public static class Network {
        private String ip;
        private String version;
        private Location location;
        private Proxy proxy;
        private String port;
        
        public String getIp() {
            return ip;
        }
        
        public void setIp(String ip) {
            this.ip = ip;
        }
        
        public String getVersion() {
            return version;
        }
        
        public void setVersion(String version) {
            this.version = version;
        }
        
        public Location getLocation() {
            return location;
        }
        
        public void setLocation(Location location) {
            this.location = location;
        }
        
        public Proxy getProxy() {
            return proxy;
        }
        
        public void setProxy(Proxy proxy) {
            this.proxy = proxy;
        }
        
        public String getPort() {
            return port;
        }
        
        public void setPort(String port) {
            this.port = port;
        }
        
        /**
         * IP地址地理位置信息
         */
        public static class Location {
            private String country;
            private String city;
            private String district;
            private String isp;
            
            public String getCountry() {
                return country;
            }
            
            public void setCountry(String country) {
                this.country = country;
            }
            
            public String getCity() {
                return city;
            }
            
            public void setCity(String city) {
                this.city = city;
            }
            
            public String getDistrict() {
                return district;
            }
            
            public void setDistrict(String district) {
                this.district = district;
            }
            
            public String getIsp() {
                return isp;
            }
            
            public void setIsp(String isp) {
                this.isp = isp;
            }
        }
        
        /**
         * 代理信息
         */
        public static class Proxy {
            @SerializedName("forwarded_for")
            private String forwardedFor;
            private String via;
            
            public String getForwardedFor() {
                return forwardedFor;
            }
            
            public void setForwardedFor(String forwardedFor) {
                this.forwardedFor = forwardedFor;
            }
            
            public String getVia() {
                return via;
            }
            
            public void setVia(String via) {
                this.via = via;
            }
        }
    }
    
    /**
     * 客户端信息
     */
    public static class Client {
        private Device device;
        private Headers headers;
        private String referer;
        
        public Device getDevice() {
            return device;
        }
        
        public void setDevice(Device device) {
            this.device = device;
        }
        
        public Headers getHeaders() {
            return headers;
        }
        
        public void setHeaders(Headers headers) {
            this.headers = headers;
        }
        
        public String getReferer() {
            return referer;
        }
        
        public void setReferer(String referer) {
            this.referer = referer;
        }
        
        /**
         * 设备信息
         */
        public static class Device {
            private String type;
            private String os;
            private String browser;
            
            public String getType() {
                return type;
            }
            
            public void setType(String type) {
                this.type = type;
            }
            
            public String getOs() {
                return os;
            }
            
            public void setOs(String os) {
                this.os = os;
            }
            
            public String getBrowser() {
                return browser;
            }
            
            public void setBrowser(String browser) {
                this.browser = browser;
            }
        }
        
        /**
         * 请求头信息
         */
        public static class Headers {
            @SerializedName("user_agent")
            private String userAgent;
            private String language;
            private String encoding;
            private String accept;
            private String dnt;
            private String cookie;
            
            public String getUserAgent() {
                return userAgent;
            }
            
            public void setUserAgent(String userAgent) {
                this.userAgent = userAgent;
            }
            
            public String getLanguage() {
                return language;
            }
            
            public void setLanguage(String language) {
                this.language = language;
            }
            
            public String getEncoding() {
                return encoding;
            }
            
            public void setEncoding(String encoding) {
                this.encoding = encoding;
            }
            
            public String getAccept() {
                return accept;
            }
            
            public void setAccept(String accept) {
                this.accept = accept;
            }
            
            public String getDnt() {
                return dnt;
            }
            
            public void setDnt(String dnt) {
                this.dnt = dnt;
            }
            
            public String getCookie() {
                return cookie;
            }
            
            public void setCookie(String cookie) {
                this.cookie = cookie;
            }
        }
    }
    
    /**
     * 服务器信息
     */
    public static class Server {
        private String software;
        private String protocol;
        private String method;
        private String scheme;
        private String port;
        private String time;
        
        public String getSoftware() {
            return software;
        }
        
        public void setSoftware(String software) {
            this.software = software;
        }
        
        public String getProtocol() {
            return protocol;
        }
        
        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }
        
        public String getMethod() {
            return method;
        }
        
        public void setMethod(String method) {
            this.method = method;
        }
        
        public String getScheme() {
            return scheme;
        }
        
        public void setScheme(String scheme) {
            this.scheme = scheme;
        }
        
        public String getPort() {
            return port;
        }
        
        public void setPort(String port) {
            this.port = port;
        }
        
        public String getTime() {
            return time;
        }
        
        public void setTime(String time) {
            this.time = time;
        }
    }
    
    /**
     * 安全信息
     */
    public static class Security {
        private String https;
        private TLS tls;
        
        public String getHttps() {
            return https;
        }
        
        public void setHttps(String https) {
            this.https = https;
        }
        
        public TLS getTls() {
            return tls;
        }
        
        public void setTls(TLS tls) {
            this.tls = tls;
        }
        
        /**
         * TLS信息
         */
        public static class TLS {
            private String version;
            private String cipher;
            
            public String getVersion() {
                return version;
            }
            
            public void setVersion(String version) {
                this.version = version;
            }
            
            public String getCipher() {
                return cipher;
            }
            
            public void setCipher(String cipher) {
                this.cipher = cipher;
            }
        }
    }
} 
package com.everonet.demo.miniprograms.model;

import java.util.List;

public class MiniAppRespone {

    private ResultEntity mini_app;

    private List<ResultEntity> mini_apps;

    public ResultEntity getMini_app() {
        return mini_app;
    }

    public void setMini_app(ResultEntity mini_app) {
        this.mini_app = mini_app;
    }

    public List<ResultEntity> getMini_apps() {
        return mini_apps;
    }

    public void setMini_apps(List<ResultEntity> mini_apps) {
        this.mini_apps = mini_apps;
    }

    public static class ResultEntity {
        private String gid;
        private int version;
        private String module_name;
        private String display_name;
        private String icon_uri;
        private String bundle_uri;
        private String icon_digest;
        private String bundle_digest;

        public String getGid() {
            return gid;
        }

        public void setGid(String gid) {
            this.gid = gid;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public String getModule_name() {
            return module_name;
        }

        public void setModule_name(String module_name) {
            this.module_name = module_name;
        }

        public String getDisplay_name() {
            return display_name;
        }

        public void setDisplay_name(String display_name) {
            this.display_name = display_name;
        }

        public String getIcon_uri() {
            return icon_uri;
        }

        public void setIcon_uri(String icon_uri) {
            this.icon_uri = icon_uri;
        }

        public String getBundle_uri() {
            return bundle_uri;
        }

        public void setBundle_uri(String bundle_uri) {
            this.bundle_uri = bundle_uri;
        }

        public String getIcon_digest() {
            return icon_digest;
        }

        public void setIcon_digest(String icon_digest) {
            this.icon_digest = icon_digest;
        }

        public String getBundle_digest() {
            return bundle_digest;
        }

        public void setBundle_digest(String bundle_digest) {
            this.bundle_digest = bundle_digest;
        }
    }


}

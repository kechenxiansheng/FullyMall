package com.cm.fm.mall.model.bean;

import java.util.List;

/** json解析bean
 *  参数需要和json文件的字段对应，否则 工具解析时找不到
 */
//TODO
public class ClassifyCategory{
    int code;
    List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }


    //TODO 类别信息bean
    public static class DataBean{
        String type;
        List<DataListBean> dataList;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<DataListBean> getDatalist() {
            return dataList;
        }

        public void setDatalist(List<DataListBean> dataList) {
            this.dataList = dataList;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "type='" + type + '\'' +
                    ", dataList=" + dataList +
                    '}';
        }

        //TODO 子类别信息bean
        public static class DataListBean{
            String name;        //子类名
            String image;       //子类图片

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }

            @Override
            public String toString() {
                return "DataListBean{" +
                        "name='" + name + '\'' +
                        ", image='" + image + '\'' +
                        '}';
            }
        }
    }






}


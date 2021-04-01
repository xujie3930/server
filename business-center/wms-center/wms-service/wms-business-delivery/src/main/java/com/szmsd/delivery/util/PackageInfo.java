package com.szmsd.delivery.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-04-01 19:33
 */
public class PackageInfo {

    private double length;
    private double width;
    private double height;

    public PackageInfo() {
    }

    public PackageInfo(Double length, Double width, Double height) {
        this.length = Utils.defaultValue(length);
        this.width = Utils.defaultValue(width);
        this.height = Utils.defaultValue(height);
    }

    public PackageInfo reset() {
        List<Double> list = new ArrayList<>();
        list.add(this.length);
        list.add(this.width);
        list.add(this.height);
        list.sort(Double::compareTo);
        return new PackageInfo(list.get(0), list.get(1), list.get(2));
    }

    public void join(PackageInfo p) {
        this.length = Math.max(this.length, p.length);
        this.width = Math.max(this.width, p.width);
        this.height = this.height + p.height;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}

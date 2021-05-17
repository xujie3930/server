package com.szmsd.common.log.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.AbstractMatcherFilter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * @author zhangyuyuan
 * @date 2021-05-17 10:04
 */
public class ManyLevelFilter extends AbstractMatcherFilter<ILoggingEvent> {

    private Level level;
    private FilterModel model;

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (!isStarted()) {
            return FilterReply.NEUTRAL;
        }
        if (FilterModel.EQ.equals(this.model)) {
            if (event.getLevel().equals(this.level)) {
                return this.onMatch;
            }
        } else if (FilterModel.LE.equals(this.model)) {
            if (event.getLevel().toInt() <= this.level.toInt()) {
                return this.onMatch;
            }
        } else if (FilterModel.GE.equals(this.model)) {
            if (event.getLevel().toInt() >= this.level.toInt()) {
                return this.onMatch;
            }
        }
        return this.onMismatch;
    }

    @Override
    public void start() {
        if (this.level != null && this.model != null) {
            super.start();
        }
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public void setModel(FilterModel model) {
        this.model = model;
    }
}

package com.szmsd.common.core.command;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.springframework.util.Assert;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 */
public final class CommandInvoker {

    private Table<Command<? extends Object>, Consumer<? extends Object>, Boolean> commandTable = HashBasedTable.create();

    public CommandInvoker() {
    }

    /**
     * 添加一个命名
     *
     * @param command
     * @param <T>
     */
    public <T> void add(Command<T> command) {
        Assert.notNull(command, "command不能为空");
        commandTable.put(command, o -> {
        }, true);
    }

    /**
     * 添加一个命令，并指定运行的前置条件，满足则执行，否则忽略
     *
     * @param command
     * @param predicate
     * @param <T>
     */
    @Deprecated
    public <T> void add(Command<T> command, Supplier<Boolean> predicate) {
        Assert.notNull(command, "command不能为空");
        Assert.notNull(predicate, "predicate不能为空");
        commandTable.put(command, o -> {
        }, predicate.get());
    }

    /**
     * 添加一个命令，并指定运行的前置条件，满足则执行，否则忽略
     *
     * @param command
     * @param predicate
     * @param <T>
     */
    public <T> void add(Command<T> command, Boolean predicate) {
        Assert.notNull(command, "command不能为空");
        Assert.notNull(predicate, "predicate不能为空");
        commandTable.put(command, o -> {
        }, predicate);
    }

    /**
     * 添加一个命令，并声明该命令的返回值，触发consumer
     *
     * @param command
     * @param consumer
     * @param <T>
     */
    public <T> void add(Command<T> command, Consumer<T> consumer) {
        Assert.notNull(command, "command不能为空");
        Assert.notNull(consumer, "consumer不能为空");
        commandTable.put(command, consumer, true);
    }

    /**
     * 添加一个命令，并声明该命令的返回值，触发consumer， 并指定运行的前置条件，满足则执行，否则忽略
     *
     * @param command
     * @param consumer
     * @param predicate
     * @param <T>
     */
    @Deprecated
    public <T> void add(Command<T> command, Consumer<T> consumer, Supplier<Boolean> predicate) {
        Assert.notNull(command, "command不能为空");
        Assert.notNull(predicate, "predicate不能为空");
        Assert.notNull(consumer, "consumer不能为空");
        commandTable.put(command, consumer, predicate.get());
    }

    /**
     * 添加一个命令，并声明该命令的返回值，触发consumer， 并指定运行的前置条件，满足则执行，否则忽略
     *
     * @param command
     * @param consumer
     * @param predicate
     * @param <T>
     */
    public <T> void add(Command<T> command, Consumer<T> consumer, Boolean predicate) {
        Assert.notNull(command, "command不能为空");
        Assert.notNull(predicate, "predicate不能为空");
        Assert.notNull(consumer, "consumer不能为空");
        commandTable.put(command, consumer, predicate);
    }

    public void clear() {
        commandTable.clear();
    }

    public void execute() {
        List<Table.Cell<Command<?>, Consumer<?>, Boolean>> collect = commandTable.cellSet().stream().filter(cell -> cell.getValue()).collect(Collectors.toList());
        for (Table.Cell<Command<?>, Consumer<?>, Boolean> cell : collect) {
            if (cell.getValue() != null && cell.getValue()) {
                Object result = cell.getRowKey().execute();
                Consumer consumer = cell.getColumnKey();
                consumer.accept(result);
            }
        }
    }
}

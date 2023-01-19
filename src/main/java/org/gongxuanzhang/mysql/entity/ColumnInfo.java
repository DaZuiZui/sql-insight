package org.gongxuanzhang.mysql.entity;

import lombok.Data;
import org.gongxuanzhang.mysql.exception.MySQLException;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 列信息
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class ColumnInfo implements ExecuteInfo {

    private ColumnType type;
    private String name;
    private String comment;
    private boolean autoIncrement;
    private boolean notNull;
    private boolean unique;
    private String defaultValue;




}
